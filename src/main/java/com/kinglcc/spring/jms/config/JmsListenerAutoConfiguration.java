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
