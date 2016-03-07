/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
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
