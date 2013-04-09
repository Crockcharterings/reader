package com.sismics.reader.rest.resource;

import java.security.Principal;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.codehaus.jettison.json.JSONException;

import com.sismics.reader.rest.constant.BaseFunction;
import com.sismics.rest.exception.ForbiddenClientException;
import com.sismics.security.IPrincipal;
import com.sismics.security.UserPrincipal;
import com.sismics.util.filter.TokenBasedSecurityFilter;

/**
 * Base class of REST resources.
 * 
 * @author jtremeaux
 */
public abstract class BaseResource {
    /**
     * Injects the HTTP request.
     */
    @Context
    protected HttpServletRequest request;
    
    /**
     * Application key.
     */
    @QueryParam("app_key")
    protected String appKey;
    
    /**
     * Principal of the authenticated user.
     */
    protected IPrincipal principal;

    /**
     * This method is used to check if the user is authenticated.
     * 
     * @return True if the user is authenticated and not anonymous
     */
    protected boolean authenticate() {
        Principal principal = (Principal) request.getAttribute(TokenBasedSecurityFilter.PRINCIPAL_ATTRIBUTE);
        if (principal != null && principal instanceof IPrincipal) {
            this.principal = (IPrincipal) principal;
            return !this.principal.isAnonymous();
        } else {
            return false;
        }
    }
    
    /**
     * Checks if the user has a base function.
     * 
     * @param baseFunction Base function to check
     * @throws JSONException
     */
    protected void checkBaseFunction(BaseFunction baseFunction) throws JSONException {
        if (principal == null || !(principal instanceof UserPrincipal)) {
            throw new ForbiddenClientException();
        }
        Set<String> baseFunctionSet = ((UserPrincipal) principal).getBaseFunctionSet();
        if (baseFunctionSet == null || !(baseFunctionSet.contains(baseFunction.name()))) {
            throw new ForbiddenClientException();
        }
    }
}
