package io.doeasy.retry.expression;

import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * 
 * @author wangdong
 * 
 */
public class RetryEvaluationContext extends StandardEvaluationContext {

	public RetryEvaluationContext(Exception exception) {
		super();
		//TODO:
		setVariable("exception", exception);
	}

}
