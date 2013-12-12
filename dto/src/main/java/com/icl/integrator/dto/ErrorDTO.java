package com.icl.integrator.dto;

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

    public ErrorDTO(String errorMessage, int errorCode) {
        this(errorMessage, "", errorCode);
    }

    public ErrorDTO(String errorMessage) {
        this(errorMessage, -1);
    }

    public ErrorDTO() {
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getDeveloperMessage() {
        return developerMessage;
    }
}
