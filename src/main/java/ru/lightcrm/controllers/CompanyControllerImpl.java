package ru.lightcrm.controllers;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.lightcrm.controllers.interfaces.CompanyController;
import ru.lightcrm.entities.dtos.CompanyDto;
import ru.lightcrm.entities.dtos.ContactDto;
import ru.lightcrm.services.interfaces.CompanyService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CompanyControllerImpl implements CompanyController {

    private final CompanyService companyService;

    @GetMapping
    public List<CompanyDto> getCompanyContent() {
        return companyService.findDtoAll();
    }

    @Override
    public List<CompanyDto> getManagedCompanies(Long id) {
        return companyService.findByManagerId(id);
    }

    @Override
    public CompanyDto getCompany(Long id) {
        return companyService.findDtoById(id);
    }

    @Override
    public CompanyDto saveCompany(CompanyDto companyDto) {
        companyDto.setId(null);
        return companyService.saveOrUpdate(companyDto);
    }

    @Override
    public void delete(Long id) {
        companyService.deleteById(id);
    }

    @Override
    public CompanyDto updateCompany(CompanyDto companyDto) {
        return companyService.saveOrUpdate(companyDto);
    }

    @Override
    public void deleteContact(Long id) {
        companyService.deleteContactById(id);
    }

    @Override
    public ContactDto updateContact(ContactDto contactDto) {
        return companyService.saveOrUpdateContact(contactDto);
    }

    @Override
    public ContactDto saveContact(ContactDto contactDto) {
        return companyService.saveOrUpdateContact(contactDto);
    }
}
