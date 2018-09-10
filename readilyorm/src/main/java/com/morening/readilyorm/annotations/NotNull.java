package com.morening.readilyorm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by morening on 2018/9/8.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
/**
 * This annotation will add NOT NULL constraint when creating table
 */
public @interface NotNull {

}
