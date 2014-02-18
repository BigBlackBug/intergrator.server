package com.icl.integrator.services;

import com.icl.integrator.model.HttpAction;
import com.icl.integrator.model.JMSServiceEndpoint;
import com.icl.integrator.util.IntegratorException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
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
public class EndpointResolverService {

	public static final String DEFAULT_PROTOCOL = "HTTP";

	@PersistenceContext
	private EntityManager em;

	@Transactional
	public URL getServiceURL(String serviceName, String action)
			throws IntegratorException {
		Query query = em.createQuery(
				"select " +
						"address.serviceURL,address.servicePort," +
						"action " +
						"from HttpServiceEndpoint address " +
						"join address.actions action " +
						"where address.serviceName=:serviceName " +
						"and action.actionName=:actionName")
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
			throw new IntegratorException("Сервис " + serviceName + ", " +
					                              "принимающий запросы типа " +
					                              action + ", не " +
					                              "зарегистрирован",
			                              ex);
		} catch (MalformedURLException ex) {
			throw new IntegratorException("При регистрации сервиса был указан" +
					                              " невалидный адрес, " +
					                              "поэтому послать запрос " +
					                              "на него невозможно",
			                              ex);
		}
	}

	@Transactional
	public JMSServiceEndpoint getJmsEndpoint(String serviceName)
			throws IntegratorException {
		TypedQuery<JMSServiceEndpoint> query = em.createQuery(
				"select ep from JMSServiceEndpoint ep " +
						"where ep.serviceName=:serviceName",
				JMSServiceEndpoint.class)
				.setParameter("serviceName", serviceName);
		try {
			return query.getSingleResult();
		} catch (NoResultException ex) {
			throw new IntegratorException("Сервис " + serviceName +
					                              " не зарегистрирован", ex);
		}
	}

	private static final class QueryResult {

		final String serviceURL;

		final String actionURL;

		final int servicePort;

		public QueryResult(Object[] queryResult) {
			this.serviceURL = String.valueOf(queryResult[0]);
			this.servicePort = Integer.valueOf(String.valueOf(queryResult[1]));
			this.actionURL = ((HttpAction) queryResult[2]).getActionURL();
		}
	}

}
