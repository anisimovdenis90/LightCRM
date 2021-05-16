package ru.lightcrm.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.lightcrm.entities.FileInfo;
import ru.lightcrm.entities.Profile;
import ru.lightcrm.exceptions.ResourceNotFoundException;
import ru.lightcrm.repositories.FileInfoRepository;
import ru.lightcrm.services.interfaces.FileManagerService;
import ru.lightcrm.services.interfaces.FileService;
import ru.lightcrm.services.interfaces.ProfileService;

import javax.validation.ValidationException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private static final DateTimeFormatter DTF_SUFFIX_FILENAME = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
    private final FileInfoRepository fileInfoRepository;
    private final FileManagerService fileManagerService;
    private ProfileService profileService;

    @Autowired
    public void setProfileService(ProfileService profileService) {
        this.profileService = profileService;
    }

    @Override
    @Transactional(rollbackFor = ValidationException.class)
    public FileInfo upload(MultipartFile resource) {
        try {
            String prefixKeyName = generateKeyName(resource.getName());
            String suffix = getSuffixFromFileName(resource.getOriginalFilename());
            String fileKeyName = prefixKeyName + "." + suffix;
            FileInfo fileInfo = FileInfo.createNewFileInfo(resource, fileKeyName);
            fileInfo = fileInfoRepository.save(fileInfo);
            fileManagerService.upload(resource.getBytes(), fileKeyName);
            return fileInfo;
        } catch (IOException | NullPointerException e) {
            throw new ValidationException("Некорректно загружен файл: " + resource.getOriginalFilename());
        }
    }

    @Override
    @Transactional(rollbackFor = ValidationException.class)
    public void uploadPhoto(MultipartFile resource, String login) {
        FileInfo previewFilePhoto;
        try {
            previewFilePhoto = savePreview(resource);
        } catch (IOException e) {
            throw new ValidationException(e.getMessage());
        }
        FileInfo photoFileInfo = upload(resource);
        Profile profile = profileService.findByUserLogin(login);
        if (profile.getPhoto() != null) {
            deleteFiles(profile.getPhoto(), profile.getPreview());
        }
        profile.setPhoto(photoFileInfo);
        profile.setPreview(previewFilePhoto);
        profileService.saveProfile(profile);
    }

    @Override
    public FileInfo savePreview(MultipartFile origPhotoData) throws IOException {
        try {
            String prefixKeyName = generateKeyName(origPhotoData.getName());
            String prefixName = origPhotoData.getOriginalFilename().substring(0, origPhotoData.getOriginalFilename().lastIndexOf(".")) + "_preview";
            String suffix = getSuffixFromFileName(origPhotoData.getOriginalFilename());
            String previewName = prefixName + "." + suffix;
            String previewKeyName = prefixKeyName + "." + suffix;
            long previewSize = fileManagerService.savePreview(origPhotoData.getBytes(), previewKeyName);
            FileInfo previewFileInfo = FileInfo.createNewFileInfo(previewName, previewKeyName, origPhotoData.getContentType(), previewSize);
            previewFileInfo = fileInfoRepository.save(previewFileInfo);
            return previewFileInfo;
        } catch (IOException | NullPointerException e) {
            throw new IOException("Некорректно загружен файл: " + origPhotoData.getOriginalFilename());
        }
    }

    @Override
    public FileInfo findById(Long id) {
        return fileInfoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("FileInfo с id %s не найден", id)));
    }

    @Override
    public Resource download(String keyName) {
        try {
            return fileManagerService.download(keyName);
        } catch (IOException e) {
            throw new ValidationException("Ошибка отправки файла");
        }
    }

    @Override
    public FileInfo findPhotoFileInfoByUserLogin(String login) {
        return fileInfoRepository.findPhotoFileInfoByUserLogin(login).orElse(null);
    }

    @Override
    public FileInfo findPreviewFileInfoByUserLogin(String login) {
        return fileInfoRepository.findPreviewFileInfoByUserLogin(login).orElse(null);
    }

    @Override
    public FileInfo findPreviewFileInfoByProfileId(Long id) {
        return fileInfoRepository.findPreviewFileInfoByProfileId(id).orElse(null);
    }

    @Override
    public void deleteFiles(FileInfo... fileInfos) {
        String[] keyNames = Arrays.stream(fileInfos)
                .map(FileInfo::getKeyName).toArray(String[]::new);
        fileInfoRepository.deleteAll(Arrays.asList(fileInfos));
        fileManagerService.deleteFiles(keyNames);
    }

    private String generateKeyName(String name) {
        OffsetDateTime now = OffsetDateTime.now();
        return DigestUtils.md5DigestAsHex((name + now).getBytes(StandardCharsets.UTF_8))
                + "-" + now.format(DTF_SUFFIX_FILENAME);
    }

    private String getSuffixFromFileName(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}
