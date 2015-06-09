package io.doeasy.retry;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Retry Annotation,封装了spring-retry
 * @author wangdong
 *
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Retry {
	/**
	 * 重试次数，默认为3次
	 * @return
	 */
	int attempts() default 3;
	
	/**
	 * 触发retry的异常
	 * @return
	 */
	Class<? extends Exception> exception() default Exception.class;
	
	/**
	 * 条件表达式<br/>
	 * expression language like: #exception.message.contains("connection timeout"), <br/>
	 * <b><i>#exception</></b> is a variable will be replace by framework using real exception.
	 * @return
	 */
	String when() default ""; //
	
	/**
	 * Backoff annotation, will provide 
	 * @return
	 */
	Backoff[] backoff() default {} ;
}
