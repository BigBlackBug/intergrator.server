package com.icl.integrator.dto;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 24.01.14
 * Time: 13:07
 * To change this template use File | Settings | File Templates.
 */
public class IntegratorPacket<T> {

    private T packet;

    private DestinationDescriptorDTO responseDestinationDescriptor;

    public IntegratorPacket() {

    }

    public IntegratorPacket(
            DestinationDescriptorDTO responseDestinationDescriptor) {
        this.responseDestinationDescriptor = responseDestinationDescriptor;
    }

    public IntegratorPacket(T packet,
                            DestinationDescriptorDTO responseDestinationDescriptor) {
        this.packet = packet;
        this.responseDestinationDescriptor = responseDestinationDescriptor;
    }

    public IntegratorPacket(T packet) {
        this.packet = packet;
    }

    public T getPacket() {
        return packet;
    }

    public void setPacket(T packet) {
        this.packet = packet;
    }

    public DestinationDescriptorDTO getResponseDestinationDescriptor() {
        return responseDestinationDescriptor;
    }

    public void setResponseDestinationDescriptor(
            DestinationDescriptorDTO responseDestinationDescriptor) {
        this.responseDestinationDescriptor = responseDestinationDescriptor;
    }
}
