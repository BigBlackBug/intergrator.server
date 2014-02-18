package com.icl.integrator.dto.destination;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 30.01.14
 * Time: 16:01
 * To change this template use File | Settings | File Templates.
 */
public abstract class DestinationDescriptor {

    private final DescriptorType descriptorType;

    protected DestinationDescriptor(DescriptorType descriptorType) {
        this.descriptorType = descriptorType;
    }

    public DescriptorType getDescriptorType() {
        return descriptorType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DestinationDescriptor that = (DestinationDescriptor) o;

        if (descriptorType != that.descriptorType) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return descriptorType.hashCode();
    }

    public enum DescriptorType {
        RAW, SERVICE
    }
}
