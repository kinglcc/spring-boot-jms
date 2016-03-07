package com.kinglcc.spring.jms.core.listener;

import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Session;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.jms.listener.adapter.MessagingMessageListenerAdapter;
import org.springframework.messaging.handler.invocation.InvocableHandlerMethod;

import com.kinglcc.spring.jms.JmsException;
import com.kinglcc.spring.jms.filter.JmsFilter;
import com.kinglcc.spring.jms.filter.MessageFilter;

/**
 * MethodJmsListenerAdapter
 * <pre>
 * A {@link javax.jms.MessageListener} adapter that invokes a configurable
 * {@link InvocableHandlerMethod}.
 * Include some {@link MessageFilter}, that can filter useless message don't been handled.
 * </pre>
 *
 * @author liaochaochao
 * @since 2016年1月26日 下午2:18:29
 */
public class MethodJmsFilterListenerAdapter extends MessagingMessageListenerAdapter implements BeanFactoryAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodJmsFilterListenerAdapter.class);

    private List<MessageFilter> filters = new ArrayList<MessageFilter>();
    private BeanFactory beanFactory;

    public void setHandlerMethod(InvocableHandlerMethod handlerMethod) {
        super.setHandlerMethod(handlerMethod);
        JmsFilter jmsFilter = handlerMethod.getMethodAnnotation(JmsFilter.class);
        if (null != jmsFilter) {
            for (String fn : jmsFilter.value()) {
                filters.add(resolveMessageFilter(fn));
            }
        }
    }

    private MessageFilter resolveMessageFilter(String filterName) {
        MessageFilter filter = null;
        if (StringUtils.isNotBlank(filterName)) {
            filter = beanFactory.getBean(filterName, MessageFilter.class);
        }
        if (null == filter) {
            throw new JmsException(String.format("NOT FOUND the filter named (%s)", filterName));
        }
        return filter;
    }

    @Override
    public void onMessage(javax.jms.Message jmsMessage, Session session) throws JMSException {
        for (MessageFilter filter : filters) {
            if (!filter.doFilter(jmsMessage, session)) {
                LOGGER.debug("Reject the message {} because it has already resolved!", jmsMessage.getJMSMessageID());
                return;
            }
        }
        super.onMessage(jmsMessage, session);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

}
