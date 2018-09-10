package com.morening.readilyorm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by morening on 2018/9/1.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
/**
 * This annotation is used to resolve two models's relationship
 * Typically, A contains more than one B instances,
 * then need to add 'ToMany' in A's field and set reference key for A and B's type
 */
public @interface ToMany {
    String rk();
    Class<?> type();
}
