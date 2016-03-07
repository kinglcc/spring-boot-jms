package com.kinglcc.spring.jms;

import javax.jms.JMSException;
import javax.jms.Message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.stereotype.Service;

@Service
public class JmsService {

    @Autowired
    private JmsTemplate jmsTemplate;

    public <T> void send(final String destinaionName, T message) {
        jmsTemplate.convertAndSend(destinaionName, message);
    }

    public <T> void send(final String destinaionName, final String messageGroup, T message) {
        jmsTemplate.convertAndSend(destinaionName, message, new MessagePostProcessor() {

            @Override
            public Message postProcessMessage(Message message) throws JMSException {
                message.setJMSType(messageGroup);
                return message;
            }
        });
    }

}
