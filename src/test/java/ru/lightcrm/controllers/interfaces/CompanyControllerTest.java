package ru.lightcrm.controllers.interfaces;

import java.util.Collections;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.lightcrm.entities.Company;
import ru.lightcrm.entities.Contact;
import ru.lightcrm.entities.dtos.CompanyDto;
import ru.lightcrm.entities.dtos.ContactDto;
import ru.lightcrm.services.interfaces.CompanyService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CompanyControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CompanyService companyService;

    private static List<CompanyDto> testListCompanyDto;
    private static CompanyDto testCompanyDto;

    @BeforeAll
    public static void init() {
        testCompanyDto = new CompanyDto();
        testCompanyDto.setId(1L);
        testCompanyDto.setName("Газпром");
        testCompanyDto.setType(true);
        testCompanyDto.setInn(50282517112359L);
        testCompanyDto.setBillNumber(50282517112359L);
        testCompanyDto.setPhoneNumber("+79999992324");
        testCompanyDto.setEmail("gazprom@gazprom.ru");

        Company company = new Company();
        company.setId(1L);

        Contact contact = new Contact();
        contact.setId(1L);
        contact.setName("TEST");
        contact.setCompany(company);

        Set<ContactDto> contactDtos = new HashSet<>(Collections.singletonList(new ContactDto(contact)));
        testCompanyDto.setContacts(contactDtos);
        testListCompanyDto = List.of(testCompanyDto);
    }


    @Test
    @WithMockUser(username = "Bob", authorities = "ADMIN")
    public void getCompanyContent() throws Exception {

        given(companyService.findDtoAll()).willReturn(testListCompanyDto);

        mvc.perform(get("/api/v1/companies")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is(testCompanyDto.getName())))
                .andExpect(jsonPath("$[0].inn", is(testCompanyDto.getInn())))
                .andExpect(jsonPath("$[0].billNumber", is(testCompanyDto.getBillNumber())))
                .andExpect(jsonPath("$[0].phoneNumber", is(testCompanyDto.getPhoneNumber())))
                .andExpect(jsonPath("$[0].email", is(testCompanyDto.getEmail())));
    }

    @Test
    @WithMockUser(username = "Bob", authorities = "ADMIN")
    public void deleteTest() throws Exception {
        mvc.perform(delete("/api/v1/companies/" + testCompanyDto.getId()))
                .andExpect(status().isOk());

        mvc.perform(get("/api/v1/companies")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}