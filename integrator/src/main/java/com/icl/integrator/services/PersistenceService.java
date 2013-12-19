package com.icl.integrator.services;

import com.icl.integrator.model.HttpAction;
import com.icl.integrator.model.HttpServiceEndpoint;
import com.icl.integrator.model.JMSAction;
import com.icl.integrator.model.JMSServiceEndpoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public HttpAction saveAction(HttpAction action) {
        if (action == null) {
            em.persist(action);
            return action;
        } else {
            return em.merge(action);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public JMSAction saveAction(JMSAction action) {
        if (action == null) {
            em.persist(action);
            return action;
        } else {
            return em.merge(action);
        }
    }

    @Transactional
    public HttpServiceEndpoint saveService(HttpServiceEndpoint endpoint) {
        if (endpoint == null) {
            em.persist(endpoint);
            return endpoint;
        } else {
            return em.merge(endpoint);
        }
    }

    @Transactional
    public JMSServiceEndpoint saveService(JMSServiceEndpoint endpoint) {
        if (endpoint == null) {
            em.persist(endpoint);
            return endpoint;
        } else {
            return em.merge(endpoint);
        }
    }
}
