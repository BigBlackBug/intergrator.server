package com.icl.integrator.httpclient;

/**
 * Created by e.shahmaev on 02.04.2014.
 */
public class IntegratorClientSettings {

    public static final int DEFAULT_TIMEOUT = 5000;

    private final int connectionTimeout;

    private final int readTimeout;

    public IntegratorClientSettings(int connectionTimeout, int readTimeout) {

        this.connectionTimeout = connectionTimeout;
        this.readTimeout = readTimeout;
    }

    public static IntegratorClientSettings createDefaultSettings() {
        return new IntegratorClientSettings(DEFAULT_TIMEOUT, DEFAULT_TIMEOUT);
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }
}
