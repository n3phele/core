/*
 * (C) Copyright 2010-2013. Nigel Cook. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * 
 * Adapted from Luis Gervaso woorea project.
 * 
 * Licensed under the terms described in LICENSE file that accompanied this code, (the "License"); you may not use this file
 * except in compliance with the License. 
 * 
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on 
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the 
 *  specific language governing permissions and limitations under the License.
 *
 */
package n3phele.storage.swift;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRootElement;

import n3phele.storage.swift.Authentication.ApiAccessKeyCredentials;
import n3phele.storage.swift.Authentication.PasswordCredentials;

import com.sun.jersey.api.client.WebResource;

public class Authenticate {
	
	private Authentication authentication;
	
	public Authenticate(Authentication authentication) {
		this.authentication = authentication;
	}
	
	public static Authenticate withPasswordCredentials(String username, String password) {
		Authentication authentication = new Authentication();
		PasswordCredentials passwordCredentials = new PasswordCredentials();
		passwordCredentials.setUsername(username);
		passwordCredentials.setPassword(password);
		authentication.setPasswordCredentials(passwordCredentials);
		return new Authenticate(authentication);
	}
	
	public static Authenticate withToken(String id) {
		Authentication authentication = new Authentication();
		Authentication.Token token = new Authentication.Token();
		token.setId(id);
		authentication.setToken(token);
		return new Authenticate(authentication);
	}
	
	public static Authenticate withApiAccessKeyCredentials(String accessKey, String secretKey) {
		Authentication authentication = new Authentication();
		ApiAccessKeyCredentials passwordCredentials = new ApiAccessKeyCredentials();
		passwordCredentials.setAccessKey(accessKey);
		passwordCredentials.setSecretKey(secretKey);
		authentication.setApiAccessKeyCredentials(passwordCredentials);
		return new Authenticate(authentication);
	}
	
	public Authenticate withTenantId(String tenantId) {
		authentication.setTenantId(tenantId);
		return this;
	}
	
	public Authenticate withTenantName(String tenantName) {
		authentication.setTenantName(tenantName);
		return this;
	}

	public Access getAccess(WebResource target) {
		WrappedAccess wrapped = target.path("/tokens").type(MediaType.APPLICATION_JSON_TYPE).accept(MediaType.APPLICATION_JSON).post(WrappedAccess.class, new WrappedAuthentication(authentication));
		if(wrapped != null)
			return wrapped.access;
		return null;
	}

	@XmlRootElement
	public static class WrappedAuthentication extends Authentication {
		public WrappedAuthentication() {}
		WrappedAuthentication(Authentication auth) {
			this.auth = auth;
		}
		public Authentication auth;
	}
	
	@XmlRootElement(name="access")
	public static class WrappedAccess  {
		public WrappedAccess() {}
		WrappedAccess(Access access) {
			this.access = access;
		}
		public Access access;
	}
}
