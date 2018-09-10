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
 * This annotation is used to One-to-One relationship.
 * If A have only one B instance, need to add 'ToOne' in A and set foreign key for B
 */
public @interface ToOne {
    String fk();
}
