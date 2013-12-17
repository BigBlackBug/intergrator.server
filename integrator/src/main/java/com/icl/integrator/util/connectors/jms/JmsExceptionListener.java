package com.icl.integrator.util.connectors.jms;

import org.springframework.stereotype.Component;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;

@Component
public class JmsExceptionListener implements ExceptionListener {

    public void onException(final JMSException e) {
        e.printStackTrace();
    }
}