package com.icl.integrator.dto;

import com.icl.integrator.util.Utils;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 05.12.13
 * Time: 12:51
 * To change this template use File | Settings | File Templates.
 */
public class ErrorDTO {

    private String errorMessage;

    private String developerMessage;

    private int errorCode;

    public ErrorDTO(String errorMessage, String developerMessage,
                    int errorCode) {
        this.errorMessage = errorMessage;
        this.developerMessage = developerMessage;
        this.errorCode = errorCode;
    }

    public ErrorDTO(String errorMessage, String developerMessage) {
        this(errorMessage, developerMessage, -1);
    }

    public ErrorDTO(Exception ex) {
        this(ex.getMessage(), Utils.getStackTraceAsString(ex));
    }

    public ErrorDTO(String errorMessage, int errorCode) {
        this(errorMessage, "", errorCode);
    }

    public ErrorDTO(String errorMessage) {
        this(errorMessage, -1);
    }

    public ErrorDTO() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ErrorDTO errorDTO = (ErrorDTO) o;

        if (errorCode != errorDTO.errorCode) {
            return false;
        }
        if (developerMessage != null ? !developerMessage
                .equals(errorDTO.developerMessage) : errorDTO.developerMessage != null) {
            return false;
        }
        return errorMessage.equals(errorDTO.errorMessage);
    }

    @Override
    public int hashCode() {
        int result = errorMessage.hashCode();
        result = 31 * result + (developerMessage != null ? developerMessage
                .hashCode() : 0);
        result = 31 * result + errorCode;
        return result;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getDeveloperMessage() {
        return developerMessage;
    }

    public void setDeveloperMessage(String developerMessage) {
        this.developerMessage = developerMessage;
    }
}
