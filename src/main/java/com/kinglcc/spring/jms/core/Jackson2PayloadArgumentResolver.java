/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package com.kinglcc.spring.jms.core;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import org.springframework.core.MethodParameter;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.messaging.handler.annotation.support.PayloadArgumentResolver;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.Validator;

import com.kinglcc.spring.jms.core.converter.GenericMessageAdapterConverter;
import com.kinglcc.spring.jms.core.converter.Jackson2JmsMessageConverter.GenericMessage;

/**
 * Jackson2PayloadArgumentResolver
 * <pre>The payload argument resolver implments using Jackson2</pre>
 *
 * @author liaochaochao
 * @since 2016年1月28日 下午4:19:17
 */
public class Jackson2PayloadArgumentResolver extends PayloadArgumentResolver {

    private final MessageConverter converter;

    /**
     * Create a new {@code JsonPayloadArgumentResolver} with the given
     * {@link MessageConverter}.
     * @param messageConverter the MessageConverter to use (required)
     * @since 4.0.9
     */
    public Jackson2PayloadArgumentResolver(MessageConverter messageConverter) {
        super(messageConverter, null);
        this.converter = messageConverter;
    }

    /**
     * Create a new {@code JsonPayloadArgumentResolver} with the given
     * {@link MessageConverter} and {@link Validator}.
     * @param messageConverter the MessageConverter to use (required)
     * @param validator the Validator to use (optional)
     */
    public Jackson2PayloadArgumentResolver(MessageConverter messageConverter, Validator validator) {
        super(messageConverter, validator);
        this.converter = messageConverter;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return true;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, Message<?> message) throws Exception {
        Payload ann = parameter.getParameterAnnotation(Payload.class);
        if (ann != null && StringUtils.hasText(ann.value())) {
            throw new IllegalStateException("@Payload SpEL expressions not supported by this resolver");
        }

        Object payload = message.getPayload();
        boolean isGeneric = isGenericMessage(payload);
        if (isGeneric) {
            payload = ((GenericMessage) payload).getContent();
            MessageBuilder<Object> builder = MessageBuilder.withPayload(payload);
            message = builder.copyHeadersIfAbsent(message.getHeaders()).build();
        }
        if (isEmptyPayload(payload)) {
            if (ann == null || ann.required()) {
                bindEmptyPayloadError(parameter, message, payload);
            }
            return null;
        }

        if (isGeneric) {
            return convertFromMessage(parameter, message);
        } else {
            if (ClassUtils.isAssignable(parameter.getParameterType(), payload.getClass())) {
                validate(message, parameter, payload);
                return payload;
            }
            return convertFromMessage(parameter, message);
        }
    }

    private Object convertFromMessage(MethodParameter parameter, Message<?> message) {
        Object payload = message.getPayload();
        Class<?> targetClass = parameter.getParameterType();
        if (targetClass.isInterface() || Modifier.isAbstract(targetClass.getModifiers())) {
            return payload;
        }

        if (this.converter instanceof GenericMessageAdapterConverter) {
            payload = convertJavaTypeFromMessage(message, parameter);
            validate(message, parameter, payload);
            return payload;
        }

        payload = convertClassFromMessage(message, targetClass);
        validate(message, parameter, payload);
        return payload;
    }

    private boolean isGenericMessage(Object payload) {
        return payload instanceof GenericMessage;
    }

    private Object convertClassFromMessage(Message<?> message, Class<?> targetClass) {
        Object payload = this.converter.fromMessage(message, targetClass);
        if (payload == null) {
            throw new MessageConversionException(message,
                    "No converter found to convert to " + targetClass + ", message=" + message);
        }
        return payload;
    }

    private Object convertJavaTypeFromMessage(Message<?> message, MethodParameter parameter) {
        Type targetType = parameter.getGenericParameterType();
        Class<?> contextClass = parameter.getContainingClass();
        Object payload = ((GenericMessageAdapterConverter) converter).fromMessage(message, targetType, contextClass);
        if (payload == null) {
            throw new MessageConversionException(message,
                    "No converter found to convert to " + targetType + ", message=" + message);
        }
        return payload;
    }

    private void bindEmptyPayloadError(MethodParameter parameter, Message<?> message, Object payload) {
        String paramName = getParameterName(parameter);
        BindingResult bindingResult = new BeanPropertyBindingResult(payload, paramName);
        bindingResult.addError(new ObjectError(paramName, "@Payload param is required"));
        throw new MethodArgumentNotValidException(message, parameter, bindingResult);
    }

    private String getParameterName(MethodParameter param) {
        String paramName = param.getParameterName();
        return (paramName != null ? paramName : "Arg " + param.getParameterIndex());
    }

}
