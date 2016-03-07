package com.kinglcc.spring.jms.filter;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * JmsFilter
 * <pre>The jms message filter annotation</pre>
 *
 * @author liaochaochao
 * @since 2016年1月26日 下午1:46:33
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JmsFilter {

    /**
     * The bean identifier of jms message filters
     * 
     * @return The bean identifier implements MessageFilters
     */
    String[] value() default {};

}
