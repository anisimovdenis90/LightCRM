package ru.lightcrm.annotations;

import ru.lightcrm.entities.SearchableEntity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Помечает контроллер поисковой сущности.
 * Используется как метаинформация для получения url ресурса,
 * которая впоследствии передается на фронт.
 * Если для сущности нет url, аннотацию можно не ставить.
 * Поля аннотации обязательные для заполнения.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SearchableEntityController {

    /**
     * url ресурса (сущности) на сервере.
     * Может быть null, если нет url для сущности.
     */
    String url();

    /**
     * Класс сущности, используется для сопоставления.
     */
    Class<? extends SearchableEntity> entityClass();
}
