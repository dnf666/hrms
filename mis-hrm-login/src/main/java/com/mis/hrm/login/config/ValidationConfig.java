package com.mis.hrm.login.config;

import org.hibernate.validator.HibernateValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

/**
 * @author May
 */
@Configuration
public class ValidationConfig {
    /**
     * 配置校验器
     * 国际化校验资源
     */
    @Bean
    @Primary
    public ResourceBundleMessageSource getResourceBundleMessageSource(){
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setBasenames("ValidationMessages");
        return messageSource;
    }

    /**
     * 属性校验
     */
    @Bean
    @Primary
    public LocalValidatorFactoryBean getLocalValidatorFactoryBean(ResourceBundleMessageSource messageSource){
        LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
        validatorFactoryBean.setProviderClass(HibernateValidator.class);
        validatorFactoryBean.setValidationMessageSource(messageSource);
        return validatorFactoryBean;
    }

    /**
     * 方法校验
     */
    @Bean
    public MethodValidationPostProcessor getMethodValidationPostProcessor(LocalValidatorFactoryBean
                                                                                      validator){
        MethodValidationPostProcessor methodValidationPostProcessor =
                new MethodValidationPostProcessor();
        methodValidationPostProcessor.setValidator(validator);
        return methodValidationPostProcessor;
    }
}
