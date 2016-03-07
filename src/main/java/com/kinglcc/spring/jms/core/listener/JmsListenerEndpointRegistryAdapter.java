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
