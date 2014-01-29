package com.icl.integrator.model;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.hibernate.annotations.Type;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 09.12.13
 * Time: 14:17
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "TASK_LOG_ENTRY")
public class TaskLogEntry extends AbstractEntity {

    @Column(name = "LOG_MESSAGE", nullable = false, length = 1024)
    private String message;

    @Column(name = "EXTRA_LOG_MESSAGE")
    @Type(type = "org.hibernate.type.StringClobType")
    @Lob
    private String additionalMessage;

    @Column(name = "ATTACHED_JSON")
    @Basic(fetch = FetchType.LAZY)
    @Type(type = "org.hibernate.type.StringClobType")
    @Lob
    private String dataJson;

    public TaskLogEntry() {
    }

    public TaskLogEntry(String message,
                        String additionalMessage,
                        ObjectNode data) {
        this.setMessage(message);
        this.setAdditionalMessage(additionalMessage);
        this.setDataJson(data != null ? data.toString() : null);
    }

    public TaskLogEntry(String message,
                        String additionalMessage) {
        this(message, additionalMessage, null);
    }

    public TaskLogEntry(String message) {
        this(message, "", null);
    }

    public TaskLogEntry(String message, ObjectNode data) {
        this(message, "", data);
    }

    public String getDataJson() {

        return dataJson;
    }

    public void setDataJson(String dataJson) {
        this.dataJson = dataJson;
    }

    public String getAdditionalMessage() {
        return additionalMessage;
    }

    public void setAdditionalMessage(String additionalMessage) {
        this.additionalMessage = additionalMessage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
