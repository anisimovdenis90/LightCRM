package ru.lightcrm.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.lightcrm.controllers.interfaces.SearchController;
import ru.lightcrm.entities.dtos.SearchItemDto;
import ru.lightcrm.exceptions.LightCrmError;
import ru.lightcrm.services.interfaces.SearchableEntityService;

import java.util.Set;

@RestController
@RequiredArgsConstructor
public class SearchControllerIml implements SearchController {

    private final SearchableEntityService searchableEntityService;

    @Override
    public ResponseEntity<?> getSearchResult(String searchString) {
        Set<SearchItemDto> searchResults = searchableEntityService.getSearchResults(searchString);
        return searchResults.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body(new LightCrmError(HttpStatus.NOT_FOUND.value(), "Ничего не найдено"))
                : ResponseEntity.ok(searchResults);
    }
}
