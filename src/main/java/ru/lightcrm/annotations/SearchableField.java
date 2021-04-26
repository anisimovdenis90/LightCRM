package ru.lightcrm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Помечает поля поисковой сущности (унаследованной от класса SearchableEntity), по которым должен вестись поиск.
 * При сохранении сущности, строковые представления полей, помеченных данной аннотацией,
 * конкатенируются в общее поле searchIndex.
 *
 * Если аннотация ставиться над другой сущностью или объектом,
 * строковые представления полей которого должны попасть в searchIndex,
 * тогда необходимо перечислить название полей в массив fieldsNames.
 *
 * Применение аннотации:
 * Можно помечать другие сущности: в поле fieldsNames перечисляются поля, которые должны учитываться при поиске.
 * Можно помечать коллекции (List, Set) сущностей с перечислением названий необходимых полей.
 *
 * Поле position используется для указания порядка сохранения помеченных полей в searchIndex.
 * Если не указывать position, то сохранение происходит в порядке объявления в полей классе.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SearchableField {

    int position() default 0;

    String[] fieldsNames() default {};
}
