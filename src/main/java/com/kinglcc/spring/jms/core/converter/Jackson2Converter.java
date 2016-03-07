package com.kinglcc.spring.jms.core.converter;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Jackson2Converter
 * <pre>
 * Use {@link Jackson2JmsMessageConverter}
 * </pre>
 *
 * @author liaochaochao
 * @since 2016年1月29日 下午4:56:31
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Jackson2Converter {

}
