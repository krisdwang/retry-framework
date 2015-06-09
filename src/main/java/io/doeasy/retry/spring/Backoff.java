package io.doeasy.retry.spring;

import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;

/**
 * So far, just support two kind of {@link BackOffPolicy}.<br/>
 * If choose {@link  org.springframework.retry.backoff.FixedBackOffPolicy},  just period property will be parse, others will be ignored; otherwise, 
 * if choose {@link ExponentialBackOffPolicy}, period property is ignored.
 * @author wangdong1
 *
 */
public @interface Backoff {
    Class<? extends BackOffPolicy> type() default FixedBackOffPolicy.class;
    
    long period() default 0L;       //default 0ms, means no backoff policy
    
    /**
     *  The initial sleep interval.
     */
    long initialInterval() default 100L; //100 ms
    
    /**
     *  The value to increment the exp seed with for each retry attempt.
     */
    double multiplier() default 2.0;  
}
