package ru.lightcrm.repositories;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;
import ru.lightcrm.annotations.SearchableField;
import ru.lightcrm.entities.SearchableEntity;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Класс, который расширяет стандартный спринговый репозиторий SimpleJpaRepository, для добавления общего метода для всех сущностей, по которым ведется поиск
 *
 * @param <T>  - тип сущности
 * @param <ID> - тип id
 */
@Log4j2
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
        prepareIndexSearchField(entity);
        return super.save(entity);
    }

    @Override
    public <S extends T> S saveAndFlush(@NotNull S entity) {
        prepareIndexSearchField(entity);
        return super.saveAndFlush(entity);
    }

    @Override
    public <S extends T> List<S> saveAll(@NotNull Iterable<S> entities) {
        for (S entity : entities) {
            prepareIndexSearchField(entity);
        }
        return super.saveAll(entities);
    }

    /**
     * Формирует поле сущности, используемое для поиска,
     * путем конкатенации значений полей, помеченных аннотацией @SearchableField.
     *
     * Учитывается порядок полей, если указан в аннотации.
     *
     * Формирует searchIndex у сущностей, в случае их каскадного сохранения
     */
    private void prepareIndexSearchField(Object entity) {
        if (!(entity instanceof SearchableEntity)) {
            return;
        }
        Map<Integer, String> map = new HashMap<>();
        StringJoiner sj = new StringJoiner(" ");
        for (Field entityField : entity.getClass().getDeclaredFields()) {
            checkCascadeSave(entityField, (SearchableEntity) entity);

            if (entityField.isAnnotationPresent(SearchableField.class)) {
                SearchableField annotation = entityField.getAnnotation(SearchableField.class);
                Integer position = annotation.position();

                if (Arrays.asList(entityField.getType().getInterfaces()).contains(Collection.class)) {
                    entityField.setAccessible(true);
                    Object fieldValueOfEntityField = ReflectionUtils.getField(entityField, entity);
                    if (fieldValueOfEntityField != null) {
                        for (Object collectionElement : ((Collection<?>) fieldValueOfEntityField)) {
                            valueProcessing(entity, map, annotation, position, collectionElement);
                        }
                    } else {
                        String message = String.format("Нет доступа к полю %s сущности %s", entityField.getName(), entity.getClass().getSimpleName());
                        log.error(message);
                        throw new RuntimeException(message);
                    }
                } else {
                    fieldProcessing(entity, map, entityField, annotation, position);
                }
            }
        }
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            sj.add(entry.getValue());
        }
        ((SearchableEntity) entity).setSearchIndex(sj.toString());
    }

    private void valueProcessing(Object entity, Map<Integer, String> map, SearchableField annotation, Integer position, Object collectionElement) {
        if (collectionElement instanceof SearchableEntity && annotation.fieldsNames().length == 0) {
            putValueInMap(((SearchableEntity) collectionElement).getSearchIndex(), map, position);
        } else if (annotation.fieldsNames().length > 0) {
            for (String fieldName : annotation.fieldsNames()) {
                Field collectionElementField = ReflectionUtils.findField(collectionElement.getClass(), fieldName);
                if (collectionElementField != null) {
                    collectionElementField.setAccessible(true);
                    Object collectionElementFieldValue = ReflectionUtils.getField(collectionElementField, collectionElement);
                    putValueInMap(collectionElementFieldValue, map, position);
                } else {
                    String message = String.format("Не найдено поле %s объекта коллекции, являющейся полем %s сущности %s",
                            fieldName, collectionElement.getClass().getSimpleName(), entity.getClass().getSimpleName());
                    log.error(message);
                    throw new RuntimeException(message);
                }
            }
        } else {
            putValueInMap(collectionElement, map, position);
        }
    }

    private void fieldProcessing(Object entity, Map<Integer, String> valueMap, Field entityField, SearchableField fieldAnnotation, Integer fieldPosition) {
        if (entityField.getType().getSuperclass() != null
                && entityField.getType().getSuperclass().equals(SearchableEntity.class)
                && fieldAnnotation.fieldsNames().length == 0) {
            entityField.setAccessible(true);
            Object fieldValueOfEntityField = ReflectionUtils.getField(entityField, entity);
            if (fieldValueOfEntityField != null) {
                putValueInMap(((SearchableEntity) fieldValueOfEntityField).getSearchIndex(), valueMap, fieldPosition);
            } else {
                String message = String.format("Нет доступа к полю %s сущности %s", entityField.getName(), entity.getClass().getSimpleName());
                log.error(message);
                throw new RuntimeException(message);
            }
        } else if (fieldAnnotation.fieldsNames().length > 0) {
            Class<?> fieldOfEntityType = entityField.getType();
            for (String fieldNameOfEntityField : fieldAnnotation.fieldsNames()) {
                Field typeEntityField = ReflectionUtils.findField(fieldOfEntityType, fieldNameOfEntityField);
                if (typeEntityField != null) {
                    entityField.setAccessible(true);
                    Object fieldValueOfEntityField = ReflectionUtils.getField(entityField, entity);
                    typeEntityField.setAccessible(true);
                    Object data = ReflectionUtils.getField(typeEntityField, fieldValueOfEntityField);
                    putValueInMap(data, valueMap, fieldPosition);
                } else {
                    String message = String.format("Не найдено поле %s объекта, являющегося полем %s сущности %s",
                            fieldNameOfEntityField, fieldOfEntityType.getSimpleName(), entity.getClass().getSimpleName());
                    log.error(message);
                    throw new RuntimeException(message);
                }
            }
        } else {
            putFieldInMap(entity, entityField, valueMap, fieldPosition);
        }
    }

    private void checkCascadeSave(Field fieldOfEntity, SearchableEntity entity) {
        if (fieldOfEntity.getType().getSuperclass() == null
                || !fieldOfEntity.getType().getSuperclass().equals(SearchableEntity.class)) {
            return;
        }

        for (Annotation annotation : fieldOfEntity.getAnnotations()) {
            if (annotation.annotationType().equals(OneToOne.class)) {
                if (needCascadeSave(fieldOfEntity.getAnnotation(OneToOne.class).cascade())) {
                    processCascadeSave(fieldOfEntity, entity);
                }
                break;
            } else if (annotation.annotationType().equals(OneToMany.class)) {
                if (needCascadeSave(fieldOfEntity.getAnnotation(OneToMany.class).cascade())) {
                    processCascadeSave(fieldOfEntity, entity);
                }
                break;
            } else if (annotation.annotationType().equals(ManyToOne.class)) {
                if (needCascadeSave(fieldOfEntity.getAnnotation(ManyToOne.class).cascade())) {
                    processCascadeSave(fieldOfEntity, entity);
                }
                break;
            } else if (annotation.annotationType().equals(ManyToMany.class)) {
                if (needCascadeSave(fieldOfEntity.getAnnotation(ManyToMany.class).cascade())) {
                    processCascadeSave(fieldOfEntity, entity);
                }
                break;
            }
        }
    }

    private void processCascadeSave(Field field, SearchableEntity entity) {
        field.setAccessible(true);
        prepareIndexSearchField(ReflectionUtils.getField(field, entity));
    }

    private boolean needCascadeSave(CascadeType[] cascade) {
        List<CascadeType> list = Arrays.asList(cascade);
        return list.contains(CascadeType.MERGE) || list.contains(CascadeType.PERSIST) || list.contains(CascadeType.ALL);
    }

    private void putFieldInMap(Object entity, Field fieldOfEntity, Map<Integer, String> map, Integer position) {
        fieldOfEntity.setAccessible(true);
        putValueInMap(ReflectionUtils.getField(fieldOfEntity, entity), map, position);
    }

    private void putValueInMap(Object dataOfField, Map<Integer, String> map, Integer position) {
        if (map.containsKey(position)) {
            position = findPosition(map, position);
        }
        map.put(position, "" + dataOfField);
    }

    private Integer findPosition(Map<Integer, String> map, Integer startPosition) {
        while (map.containsKey(startPosition)) {
            startPosition++;
        }
        return startPosition;
    }
}
