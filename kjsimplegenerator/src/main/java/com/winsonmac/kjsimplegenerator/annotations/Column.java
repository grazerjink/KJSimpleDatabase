package com.winsonmac.kjsimplegenerator.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface Column {

    public String name() default "";

    public boolean primaryKey() default false;

    public boolean autoIncrement() default false;

    public boolean notNull() default false;

    public boolean unique() default false;

    public String defaultValue() default "";

    public String checkExpression() default "";


}
