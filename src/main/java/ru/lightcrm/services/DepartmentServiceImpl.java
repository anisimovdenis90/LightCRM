package ru.lightcrm.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.lightcrm.entities.Department;
import ru.lightcrm.entities.dtos.DepartmentDto;
import ru.lightcrm.exceptions.ResourceNotFoundException;
import ru.lightcrm.repositories.DepartmentRepository;
import ru.lightcrm.repositories.ProfileRepository;
import ru.lightcrm.services.interfaces.DepartmentService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final ProfileRepository profileRepository;

    @Override
    public List<DepartmentDto> findAll() {
        return departmentRepository.findAll().stream().map(DepartmentDto::new).collect(Collectors.toList());
    }
    @Override
    public List<String> getAllDepartmentNames() {
        return departmentRepository.findAll().stream().map(Department::getName).collect(Collectors.toList());
    }

    @Override
    public DepartmentDto findDtoById(Long id) {
        return new DepartmentDto(departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Отдел с id = %s не найден", id))));
    }

    @Override
    public Department findById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Отдел с id = %s не найден", id)));
    }

    @Override
    public Department findOneByName(String name) {
        return departmentRepository.findOneByName(name)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Отдел с наименованием = \"%s\" не найден", name)));
    }

    @Override
    public DepartmentDto findOneDtoByName(String name) {
        return new DepartmentDto(findOneByName(name));
    }

    @Override
    public DepartmentDto findOneByLeaderId(Long id) {
        return new DepartmentDto(departmentRepository.findOneByLeaderId(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Отдел руководителя (id = %d) не найден", id))));
    }

    @Override
    public void deleteById(Long id) {
        Department department = departmentRepository.findOneById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Не найден отдел с (id = %d)", id)));
        if (department.getEmployees() == null){
            departmentRepository.deleteById(id);
        }
        else {
            throw new ResourceNotFoundException(String.format("К данному отделу привязаны сотрудники. Переведите сотрудников прежде чем удалять отдел!"));
        }
    }

    @Override
    public DepartmentDto saveOrUpdate(DepartmentDto departmentDto) {
        Department department;
        if (departmentDto.getId() == null) {
            department = new Department();
        } else {
            department = departmentRepository.findById(departmentDto.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(String.format("Департамент с id = %s не найден", departmentDto.getId())));
        }
        department.setName(departmentDto.getName());
        department.setDescription(departmentDto.getDescription());
        department.setLeader(profileRepository.findById(departmentDto.getLeaderId())
                        .orElseThrow(() -> new ResourceNotFoundException(String.format("Не найден руководитель отдела с (id = %d)", departmentDto.getLeaderId()))));
        department.setLeadDepartment(findById(departmentDto.getLeadDepartmentId()));

        return new DepartmentDto(departmentRepository.save(department));
    }
}
