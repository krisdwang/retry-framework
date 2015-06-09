package io.doeasy.retry.handler;

import io.doeasy.retry.exception.MethodNotSupportException;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * @author wangdong1
 * 
 */
public class RetryGroupHandler implements Handler {

    private final Handler[] handlers;
    private final AtomicInteger maxAttempts;
    private AtomicInteger retryTimes = new AtomicInteger(0);

    private boolean breaked = false;

    public RetryGroupHandler(Handler[] handlers, int maxAttempts) {
        this.handlers = handlers;
        this.maxAttempts = new AtomicInteger();
        this.maxAttempts.set(maxAttempts);
    }

    public boolean matches(Object[] args, Exception exception) {
        boolean flag = false;
        for (Handler handler : handlers) {
            if (handler.matches(args, exception)) {
                breaked |= !handler.retryAgain();
                flag = true;
            }
        }
        return flag;
    }

    public boolean retryAgain() {
        return (retryTimes.addAndGet(1) < this.maxAttempts.intValue() && !breaked);
    }

    public int getBackoffPeriod() {
        throw new MethodNotSupportException("Method is not supported.");
    }

    public int getRetryTimes() {
        return this.retryTimes.intValue();
    }

    public Handler[] getHandlers() {
        return this.handlers;
    }

}
