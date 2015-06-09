package io.doeasy.retry.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/META-INF/spring/retry/retry.xml" })
public class RetryTest {

    @Autowired
    private RestService restService;

    @Test
    public void testRetryWithRuntimeException() {
        try {
            restService.mockRestEasyServiceCallingWithBackoff();
        } catch (Exception e) {
            
        }

    }

    @Test
    public void testRetryWithRuntimeExceptionAndCondition() {
        try {
            restService.mockRestEasyServiceCallingWithBackoffAndCondition();
        } catch (Exception e) {

        }
    }

    @Test
    public void testRetryGroupWithRuntimeException() {
        try {
            restService.mockRestEasyServiceCallingWithRetryGroup();
        } catch (Exception e) {

        }
    }

    @Test
    public void testSpringRetry() {
        restService.mockSpringRetry();
    }
}
