package io.doeasy.retry.spring;

import org.springframework.retry.RetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;

/**
 * Retry Policy,  <br/>
 * If choose <b>{@link TimeoutRetryPolicy}</b>, timeout property will be ignored,
 * otherwise, if choose <b>{@link SimpleRetryPolicy} </b>, attempts property will be ignored,
 * so far, just support these two kind of Retry Policies, for more information, please
 * see {@link http://docs.spring.io/spring-batch/2.1.x/reference/html/retry.html}
 * 
 * @author wangdong1
 *
 */
public @interface Policy {
    Class<? extends RetryPolicy> type() default SimpleRetryPolicy.class;
    int attempts() default 3;
    long timeout() default 0; 
}
