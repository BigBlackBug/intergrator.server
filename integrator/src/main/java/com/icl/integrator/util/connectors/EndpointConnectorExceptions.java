package com.icl.integrator.util.connectors;

/**
 * Created by bigblackbug on 2/5/14.
 */
public final class EndpointConnectorExceptions {

    public static class JMSIntegratorException extends Exception {

        public JMSIntegratorException(Throwable cause) {
            super(cause);
        }

        public JMSIntegratorException(String message, Throwable cause) {
            super(message, cause);
        }

        public JMSIntegratorException(String message) {
            super(message);
        }
    }

    public static class HttpIntegratorException extends Exception {

        public HttpIntegratorException(Throwable cause) {
            super(cause);
        }

        public HttpIntegratorException(String message, Throwable cause) {
            super(message, cause);
        }

        public HttpIntegratorException(String message) {
            super(message);
        }
    }

}
