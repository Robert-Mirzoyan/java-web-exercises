package com.bobocode.annotation;

import com.bobocode.config.StringTrimmingConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that can be placed on configuration class to import {@link StringTrimmingConfiguration}
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(StringTrimmingConfiguration.class)
public @interface EnableStringTrimming {
}
