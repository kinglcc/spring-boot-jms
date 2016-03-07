/**
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package com.kinglcc.spring.jms.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * InetUtils
 * <pre>The inet tools</pre>
 *
 * @author liaochaochao
 * @since 2016年1月26日 下午8:52:24
 */
public class InetUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(InetUtils.class);

    public static final String LOCALHOST = "localhost";
    public static final String LOCALIP = "127.0.0.1";

    public static String getLocalHostName() {
        try {
            return (InetAddress.getLocalHost()).getHostName();
        } catch (UnknownHostException e) {
            String host = e.getMessage();
            if (host != null) {
                int colon = host.indexOf(':');
                if (colon > 0) {
                    return host.substring(0, colon);
                }
            }
            LOGGER.error("Get local hostname error", e);
        }
        return LOCALHOST;
    }

    public static String getLocalAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            LOGGER.error("Get local IP address error", e);
        }
        return LOCALIP;
    }

}
