package ru.lightcrm.entities.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.lightcrm.entities.Department;
import ru.lightcrm.entities.Profile;
import ru.lightcrm.utils.CustomDateDeserializer;

import javax.validation.constraints.Min;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ApiModel(description = "Класс, содержащий минимально необходимые данные для создания нового профиля", subTypes = {ProfileDto.class, ProfileFullDto.class, SystemUserDto.class})
@JsonRootName("ProfileMiniDto")
public class ProfileMiniDto {

    @Min(value = 1, message = "Уникальный идентификатор не может быть меньше 1")
    @ApiModelProperty(notes = "Уникальный идентификатор профиля", dataType = "Long", example = "1", required = true, position = 1)
    @JsonProperty("id")
    private Long id;

    @Size(min = 3, max = 50, message = "Имя сотрудника должно содержать от 3 до 50 символов")
    @ApiModelProperty(notes = "Имя сотрудника", dataType = "String", example = "Иван", required = true, position = 2)
    @JsonProperty("firstname")
    private String firstname;

    @Size(min = 3, max = 50, message = "Фамилия сотрудника должна содержать от 3 до 50 символов")
    @ApiModelProperty(notes = "Фамилия сотрудника", dataType = "String", example = "Иванов", required = true, position = 3)
    @JsonProperty("lastname")
    private String lastname;

    @Size(min = 3, max = 50, message = "Отчество сотрудника должно содержать от 3 до 50 символов")
    @ApiModelProperty(notes = "Отчество сотрудника", dataType = "String", example = "Иванович", required = true, position = 4)
    @JsonProperty("middlename")
    private String middlename;

    @Size(min = 3, max = 50, message = "Название должности сотрудника должно содержать от 3 до 50 символов")
    @ApiModelProperty(notes = "Название должности сотрудника", dataType = "String", required = true, position = 5)
    @JsonProperty("staffUnitName")
    private String staffUnitName;

    @PastOrPresent(message = "Дата найма должна быть не позже настоящего времени")
    @ApiModelProperty(notes = "Дата найма сотрудника", dataType = "OffsetDateTime", example = "2000-12-25", position = 6)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @JsonProperty("employmentDate")
    private OffsetDateTime employmentDate;

    @ApiModelProperty(notes = "Список отделов, к которым приписан сотрудник", dataType = "List<String>", required = true, position = 7)
    @JsonProperty("departmentNames")
    private List<String> departmentNames;

    public ProfileMiniDto(Profile profile) {
        this.id = profile.getId();
        this.firstname = profile.getFirstname();
        this.lastname = profile.getLastname();
        this.middlename = profile.getMiddlename();
        this.employmentDate = profile.getEmploymentDate();
        // StaffUnit
        this.staffUnitName = profile.getStaffUnit() != null
                ? profile.getStaffUnit().getName()
                : null;
        // Department
        this.departmentNames = profile.getDepartments() != null
                ? profile.getDepartments().stream().map(Department::getName).collect(Collectors.toList())
                : Collections.emptyList();
    }
}
