package com.icl.integrator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icl.integrator.api.SourceService;
import com.icl.integrator.dto.ResponseFromTargetDTO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 29.01.14
 * Time: 13:55
 * To change this template use File | Settings | File Templates.
 */
@Component
public class SourceJmsController implements SourceService, MessageListener {

    private final static Log logger = LogFactory.getLog(
            SourceJmsController.class);

    @Autowired
    private ObjectMapper mapper;

    @Override
    public void handleResponseFromTarget(ResponseFromTargetDTO responseDTO) {
        //TODO implement

    }

    @Override
    public void onMessage(Message message) {
        ResponseFromTargetDTO response;
        if (message instanceof TextMessage) {
            String content;
            try {
                content = ((TextMessage) message).getText();
            } catch (JMSException e) {
                logger.error("Ошибка получения текста сообщения", e);
                return;
            }
            try {
                response = mapper.readValue(content,
                                            ResponseFromTargetDTO.class);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            logger.info("received a response from target " + response
                    .getServiceName());
        } else {

        }

    }
}
