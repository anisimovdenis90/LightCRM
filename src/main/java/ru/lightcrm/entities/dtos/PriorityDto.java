package ru.lightcrm.entities.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import ru.lightcrm.entities.Priority;

@ApiModel(description = "Права DTO")
@Data
@JsonRootName("PriorityDto")
public class PriorityDto {

    @ApiModelProperty(notes = "Идентификатор права", example = "1", required = true, position = 1)
    @JsonProperty("id")
    private Long id;

    @ApiModelProperty(notes = "Наименование права", example = "CHANGE_TASK", required = true, position = 2)
    @JsonProperty("name")
    private String name;

    @ApiModelProperty(notes = "Наименование права для отображения", example = "Изменение задачи", required = true, position = 3)
    @JsonProperty("visibleName")
    private String visibleName;

    public PriorityDto(Priority priority) {
        this.id = priority.getId();
        this.name = priority.getName();
        this.visibleName = priority.getVisibleName();
    }
}
