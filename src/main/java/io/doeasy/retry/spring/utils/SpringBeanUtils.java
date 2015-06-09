package io.doeasy.retry.spring.utils;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;

/**
 * Utility for creating spring bean.
 * @author wangdong1
 *
 */
public class SpringBeanUtils {

    public BeanDefinition createBean( final String beanName ) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.childBeanDefinition(beanName);
        return builder.getBeanDefinition(); 
    }
    
    
}
