package io.doeasy.retry.spring;

import org.springframework.retry.RecoveryCallback;

/**
 * Recovery annotation for Spring Retry RecoveryCallback interface
 * @author wangdong1
 *
 */
public @interface Recovery {
   Class<? extends RecoveryCallback<?>> recover();
}
