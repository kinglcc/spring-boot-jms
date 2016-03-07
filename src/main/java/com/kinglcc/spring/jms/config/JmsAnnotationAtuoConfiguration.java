package com.kinglcc.spring.jms.config;

import javax.jms.ConnectionFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.JmsProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.destination.DestinationResolver;
import org.springframework.transaction.jta.JtaTransactionManager;

import com.kinglcc.spring.jms.core.ClientIdGenerator;
import com.kinglcc.spring.jms.core.listener.DynamicJmsListenerContainerFactory;

/**
 * JmsAutoConfiguration
 * <pre>The default jms configuration</pre>
 *
 * @author liaochaochao
 * @since 2016年1月19日 下午3:39:04
 */
@Configuration
@ConditionalOnClass(EnableJms.class)
@AutoConfigureAfter(JmsAutoConfiguration.class)
public class JmsAnnotationAtuoConfiguration {

    @Autowired(required = false)
    private DestinationResolver destinationResolver;
    @Autowired(required = false)
    @Qualifier("genericJmsMessageConverter")
    private MessageConverter messageConverter;
    @Autowired(required = false)
    private JtaTransactionManager transactionManager;

    @Autowired
    private JmsProperties properties;

    @Value("${jms.message.clientId:}")
    private String clientId;
    @Value("${spring.application.name}")
    private String applicationName;
    @Value("${server.port}")
    private String applicationPort;

    @Bean
    @ConditionalOnMissingBean(name = "jmsListenerContainerFactory")
    public DynamicJmsListenerContainerFactory jmsListenerContainerFactory(ConnectionFactory connectionFactory) {
        DynamicJmsListenerContainerFactory factory = new DynamicJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setPubSubDomain(this.properties.isPubSubDomain());
        if (this.transactionManager != null) {
            factory.setTransactionManager(this.transactionManager);
        } else {
            factory.setSessionTransacted(true);
        }
        if (this.destinationResolver != null) {
            factory.setDestinationResolver(this.destinationResolver);
        }
        if (this.messageConverter != null) {
            factory.setMessageConverter(messageConverter);
        }
        ClientIdGenerator clientIdGenerator = new ClientIdGenerator(clientId, applicationName, applicationPort);
        factory.setClientIdGenerator(clientIdGenerator);
        return factory;
    }

    @Bean
    @ConditionalOnMissingBean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setPubSubDomain(this.properties.isPubSubDomain());
        if (this.destinationResolver != null) {
            jmsTemplate.setDestinationResolver(this.destinationResolver);
        }
        jmsTemplate.setMessageConverter(messageConverter);
        return jmsTemplate;
    }

}
