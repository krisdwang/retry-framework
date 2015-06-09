package io.doeasy.retry.exception;

/**
 * 
 * @author wangdong1
 * 
 */
@SuppressWarnings("serial")
public class IllegelRetryAnnotationException extends RuntimeException {
	
	public IllegelRetryAnnotationException(String message) {
		super(message);
	}

	public IllegelRetryAnnotationException(Throwable e) {
		super(e);
	}

	public IllegelRetryAnnotationException(String message, Throwable cause) {
		super(message, cause);
	}
}
