package ru.lightcrm.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.lightcrm.entities.Company;
import ru.lightcrm.entities.Contact;
import ru.lightcrm.entities.Profile;
import ru.lightcrm.entities.dtos.CompanyDto;
import ru.lightcrm.entities.dtos.ContactDto;
import ru.lightcrm.exceptions.ResourceNotFoundException;
import ru.lightcrm.repositories.CompanyRepository;
import ru.lightcrm.repositories.ContactRepository;
import ru.lightcrm.services.interfaces.CompanyService;
import ru.lightcrm.services.interfaces.ProfileService;
import ru.lightcrm.services.interfaces.TaskService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepository companiesRepository;
    private ContactRepository contactRepository;
    private ProfileService profileService;
    private TaskService taskService;

    @Autowired
    public void setContactRepository(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Autowired
    public void setProfileService(ProfileService profileService) {
        this.profileService = profileService;
    }

    @Autowired
    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public Company findById(Long id) {
        return companiesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Компания с id '%s' не найдена", id)));
    }

    @Override
    public Company findByName(String name) {
        return companiesRepository.findOneByName(name)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Компания '%s' не найдена", name)));
    }

    public CompanyDto findDtoById(Long id) {
        return new CompanyDto(findById(id));
    }

    public CompanyDto findDtoByName(String name) {
        return new CompanyDto(findByName(name));
    }

    public CompanyDto findDtoByInn(Long inn) {
        return new CompanyDto(companiesRepository.findOneByInn(inn)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Компания с ИНН '%s' не найдена", inn))));
    }

    public List<CompanyDto> findDtoAll() {
        return companiesRepository.findAll().stream().map(c -> {
            CompanyDto companyDto = new CompanyDto(c);
            companyDto.setTasksCount(taskService.countByCompanyId(c.getId()));
            return companyDto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<CompanyDto> findByManagerId(Long id) {
        return companiesRepository.findByManagerId(id).stream().map(CompanyDto::new).collect(Collectors.toList());
    }

    @Override
    public CompanyDto saveOrUpdate(CompanyDto companyDto) {
        Company company;
        if (companyDto.getId() == null) {
            company = new Company();
            company.setContacts(null);
            company.setComments(null);
        } else {
            company = companiesRepository.findById(companyDto.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(String.format("Компания с id '%s' не найдена", companyDto.getId())));
        }
        company.setName(companyDto.getName());
        company.setType(companyDto.isType());
        company.setInn(companyDto.getInn());
        company.setBillNumber(companyDto.getBillNumber());
        company.setPhoneNumber(companyDto.getPhoneNumber());
        company.setEmail(companyDto.getEmail());

        Set<Profile> managers = companyDto.getManagers().stream()
                .map(m -> profileService.findById(m.getId())).collect(Collectors.toSet());
        company.setManagers(managers);

        return new CompanyDto(companiesRepository.save(company));
    }

    @Override
    public void deleteById(Long id) {
        companiesRepository.deleteById(id);
    }

    @Override
    public void deleteContactById(Long id) {
        contactRepository.deleteById(id);
    }

    @Override
    public ContactDto saveOrUpdateContact(ContactDto contactDto) {
        Contact contact;
        if (contactDto.getId() == null) {
            contact = new Contact();
        } else {
            contact = contactRepository.findById(contactDto.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(String.format("Контакт с Id %s не найден.", contactDto.getId())));
        }
        contact.setName(contactDto.getName());
        contact.setDescription(contactDto.getDescription());
        contact.setEmail(contactDto.getEmail());
        contact.setPost(contactDto.getPost());
        contact.setPhone(contactDto.getPhone());
        contact.setCompany(findById(contactDto.getCompanyId()));

        return new ContactDto(contactRepository.save(contact));
    }
}
