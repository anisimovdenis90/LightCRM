package ru.lightcrm.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.lightcrm.entities.Company;
import ru.lightcrm.entities.Contact;
import ru.lightcrm.entities.Profile;
import ru.lightcrm.entities.dtos.CompanyDto;
import ru.lightcrm.repositories.CompanyRepository;
import ru.lightcrm.repositories.ContactRepository;
import ru.lightcrm.services.interfaces.CompanyService;

import java.util.*;


@SpringBootTest
@ActiveProfiles("test")
class CompanyServiceTest {

    @Autowired
    private CompanyService companyService;

    @MockBean
    private CompanyRepository companyRepository;

    @MockBean
    private ContactRepository contactRepository;

    private static final String COMPANY_NAME = "Газпром";
    private static final boolean COMPANY_TYPE = true;
    private static final Long COMPANY_INN = 50282517112359L;
    private static final Long COMPANY_BILL_NUMBER = 50282517112359L;
    private static final String COMPANY_PHONE_NUMBER = "+79999992324";
    private static final String COMPANY_EMAIL = "gazprom@gazprom.ru";
    private static final int COMPANY_COUNT = 3;

    @BeforeEach
    private void init() {
        List<Company> companies = new ArrayList<>();
        List<Contact> contacts = new ArrayList<>();

        for (long i = 1; i <= COMPANY_COUNT; i++) {
            Company company = new Company();
            company.setId(i);
            company.setName(COMPANY_NAME + " " + i);
            company.setType(COMPANY_TYPE);
            company.setInn(COMPANY_INN);
            company.setBillNumber(COMPANY_BILL_NUMBER);
            company.setPhoneNumber(COMPANY_PHONE_NUMBER);
            company.setEmail(COMPANY_EMAIL);

            Set<Contact> contactSet = new HashSet<>(1);
            Contact contact = new Contact();
            contact.setId(1L);
            contact.setName("TEST");
            contact.setCompany(new Company());
            contactSet.add(contact);
            contacts.add(contact);
            company.setContacts(contactSet);

            Set<Profile> profiles = new HashSet<>(1);
            Profile profile = new Profile();
            profile.setId(1L);
            profiles.add(profile);
            company.setManagers(profiles);

            companies.add(company);
        }

        Mockito.doReturn(Optional.of(companies.get(0))).when(companyRepository).findOneByName(companies.get(0).getName());
        Mockito.doReturn(Optional.of(companies.get(0))).when(companyRepository).findOneByInn(companies.get(0).getInn());
        Mockito.doReturn(Optional.of(companies.get(0))).when(companyRepository).findById(companies.get(0).getId());
        Mockito.doReturn(companies).when(companyRepository).findAll();
        Mockito.doAnswer(invocation -> {
            companies.remove((int)(companies.get(0).getId() - 1));
            return null;
        }).when(companyRepository).deleteById(companies.get(0).getId());

        // Contacts
        Mockito.doReturn(contacts).when(contactRepository).findAll();
        Mockito.doAnswer(invocation -> {
            contacts.remove((int)(contacts.get(0).getId() - 1));
            return null;
        }).when(contactRepository).deleteById(contacts.get(0).getId());
    }

    @Test
    void findByName() {
        Long id = 1L;
        final String name = COMPANY_NAME + " " + id;

        CompanyDto companyDto = companyService.findDtoByName(name);
        Assertions.assertNotNull(companyDto);
        Assertions.assertEquals(id, companyDto.getId());
        Assertions.assertEquals(COMPANY_NAME + " " + id, companyDto.getName());
        Assertions.assertEquals(COMPANY_TYPE, companyDto.isType());
        Assertions.assertEquals(COMPANY_INN, companyDto.getInn());
        Assertions.assertEquals(COMPANY_BILL_NUMBER, companyDto.getBillNumber());
        Assertions.assertEquals(COMPANY_PHONE_NUMBER, companyDto.getPhoneNumber());
        Assertions.assertEquals(COMPANY_EMAIL, companyDto.getEmail());
        Assertions.assertEquals(1, companyDto.getContacts().size());
        Assertions.assertEquals(1, companyDto.getManagers().size());
    }

