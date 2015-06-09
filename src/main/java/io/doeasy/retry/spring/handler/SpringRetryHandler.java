package io.doeasy.retry.spring.handler;

import io.doeasy.retry.exception.IllegelRetryAnnotationException;
import io.doeasy.retry.spring.Backoff;
import io.doeasy.retry.spring.Policy;
import io.doeasy.retry.spring.Recovery;
import io.doeasy.retry.spring.SpringRetry;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.extern.apachecommons.CommonsLog;

import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryListener;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.policy.TimeoutRetryPolicy;
import org.springframework.util.ObjectUtils;

/**
 * @author wangdong1
 * 
 */
@CommonsLog
public class SpringRetryHandler {
    final SpringRetry retry;

    private AtomicInteger retryTimes = null;

    public void setRetryTimes(int retryTimes) {
        this.retryTimes.set(retryTimes);
    }

    public int getRetryTimes() {
        return this.retryTimes.getAndIncrement();
    }

    public SpringRetryHandler(SpringRetry retry) {
        this.retry = retry;
        retryTimes = new AtomicInteger(0);
    }

    public boolean matches() {
        if (!retryPolicyVaidate()) {
            return false;
        }
        if (!this.backoffPolicyValidate()) {
            return false;
        }
        return true;
    }

    public RetryPolicy parseSpringRetryPolicy(final Method method) {
        Policy policy = method.getAnnotation(SpringRetry.class).policy();
        RetryPolicy retryPolicy = null;
        if (policy.type() == SimpleRetryPolicy.class) {
            int attempts = policy.attempts();
            SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy();
            simpleRetryPolicy.setMaxAttempts(attempts);
            retryPolicy = simpleRetryPolicy;
        } else if (policy.type() == TimeoutRetryPolicy.class) {
            long timeout = policy.timeout();
            TimeoutRetryPolicy timeoutRetryPolicy = new TimeoutRetryPolicy();
            timeoutRetryPolicy.setTimeout(timeout);
            retryPolicy = timeoutRetryPolicy;
        }
        return retryPolicy;
    }

    public BackOffPolicy parseSpringBackOffPolicy(final Method method) {
        Backoff backoff = method.getAnnotation(SpringRetry.class).backoff();
        BackOffPolicy backoffPolicy = null;
        if (backoff.type() == FixedBackOffPolicy.class) {
            long period = backoff.period();
            FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
            fixedBackOffPolicy.setBackOffPeriod(period);
            backoffPolicy = fixedBackOffPolicy;
        } else if(backoff.type() == ExponentialBackOffPolicy.class ) {
            long initialInterval = backoff.initialInterval();
            double multiplier = backoff.multiplier();
            ExponentialBackOffPolicy exponentialBackOffPolicy = new ExponentialBackOffPolicy();
            exponentialBackOffPolicy.setInitialInterval(initialInterval);
            exponentialBackOffPolicy.setMultiplier(multiplier);
            backoffPolicy = exponentialBackOffPolicy;
        }
        return backoffPolicy;
    }
    
    @SuppressWarnings("unchecked")
    public RecoveryCallback< Object> parseSpringRecovery(final Method method) {
        Recovery[] recoveries = method.getAnnotation(SpringRetry.class).recovery();
        if(ObjectUtils.isEmpty(recoveries)) {
            return null;
        }
        
        Recovery recovery = recoveries[0];
        Class<?>[] interfaces = recovery.recover().getInterfaces();
        if(!Arrays.asList(interfaces).contains(RecoveryCallback.class)) {
            return null;
        }
        
        try {
            Class<?> callback = Class.forName(recovery.recover().getName());
            return (RecoveryCallback<Object>) callback.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public RetryListener[] retryListeners(final Method method) {
        Class<? extends RetryListener>[] retryListeners = method.getAnnotation(SpringRetry.class).listeners();
        if(!ObjectUtils.isEmpty(retryListeners)) {
            List<RetryListener> list = new ArrayList< RetryListener>();
            for(Class<? extends RetryListener> retryListener : retryListeners) {
                RetryListener listener;
                try {
                    listener = (RetryListener) Class.forName(retryListener.getName()).newInstance();
                } catch (Exception e) {
                    log.error(e.getMessage());
                    throw new RuntimeException(e);
                }
                list.add(listener);
            }
            
            return list.toArray(new RetryListener[list.size()]);
        }
        return null;
    }

    private boolean retryPolicyVaidate() {
        if (retry.policy() == null) { // retry policy must be provided
            log.warn("retry policy must be provided");
            return false;
        } else {
            if (retry.policy().type() != SimpleRetryPolicy.class && retry.policy().type() != TimeoutRetryPolicy.class) {
                log.warn("So far, we just support SimpleRetryPolicy or TimeoutRetryPolicy");
                return false;
            } else {
                if (retry.policy().type() == SimpleRetryPolicy.class && retry.policy().attempts() < 0) {
                    throw new IllegelRetryAnnotationException(
                            "For SimpleRetryPolicy, attempts should be greater or equals 0.");
                }
                if (retry.policy().type() == TimeoutRetryPolicy.class && retry.policy().timeout() < 0) {
                    throw new IllegelRetryAnnotationException(
                            "For TimeoutRetryPolicy, timeout should be greater or equals 0.");
                }
            }
            return true;
        }
    }

    private boolean backoffPolicyValidate() {
        if (retry.backoff() == null) {
            log.warn("Backoff policy must be provided");
            return false;
        } else {
            if (retry.backoff().type() != FixedBackOffPolicy.class
                    && retry.backoff().type() != ExponentialBackOffPolicy.class) {
                log.warn("So far, we just support FixedBackOffPolicy or ExponentialBackOffPolicy");
                return false;
            } else {
                if (retry.backoff().type() == FixedBackOffPolicy.class && retry.backoff().period() < 0) {
                    throw new IllegelRetryAnnotationException(
                            "For FixedBackOffPolicy, period should be greater or equals 0.");
                }
                if (retry.backoff().type() == ExponentialBackOffPolicy.class
                        && (retry.backoff().initialInterval() < 0 || retry.backoff().multiplier() < 0.0)) {
                    throw new IllegelRetryAnnotationException(
                            "For ExponentialBackOffPolicy, initialInterval or multiplier should be greater or equals 0.");
                }
            }
            return true;
        }
    }
}
