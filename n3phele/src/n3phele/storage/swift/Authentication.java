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

public class Authentication  {
	
	public static final class Token {
		
		private String id;

		/**
		 * @return the id
		 */
		public String getId() {
			return id;
		}

		/**
		 * @param id the id to set
		 */
		public void setId(String id) {
			this.id = id;
		}
		
	}

	public static final class PasswordCredentials {
		
		private String username;
		
		private String password;

		/**
		 * @return the username
		 */
		public String getUsername() {
			return username;
		}

		/**
		 * @param username the username to set
		 */
		public void setUsername(String username) {
			this.username = username;
		}

		/**
		 * @return the password
		 */
		public String getPassword() {
			return password;
		}

		/**
		 * @param password the password to set
		 */
		public void setPassword(String password) {
			this.password = password;
		}
		
	}
	
	public static final class ApiAccessKeyCredentials {
		
		private String accessKey;
		
		private String secretKey;

		/**
		 * @return the accessKey
		 */
		public String getAccessKey() {
			return accessKey;
		}

		/**
		 * @param accessKey the accessKey to set
		 */
		public void setAccessKey(String accessKey) {
			this.accessKey = accessKey;
		}

		/**
		 * @return the secretKey
		 */
		public String getSecretKey() {
			return secretKey;
		}

		/**
		 * @param secretKey the secretKey to set
		 */
		public void setSecretKey(String secretKey) {
			this.secretKey = secretKey;
		}
		
	}
	
	private String tenantId;
	
	private String tenantName;
	
	private PasswordCredentials passwordCredentials;
	
	private ApiAccessKeyCredentials apiAccessKeyCredentials;
	
	private Token token;

	/**
	 * @return the tenantId
	 */
	public String getTenantId() {
		return tenantId;
	}

	/**
	 * @param tenantId the tenantId to set
	 */
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	/**
	 * @return the tenantName
	 */
	public String getTenantName() {
		return tenantName;
	}

	/**
	 * @param tenantName the tenantName to set
	 */
	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}

	/**
	 * @return the passwordCredentials
	 */
	public PasswordCredentials getPasswordCredentials() {
		return passwordCredentials;
	}

	/**
	 * @param passwordCredentials the passwordCredentials to set
	 */
	public void setPasswordCredentials(PasswordCredentials passwordCredentials) {
		this.passwordCredentials = passwordCredentials;
	}

	/**
	 * @return the apiAccessKeyCredentials
	 */
	public ApiAccessKeyCredentials getApiAccessKeyCredentials() {
		return apiAccessKeyCredentials;
	}

	/**
	 * @param apiAccessKeyCredentials the apiAccessKeyCredentials to set
	 */
	public void setApiAccessKeyCredentials(
			ApiAccessKeyCredentials apiAccessKeyCredentials) {
		this.apiAccessKeyCredentials = apiAccessKeyCredentials;
	}

	/**
	 * @return the token
	 */
	public Token getToken() {
		return token;
	}

	/**
	 * @param token the token to set
	 */
	public void setToken(Token token) {
		this.token = token;
	}
	
}
