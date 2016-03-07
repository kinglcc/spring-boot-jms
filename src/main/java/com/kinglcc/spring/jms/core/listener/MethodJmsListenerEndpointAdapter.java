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
