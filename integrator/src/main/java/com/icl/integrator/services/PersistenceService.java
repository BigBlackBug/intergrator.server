package com.icl.integrator.services;

import com.icl.integrator.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 19.12.13
 * Time: 13:51
 * To change this template use File | Settings | File Templates.
 */
//TODO обработчик на создание. проверять есть ли сгенеренный сервис или экшон
	//также убрать getSingleResult
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
    public <T extends AbstractEntity> void persist(T entity) {
        em.persist(entity);
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
    public List<String> getActions(String serviceName) {
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
                    "select action from JMSServiceEndpoint ep join " +
                            "ep.actions action where ep.id=:endpointId and " +
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
}
