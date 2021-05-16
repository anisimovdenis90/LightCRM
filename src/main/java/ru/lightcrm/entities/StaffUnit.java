package ru.lightcrm.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@Table(name = "staff_units")
public class StaffUnit extends BaseEntity {

    @Column(name = "name")
    private String name;

    @ManyToMany
    @JoinTable(name = "staff_units_roles",
            joinColumns = @JoinColumn(name = "staff_unit_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;
}
