package ru.lightcrm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Интерфейс, описывающий новый общий метод всех репозиториев
 * Репозитории сущностей, по которым требуется вести поиск (у каждой такой сущности должен быть свой репозиторий!), должны реализовывать данный интерфейс
 */
@NoRepositoryBean
public interface SearchableEntityRepository<T, ID> extends JpaRepository<T, ID> {

    List<T> searchBySearchIndexLike(@NotNull String searchIndex);
}
