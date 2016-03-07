package com.kinglcc.spring.jms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.jms.support.destination.DestinationResolver;

import com.kinglcc.spring.jms.core.PrefixDestinationResolver;
import com.kinglcc.spring.jms.core.converter.GenericJmsMessageConverter;
import com.kinglcc.spring.jms.core.converter.Jackson2JmsMessageConverter;
import com.kinglcc.spring.jms.core.converter.JmsMessageConverter;

/**
 * JmsMessageAtuoConfiguration
 * <pre>
 * some jms tools:
 * {@link MessageConverter}
 * {@link DestinationResolver}
 * </pre>
 *
 * @author liaochaochao
 * @since 2016年1月27日 下午7:52:14
 */
@Configuration
@ConditionalOnClass(EnableJms.class)
@AutoConfigureBefore(JmsAutoConfiguration.class)
public class JmsMessageAtuoConfiguration {

    @Value("${jms.message.encoding:UTF-8}")
    private String encoding;

    @Bean
    @ConditionalOnMissingBean
    public DestinationResolver destinationResolver() {
        return new PrefixDestinationResolver();
    }

    @Bean(name = "jackson2JmsMessageConverter")
    @Order
    @ConditionalOnMissingBean(JmsMessageConverter.class)
    public JmsMessageConverter jmsMessageConverter() {
        Jackson2JmsMessageConverter messageConverter = new Jackson2JmsMessageConverter();
        messageConverter.setTargetType(MessageType.TEXT);
        messageConverter.setEncoding(encoding);
        return messageConverter;
    }

    @Bean(name = "genericJmsMessageConverter")
    public MessageConverter messageConverter() {
        return new GenericJmsMessageConverter();
    }

}
