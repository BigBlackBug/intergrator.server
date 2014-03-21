package com.icl.integrator.services;

import com.icl.integrator.dto.DeliveryPacketType;
import com.icl.integrator.dto.registration.ActionMethod;
import com.icl.integrator.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 19.12.13
 * Time: 13:51
 * To change this template use File | Settings | File Templates.
 */
@Service
public class PersistenceService {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public HttpServiceEndpoint getHttpService(String serviceName) throws
            NoResultException {
        String query = "select ep from HttpServiceEndpoint ep where " +
                "ep.serviceName=:serviceName";
        return em.createQuery(query, HttpServiceEndpoint.class).
                setParameter("serviceName", serviceName).getSingleResult();
    }

    @Transactional
    public JMSServiceEndpoint getJmsService(String serviceName) throws
            NoResultException {
        String query = "select ep from JMSServiceEndpoint ep where " +
                "ep.serviceName=:serviceName";
        return em.createQuery(query, JMSServiceEndpoint.class).
                setParameter("serviceName", serviceName).getSingleResult();
    }

    @Transactional
    public <T extends AbstractEntity> T merge(T entity) {
        return em.merge(entity);
    }

	@Transactional
	public <T extends AbstractEntity> T saveOrUpdate(T entity) {
		if(entity.getId() == null){
			em.persist(entity);
			return entity;
		} else {
			return em.merge(entity);
		}
	}

	@Transactional
	public <T extends AbstractEntity> T refresh(T entity) {
		em.refresh(entity);
		return entity;
	}

    @Transactional
    public <T extends AbstractEntity> T persist(T entity) {
        em.persist(entity);
	    return entity;
    }

	@Transactional
	public <T extends AbstractEntity> T find(Class<T> entityClass,UUID id) {
		return em.find(entityClass,id);
	}

    @Transactional
    public List<HttpServiceEndpoint> getHttpServices() {
        return em.createQuery("select ep from HttpServiceEndpoint ep",
                              HttpServiceEndpoint.class).getResultList();
    }

    @Transactional
    public HttpAction getHttpAction(String actionName, UUID endpointID) {
        return em.createQuery(
                "select action from HttpAction action where " +
                        "endpoint.id=:endpointID and action.actionName=:actionName",
                HttpAction.class)
                .setParameter("actionName", actionName)
                .setParameter("endpointID", endpointID)
                .getSingleResult();
    }

    @Transactional
    public JMSAction getJmsAction(String actionName, UUID endpointID) {
        return em.createQuery(
                "select action from JMSAction action where " +
                        "endpoint.id=:endpointID and action.actionName=:actionName",
                JMSAction.class)
                .setParameter("actionName", actionName)
                .setParameter("endpointID", endpointID).getSingleResult();
    }

    @Transactional
    public List<JMSServiceEndpoint> getJmsServices() {
        return em.createQuery("select ep from JMSServiceEndpoint ep",
                              JMSServiceEndpoint.class).getResultList();
    }

//    @Transactional
//    public List<String> getHttpActions(String serviceName) {
//        String query =
//                "select action.actionName from HttpServiceEndpoint ep join " +
//                        "ep.httpActions action where ep.serviceName=:serviceName";
//        return em.createQuery(query, String.class).
//                setParameter("serviceName", serviceName).getResultList();
//    }
//
//    @Transactional
//    public List<String> getJmsActions(String serviceName) {
//        String query =
//                "select action.actionName from JMSServiceEndpoint ep join " +
//                        "ep.jmsActions action where ep.serviceName=:serviceName";
//        return em.createQuery(query, String.class).
//                setParameter("serviceName", serviceName).getResultList();
//    }

    @Transactional
    public List<String> getActionNames(String serviceName) {
        String query =
                "select action.actionName from AbstractEndpointEntity ep join " +
                        "ep.actions action where ep.serviceName=:serviceName";
        return em.createQuery(query, String.class).
                setParameter("serviceName", serviceName).getResultList();
    }

