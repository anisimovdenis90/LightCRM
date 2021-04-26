package ru.lightcrm.services;

import org.springframework.stereotype.Service;
import ru.lightcrm.entities.SearchableEntity;
import ru.lightcrm.entities.dtos.SearchItemDto;
import ru.lightcrm.repositories.SearchableEntityRepository;
import ru.lightcrm.services.interfaces.SearchableEntityService;
import ru.lightcrm.utils.SearchUtil;

import java.util.*;

@Service
public class SearchableEntityServiceImpl implements SearchableEntityService {

    /**
     * Вызывает общий метод поиска по фразе репозиториев всех поисковых сущностей
     *
     * @param searchString - фраза для поиска
     * @return - список результатов
     */
    @Override
    public Set<SearchItemDto> getSearchResults(String searchString) {
        if (searchString.isBlank()) {
            return Collections.emptySet();
        }
//        if (searchString.trim().contains(" ")) {
//            return getSearchResultSplit(searchString);
//        }
        Set<SearchItemDto> foundSet = new HashSet<>();
        for (SearchableEntityRepository<?, ?> rep : SearchUtil.getSearchableEntityRepositories()) {
            rep.searchBySearchIndexLike(searchString).stream()
                    .filter(o -> o instanceof SearchableEntity)
                    .map(o -> (SearchableEntity) o)
                    .map(SearchItemDto::new)
                    .forEach(foundSet::add);
        }
        return foundSet;
    }

    /**
     * Для поиска по фразам, разделенным пробелом
     */
    private Set<SearchItemDto> getSearchResultSplit(String searchString) {
        List<String> searchStrings = new ArrayList<>();
        Set<SearchItemDto> foundSet = new HashSet<>();
        for (String searchText : searchString.split(" ")) {
            String trim = searchText.trim();
            searchStrings.add(trim);
            foundSet.addAll(getSearchResults(trim));
        }
        foundSet.removeIf(searchItemDto -> !searchStrings.stream().allMatch(s -> searchItemDto.getIndexData().toLowerCase().contains(s.toLowerCase())));
        return foundSet;
    }
}
