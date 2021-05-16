package ru.lightcrm.entities.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.lightcrm.entities.Priority;
import ru.lightcrm.entities.Profile;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@ApiModel(description = "Класс, представляющий профиль с основными сведениями о конкретном сотруднике", parent = ProfileMiniDto.class, subTypes = ProfileFullDto.class)
@JsonRootName("ProfileDto")
public class ProfileDto extends ProfileMiniDto {

    // User
    @Min(1)
    @ApiModelProperty(notes = "Уникальный идентификатор данных авторизации сотрудника", dataType = "Long", example = "1", required = true, position = 7)
    @JsonProperty("userId")
    private Long userId;

    @Size(min = 3, max = 50, message = "Логин сотрудника должен содержать от 3 до 50 символов")
    @ApiModelProperty(notes = "Логин сотрудника.", dataType = "String", example = "Aladdin", position = 8)
    @JsonProperty("userLogin")
    private String userLogin;

    @ApiModelProperty(notes = "Список прав сотрудника", dataType = "Set<PriorityDto>", required = true, position = 9)
    @JsonProperty("priorities")
    private Set<PriorityDto> priorities;

    // StaffUnit
    @NotNull
    @Min(1)
    @ApiModelProperty(notes = "Уникальный идентификатор должности, занимаемой сотрудником", dataType = "Long", example = "1", required = true, position = 10)
    @JsonProperty("staffUnitId")
    private Long staffUnitId;

    @ApiModelProperty(notes = "Список ролей сотрудника", dataType = "Set<RoleDto>", required = true, position = 11)
    @JsonProperty("roles")
    private Set<RoleDto> roles;

    @ApiModelProperty(notes = "Список базовых прав доступа", dataType = "Set<PriorityDto>", required = true, position = 12)
    @JsonProperty("basePriorities")
    private Set<PriorityDto> basePriorities;

    public ProfileDto(Profile profile) {
        super(profile);
        // User
        this.userId = profile.getUser().getId();
        this.userLogin = profile.getUser().getLogin();
        this.priorities = profile.getUser().getPriorities() != null
                ? profile.getUser().getPriorities().stream().map(PriorityDto::new).collect(Collectors.toSet())
                : Collections.emptySet();
        // StaffUnit
        this.staffUnitId = profile.getStaffUnit() != null
                ? profile.getStaffUnit().getId()
                : null;
        this.roles = profile.getStaffUnit() != null
                ? profile.getStaffUnit().getRoles().stream().map(RoleDto::new).collect(Collectors.toSet())
                : Collections.emptySet();
        this.basePriorities = profile.getStaffUnit() != null
                ? profile.getStaffUnit().getRoles().stream()
                .flatMap(role -> role.getPriorities().stream())
                .map(PriorityDto::new)
                .collect(Collectors.toSet())
                : Collections.emptySet();
    }
}
