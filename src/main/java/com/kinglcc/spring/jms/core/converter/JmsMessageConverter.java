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
