package com.icl.integrator.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: BigBlackBug
 * Date: 29.11.13
 * Time: 9:29
 * To change this template use File | Settings | File Templates.
 */
@Service
public class AddressMappingService {

    public static final String DEFAULT_PROTOCOL = "HTTP";

    public static final String ACCEPT_RESPONSE_ACTION = "accept_response";

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public URL getServiceURL(String serviceName, String action) {
        Query query = em.createQuery(
                "select " +
                        "addr.serviceURL,addr.servicePort," +
                        "actions.actionURL " +
                        "from AddressMapping addr " +
                        "join addr.actionMappings actions " +
                        "where addr.serviceName=:serviceName " +
                        "and actions.actionName=:actionName")
                .setParameter("actionName",
                              action).setParameter
                        ("serviceName", serviceName);
        QueryResult queryResult;
        try {
            queryResult =
                    new QueryResult((Object[]) query.getSingleResult());
            return new URL(DEFAULT_PROTOCOL, queryResult.serviceURL,
                           queryResult.servicePort,
                           queryResult.actionURL);
        } catch (NoResultException ex) {
            return null;  //TODO add NPE handler
        } catch (MalformedURLException e) {
            return null;
        }
    }

    private static final class QueryResult {

        final String serviceURL;

        final String actionURL;

        final int servicePort;

        public QueryResult(Object[] queryResult) {
            this.serviceURL = String.valueOf(queryResult[0]);
            this.servicePort = Integer.valueOf(String.valueOf(queryResult[1]));
            this.actionURL = String.valueOf(queryResult[2]);
        }
    }

}
