package ru.lightcrm.repositories;

import org.springframework.data.jpa.repository.Query;
import ru.lightcrm.entities.Company;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends SearchableEntityRepository<Company, Long> {

    Optional<Company> findOneByName(@NotNull String name);

    Optional<Company> findOneByInn(@NotNull Long inn);

    Optional<Company> findOneById(@NotNull Long id);

    @Query(value = "SELECT * FROM companies c INNER JOIN companies_managers cm " +
                   "ON c.id = cm.company_id WHERE cm.profile_id = ?1",
           nativeQuery = true)
    List<Company> findByManagerId(@NotNull Long managerId);
}
