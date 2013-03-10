/**
 * @author Nigel Cook
 *
 * (C) Copyright 2010-2012. Nigel Cook. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * 
 * Licensed under the terms described in LICENSE file that accompanied this code, (the "License"); you may not use this file
 * except in compliance with the License. 
 * 
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on 
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the 
 *  specific language governing permissions and limitations under the License.
 */
package n3phele.service.rest.impl;

import java.security.Principal;
import java.util.logging.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import n3phele.service.core.AuthenticationException;
import n3phele.service.core.ForbiddenException;
import n3phele.service.core.Resource;
import n3phele.service.model.core.User;

import com.sun.jersey.api.container.MappableContainerException;
import com.sun.jersey.core.util.Base64;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

public class BasicSecurityFilter implements ContainerRequestFilter {
	private final static Logger log = Logger.getLogger(BasicSecurityFilter.class.getName()); 
	@SuppressWarnings("unused")
	private final static User root = UserResource.Root; // forces root creation before other db access
    @Context UriInfo uriInfo;
    @Context SecurityContext securityContext;
    private static final String REALM = "N3phele user authentication";

    public ContainerRequest filter(ContainerRequest request) {
    	if(securityContext == null || securityContext.getUserPrincipal() == null) {
	        User user = authenticate(request);
	        request.setSecurityContext(new Authorizer(user));
    	} else {
    		log.info(String.format("Security context exists for %s", securityContext.getUserPrincipal().getName()));
    	}
        return request;
    }

    private User authenticate(ContainerRequest request) {
        // Extract authentication credentials
        String authentication = request.getHeaderValue(ContainerRequest.AUTHORIZATION);
        if (authentication == null) {
        	log.info("Authentication against path "+request.getPath());
        	if(_isSecure() && !"user/login".equals(request.getPath())) {
        		log.warning("No authentication credentials supplied. Path is "+request.getPath());
	            throw new MappableContainerException
	                    (new AuthenticationException("Authentication credentials are required", REALM));
        	} else {
        		return null;
        	}
        }
        if (!authentication.startsWith("Basic ")) {
            return null;
            // additional checks should be done here
            // "Only HTTP Basic authentication is supported"
        }
        
        int end = authentication.indexOf(',');
        if(end > 0) {
        	log.severe("Authentication is "+request.getHeaderValue(ContainerRequest.AUTHORIZATION));
        	log.severe("end is "+end);
        	authentication = authentication.substring(0,end);
        	log.severe("trimmed authentication is "+authentication);
        }
        
        String uidpassword = Base64.base64Decode(authentication.substring("Basic ".length()));
        int idx = uidpassword.indexOf(':');
        if(idx < 0) {
        	throw new WebApplicationException(400); // "Invalid syntax for username and password"
        }
        
       
        String username = uidpassword.substring(0,idx).replace(".at-.", "@");
        String password = uidpassword.substring(idx+1);
        if ((username == null) || (password == null)) {
            throw new WebApplicationException(400);
            // "Missing username or password"
        }

        // Validate the extracted credentials
        User user=null;
        try {
        	user = UserResource.dao.load(username, UserResource.Root);
        } catch (Exception badUser) {
        	
        }
        if(user == null && (username .equals("signup") && password.equals("newuser"))) {
        	user = signupUser;
        } else if(user == null || !user.getCredential().decrypt().getSecret().equals(password)) {
        	log.severe("Invalid username or password user='"+username+"' password='"+password+"'");
        	log.severe("Authorization header is '"+request.getHeaderValue(ContainerRequest.AUTHORIZATION)+"'");
            throw new MappableContainerException(new ForbiddenException("Invalid username or password"));
        }
        return user;
    }
    private boolean _isSecure() {
    	return "https".equals(uriInfo.getRequestUri().getScheme()) || !production;
    }
    
    public class Authorizer implements SecurityContext {

        private User user;

        public Authorizer(final User user) {
            this.user = user;
        }

        public Principal getUserPrincipal() {
            return this.user;
        }

        public boolean isUserInRole(String role) {
        	log.info("Checking role "+role+" with user "+(user==null?"null":user.getName()));
        	if(user == null && role.equals("admin")) return true;  // assumes google admin enforcement for cron and tasks
        	if(user == null) {
        		log.warning("Null user checking in role");
        		throw new MappableContainerException (new ForbiddenException("Authentication credentials are required"));
        	}
        	if("authenticated".equals(role)) return user != null && user != signupUser;
        	if("administrator".equals(role)) return user.isAdmin();
        	if("root".equals(role)) return user.getId() == UserResource.Root.getId();
        	if("signup".equals(role)) return user == signupUser;
        	log.severe("Checking for role "+role);
        	return false;
        }

        public boolean isSecure() {
            return _isSecure();
        }

        public String getAuthenticationScheme() {
            return SecurityContext.BASIC_AUTH;
        }
    }
   
    private final static User signupUser = new User("signup", "signup", "signup", "newuser");
    private final boolean production = Resource.get("production", false); 
    
}