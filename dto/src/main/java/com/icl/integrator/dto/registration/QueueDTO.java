package com.icl.integrator.dto.registration;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 17.12.13
 * Time: 10:58
 * To change this template use File | Settings | File Templates.
 */
public class QueueDTO implements ActionDescriptor {

    private String username;

    private String password;

    private String queueName;

    public QueueDTO() {

    }

    public QueueDTO(String queueName, String username, String password) {
        this.queueName = queueName;
        this.username = username;
        this.password = password;
    }

    public QueueDTO(String queueName) {
        this.queueName = queueName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        QueueDTO queueDTO = (QueueDTO) o;

        if (!password.equals(queueDTO.password)) {
            return false;
        }
        if (!queueName.equals(queueDTO.queueName)) {
            return false;
        }
        if (!username.equals(queueDTO.username)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = username.hashCode();
        result = 31 * result + password.hashCode();
        result = 31 * result + queueName.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "username: " + username + " queue: " + queueName;
    }
}
