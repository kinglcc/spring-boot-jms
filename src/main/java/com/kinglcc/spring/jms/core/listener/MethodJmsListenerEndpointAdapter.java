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

import java.lang.reflect.Method;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.jms.config.MethodJmsListenerEndpoint;
import org.springframework.jms.listener.adapter.MessagingMessageListenerAdapter;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;

/**
 * MethodJmsListenerEndpointAdapter
 * <pre>The {@link MethodJmsListenerEndpoint} support {@link BeanFactoryAware}.</pre>
 *
 * @author liaochaochao
 * @since 2016年1月26日 下午3:02:45
 */
public class MethodJmsListenerEndpointAdapter extends MethodJmsListenerEndpoint implements BeanFactoryAware {

    private  MethodJmsListenerEndpoint endpoint;
    private BeanFactory beanFactory;

    public MethodJmsListenerEndpointAdapter(MethodJmsListenerEndpoint endpoint,
            MessageHandlerMethodFactory messageHandlerMethodFactory) {
        this.endpoint = endpoint;
        this.setMessageHandlerMethodFactory(messageHandlerMethodFactory);
    }

    @Override
    public Object getBean() {
        return endpoint.getBean();
    }

    @Override
    public Method getMethod() {
        return endpoint.getMethod();
    }

    @Override
    public String getId() {
        return endpoint.getId();
    }

    @Override
    public String getDestination() {
        return endpoint.getDestination();
    }

    @Override
    public String getSubscription() {
        return endpoint.getSubscription();
    }

    @Override
    public String getSelector() {
        return endpoint.getSelector();
    }

    @Override
    public String getConcurrency() {
        return endpoint.getConcurrency();
    }

    @Override
    protected MessagingMessageListenerAdapter createMessageListenerInstance() {
        MethodJmsFilterListenerAdapter listener =  new MethodJmsFilterListenerAdapter();
        listener.setBeanFactory(beanFactory);
        return listener;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

}
