package org.jasig.schedassist.impl.caldav;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.protocol.HttpContext;

/**
 * {@link HttpRequestInterceptor} that performs pre-emptive authentication.
 *
 * @author Nicholas Blair
 * @version $Id: CaldavCalendarDataImportServiceImpl.java $
 */
class PreemptiveAuthInterceptor implements HttpRequestInterceptor {
	/**
	 * 
	 */
	static final String PREEMPTIVE_AUTH = "org.jasig.schedassist.impl.caldav.preemptive-auth";
	private final AuthScope caldavAdminAuthScope;
	private final Log log = LogFactory.getLog(this.getClass());
    /**
	 * @param caldavAdminAuthScope
	 */
	public PreemptiveAuthInterceptor(AuthScope caldavAdminAuthScope) {
		this.caldavAdminAuthScope = caldavAdminAuthScope;
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.http.HttpRequestInterceptor#process(org.apache.http.HttpRequest, org.apache.http.protocol.HttpContext)
	 */
	public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
        AuthState authState = (AuthState) context.getAttribute(ClientContext.TARGET_AUTH_STATE);
        if (authState.getAuthScheme() == null) {
            AuthScheme authScheme = (AuthScheme) context.getAttribute(PREEMPTIVE_AUTH);
            CredentialsProvider credsProvider = (CredentialsProvider) context.getAttribute(ClientContext.CREDS_PROVIDER);
            if (authScheme != null) {
                Credentials creds = credsProvider.getCredentials(caldavAdminAuthScope);
                if (creds == null) {
                    throw new HttpException("No credentials for preemptive authentication");
                }
                authState.setAuthScheme(authScheme);
                authState.setCredentials(creds);
                if(log.isTraceEnabled()) {
                	log.trace("successfully set credentials " + creds + " and authScheme " + authScheme + " for request " + request);
                }
            } else {
            	log.warn(PREEMPTIVE_AUTH + " authScheme not found in context, failed to set scheme and credentials for " + request);
            }
        } else {
        	log.warn("context's authState attribute (" + authState + ") has non-null AuthScheme for request " + request);
        }

    }

}