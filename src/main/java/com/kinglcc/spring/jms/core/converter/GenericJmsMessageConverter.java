package com.kinglcc.spring.jms.core.converter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.SimpleMessageConverter;
import org.springframework.util.ClassUtils;

/**
 * GenericJmsMessageConverter
 * <pre>
 * Generic jms message converter:
 * The default {@link MessageConverter} is {@link SimpleMessageConverter}.
 * Support multi {@link JmsMessageConverter}
 * </pre>
 * 
 * @author liaochaochao
 * @since 2016年1月27日 下午9:32:23
 */
public class GenericJmsMessageConverter implements MessageConverter, BeanClassLoaderAware,
        BeanFactoryAware, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericJmsMessageConverter.class);

    private static final String CONVERTER_PROP = "jmsConverter@";

    private BeanFactory beanFactory;
    private ClassLoader beanClassLoader;

    private MessageConverter defaultMessageConverter = new SimpleMessageConverter();
    private List<JmsMessageConverter> messageConverters = new LinkedList<JmsMessageConverter>();

    @Override
    public Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {
        MessageConverter converter = getMessageConverterTo(object);
        Message message = converter.toMessage(object, session);
        setConverterOnMessage(message, converter);
        return message;
    }

    private MessageConverter getMessageConverterTo(Object object) {
        for (JmsMessageConverter converter : messageConverters) {
            if (converter.canConvertTo(object)) {
                return converter;
            }
        }
        return defaultMessageConverter;
    }

    private void setConverterOnMessage(Message message, MessageConverter converter) throws JMSException {
        message.setStringProperty(CONVERTER_PROP, converter.getClass().getName());
    }

    @Override
    public Object fromMessage(Message message) throws JMSException, MessageConversionException {
        MessageConverter converter = getMessageConverterFrom(message);
        return converter.fromMessage(message);
    }

    private MessageConverter getMessageConverterFrom(Message message) throws JMSException {
        String converterName = message.getStringProperty(CONVERTER_PROP);
        if (StringUtils.isNotBlank(converterName)) {
            return getConverterByConverterName(converterName);
        }
        for (JmsMessageConverter converter : messageConverters) {
            if (converter.canConvertFrom(message)) {
                return converter;
            }
        }
        return defaultMessageConverter;
    }

    public List<JmsMessageConverter> getMessageConverters() {
        return Collections.unmodifiableList(messageConverters);
    }

    public void setMessageConverters(List<JmsMessageConverter> messageConverters) {
        this.messageConverters = messageConverters;
    }

    protected MessageConverter getConverterByConverterName(String converterName) {

        try {
            Class<?> converterClass = ClassUtils.forName(converterName, this.beanClassLoader);
            for (JmsMessageConverter converter : messageConverters) {
                if (ClassUtils.isAssignableValue(converterClass, converter)) {
                    return converter;
                }
            }
        } catch (Throwable ex) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.warn("NOT FOUND the converter named {}", converterName);
            }
            LOGGER.warn("NOT FOUND the converter named {}", converterName, ex);
        }
        return defaultMessageConverter;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (beanFactory instanceof ListableBeanFactory) {
            Map<String, JmsMessageConverter> instances =
                    ((ListableBeanFactory) this.beanFactory).getBeansOfType(JmsMessageConverter.class);
            if (null != instances && instances.size() > 0) {
                messageConverters.addAll(instances.values());
            }
        }
    }

}
