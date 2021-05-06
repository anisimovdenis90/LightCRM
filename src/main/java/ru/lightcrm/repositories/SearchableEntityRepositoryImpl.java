package ru.lightcrm.repositories;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.lightcrm.annotations.SearchableEntityAndAnnotationProcessor;
import ru.lightcrm.entities.SearchableEntity;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Класс, который расширяет стандартный спринговый репозиторий SimpleJpaRepository, для добавления общего метода для всех сущностей, по которым ведется поиск
 *
 * @param <T>  - тип сущности
 * @param <ID> - тип id
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class SearchableEntityRepositoryImpl<T, ID> extends SimpleJpaRepository<T, ID> implements SearchableEntityRepository<T, ID> {

    private final EntityManager entityManager;

    public SearchableEntityRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

    /**
     * Общий для всех репозиториев метод поиска по фразе, вызывается для сущностей, по которым ведется поиск
     * Поиск ведется без учета регистра
     * Все сущности, по которым ведется поиск, должны быть унаследованы от класса SearchableEntity, у которого есть поле searchIndex
     *
     * @param searchIndex - поисковая фраза
     * @return - результат в виде списка
     */
    @Override
    @Transactional
    public List<T> searchBySearchIndexLike(String searchIndex) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> cQuery = builder.createQuery(getDomainClass());
        Root<T> root = cQuery.from(getDomainClass());
        cQuery
                .select(root)
                .where(builder
                        .like(builder.lower(root.get("searchIndex")), "%" + searchIndex.toLowerCase() + "%"));
        TypedQuery<T> query = entityManager.createQuery(cQuery);
        return query.getResultList();
    }

    /**
     * Перед методами сохранения вызывается метод prepareIndexSearchField
     */
    @Override
    public <S extends T> S save(@NotNull S entity) {
        if (entity instanceof SearchableEntity) {
            SearchableEntityAndAnnotationProcessor.prepareIndexSearchField((SearchableEntity) entity);
        }
        return super.save(entity);
    }

    @Override
    public <S extends T> S saveAndFlush(@NotNull S entity) {
        if (entity instanceof SearchableEntity) {
            SearchableEntityAndAnnotationProcessor.prepareIndexSearchField((SearchableEntity) entity);
        }
        return super.saveAndFlush(entity);
    }

    @Override
    public <S extends T> List<S> saveAll(@NotNull Iterable<S> entities) {
        for (S entity : entities) {
            if (entity instanceof SearchableEntity) {
                SearchableEntityAndAnnotationProcessor.prepareIndexSearchField((SearchableEntity) entity);
            }
        }
        return super.saveAll(entities);
    }
}
