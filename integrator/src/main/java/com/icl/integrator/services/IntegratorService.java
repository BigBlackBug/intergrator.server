package com.icl.integrator.services;

import com.icl.integrator.dto.ServiceDTO;
import com.icl.integrator.model.HttpServiceEndpoint;
import com.icl.integrator.model.JMSServiceEndpoint;
import com.icl.integrator.util.EndpointType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 21.01.14
 * Time: 11:57
 * To change this template use File | Settings | File Templates.
 */
@Service
public class IntegratorService {

    @Autowired
    private PersistenceService persistenceService;

    public List<String> getSupportedActions(ServiceDTO serviceDTO) {
        EndpointType endpointType = serviceDTO.getEndpointType();
        switch (endpointType) {
            case HTTP: {
                return persistenceService.getHttpActions(
                        serviceDTO.getServiceName());
            }
            case JMS: {
                return persistenceService.getJmsActions(
                        serviceDTO.getServiceName());
            }
            default: {
                return null;
            }
        }

    }

    public List<ServiceDTO> getServiceList() {
        List<ServiceDTO> result = new ArrayList<>();
        List<HttpServiceEndpoint> httpServices =
                persistenceService.getHttpServices();
        for (HttpServiceEndpoint service : httpServices) {
            result.add(new ServiceDTO(service.getServiceName(),
                                      EndpointType.HTTP));
        }
        List<JMSServiceEndpoint> jmsServices =
                persistenceService.getJmsServices();
        for (JMSServiceEndpoint service : jmsServices) {
            result.add(new ServiceDTO(service.getServiceName(),
                                      EndpointType.JMS));
        }
        return result;
    }

}
