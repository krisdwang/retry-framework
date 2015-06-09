package io.doeasy.retry.handler;

/**
 * 
 * @author wangdong1
 *
 */
public interface Handler {
	
	public boolean matches(Object[] args,  Exception exception);
	
	public boolean retryAgain();
	
	public int getBackoffPeriod();
	
	public int getRetryTimes();
}
