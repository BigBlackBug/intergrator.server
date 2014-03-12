package com.icl.integrator.dto;

import com.icl.integrator.dto.destination.DestinationDescriptor;

/**
 * Универсальный пакет, принимаемый интегратором.
 * Если поле responseHandlerDescriptor установлено,
 * то по указанному в нём адресу будет выслан ответ от интегратора.
 */
public class IntegratorPacket<T, Y extends DestinationDescriptor> {

    private T packet;

    private IntegratorMethod method;

    private Y responseHandlerDescriptor;

    public IntegratorPacket() {

    }

    public IntegratorPacket(Y responseHandlerDescriptor) {
        this.responseHandlerDescriptor = responseHandlerDescriptor;
    }

    public IntegratorPacket(T packet,
                            Y responseHandlerDescriptor) {
        this.packet = packet;
        this.responseHandlerDescriptor = responseHandlerDescriptor;
    }

    public IntegratorPacket(T packet) {
        this.packet = packet;
    }

	public IntegratorPacket(IntegratorMethod method, T packet, Y responseHandlerDescriptor) {
		this.method = method;
		this.packet = packet;
		this.responseHandlerDescriptor = responseHandlerDescriptor;
	}

	public IntegratorMethod getMethod() {
        return method;
    }

    public void setMethod(IntegratorMethod method) {
        this.method = method;
    }

    public T getPacket() {
        return packet;
    }

    public void setPacket(T packet) {
        this.packet = packet;
    }

    public Y getResponseHandlerDescriptor() {
        return responseHandlerDescriptor;
    }

    public void setResponseHandlerDescriptor(Y responseHandlerDescriptor) {
        this.responseHandlerDescriptor = responseHandlerDescriptor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        IntegratorPacket that = (IntegratorPacket) o;

        if (method != that.method) {
            return false;
        }
        if (packet != null ? !packet
                .equals(that.packet) : that.packet != null) {
            return false;
        }
        if (responseHandlerDescriptor != null ? !responseHandlerDescriptor
                .equals(that.responseHandlerDescriptor) : that.responseHandlerDescriptor != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = packet != null ? packet.hashCode() : 0;
        result = 31 * result + (method != null ? method.hashCode() : 0);
        result =
                31 * result + (responseHandlerDescriptor != null ? responseHandlerDescriptor
                        .hashCode() : 0);
        return result;
    }
}
