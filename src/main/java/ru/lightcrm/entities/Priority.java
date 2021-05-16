package ru.lightcrm.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@Table(name = "priorities")
public class Priority extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "visible_name")
    private String visibleName;
}
