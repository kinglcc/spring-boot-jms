package com.kinglcc.spring.jms.core;

import org.apache.commons.lang.StringUtils;

/**
 * DestinationType
 * <pre> The JMS destination type for this listener:
 * queue, topic, durableTopic, sharedTopic or sharedDurableTopic.
 * This enables potentially the pubSubDomain, subscriptionDurable
 * and subscriptionShared properties of the container.
 * The default is queue.</pre>
 *
 * @author liaochaochao
 * @since 2016年1月25日 下午1:49:14
 */
public enum DestinationType {

    QUEUE("queue"),
    TOPIC("topic", true, false, false),
    DURABLETOPIC("durableTopic", true, true, false),
    SHAREDTOPIC("sharedTopic", true, false, true),
    SHAREDDURABLETOPIC("sharedDurableTopic", true, true, true);

    public static final String TOPIC_PREFIX = "TOPIC@";
    public static final String QUEUE_PREFIX = "QUEUE@";
    public static final String DURABLE_PREFIX = "DURABLE@";
    public static final String SHARED_PREFIX = "SHARED@";

    private String alias;
    private boolean pubSubDomain;
    private boolean subscriptionDurable;
    private boolean subscriptionShared;

    DestinationType(String alias) {
        this.alias = alias;
    }

    DestinationType(String alias, boolean pubSubDomain, boolean subscriptionDurable, boolean subscriptionShared) {
        this.alias = alias;
        this.pubSubDomain = pubSubDomain;
        this.subscriptionDurable = subscriptionDurable;
        this.subscriptionShared = subscriptionShared;
    }

    public static DestinationType asDestinationType(String destinationName) {
        if (StringUtils.isNotBlank(destinationName)) {
            if (StringUtils.startsWith(destinationName, TOPIC_PREFIX)) {
                return asTopicType(destinationName);
            }
        }
        return QUEUE;
    }

    private static DestinationType asTopicType(String destinationName) {
        if (StringUtils.contains(destinationName, SHARED_PREFIX)) {
            if (StringUtils.contains(destinationName, DURABLE_PREFIX)) {
                return SHAREDDURABLETOPIC;
            }
            return SHAREDTOPIC;
        } else if (StringUtils.contains(destinationName, DURABLE_PREFIX)) {
            return DURABLETOPIC;
        }
        return TOPIC;
    }

    public String getDestinationName(String destinationNameWithType) {
        switch (this) {
            case SHAREDDURABLETOPIC:
                String name = StringUtils.substringAfter(destinationNameWithType, SHARED_PREFIX);
                if (StringUtils.startsWith(name, DURABLE_PREFIX)) {
                    return StringUtils.substringAfter(name, DURABLE_PREFIX);
                }
                return name;
            case SHAREDTOPIC:
                return StringUtils.substringAfter(destinationNameWithType, SHARED_PREFIX);
            case DURABLETOPIC:
                return StringUtils.substringAfter(destinationNameWithType, DURABLE_PREFIX);
            case TOPIC:
                return StringUtils.substringAfter(destinationNameWithType, TOPIC_PREFIX);
            default:
                if (StringUtils.startsWith(destinationNameWithType, QUEUE_PREFIX)) {
                    return StringUtils.substringAfter(destinationNameWithType, QUEUE_PREFIX);
                }
                return destinationNameWithType;
        }

    }

    public String getAlias() {
        return alias;
    }

    public boolean isPubSubDomain() {
        return pubSubDomain;
    }

    public boolean isSubscriptionDurable() {
        return subscriptionDurable;
    }

    public boolean isSubscriptionShared() {
        return subscriptionShared;
    }

}
