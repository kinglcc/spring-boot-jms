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
package com.kinglcc.spring.jms.config;

import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;

import com.kinglcc.spring.jms.core.Jackson2PayloadArgumentResolver;
import com.kinglcc.spring.jms.core.converter.Jackson2MessageAdapterConverter;
import com.kinglcc.spring.jms.core.listener.JmsListenerEndpointRegistryAdapter;
import com.kinglcc.spring.jms.core.listener.MethodJmsListenerConfigurer;

/**
 * JmsListenerAutoConfiguration
 * <pre>
 * Custom {@link JmsListenerConfigurer}.
 * The converter for {@link Message}.
 * The JmsListenerEndpointRegistry support {@link BeanFactoryAware}
 * </pre>
 *
 * @author liaochaochao
 * @since 2016年1月28日 下午9:51:32
 */
@Configuration
@ConditionalOnClass(JmsListenerConfigurer.class)
@ConditionalOnExpression("${jms.message.filter:true}")
@AutoConfigureAfter(JmsAnnotationAtuoConfiguration.class)
public class JmsListenerAutoConfiguration {

    @Autowired
    @Qualifier("jmsListenerEndpointRegistry")
    private JmsListenerEndpointRegistry jmsListenerEndpointRegistry;

    @Autowired(required = false)
    @Qualifier("jackson2MessageAdapterConverter")
    private MessageConverter messageConverter;

    @Bean(name = "jmsListenerEndpointRegistry")
    public JmsListenerEndpointRegistry jmsListenerEndpointRegistry() {
        return new JmsListenerEndpointRegistryAdapter();
    }

    @Bean(name = "jackson2MessageAdapterConverter")
    public MessageConverter messageConverter() {
        return new Jackson2MessageAdapterConverter();
    }

    @Bean
    @ConditionalOnBean(name = "jackson2MessageAdapterConverter")
    public HandlerMethodArgumentResolver handlerMethodArgumentResolver() {
        return new Jackson2PayloadArgumentResolver(messageConverter);
    }

    @Bean
    @ConditionalOnMissingBean
    public JmsListenerConfigurer jmsListenerConfigurer() {
        MethodJmsListenerConfigurer configurer = new MethodJmsListenerConfigurer();
        configurer.setEndpointRegistry(jmsListenerEndpointRegistry);
        configurer.setMessageConverter(messageConverter);
        return configurer;
    }

}
