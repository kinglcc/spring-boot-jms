package com.kinglcc.spring.jms.core.converter;

import javax.jms.Message;

import org.springframework.jms.support.converter.MessageConverter;

/**
 * JmsMessageConverter
 * <pre>
 * support {@link #canConvertTo(Object)} and {@link #canConvertFrom(Message)}
 * </pre>
 *
 * @see GenericJmsMessageConverter
 * @author liaochaochao
 * @since 2016年1月29日 下午12:59:38
 */
public interface JmsMessageConverter extends MessageConverter {

    boolean canConvertTo(Object payload);

    boolean canConvertFrom(Message message);
}
