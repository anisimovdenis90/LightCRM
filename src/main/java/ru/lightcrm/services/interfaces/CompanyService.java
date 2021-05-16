package ru.lightcrm.services.interfaces;

import ru.lightcrm.entities.Company;
import ru.lightcrm.entities.dtos.CompanyDto;
import ru.lightcrm.entities.dtos.ContactDto;

import java.util.List;

public interface CompanyService {
    CompanyDto findDtoByName(String name);
    CompanyDto findDtoByInn(Long inn);
    CompanyDto findDtoById(Long id);
    Company findById(Long id);
    Company findByName(String name);
    List<CompanyDto> findDtoAll();
    CompanyDto saveOrUpdate(CompanyDto companyDto);
    void deleteById(Long id);
    void deleteContactById(Long id);
    ContactDto saveOrUpdateContact(ContactDto contactDto);
    List<CompanyDto> findByManagerId(Long id);
}
