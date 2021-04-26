package ru.lightcrm.utils;

import lombok.experimental.UtilityClass;
import ru.lightcrm.entities.SearchableEntity;
import ru.lightcrm.repositories.SearchableEntityRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@UtilityClass
public class SearchUtil {

    private static final Map<String, Object> SEARCHABLE_REPOSITORIES_BY_ENTITY_NAME_MAP = new HashMap<>();
    private static final Map<String, String> URI_BY_ENTITY_CLASS_MAP = new HashMap<>();
    private static final Map<String, String> LOCALIZED_ENTITY_NAME = new HashMap<>();

    static {
        LOCALIZED_ENTITY_NAME.put("Profile", "Профиль");
        LOCALIZED_ENTITY_NAME.put("Task", "Задача");
        LOCALIZED_ENTITY_NAME.put("Company", "Компания");
        LOCALIZED_ENTITY_NAME.put("Contact", "Контакт");
        LOCALIZED_ENTITY_NAME.put("Comment", "Комментарий");
        LOCALIZED_ENTITY_NAME.put("Department", "Департамент");
        LOCALIZED_ENTITY_NAME.put("StaffUnit", "Должность");
        LOCALIZED_ENTITY_NAME.put("User", "Пользователь");
    }

    public Map<String, Object> getRepByEntityNameMap() {
        return SEARCHABLE_REPOSITORIES_BY_ENTITY_NAME_MAP;
    }

    public Map<String, String> getUriByEntityClassMap() {
        return URI_BY_ENTITY_CLASS_MAP;
    }

    public String getLocalizedEntityName(String entityName) {
        for (Map.Entry<String, String> entry : LOCALIZED_ENTITY_NAME.entrySet()) {
            if (entityName.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * На всякий случай проверка на null, хотя такого быть не должно
     *
     */
    public List<SearchableEntityRepository<?, ?>> getSearchableEntityRepositories() {
        return SEARCHABLE_REPOSITORIES_BY_ENTITY_NAME_MAP.values().stream()
                .filter(Objects::nonNull)
                .map(rep -> (SearchableEntityRepository<?, ?>) rep)
                .collect(Collectors.toList());
    }

    public String getUrl(SearchableEntity entity) {
        for (Map.Entry<String, String> entry : URI_BY_ENTITY_CLASS_MAP.entrySet()) {
            if (entity.getClass().getSimpleName().startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * Оконцовывает url символом / при необходимости
     *
     * @param notFormatUrl - url, указанный в аннотации SearchableController
     */
    public String formatUrl(String notFormatUrl) {
        if (notFormatUrl.isBlank()) {
            return null;
        }
        return (notFormatUrl.startsWith("/") ? notFormatUrl : (notFormatUrl = "/" + notFormatUrl)).endsWith("/") ? notFormatUrl : notFormatUrl + "/";
    }
}

