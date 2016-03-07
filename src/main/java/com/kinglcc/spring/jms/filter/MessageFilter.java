package com.kinglcc.spring.jms.filter;

import javax.jms.Message;
import javax.jms.Session;

/**
 * JmsFilter
 * <pre>Filter messages, never been handled</pre>
 *
 * @author liaochaochao
 * @since 2016年1月26日 下午1:44:07
 */
public interface MessageFilter {

    boolean doFilter(Message jmsMessage, Session session);

}
