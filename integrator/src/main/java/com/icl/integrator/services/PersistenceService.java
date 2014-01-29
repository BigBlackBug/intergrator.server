package com.icl.integrator.services;

import com.icl.integrator.model.AbstractEntity;
import com.icl.integrator.model.HttpServiceEndpoint;
import com.icl.integrator.model.JMSServiceEndpoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

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
    public <T extends AbstractEntity> T save(T entity) {
        if (entity == null) {  //TODO WAIT WHAT?
            em.persist(entity);
        } else {
            entity = em.merge(entity);
        }
        return entity;
    }

    @Transactional
    public List<HttpServiceEndpoint> getHttpServices() {
        return em.createQuery("select ep from HttpServiceEndpoint ep",
                              HttpServiceEndpoint.class).getResultList();
    }

    @Transactional
    public List<JMSServiceEndpoint> getJmsServices() {
        return em.createQuery("select ep from JMSServiceEndpoint ep",
                              JMSServiceEndpoint.class).getResultList();
    }

    @Transactional
    public List<String> getHttpActions(String serviceName) {
        String query =
                "select action.actionName from HttpServiceEndpoint ep join " +
                        "ep.httpActions action where ep.serviceName=:serviceName";
        return em.createQuery(query, String.class).
                setParameter("serviceName", serviceName).getResultList();
    }

    @Transactional
    public List<String> getJmsActions(String serviceName) {
        String query =
                "select action.actionName from JMSServiceEndpoint ep join " +
                        "ep.jmsActions action where ep.serviceName=:serviceName";
        return em.createQuery(query, String.class).
                setParameter("serviceName", serviceName).getResultList();
    }

}
