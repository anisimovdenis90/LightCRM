package ru.lightcrm.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.lightcrm.entities.dtos.SystemUserDto;
import ru.lightcrm.annotations.SearchableField;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "profiles")
@Data
@NoArgsConstructor
public class Profile extends SearchableEntity {

    @SearchableField(position = 1)
    @Column(name = "firstname")
    private String firstname;

    @SearchableField()
    @Column(name = "lastname")
    private String lastname;

    @SearchableField(position = 2)
    @Column(name = "middlename")
    private String middlename;

    @Column(name = "sex")
    private String sex;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "about", columnDefinition = "TEXT")
    private String about;

    @Column(name = "birthdate")
    private OffsetDateTime birthdate;

    @Column(name = "employment_date")
    private OffsetDateTime employmentDate;

    @Column(name = "dismissal_date")
    private OffsetDateTime dismissalDate;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "staff_unit_id")
    private StaffUnit staffUnit;

    @OneToOne
    @JoinColumn(name = "photo_id")
    private FileInfo photo;

    @OneToOne
    @JoinColumn(name = "preview_id")
    private FileInfo preview;

    @OneToMany(mappedBy = "leader")
    private List<Department> managedDepartments;

    @ManyToMany
    @JoinTable(name = "departments_profiles",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "department_id"))
    private List<Department> departments;

    @OneToMany(mappedBy = "author")
    private List<Comment> comments;

    public static Profile createNewProfileForUserRegistration(SystemUserDto systemUserDto, User user, StaffUnit staffUnit, List<Department> departments) {
        Profile profile = new Profile();
        profile.setUser(user);
        profile.setStaffUnit(staffUnit);
        profile.setDepartments(departments);
        profile.setFirstname(systemUserDto.getFirstname());
        profile.setLastname(systemUserDto.getLastname());
        profile.setMiddlename(systemUserDto.getMiddlename());
        profile.setEmploymentDate(systemUserDto.getEmploymentDate() != null
                ? systemUserDto.getEmploymentDate()
                : OffsetDateTime.now());
        return profile;
    }
}
