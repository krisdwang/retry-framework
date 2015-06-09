package io.doeasy.retry;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author wangdong1
 *
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RetryGroup {
	/**
	 * 
	 * @return
	 */
	Retry[] value();
	
	/**
	 * 
	 * @return
	 */
	int maxAttempts() default 3;
}
