package io.doeasy.retry.spring;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.retry.RetryListener;

/**
 * Retry annotation that use spring retry framework
 * @author wangdong1
 *
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface SpringRetry {
    Policy policy();
    Backoff backoff();
    
    /**
     * Recovery[] is very tricky, because we want to recovery() can be Null, 
     * but Java Annotation semantics does not support, array solution is just a work around, 
     * in fact, this array just can be put one Recovery annotation, if multiple ones, we just choose the 
     * first one.
     * @return
     */
    Recovery[] recovery() default {} ;
    Class<? extends RetryListener>[] listeners() default {};
}
