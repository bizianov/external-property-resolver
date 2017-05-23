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

/**
 * Created by viacheslav on 5/23/17.
 */
@Component
public class ExternalPropertyContextListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private ConfigurableListableBeanFactory beanFactory;
    @Autowired
    private PropertyReader propertyReader;
    private static final String DELIMITER = ".";
    String prefix;
    String property;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        ApplicationContext applicationContext = contextRefreshedEvent.getApplicationContext();
        String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
        Arrays.asList(beanDefinitionNames)
                .stream()
                .forEach(beanDefinitionName -> {
                    BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);
                    String beanClassName = beanDefinition.getBeanClassName();
                    if (beanClassName != null) {
                        try {
                            Class<?> beanClass = Class.forName(beanClassName);
                            ConfigurationProperties configurationPropertiesAnnotation = beanClass.getAnnotation(ConfigurationProperties.class);
                            if (configurationPropertiesAnnotation != null) {
                                prefix = configurationPropertiesAnnotation.prefix();
                            }
                            Field[] declaredFields = beanClass.getDeclaredFields();
                            Arrays.asList(declaredFields)
                                    .stream()
                                    .filter(new ExternalPropertyAnnotationFieldPredicate())
                                    .forEach(field -> {
                                        String fieldName = field.getName();
                                        if (prefix.isEmpty()) {
                                            property = fieldName;
                                        } else {
                                            property = new StringBuilder(prefix)
                                                    .append(DELIMITER)
                                                    .append(fieldName)
                                                    .toString();
                                        }
                                        String externalProperty = propertyReader.getExternalProperty(property);
                                        field.setAccessible(true);
                                        Object bean = applicationContext.getBean(beanClass);
                                        ReflectionUtils.setField(field, bean, externalProperty);
                                    });
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
