package com.icl.integrator.httpclient;

import com.icl.integrator.deserializer.IntegratorObjectMapper;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

public class IntegratorRestTemplate extends RestTemplate {

	private final HttpClient httpClient;

	private final CookieStore cookieStore;

	private final HttpContext httpContext;

	public IntegratorRestTemplate(IntegratorClientSettings integratorClientSettings) {
		super();
		HttpParams params = new BasicHttpParams();
		HttpClientParams.setRedirecting(params, false);
		httpClient = new DefaultHttpClient(params);
		cookieStore = new BasicCookieStore();
		httpContext = new BasicHttpContext();
		httpContext.setAttribute(ClientContext.COOKIE_STORE, getCookieStore());

		StatefulHttpComponentsClientHttpRequestFactory factory =
				new StatefulHttpComponentsClientHttpRequestFactory(httpClient, httpContext);
		factory.setReadTimeout(integratorClientSettings.getReadTimeout());
		factory.setConnectTimeout(integratorClientSettings.getConnectionTimeout());

		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setObjectMapper(new IntegratorObjectMapper());
		//заменяем джексонконвертер на свой
		List<HttpMessageConverter<?>> messageConverters = getMessageConverters();
		messageConverters.remove(messageConverters.size() - 1);
		messageConverters.add(converter);
//		getMessageConverters().add(new ByteArrayHttpMessageConverter());
//		getMessageConverters().add(new StringHttpMessageConverter());
//		getMessageConverters().add(new ResourceHttpMessageConverter());
//		getMessageConverters().add(new SourceHttpMessageConverter());
//		getMessageConverters().add(new FormHttpMessageConverter());

		super.setRequestFactory(factory);
	}

	public HttpClient getHttpClient() {
		return httpClient;
	}

	public CookieStore getCookieStore() {
		return cookieStore;
	}

	public HttpContext getHttpContext() {
		return httpContext;
	}

	private static class StatefulHttpComponentsClientHttpRequestFactory extends
			HttpComponentsClientHttpRequestFactory {

		private final HttpContext httpContext;

		public StatefulHttpComponentsClientHttpRequestFactory(HttpClient httpClient,
		                                                      HttpContext httpContext) {
			super(httpClient);
			this.httpContext = httpContext;
		}

		@Override
		protected HttpContext createHttpContext(HttpMethod httpMethod, URI uri) {
			return this.httpContext;
		}
	}
}
