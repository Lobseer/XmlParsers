package home.anno;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

/**
 * Class description
 *
 * @author lobseer on 01.02.2017.
 */
@Target({FIELD, METHOD, TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlContainer {
    Class containType();
}
