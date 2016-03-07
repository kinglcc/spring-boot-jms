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
package com.kinglcc.spring.jms.core.listener;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpoint;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.jms.config.MethodJmsListenerEndpoint;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;

/**
 * JmsListenerEndpointRegistryAdapter
 * <pre>The {@link JmsListenerEndpointRegistry} support {@link BeanFactoryAware}</pre>
 *
 * @author liaochaochao
 * @since 2016年1月28日 下午8:32:08
 */
public class JmsListenerEndpointRegistryAdapter extends JmsListenerEndpointRegistry implements BeanFactoryAware {

    private MessageHandlerMethodFactory messageHandlerMethodFactory;
    private BeanFactory beanFactory;

    @Override
    public void registerListenerContainer(JmsListenerEndpoint endpoint, JmsListenerContainerFactory<?> factory) {
        JmsListenerEndpoint jmsListenerEndpoint = endpoint;
        if (endpoint instanceof MethodJmsListenerEndpoint) {
            MethodJmsListenerEndpointAdapter methodEndpoint = new MethodJmsListenerEndpointAdapter(
                    (MethodJmsListenerEndpoint) endpoint, messageHandlerMethodFactory);
            methodEndpoint.setBeanFactory(beanFactory);
            jmsListenerEndpoint = methodEndpoint;
        }
        super.registerListenerContainer(jmsListenerEndpoint, factory);
    }

    public void setMessageHandlerMethodFactory(MessageHandlerMethodFactory messageHandlerMethodFactory) {
        this.messageHandlerMethodFactory = messageHandlerMethodFactory;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

}
