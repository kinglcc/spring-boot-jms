package com.kinglcc.spring.jms.filter;

import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.kinglcc.spring.jms.utils.JsonUtils;

/**
 * SharedTopicFilter
 * <pre>The filter implements JMS2.0 sharedTopic</pre>
 *
 * @author liaochaochao
 * @since 2016年1月26日 下午3:20:56
 */
@ConditionalOnClass(RedisTemplate.class)
@Component("sharedTopicFilter")
public class SharedTopicFilter implements MessageFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SharedTopicFilter.class);

    private static final String VALUE_STORE_IN_REDIS = "1";
    private static final int MSGID_EXPIREDTIME = 30;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean doFilter(Message jmsMessage, Session session) {
        try {
            String messageId = jmsMessage.getJMSMessageID();
            return putIfAbsent(messageId, VALUE_STORE_IN_REDIS, MSGID_EXPIREDTIME, TimeUnit.MINUTES);
        } catch (JMSException e) {
            LOGGER.error("Get JMS message id error", e);
        }
        return false;
    }

    /**
     * Set the value of a key, only if the key does not exist
     * 
     * @param key The key
     * @param value The value
     * @return true or false
     */
    private <T> boolean putIfAbsent(final String key, final T value) {
        Boolean result = stringRedisTemplate.boundValueOps(key).setIfAbsent(JsonUtils.toJson(value));
        return null != result && result;
    }

    /**
     * Set the value of a key, only if the key does not exist
     * 
     * @param key The key
     * @param value The value
     * @param expiredTime The expired time
     * @param timeUnit The time unit of the expired time
     * @return true or false
     */
    private <T> boolean putIfAbsent(final String key, final T value, final long expiredTime, TimeUnit timeUnit) {
        if (putIfAbsent(key, value)) {
            setExpiredTime(key, expiredTime, timeUnit);
            return true;
        }
        return false;
    }

    private boolean setExpiredTime(String key, long expiredTime, TimeUnit timeUnit) {
        Boolean result = stringRedisTemplate.expire(key, expiredTime, timeUnit);
        return null != result && result;
    }

}
