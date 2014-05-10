package com.icl.integrator.dto;

import com.icl.integrator.util.ExceptionUtils;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 05.12.13
 * Time: 12:51
 * To change this template use File | Settings | File Templates.
 */
public class ErrorDTO implements Serializable {

	private String errorMessage;

	private String developerMessage;

	private ErrorCode errorCode = ErrorCode.DEFAULT;

    public ErrorDTO(String errorMessage, String developerMessage,
                    ErrorCode errorCode) {
        this.errorMessage = errorMessage;
        this.developerMessage = developerMessage;
		this.errorCode = errorCode;
	}

	public ErrorDTO(String errorMessage, String developerMessage) {
        this(errorMessage, developerMessage, ErrorCode.DEFAULT);
    }

    public ErrorDTO(Throwable ex) {
        this(ex, ErrorCode.DEFAULT);
    }

    public ErrorDTO(String errorMessage, ErrorCode errorCode) {
        this(errorMessage, "", errorCode);
    }

	public ErrorDTO(String errorMessage) {
        this(errorMessage, ErrorCode.DEFAULT);
    }

    ErrorDTO() {
	}

    public ErrorDTO(Throwable ex, ErrorCode errorCode) {
        String message = ex.getMessage();
        if (message == null) {
            message = ex.getClass().getSimpleName();
        }
        this.errorMessage = message;
        this.developerMessage = ExceptionUtils.getStackTraceAsString(ex);
        this.errorCode = errorCode;
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
				.equals(errorDTO.developerMessage) :
				errorDTO.developerMessage != null) {
			return false;
		}
		return errorMessage.equals(errorDTO.errorMessage);
	}

    @Override
    public int hashCode() {
        int result = errorMessage.hashCode();
        result = 31 * result + (developerMessage != null ? developerMessage.hashCode() : 0);
        result = 31 * result + errorCode.hashCode();
        return result;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
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

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Message: '").append(errorMessage).append("' DevMessage: '")
				.append(developerMessage).append("' Code: '").append(errorCode).append("'");
		return sb.toString();
	}
}
