package org.example.cosmocats.feature;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.cosmocats.feature.exception.FeatureNotAvailableException;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class FeatureToggleAspect {

    private final FeatureToggleService toggleService;

    public FeatureToggleAspect(FeatureToggleService toggleService) {
        this.toggleService = toggleService;
    }

    @Around("@annotation(org.example.cosmocats.feature.ToggleFeature) || within(@org.example.cosmocats.feature.ToggleFeature *)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();

        ToggleFeature annotation = method.getAnnotation(ToggleFeature.class);
        if (annotation == null) {
            annotation = pjp.getTarget().getClass().getAnnotation(ToggleFeature.class);
        }

        if (annotation == null) {
            return pjp.proceed();
        }

        String key = annotation.value();

        if (toggleService.isFeatureEnabled(key)) {
            return pjp.proceed();
        } else {
            throw new FeatureNotAvailableException(key);
        }
    }
}