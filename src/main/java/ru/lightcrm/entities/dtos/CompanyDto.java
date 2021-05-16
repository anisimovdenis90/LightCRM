package ru.lightcrm.entities.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.lightcrm.entities.Company;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ApiModel(description = "Компания DTO")
@Data
@NoArgsConstructor
@JsonRootName("CompanyDto")
public class CompanyDto {

    @ApiModelProperty(notes = "Идентификатор компании", example = "1", required = true, position = 1)
    @JsonProperty("id")
    private Long id;

    @ApiModelProperty(notes = "Наименование компании", example = "Газпром", required = true, position = 2)
    @JsonProperty("name")
    private String name;

    @ApiModelProperty(notes = "Тип компании", example = "True", required = true, position = 3)
    @JsonProperty("type")
    private boolean type;

    @ApiModelProperty(notes = "ИНН компании", example = "1234567890123", required = true, position = 4)
    @JsonProperty("inn")
    private Long inn;

    @ApiModelProperty(notes = "Номер счета компании", example = "21321321425434", required = true, position = 5)
    @JsonProperty("billNumber")
    private Long billNumber;

    @ApiModelProperty(notes = "Список контактов компании", example = "(Иванов, Петров)", required = true, position = 6)
    @JsonProperty("contacts")
    private Set<ContactDto> contacts;

    @ApiModelProperty(notes = "Номер телефона компании", example = "88009999999", required = true, position = 7)
    @JsonProperty("phoneNumber")
    private String phoneNumber;

    @ApiModelProperty(notes = "Email компании", example = "GazpromInfo@gazprom.ru", required = true, position = 8)
    @JsonProperty("email")
    private String email;

    @ApiModelProperty(notes = "Курирующие менеджеры компании", example = "(Иванов, Петров)", required = true, position = 9)
    @JsonProperty("managers")
    private Set<ProfileMiniDto> managers;

    @ApiModelProperty(notes = "Комментарии к компании", position = 10)
    @JsonProperty("comments")
    private Set<CommentDto> comments;

    @ApiModelProperty(notes = "Количество прикрепленных задач", position = 11)
    @JsonProperty("tasksCount")
    private Integer tasksCount;

    public CompanyDto(Company company) {
        this.id = company.getId();
        this.name = company.getName();
        this.type = company.isType();
        this.inn = company.getInn();
        this.billNumber = company.getBillNumber();
        this.contacts = company.getContacts() != null
                ? company.getContacts().stream().map(ContactDto::new).collect(Collectors.toSet())
                : Collections.emptySet();
        this.phoneNumber = company.getPhoneNumber();
        this.email = company.getEmail();
        //TODO на обсуждении (карточка 81), может ли у компании не быть курирующего менеджера
        this.managers = company.getManagers() != null
                ? company.getManagers().stream().map(ProfileMiniDto::new).collect(Collectors.toSet())
                : Collections.emptySet();
        this.comments = company.getComments() != null
                ? company.getComments().stream().map(CommentDto::new).collect(Collectors.toSet())
                : Collections.emptySet();
    }
}
