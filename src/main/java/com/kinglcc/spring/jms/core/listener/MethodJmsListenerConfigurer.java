package com.kinglcc.spring.jms.core.listener;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;

/**
 * MethodJmsListenerConfigure
 *
 * @author liaochaochao
 * @since 2016年1月28日 下午2:17:34
 */
public class MethodJmsListenerConfigurer implements JmsListenerConfigurer, BeanFactoryAware, InitializingBean {

    private List<HandlerMethodArgumentResolver> handlerMethodArgumentResolvers =
            new LinkedList<HandlerMethodArgumentResolver>();
    private MessageHandlerMethodFactory messageHandlerMethodFactory;
    private JmsListenerEndpointRegistry endpointRegistry;
    private MessageConverter messageConverter;
    private BeanFactory beanFactory;

    @Override
    public void configureJmsListeners(JmsListenerEndpointRegistrar registrar) {
        registrar.setEndpointRegistry(endpointRegistry);
        registrar.setMessageHandlerMethodFactory(messageHandlerMethodFactory);
    }

    private MessageHandlerMethodFactory createDefaultJmsHandlerMethodFactory() {
        DefaultMessageHandlerMethodFactory defaultFactory = new DefaultMessageHandlerMethodFactory();
        defaultFactory.setBeanFactory(beanFactory);
        if (!handlerMethodArgumentResolvers.isEmpty()) {
            defaultFactory.setCustomArgumentResolvers(handlerMethodArgumentResolvers);
        }
        defaultFactory.setMessageConverter(messageConverter);
        defaultFactory.afterPropertiesSet();
        return defaultFactory;
    }

    public void addHandlerMethodArgumentResolver(HandlerMethodArgumentResolver argumentResolver) {
        this.handlerMethodArgumentResolvers.add(argumentResolver);
    }

    public List<HandlerMethodArgumentResolver> getHandlerMethodArgumentResolvers() {
        return handlerMethodArgumentResolvers;
    }

    public void setHandlerMethodArgumentResolvers(List<HandlerMethodArgumentResolver>
            handlerMethodArgumentResolvers) {
        this.handlerMethodArgumentResolvers = handlerMethodArgumentResolvers;
    }

    public MessageHandlerMethodFactory getMessageHandlerMethodFactory() {
        return messageHandlerMethodFactory;
    }

    public void setMessageHandlerMethodFactory(MessageHandlerMethodFactory messageHandlerMethodFactory) {
        this.messageHandlerMethodFactory = messageHandlerMethodFactory;
    }

    public JmsListenerEndpointRegistry getEndpointRegistry() {
        return endpointRegistry;
    }

    public void setEndpointRegistry(JmsListenerEndpointRegistry endpointRegistry) {
        this.endpointRegistry = endpointRegistry;
    }

    public MessageConverter getMessageConverter() {
        return messageConverter;
    }

    public void setMessageConverter(MessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (beanFactory instanceof ListableBeanFactory) {
            Map<String, HandlerMethodArgumentResolver> instances =
                    ((ListableBeanFactory) this.beanFactory).getBeansOfType(HandlerMethodArgumentResolver.class);
            if (null != instances && instances.size() > 0) {
                handlerMethodArgumentResolvers.addAll(instances.values());
            }
        }

        if (null == messageHandlerMethodFactory) {
            messageHandlerMethodFactory = createDefaultJmsHandlerMethodFactory();
        }

        if (endpointRegistry instanceof JmsListenerEndpointRegistryAdapter) {
            JmsListenerEndpointRegistryAdapter methodEndpointRegistry =
                    (JmsListenerEndpointRegistryAdapter) endpointRegistry;
            methodEndpointRegistry.setMessageHandlerMethodFactory(messageHandlerMethodFactory);
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

}
