package com.icl.integrator.services;

import com.icl.integrator.dto.DeliveryPacketType;
import com.icl.integrator.dto.ServiceDTO;
import com.icl.integrator.dto.registration.ActionMethod;
import com.icl.integrator.dto.util.EndpointType;
import com.icl.integrator.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
@Transactional
public class PersistenceService {

    @PersistenceContext
    private EntityManager em;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
    public <T extends AbstractEntity> T merge(T entity) {
        return em.merge(entity);
    }

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public <T extends AbstractEntity> T saveOrUpdate(T entity) {
		if(entity.getId() == null){
			em.persist(entity);
			return entity;
		} else {
			return em.merge(entity);
		}
	}

	public <T extends AbstractEntity> T refresh(T entity) {
		em.refresh(entity);
		return entity;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
    public <T extends AbstractEntity> T persist(T entity) {
        em.persist(entity);
	    return entity;
    }

	//TODO stub
	public void createDefaultUser(){
		try {
			em.createQuery("select e from IntegratorUser e where e.username=:username")
					.setParameter("username", "user").getSingleResult();
		}catch(NoResultException ex){
			final IntegratorUser user = new IntegratorUser();
			user.setUsername("user");
			//pass
			user.setPassword("1a1dc91c907325c69271ddf0c944bc72");
			user.setRole(RoleEnum.ROLE_USER);
			em.persist(user);
		}

	}
	public <T extends AbstractEntity> T find(Class<T> entityClass, UUID id) {
		return em.find(entityClass, id);
	}

	public AbstractEndpointEntity getEndpointEntity(String name){
		return em.createQuery(
				"select entity from AbstractEndpointEntity  entity where entity.serviceName=:name",
				AbstractEndpointEntity.class).setParameter("name", name).getSingleResult();
	}

	public IntegratorUser findUserByUsername(String username) throws NoResultException{
		return em.createQuery("select user from IntegratorUser user where user.username=:username",
		                      IntegratorUser.class).setParameter("username", username)
				.getSingleResult();
	}

	public List<ServiceDTO> getAllServices(){
		List resultList = em.createQuery(
				"select ep.serviceName,ep.type,ep.creator.username from AbstractEndpointEntity ep")
				.getResultList();
		List<ServiceDTO> result = new ArrayList<>();
		for (Object service : resultList) {
			Object[] serviceData = (Object[]) service;
			result.add(new ServiceDTO(serviceData[0].toString(),
			                          EndpointType.valueOf(serviceData[1].toString()),
			                          serviceData[2].toString()));
		}
		return result;
	}

    public HttpAction getHttpAction(String actionName, UUID endpointID) {
        return em.createQuery(
                "select action from HttpAction action where " +
                        "endpoint.id=:endpointID and action.actionName=:actionName",
                HttpAction.class)
                .setParameter("actionName", actionName)
                .setParameter("endpointID", endpointID)
                .getSingleResult();
    }

    public JMSAction getJmsAction(String actionName, UUID endpointID) {
        return em.createQuery(
                "select action from JMSAction action where " +
                        "endpoint.id=:endpointID and action.actionName=:actionName",
                JMSAction.class)
                .setParameter("actionName", actionName)
                .setParameter("endpointID", endpointID).getSingleResult();
    }

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

	public List<AutoDetectionPacket> findAutoDetectionPackets(
			DeliveryPacketType deliveryPacketType) {
		return em.createQuery(
				"select packet from AutoDetectionPacket packet where packet.deliveryPacketType=:deliveryPacketType",AutoDetectionPacket.class)
				.setParameter("deliveryPacketType", deliveryPacketType).getResultList();
	}

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

    public List<AbstractActionEntity> getActions(String serviceName) {
        return em.createQuery("select ep.actions from AbstractEndpointEntity ep where ep" +
                                      ".serviceName=:serviceName").setParameter(
                "serviceName", serviceName).getResultList();
    }

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

	public void removeService(String serviceName) {
		AbstractEndpointEntity service = findService(serviceName);
		em.remove(service);
	}

	public AbstractEndpointEntity findService(String serviceName) {
		return em.createQuery(
				"select ep from AbstractEndpointEntity ep where ep.serviceName=:serviceName",
				AbstractEndpointEntity.class).setParameter("serviceName", serviceName)
				.getSingleResult();
	}
}
