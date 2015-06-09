package io.doeasy.retry;

/**
 * backoff policy
 * @author wangdong1
 *
 */
public @interface Backoff {
	/**
	 * We consider resteasy http client, if calling failed, the retry 
	 * backoff period, default is 0 ms, means don't apply backoff policy
	 * @return
	 */
	int period() default 0; //ms
}
