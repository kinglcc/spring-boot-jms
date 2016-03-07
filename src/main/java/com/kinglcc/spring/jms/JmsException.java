/**
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package com.kinglcc.spring.jms;

/**
 * JmsException
 * <pre>TODO Add the type comment</pre>
 *
 * @author liaochaochao
 * @since 2016年3月7日 下午3:24:51
 */
public class JmsException extends RuntimeException {

    private static final long serialVersionUID = -5977473259439014443L;

    public JmsException() {
        super();
    }

    public JmsException(String message, Throwable cause) {
        super(message, cause);
    }

    public JmsException(String message) {
        super(message);
    }

    public JmsException(Throwable cause) {
        super(cause);
    }

}
