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

import org.apache.commons.lang.StringUtils;
import org.springframework.jms.config.AbstractJmsListenerEndpoint;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpoint;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import com.kinglcc.spring.jms.core.ClientIdGenerator;
import com.kinglcc.spring.jms.core.DestinationType;

/**
 * DynamicJmsListenerContainerFactory
 * <pre>The dynamic jms listener container factory.
 * Create jmsListenerContainer with the name of destination.
 * </pre>
 *
 * @author liaochaochao
 * @since 2016年1月25日 下午1:21:36
 */
public class DynamicJmsListenerContainerFactory extends DefaultJmsListenerContainerFactory {

    private static final String DEFAULT_ENDPOITID = "JmsListenerEndpointContainer#";

    private ClientIdGenerator clientIdGenerator;

    @Override
    public DefaultMessageListenerContainer createListenerContainer(JmsListenerEndpoint endpoint) {
        DefaultMessageListenerContainer instance = super.createListenerContainer(endpoint);
        instance.setClientId(resolveClientId(endpoint));
        if (endpoint instanceof AbstractJmsListenerEndpoint) {
            AbstractJmsListenerEndpoint jmsEndpoint = ((AbstractJmsListenerEndpoint) endpoint);
            DestinationType destinationType = DestinationType.asDestinationType(jmsEndpoint.getDestination());
            instance.setPubSubDomain(destinationType.isPubSubDomain());
            instance.setSubscriptionDurable(destinationType.isSubscriptionDurable());
            instance.setSubscriptionShared(destinationType.isSubscriptionShared());
        }
        endpoint.setupListenerContainer(instance);

        return instance;
    }

    private String resolveClientId(JmsListenerEndpoint endpoint) {
        if (StringUtils.contains(endpoint.getId(), DEFAULT_ENDPOITID)) {
            return clientIdGenerator.generateId();
        }
        return clientIdGenerator.generateId(endpoint.getId());
    }

    public void setClientIdGenerator(ClientIdGenerator clientIdGenerator) {
        this.clientIdGenerator = clientIdGenerator;
    }

}
