package io.doeasy.retry.spring.aspect;


import io.doeasy.retry.spring.SpringRetry;
import io.doeasy.retry.spring.handler.SpringRetryHandler;

import java.lang.reflect.Method;

import lombok.extern.apachecommons.CommonsLog;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.ObjectUtils;

@Aspect
@CommonsLog
public class SpringRetryAspect {
    private int order = 1;
    
    private RetryTemplate retryOperations = null;
    private RecoveryCallback<?> recoverer = null;
    
    @Pointcut("@annotation(io.doeasy.retry.spring.SpringRetry)")
    private void retryable() {
    }
    
    public SpringRetryAspect() {
    }
    
    public void setOrder(int order) {
        this.order = order;
    }
    
    public int getOrder() {
        return order;
    }
    
    @Around("retryable()")
    public Object retry(final ProceedingJoinPoint pjp) throws Throwable {
        final Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        
        final SpringRetryHandler handler = getSpringRetryHandler(method);
        
        if(!handler.matches()) {
            return pjp.proceed();
        }
        
        this.retryOperations = buildRetryTemplate(method, handler);
        
        RetryCallback<Object> retryCallback = new RetryCallback<Object>() {

            public Object doWithRetry(RetryContext context) throws Exception {
                Object value;
                try {                    
                    log.info(method.getName() + " has been executed " + (handler.getRetryTimes() + 1) + " times. ");
                    value = pjp.proceed();                    
                }catch (Exception e) {
                    throw e;
                } catch (Error e) {
                    throw e;
                } catch (Throwable e) {
                    throw new IllegalStateException(e);
                }                                
                return value;
            }  
        };
        
        this.recoverer = handler.parseSpringRecovery(method);
        if(this.recoverer == null) {
            return this.retryOperations.execute(retryCallback);
        } else {
            RecoveryCallback<Object> recoveryCallback = handler.parseSpringRecovery(method);
            return this.retryOperations.execute(retryCallback, recoveryCallback);
        }
    }
    
    private RetryTemplate buildRetryTemplate( final Method method, final SpringRetryHandler handler) {
        RetryTemplate retryTemplate = new RetryTemplate();
        //retry policy
        retryTemplate.setRetryPolicy(handler.parseSpringRetryPolicy(method));
        //backoff policy
        retryTemplate.setBackOffPolicy(handler.parseSpringBackOffPolicy(method));  
        //retry listeners
        RetryListener[] listeners = handler.retryListeners(method);
        if(!ObjectUtils.isEmpty(listeners)) {
            retryTemplate.setListeners(listeners);
        }
        return retryTemplate;
    }
    
    private SpringRetryHandler getSpringRetryHandler(Method method) {
        SpringRetry springRetry = method.getAnnotation(SpringRetry.class);
        return new SpringRetryHandler(springRetry);
    }
}
