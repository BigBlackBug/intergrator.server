package com.icl.integrator.util.connectors;

/**
 * Created by bigblackbug on 2/5/14.
 */
public final class EndpointConnectorExceptions {

    public static class JMSConnectorException extends Exception {

        public JMSConnectorException(Throwable cause) {
            super(cause);
        }

        public JMSConnectorException(String message, Throwable cause) {
            super(message, cause);
        }

        public JMSConnectorException(String message) {
            super(message);
        }
    }

    public static class HttpConnectorException extends Exception {

        public HttpConnectorException(Throwable cause) {
            super(cause);
        }

        public HttpConnectorException(String message, Throwable cause) {
            super(message, cause);
        }

        public HttpConnectorException(String message) {
            super(message);
        }
    }

}
