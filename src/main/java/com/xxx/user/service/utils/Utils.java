package com.xxx.user.service.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.i18n.LocaleContextHolder;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Locale;

@UtilityClass
public class Utils {
    public Locale locale() {
        return LocaleContextHolder.getLocale();
    }

    public static <T extends Annotation> T getAnnotation(JoinPoint joinPoint, Class<T> annotationClass) {
        T annotaion = null;

        Target annotationTarget = annotationClass.getAnnotation(Target.class);
        ElementType[] elementTypes = annotationTarget.value();

        boolean isMethodAnnotation = ArrayUtils.contains(elementTypes, ElementType.METHOD);
        if (isMethodAnnotation) {
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            Method method = methodSignature.getMethod();

            if (method.isAnnotationPresent(annotationClass)) {
                annotaion = method.getAnnotation(annotationClass);
            }
        }

        if (annotaion == null) {
            boolean isTypeAnnotation = ArrayUtils.contains(elementTypes, ElementType.TYPE);
            if (isTypeAnnotation) {
                Object target = joinPoint.getTarget();
                Class<?> targetClass = target.getClass();
                annotaion = targetClass.getAnnotation(annotationClass);
            }
        }

        return annotaion;
    }
}
