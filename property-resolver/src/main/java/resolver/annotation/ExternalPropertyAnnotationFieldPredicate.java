package resolver.annotation;

import java.lang.reflect.Field;
import java.util.function.Predicate;

/**
 * Created by viacheslav on 5/23/17.
 */
public class ExternalPropertyAnnotationFieldPredicate implements Predicate<Field> {

    private static final Class ANNOTATION_CLASS = ExternalProperty.class;

    @Override
    public boolean test(Field field) {
        return field.isAnnotationPresent(ANNOTATION_CLASS);
    }
}
