package ru.lightcrm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.lightcrm.entities.StaffUnit;

import java.util.Optional;


public interface StaffUnitRepository extends JpaRepository<StaffUnit, Long> {
    Optional<StaffUnit>  findOneByName(String name);
}
