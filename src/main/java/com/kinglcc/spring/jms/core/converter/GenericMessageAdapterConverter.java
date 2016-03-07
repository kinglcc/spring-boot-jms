package com.kinglcc.spring.jms.core.converter;

import java.lang.reflect.Type;

import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MessageConverter;

/**
 * GenericMessageAdapterConverter
 * <pre>
 * Spring framework convert {@link javax.jms.Message} to
 * {@link org.springframework.messaging.Message}.
 * The interface support parameter generic type.
 * </pre>
 * @see MessageConverter
 *
 * @author liaochaochao
 * @since 2016年1月28日 下午5:30:49
 */
public interface GenericMessageAdapterConverter extends MessageConverter {

    /**
     * Convert the payload of a {@link Message} from serialized form to a typed Object of
     * the specified target type.
     * @see #fromMessage(Message, Type, Class)
     * 
     * @param message the input message
     * @param targetType the target type for the conversion
     * @return the result of the conversion, or {@code null} if the converter cannot
     * perform the conversion
     */
    Object fromMessage(Message<?> message, Type targetType);

    /**
     * Convert the payload of a {@link Message} from serialized form to a typed Object of
     * the specified target type.
     * <p>If the converter does not support the specified media type or cannot perform the
     * conversion, it should return {@code null}.
     * 
     * @param message the input message
     * @param targetType the target type for the conversion
     * @param contextClass the context class for the conversion
     * @return the result of the conversion, or {@code null} if the converter cannot
     * perform the conversion
     */
    Object fromMessage(Message<?> message, Type targetType, Class<?> contextClass);

}
