/**
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package com.kinglcc.spring.jms.core;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.StringUtils;

import com.kinglcc.spring.jms.utils.InetUtils;

/**
 * ClientIdGenerator
 * <pre>Generator for Globally constant and unique Strings.</pre>
 *
 * @author liaochaochao
 * @since 2016年1月26日 下午8:49:17
 */
public class ClientIdGenerator {

    private static final String SP = "-";
    private final String clientIdPrefix;

    private AtomicInteger instanceNumber = new AtomicInteger(0);
    private String hostName;
    private String applicationName;
    private String applicationPort;

    public ClientIdGenerator(String clientId, String applicationName, String applicationPort) {
        this.hostName = InetUtils.getLocalHostName();
        if (InetUtils.LOCALHOST.equals(hostName)) {
            this.hostName = InetUtils.getLocalAddress();
        }
        this.applicationPort = applicationPort;
        String clientIdPrefix = applicationName;
        if (StringUtils.isNotBlank(clientId)) {
            clientIdPrefix = clientId;
        }
        StringBuilder sb = new StringBuilder(clientIdPrefix);
        sb.append(SP).append(hostName).append(SP).append(applicationPort).append(SP);
        this.clientIdPrefix = sb.toString();
    }

    /**
     * Generate a constant and unqiue id
     *
     * @return a constant and unique id
     */
    public synchronized String generateId() {
        return new StringBuilder(clientIdPrefix).append(instanceNumber.incrementAndGet()).toString();
    }

    public synchronized String generateId(String jmsListenerId) {
        return new StringBuilder(clientIdPrefix).append(jmsListenerId).toString();
    }

    public String getClientIdPrefix() {
        return clientIdPrefix;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getApplicationPort() {
        return applicationPort;
    }

}