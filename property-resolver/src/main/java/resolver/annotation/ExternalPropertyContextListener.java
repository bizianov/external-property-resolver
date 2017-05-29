package resolver.annotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import resolver.property.PropertyReader;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import static resolver.property.ExternalPropertyReader.EMPTY_STRING;

/**
 * Created by viacheslav on 5/23/17.
 */
@Component
public class ExternalPropertyContextListener implements ApplicationListener<ContextRefreshedEvent> {

    private final PropertyReader propertyReader;

    private static final String DELIMITER = ".";
    private String prefix;
    private String property;


    @Autowired
    public ExternalPropertyContextListener(PropertyReader propertyReader) {
        this.propertyReader = propertyReader;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        ApplicationContext applicationContext = contextRefreshedEvent.getApplicationContext();
        Map<String, Object> beansWithAnnotationMap =
                applicationContext.getBeansWithAnnotation(ConfigurationProperties.class);
        Collection<Object> beansWithAnnotation = beansWithAnnotationMap.values();
        beansWithAnnotation
                .stream()
                .forEach(bean -> {
                    Class<?> beanClass = bean.getClass();
                    prefix = beanClass.getAnnotation(ConfigurationProperties.class).prefix();
                    Field[] declaredFields = beanClass.getDeclaredFields();
                    Arrays.asList(declaredFields)
                            .stream()
                            .forEach(field -> {
                                String fieldName = field.getName();
                                property = prefix.isEmpty() ? fieldName : new StringBuilder(prefix)
                                        .append(DELIMITER)
                                        .append(fieldName)
                                        .toString();
                                String externalProperty = propertyReader.getExternalProperty(property);
                                if (!externalProperty.equals(EMPTY_STRING)) {
                                    field.setAccessible(true);
                                    ReflectionUtils.setField(field, bean, externalProperty);
                                }
                            });
                });
    }
}
