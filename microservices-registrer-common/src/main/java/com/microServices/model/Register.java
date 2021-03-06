package com.microservices.model;

import javax.inject.Named;

/**
 * Created by stephen on 27/02/2016.
 */

@Named
public class Register {
    /**
     * Hostname
     *
     * @example localhost
     */
    private String hostName;
    /**
     * Register port
     */
    private Integer port;

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

}
