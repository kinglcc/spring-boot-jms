package com.kinglcc.spring.jms.core.converter;

import java.io.IOException;
import java.lang.reflect.Type;

import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConversionException;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Jackson2MessageAdapterConverter
 * <pre>
 * Use Jackson2 to convert {@link Message}
 * </pre>
 * @see GenericMessageAdapterConverter
 * 
 * @author liaochaochao
 * @since 2016年1月28日 下午5:49:15
 */
public class Jackson2MessageAdapterConverter extends MappingJackson2MessageConverter
        implements GenericMessageAdapterConverter {

    @Override
    public Object fromMessage(Message<?> message, Type targetType, Class<?> contextClass) {
        ObjectMapper objectMapper = getObjectMapper();
        JavaType javaType = objectMapper.getTypeFactory().constructType(targetType, contextClass);
        try {
            Object payload = message.getPayload();
            if (payload instanceof byte[]) {
                return objectMapper.readValue((byte[]) payload, javaType);
            } else {
                return objectMapper.readValue((String) payload, javaType);
            }
        } catch (IOException ex) {
            throw new MessageConversionException(message, "Could not read JSON: " + ex.getMessage(), ex);
        }
    }

    @Override
    public Object fromMessage(Message<?> message, Type targetType) {
        return fromMessage(message, targetType, null);
    }

}
