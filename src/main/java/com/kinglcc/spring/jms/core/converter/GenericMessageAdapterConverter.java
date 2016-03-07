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
