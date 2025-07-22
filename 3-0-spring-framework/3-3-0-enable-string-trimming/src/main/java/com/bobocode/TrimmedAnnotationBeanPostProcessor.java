package com.bobocode;

import com.bobocode.annotation.EnableStringTrimming;
import com.bobocode.annotation.Trimmed;
import com.bobocode.config.StringTrimmingConfiguration;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * This is processor class implements {@link BeanPostProcessor}, looks for a beans where method parameters are marked with
 * {@link Trimmed} annotation, creates proxy of them, overrides methods and trims all {@link String} arguments marked with
 * {@link Trimmed}. For example if there is a string " Java   " as an input parameter it has to be automatically trimmed to "Java"
 * if parameter is marked with {@link Trimmed} annotation.
 * <p>
 *
 * Note! This bean is not marked as a {@link Component} to avoid automatic scanning, instead it should be created in
 * {@link StringTrimmingConfiguration} class which can be imported to a {@link Configuration} class by annotation
 * {@link EnableStringTrimming}
 */

// Done by ChatGPT
public class TrimmedAnnotationBeanPostProcessor implements BeanPostProcessor {
//todo: Implement TrimmedAnnotationBeanPostProcessor according to javadoc

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();

        // Check if bean has at least one method with @Trimmed parameters
        boolean hasTrimmed = Arrays.stream(beanClass.getMethods())
                .anyMatch(method -> Arrays.stream(method.getParameters())
                        .anyMatch(param -> param.isAnnotationPresent(Trimmed.class)));

        if (!hasTrimmed) {
            return bean; // no need to proxy
        }

        // Create proxy with CGLIB or Spring ProxyFactory
        ProxyFactory proxyFactory = new ProxyFactory(bean);
        proxyFactory.setProxyTargetClass(true); // CGLIB proxy

        proxyFactory.addAdvice((MethodInterceptor) invocation -> {
            Method method = invocation.getMethod();
            Object[] args = invocation.getArguments();

            // Get parameter annotations for this method
            Annotation[][] paramAnnotations = method.getParameterAnnotations();

            // For each parameter, check if @Trimmed and if String => trim
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof String) {
                    for (Annotation annotation : paramAnnotations[i]) {
                        if (annotation.annotationType().equals(Trimmed.class)) {
                            args[i] = ((String) args[i]).trim();
                        }
                    }
                }
            }

            return invocation.proceed();
        });

        return proxyFactory.getProxy();
    }
}
