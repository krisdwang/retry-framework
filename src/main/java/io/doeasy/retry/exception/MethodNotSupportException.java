package io.doeasy.retry.exception;

/**
 * 
 * @author wangdong1
 * 
 */
@SuppressWarnings("serial")
public class MethodNotSupportException extends RuntimeException {
    
    public MethodNotSupportException(String message) {
        super(message);
    }

    public MethodNotSupportException(Throwable e) {
        super(e);
    }

    public MethodNotSupportException(String message, Throwable cause) {
        super(message, cause);
    }
}