    @Test
    void findByInn() {
        Long id = 1L;

        CompanyDto companyDto = companyService.findDtoByInn(COMPANY_INN);
        Assertions.assertNotNull(companyDto);
        Assertions.assertEquals(id, companyDto.getId());
        Assertions.assertEquals(COMPANY_NAME + " " + id, companyDto.getName());
        Assertions.assertEquals(COMPANY_TYPE, companyDto.isType());
        Assertions.assertEquals(COMPANY_INN, companyDto.getInn());
        Assertions.assertEquals(COMPANY_BILL_NUMBER, companyDto.getBillNumber());
        Assertions.assertEquals(COMPANY_PHONE_NUMBER, companyDto.getPhoneNumber());
        Assertions.assertEquals(COMPANY_EMAIL, companyDto.getEmail());
        Assertions.assertEquals(1, companyDto.getContacts().size());
        Assertions.assertEquals(1, companyDto.getManagers().size());
    }

    @Test
    void findById() {
        Long id = 1L;

        CompanyDto companyDto = companyService.findDtoById(id);
        Assertions.assertNotNull(companyDto);
        Assertions.assertEquals(id, companyDto.getId());
        Assertions.assertEquals(COMPANY_NAME + " " + id, companyDto.getName());
        Assertions.assertEquals(COMPANY_TYPE, companyDto.isType());
        Assertions.assertEquals(COMPANY_INN, companyDto.getInn());
        Assertions.assertEquals(COMPANY_BILL_NUMBER, companyDto.getBillNumber());
        Assertions.assertEquals(COMPANY_PHONE_NUMBER, companyDto.getPhoneNumber());
        Assertions.assertEquals(COMPANY_EMAIL, companyDto.getEmail());
        Assertions.assertEquals(1, companyDto.getContacts().size());
        Assertions.assertEquals(1, companyDto.getManagers().size());
    }

    @Test
    void findAllDTO() {
        List<CompanyDto> companyDtoList = companyService.findDtoAll();
        Assertions.assertNotNull(companyDtoList);
        Assertions.assertEquals(COMPANY_COUNT, companyDtoList.size());
        Assertions.assertEquals(COMPANY_NAME + " 1", companyDtoList.get(0).getName());
    }

    @Test
    void deleteTest() {
        Long id = 1L;

        Assertions.assertEquals(COMPANY_COUNT, companyService.findDtoAll().size());
        companyService.deleteById(id);
        Assertions.assertEquals(COMPANY_COUNT - 1, companyService.findDtoAll().size());
    }

    @Test
    void saveOrUpdateTest() {
        Company newCompany = new Company();
        newCompany.setId(4L);
        newCompany.setName(COMPANY_NAME + " " + 4L);
        newCompany.setType(COMPANY_TYPE);
        newCompany.setInn(COMPANY_INN);
        newCompany.setBillNumber(COMPANY_BILL_NUMBER);
        newCompany.setPhoneNumber(COMPANY_PHONE_NUMBER);
        newCompany.setEmail(COMPANY_EMAIL);

        Set<Contact> contacts = new HashSet<>(1);
        Contact contact = new Contact();
        contact.setId(1L);
        contact.setName("TEST");
        contacts.add(contact);
        newCompany.setContacts(contacts);

        companyRepository.save(newCompany);
        Mockito.verify(companyRepository, Mockito.times(1)).save(newCompany);
    }

    @Test
    void deleteContactTest() {
        Long id = 1L;

        Assertions.assertEquals(COMPANY_COUNT, contactRepository.findAll().size());
        contactRepository.deleteById(id);
        Assertions.assertEquals(COMPANY_COUNT - 1, contactRepository.findAll().size());
    }

    @Test
    void saveOrUpdateContactTest() {
        Contact newContact = new Contact();
        newContact.setId(1L);
        newContact.setName("TEST");
        newContact.setCompany(new Company());

        contactRepository.save(newContact);
        Mockito.verify(contactRepository, Mockito.times(1)).save(newContact);
    }
}