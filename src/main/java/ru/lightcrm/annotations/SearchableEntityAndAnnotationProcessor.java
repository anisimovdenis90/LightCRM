package ru.lightcrm.annotations;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import ru.lightcrm.entities.SearchableEntity;

import javax.persistence.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Формирует поле сущности, используемое для поиска,
 * путем конкатенации значений полей, помеченных аннотацией @SearchableField.
 * <p>
 * Учитывается порядок полей, если указан в аннотации.
 * <p>
 * Формирует searchIndex у сущностей, в случае их каскадного сохранения
 */

@Log4j2
@Component
@SuppressWarnings("ConstantConditions")
public class SearchableEntityAndAnnotationProcessor {

    public static <E extends SearchableEntity> void prepareIndexSearchField(E entity) {
        Map<Integer, String> map = new HashMap<>();
        StringJoiner sj = new StringJoiner(" ");
        for (Field entityField : entity.getClass().getDeclaredFields()) {
            checkNeedCascadeSave(entityField, entity);

            if (entityField.isAnnotationPresent(SearchableField.class)) {
                SearchableField annotation = entityField.getAnnotation(SearchableField.class);
                Integer position = annotation.position();

                if (Arrays.asList(entityField.getType().getInterfaces()).contains(Collection.class)) {
                    entityField.setAccessible(true);
                    Object fieldValueOfEntityField = ReflectionUtils.getField(entityField, entity);
                    if (fieldValueOfEntityField == null) {
                        notFieldAccessRuntimeException(entityField.getName(), entity.getClass().getSimpleName());
                    }
                    for (Object collectionElement : ((Collection<?>) fieldValueOfEntityField)) {
                        valueProcessing(entity, map, annotation, position, collectionElement);
                    }
                } else {
                    fieldProcessing(entity, map, entityField, annotation, position);
                }
            }
        }
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            sj.add(entry.getValue());
        }
        entity.setSearchIndex(sj.toString());
    }

    private static void valueProcessing(Object entity, Map<Integer, String> map, SearchableField annotation, Integer position, Object collectionElement) {
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

    private static void fieldProcessing(Object entity, Map<Integer, String> valueMap, Field entityField, SearchableField fieldAnnotation, Integer fieldPosition) {
        if (entityField.getType().getSuperclass() != null
                && entityField.getType().getSuperclass().equals(SearchableEntity.class)
                && fieldAnnotation.fieldsNames().length == 0) {
            entityField.setAccessible(true);
            Object fieldValueOfEntityField = ReflectionUtils.getField(entityField, entity);
            if (fieldValueOfEntityField == null) {
                notFieldAccessRuntimeException(entityField.getName(), entity.getClass().getSimpleName());
            }
            putValueInMap(((SearchableEntity) fieldValueOfEntityField).getSearchIndex(), valueMap, fieldPosition);
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

    private static void checkNeedCascadeSave(Field fieldOfEntity, SearchableEntity entity) {
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

    private static boolean needCascadeSave(CascadeType[] cascade) {
        List<CascadeType> list = Arrays.asList(cascade);
        return list.contains(CascadeType.MERGE) || list.contains(CascadeType.PERSIST) || list.contains(CascadeType.ALL);
    }

    private static void processCascadeSave(Field field, SearchableEntity entity) {
        field.setAccessible(true);
        SearchableEntity searchableEntity = (SearchableEntity) ReflectionUtils.getField(field, entity);
        if (searchableEntity == null) {
            notFieldAccessRuntimeException(field.getName(), entity.getClass().getSimpleName());
        }
        prepareIndexSearchField(searchableEntity);
    }

    private static void putFieldInMap(Object entity, Field fieldOfEntity, Map<Integer, String> map, Integer position) {
        fieldOfEntity.setAccessible(true);
        putValueInMap(ReflectionUtils.getField(fieldOfEntity, entity), map, position);
    }

    private static void putValueInMap(Object dataOfField, Map<Integer, String> map, Integer position) {
        if (map.containsKey(position)) {
            position = findPosition(map, position);
        }
        map.put(position, "" + dataOfField);
    }

    private static Integer findPosition(Map<Integer, String> map, Integer startPosition) {
        while (map.containsKey(startPosition)) {
            startPosition++;
        }
        return startPosition;
    }

    private static void notFieldAccessRuntimeException(String fieldNAme, String entityANme) {
        String message = String.format("Нет доступа к полю %s сущности %s", fieldNAme, entityANme);
        log.error(message);
        throw new RuntimeException(message);
    }
}
