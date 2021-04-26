package ru.lightcrm.annotations;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import ru.lightcrm.repositories.SearchableEntityRepository;
import ru.lightcrm.utils.SearchUtil;

import java.lang.reflect.ParameterizedType;

/**
 * Класс для обработки аннотации SearchableEntitiesController, SearchableEntityController
 * Также сохранения в мапу репозитории поисковых сущностей, наследующих класс SearchableEntityRepository
 */
@Component
public class SearchableRepositoriesRefreshContextListener implements ApplicationListener<ContextRefreshedEvent> {

    /**
     * Сохраняет в мапу репозитории поисковых сущностей, результат используется в методе getSearchResults класса SearchableEntityServiceImpl
     * Сохраняет в мапу url поисковых сущностей, результат используется при формировании SearchItemDto
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextStartedEvent) {
        for (String beanName : contextStartedEvent.getApplicationContext().getBeanNamesForType(SearchableEntityRepository.class)) {
            Object bean = contextStartedEvent.getApplicationContext().getBean(beanName);
            String entityName = ((Class<?>) ((ParameterizedType) ((Class<?>) bean.getClass().getGenericInterfaces()[0]).getGenericInterfaces()[0]).getActualTypeArguments()[0]).getSimpleName();
            SearchUtil.getRepByEntityNameMap().put(entityName, bean);
        }
        for (String beanName : contextStartedEvent.getApplicationContext().getBeanNamesForAnnotation(SearchableEntitiesController.class)) {
            Object bean = contextStartedEvent.getApplicationContext().getBean(beanName);
            SearchableEntitiesController annotation = ((Class<?>) bean.getClass().getGenericInterfaces()[0]).getAnnotation(SearchableEntitiesController.class);
            for (SearchableEntityController value : annotation.value()) {
                SearchUtil.getUriByEntityClassMap().put(value.entityClass().getSimpleName(), SearchUtil.formatUrl(value.url()));
            }
        }
        for (String beanName : contextStartedEvent.getApplicationContext().getBeanNamesForAnnotation(SearchableEntityController.class)) {
            Object bean = contextStartedEvent.getApplicationContext().getBean(beanName);
            SearchableEntityController annotation = ((Class<?>) bean.getClass().getGenericInterfaces()[0]).getAnnotation(SearchableEntityController.class);
            SearchUtil.getUriByEntityClassMap().put(annotation.entityClass().getSimpleName(), SearchUtil.formatUrl(annotation.url()));
        }
    }
}
