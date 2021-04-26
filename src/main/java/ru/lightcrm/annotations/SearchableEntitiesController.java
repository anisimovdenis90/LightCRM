package ru.lightcrm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Помечает контроллер.
 * Если один контроллер возвращает несколько поисковых сущностей,
 * можно использовать данную аннотацию,
 * прописав значения для каждой сущности по отдельности.
 * Поля обязательные для заполнения.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SearchableEntitiesController {

    SearchableEntityController[] value();
}
