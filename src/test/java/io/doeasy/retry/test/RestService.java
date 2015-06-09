package io.doeasy.retry.test;

import io.doeasy.retry.Backoff;
import io.doeasy.retry.Retry;
import io.doeasy.retry.RetryGroup;
import io.doeasy.retry.spring.Policy;
import io.doeasy.retry.spring.Recovery;
import io.doeasy.retry.spring.SpringRetry;
import lombok.extern.apachecommons.CommonsLog;

import org.springframework.retry.policy.SimpleRetryPolicy;

@CommonsLog
public class RestService {

    @Retry(attempts = 8)
    public void mockRestEasyServiceCallingWithBackoff() {
        log.info("calling remote rest service...");
        throw new RuntimeException("timeout");
    }

    @Retry(backoff = { @Backoff(period = 2000), @Backoff(period = 100000) }, exception = RuntimeException.class, attempts = 3, when = "#exception.message.contains('test condition')")
    public void mockRestEasyServiceCallingWithBackoffAndCondition() {
        log.info("calling remote rest service...");
        throw new RuntimeException("test condition");
    }

    @RetryGroup(value = { @Retry(backoff = @Backoff(period = 3000), exception = RuntimeException.class, attempts = 3) })
    public void mockRestEasyServiceCallingWithRetryGroup() {
        log.info("calling remote rest service...");
        throw new RuntimeException("timeout");
    }

    @SpringRetry(
            policy = @Policy(attempts = 3, type = SimpleRetryPolicy.class), 
            backoff = @io.doeasy.retry.spring.Backoff(period = 500), 
            recovery = { @Recovery(recover = DefaultRecoveryCallback.class) },
            listeners = {DefaultRetryListener.class})
    public void mockSpringRetry() {
        log.info("calling remote rest service...");
        throw new RuntimeException("timeout");
    }
}
