package com.icl.integrator.services;

import com.icl.integrator.dto.EndpointDTO;
import com.icl.integrator.dto.registration.ActionDTO;
import com.icl.integrator.dto.registration.TargetRegistrationDTO;
import com.icl.integrator.dto.source.HttpEndpointDescriptorDTO;
import com.icl.integrator.model.ActionMapping;
import com.icl.integrator.model.AddressMapping;
import com.icl.integrator.util.connectors.ConnectionException;
import com.icl.integrator.util.connectors.EndpointConnector;
import com.icl.integrator.util.connectors.EndpointConnectorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 16.12.13
 * Time: 15:06
 * To change this template use File | Settings | File Templates.
 */
@Service
public class RegistrationService {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private EndpointConnectorFactory connectorFactory;

    @Transactional
    public void register(TargetRegistrationDTO registrationDTO) throws
            TargetRegistrationException {
        EndpointDTO endpoint = registrationDTO.getEndpoint();
        try {
            testConnection(endpoint);
        } catch (ConnectionException ex) {
            throw new TargetRegistrationException(
                    "Connection failed", ex);
        }

        switch (endpoint.getEndpointType()) {
            case HTTP: {
                HttpEndpointDescriptorDTO descriptor =
                        (HttpEndpointDescriptorDTO) endpoint.getDescriptor();
                String serviceName = registrationDTO.getServiceName();
                AddressMapping addressMapping =
                        createHttpEntity(descriptor, serviceName,
                                         registrationDTO.getActions());
                em.persist(addressMapping);
                break;
            }
            default: {
                // TODO implement with addition of jms
            }

        }
    }

    private void testConnection(EndpointDTO endpoint)
            throws ConnectionException {
        EndpointConnector connector = connectorFactory.
                createEndpointConnector(endpoint);
        connector.testConnection();
    }

    private AddressMapping createHttpEntity(HttpEndpointDescriptorDTO
                                                    targetDescriptor,
                                            String serviceName,
                                            List<ActionDTO> actions) {
        AddressMapping addressMapping = new AddressMapping();
        addressMapping.setServiceName(serviceName);
        addressMapping.setServiceURL(targetDescriptor.getHost());
        addressMapping.setServicePort(targetDescriptor.getPort());
        for (ActionDTO actionDTO : actions) {
            ActionMapping actionMapping = new ActionMapping();
            actionMapping.setActionName(actionDTO.getName());
            actionMapping.setActionURL(actionDTO.getUrl());
            actionMapping.setAddressMapping(addressMapping);
            addressMapping.addActionMaping(actionMapping);
        }
        return addressMapping;
    }
}
