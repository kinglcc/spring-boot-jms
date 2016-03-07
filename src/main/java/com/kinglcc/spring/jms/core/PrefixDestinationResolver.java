package com.kinglcc.spring.jms.core;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;

import org.springframework.jms.support.destination.DynamicDestinationResolver;
import org.springframework.util.Assert;

/**
 * PrefixDestinationResolver
 * <pre>The destination name start with {@code TOPIC_PREFIX} or {@code QUEUE_PREFIX}</pre>
 *
 * @author liaochaochao
 * @since 2016年1月19日 下午3:14:47
 */
public class PrefixDestinationResolver extends DynamicDestinationResolver {

    /**
     * Resolve the specified destination name as a dynamic destination.
     * 
     * @param session the current JMS Session
     * @param destinationName the name of the destination
     * @param pubSubDomain {@code true} if the domain is pub-sub, {@code false} if P2P
     * @return the JMS destination (either a topic or a queue)
     * @throws javax.jms.JMSException if resolution failed
     * @see #resolveTopic(javax.jms.Session, String)
     * @see #resolveQueue(javax.jms.Session, String)
     */
    @Override
    public Destination resolveDestinationName(Session session, String destinationName, boolean pubSubDomain)
            throws JMSException {

        Assert.notNull(session, "Session must not be null");
        DestinationType destinationType = DestinationType.asDestinationType(destinationName);
        if (destinationType.isPubSubDomain()) {
            pubSubDomain = true;
        }
        String destinationFinalName = destinationType.getDestinationName(destinationName);
        Assert.notNull(destinationFinalName, "Destination name must not be null");

        if (pubSubDomain) {
            return resolveTopic(session, destinationFinalName);
        } else {
            return resolveQueue(session, destinationFinalName);
        }
    }

}
