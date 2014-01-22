package com.icl.integrator.dto;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 22.01.14
 * Time: 12:53
 * To change this template use File | Settings | File Templates.
 */
public class ServiceDTOWithResponseHandler {

    private RawDestinationDescriptorDTO integratorResponseHandler;

    private ServiceDTO serviceDTO;

    public ServiceDTOWithResponseHandler() {
    }


    public ServiceDTOWithResponseHandler(ServiceDTO serviceDTO,
                                         RawDestinationDescriptorDTO integratorResponseHandler) {
        this.serviceDTO = serviceDTO;
        this.integratorResponseHandler = integratorResponseHandler;
    }

    public ServiceDTOWithResponseHandler(ServiceDTO serviceDTO) {
        this.serviceDTO = serviceDTO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ServiceDTOWithResponseHandler that = (ServiceDTOWithResponseHandler) o;

        if (integratorResponseHandler != null ? !integratorResponseHandler
                .equals(that.integratorResponseHandler) : that.integratorResponseHandler != null) {
            return false;
        }
        if (!serviceDTO.equals(that.serviceDTO)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result =
                integratorResponseHandler != null ? integratorResponseHandler
                        .hashCode() : 0;
        result = 31 * result + serviceDTO.hashCode();
        return result;
    }

    public RawDestinationDescriptorDTO getIntegratorResponseHandler() {

        return integratorResponseHandler;
    }

    public void setIntegratorResponseHandler(
            RawDestinationDescriptorDTO integratorResponseHandler) {
        this.integratorResponseHandler = integratorResponseHandler;
    }

    public ServiceDTO getServiceDTO() {
        return serviceDTO;
    }

    public void setServiceDTO(ServiceDTO serviceDTO) {
        this.serviceDTO = serviceDTO;
    }
}
