package io.doeasy.retry.aspect;

import io.doeasy.retry.Retry;
import io.doeasy.retry.RetryGroup;
import io.doeasy.retry.expression.SimpleExpressionParser;
import io.doeasy.retry.handler.Handler;
import io.doeasy.retry.handler.RetryGroupHandler;
import io.doeasy.retry.handler.RetryHandler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.apachecommons.CommonsLog;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;

@CommonsLog
@Aspect
public class RetryAspect implements Ordered {

    private int order = 1;

    private SimpleExpressionParser parser = new SimpleExpressionParser();

    @Pointcut("@annotation(io.doeasy.retry.Retry) ||  @annotation(io.doeasy.retry.RetryGroup)")
    private void retryable() {
    }

    @Around("retryable()")
    public Object retry(ProceedingJoinPoint pjp) throws Throwable {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        Handler handler = getHandler(method);

        Object result = null;
        Exception exception = null;

        if (handler == null) {
            return pjp.proceed();
        } else {

            do {
                log.info(method.getName() + " has been executed " + (handler.getRetryTimes() + 1) + " times. ");
                try {

                    if (handler instanceof RetryGroupHandler) {
                        RetryGroupHandler groupHandler = (RetryGroupHandler) handler;
                        if (groupHandler.getHandlers().length > 0) {
                            for (Handler retryHandler : groupHandler.getHandlers()) {
                                applyBackoff(retryHandler);
                            }
                        }
                    } else if (handler instanceof RetryHandler) {
                            applyBackoff(handler);
                    }
                    result = pjp.proceed();
                } catch (Exception ex) {
                    exception = ex;
                }
            } while (handler.matches(pjp.getArgs(), exception) && canRetry(handler));

            /**
             * After Retry.attempts times retry, re-throw corresponding
             * Exception
             */
            if (exception != null) {
                log.warn(method.getName() + " has been executed " + handler.getRetryTimes()
                        + " times, but some problems are still there.");
                throw exception;
            }
            return result;
        }

    }

    public void applyBackoff(Handler handler) throws Exception {
        if (handler.getBackoffPeriod() > 0 && handler.getRetryTimes() > 0) {
            log.info("Following backoff period, the next execution will be delay " + handler.getBackoffPeriod()
                    + " ms.");
            new ObjectWaitSleeper().sleep(handler.getBackoffPeriod()); // sleep
        }
    }

    private boolean canRetry(Handler handler) {
        return handler.retryAgain();
    }

    private Handler getHandler(Method method) {

        Retry retry = method.getAnnotation(Retry.class);
        RetryGroup group = method.getAnnotation(RetryGroup.class);

        if (null != retry) {
            return new RetryHandler(retry, method, parser);
        }

        if (null != group) {
            return getGroupHandler(group, method);
        }

        return null;
    }

    private Handler getGroupHandler(RetryGroup group, Method method) {
        List<Handler> handlers = new ArrayList<Handler>(group.value().length);

        for (Retry retry : group.value()) {
            handlers.add(new RetryHandler(retry, method, parser));
        }

        return new RetryGroupHandler(handlers.toArray(new Handler[handlers.size()]), group.maxAttempts());
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    private class ObjectWaitSleeper {

        public void sleep(int backoffPeriod) throws InterruptedException {
            Object mutex = new Object();
            synchronized (mutex) {
                mutex.wait(backoffPeriod);
            }
        }
    }
}
