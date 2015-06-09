package io.doeasy.retry.test;

import lombok.extern.apachecommons.CommonsLog;

import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;

@CommonsLog
public class DefaultRetryListener implements RetryListener {

    public <T> boolean open(RetryContext context, RetryCallback<T> callback) {
        log.info("-------------> DefaultRetryListener->Open");
        return true;
    }

    public <T> void close(RetryContext context, RetryCallback<T> callback, Throwable throwable) {
        log.info("-------------> DefaultRetryListener->Open");
    }

    public <T> void onError(RetryContext context, RetryCallback<T> callback, Throwable throwable) {
        log.info("-------------> DefaultRetryListener->ERROR");
    }

}
