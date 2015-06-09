package io.doeasy.retry.handler;

import io.doeasy.retry.Retry;
import io.doeasy.retry.expression.SimpleExpressionParser;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.util.Assert;

/**
 * 
 * @author wangdong1
 * 
 */
public class RetryHandler implements Handler {

	private final Retry retry;
	private final Method method;
	private final SimpleExpressionParser expressionParser;
	private AtomicInteger retryCount = null;

	public RetryHandler(Retry retry, Method method,
			SimpleExpressionParser expressionParser) {
		Assert.notNull(retry);
		Assert.notNull(method);
		Assert.notNull(expressionParser);
		this.retry = retry;
		this.method = method;
		this.expressionParser = expressionParser;
		this.retryCount = new AtomicInteger(0);
	}

	public boolean matches(Object[] args, Exception exception) {

		if (!retry.exception().isInstance(exception)) {
			return false;
		}

		if (null == retry.when() || "".equals(retry.when())) {
			return true;
		}

		return expressionParser.parse(retry.when(), method, args,  exception);
	}

	public boolean retryAgain() {
		return retryCount.incrementAndGet() < retry.attempts();
	}

	public int getBackoffPeriod() {
		if (0 == retry.backoff().length) { //back-off period is zero means don't apply back-off policy
			return 0; 
		} else {
			return retry.backoff()[0].period();
		}
	}
	
	public int getRetryTimes () {
		return this.retryCount.get();
	}
}
