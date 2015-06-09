package io.doeasy.retry.test;

import lombok.extern.apachecommons.CommonsLog;

import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryContext;

@CommonsLog
public class DefaultRecoveryCallback implements RecoveryCallback<String> {

    public String recover(RetryContext context) throws Exception {
        log.info("--------------> Enter DefaultRecoveryCallback,  you can put some compenstation logic code here.");
        return "recoveried";
    }

}