    @Transactional
    public HttpServiceEndpoint findHttpService(String host, int port) {
        String query = "select ep from HttpServiceEndpoint ep where " +
                "ep.serviceURL=:serviceURL and ep.servicePort=:servicePort";
        try {
            return em.createQuery(query, HttpServiceEndpoint.class).
                    setParameter("serviceURL", host).
                    setParameter("servicePort", port).
                    getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }
	@Transactional
    public HttpAction findHttpAction(UUID id, String path) {
        try {
            String query =
                    "select action from HttpAction action join " +
                            "action.endpoint ep where ep.id=:endpointId and action.actionURL=:actionURL";
            return em.createQuery(query, HttpAction.class).
                    setParameter("endpointId", id).
                    setParameter("actionURL", path).
                    getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }
	@Transactional
    public JMSServiceEndpoint findJmsService(String connectionFactory,
                                             String jndiProperties) {
        try {
            String query = "select ep from JMSServiceEndpoint ep where " +
                    "ep.connectionFactory=:connectionFactory and ep.jndiProperties=:jndiProperties";
            return em.createQuery(query, JMSServiceEndpoint.class).
                    setParameter("connectionFactory", connectionFactory).
                    setParameter("jndiProperties", jndiProperties).
                    getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }
	@Transactional
    public JMSAction findJmsAction(UUID id, String queueName, String username,
                                   String password) {
        try {
            String query =
                    "select action from JMSAction action join " +
                            "action.endpoint ep where ep.id=:endpointId and " +
                            "action.queueName=:queueName and " +
                            "action.username=:username and " +
                            "action.password=:password";
            return em.createQuery(query, JMSAction.class).
                    setParameter("endpointId", id).
                    setParameter("password", password).
                    setParameter("queueName", queueName).
                    setParameter("username", username).
                    getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

	@Transactional
	public List<AutoDetectionPacket> findAutoDetectionPackets(
			DeliveryPacketType deliveryPacketType) {
		return em.createQuery(
				"select packet from AutoDetectionPacket packet where packet.deliveryPacketType=:deliveryPacketType",AutoDetectionPacket.class)
				.setParameter("deliveryPacketType", deliveryPacketType).getResultList();
	}

	@Transactional
	public List<Delivery> findAllUnfinishedDeliveries() {
		return em.createQuery(
				"select delivery from Delivery delivery where " +
						"delivery.deliveryStatus!=:deliveryOK and" +
						" delivery.deliveryStatus!=:deliveryFailed",
				Delivery.class)
				.setParameter("deliveryOK", DeliveryStatus.DELIVERY_OK)
				.setParameter("deliveryFailed", DeliveryStatus.DELIVERY_FAILED)
				.getResultList();

	}

    @Transactional
    public List<AbstractActionEntity> getActions(String serviceName) {
        return em.createQuery("select ep.actions from AbstractEndpointEntity ep where ep" +
                                      ".serviceName=:serviceName").setParameter(
                "serviceName", serviceName).getResultList();
    }

    @Transactional
    public Map<String, List<AbstractEndpointEntity>> getAllActionMap() {
        Map<String, List<AbstractEndpointEntity>> result = new HashMap<>();
        List<String> actions =
                em.createQuery(
                        "select action.actionName from AbstractActionEntity action where action" +
                                ".actionMethod=:actionMethod",
                        String.class)
                        .setParameter("actionMethod", ActionMethod.HANDLE_DELIVERY)
                        .getResultList();
        if (!actions.isEmpty()) {
            for (String actionName : actions) {
                List<AbstractEndpointEntity> endpoints = em.createQuery(
                        "select ep from AbstractActionEntity a join a.endpoint ep where a" +
                                ".actionName=:actionName", AbstractEndpointEntity.class)
                        .setParameter("actionName", actionName)
                        .getResultList();
                result.put(actionName, endpoints);
            }
        }
        return result;
    }

    @Transactional
    public Map<AbstractEndpointEntity, List<AbstractActionEntity>>
    getServicesSupportingActionType(ActionMethod actionMethod) {
        List<AbstractActionEntity> actions =
                em.createQuery(
                        "select action from AbstractActionEntity action where action" +
                                ".actionMethod=:actionMethod",
                        AbstractActionEntity.class)
                        .setParameter("actionMethod", actionMethod)
                        .getResultList();
        Map<AbstractEndpointEntity, List<AbstractActionEntity>> map = new HashMap<>();
        for (AbstractActionEntity action : actions) {
            AbstractEndpointEntity endpoint = action.getEndpoint();
            List<AbstractActionEntity> savedActions = map.get(endpoint);
            if (savedActions == null) {
                savedActions = new ArrayList<>();
                savedActions.add(action);
                map.put(endpoint, savedActions);
            } else {
                savedActions.add(action);
            }
        }
        return map;
    }

}
