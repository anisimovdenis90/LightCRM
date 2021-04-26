package ru.lightcrm.services.interfaces;

import ru.lightcrm.entities.dtos.SearchItemDto;

import java.util.Set;

public interface SearchableEntityService {

    Set<SearchItemDto> getSearchResults(String searchString);
}
