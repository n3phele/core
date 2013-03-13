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
package n3phele.client.presenter.helpers;

import n3phele.client.model.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestBuilder.Method;

public class AuthenticatedRequestFactory {
	private static String defaultUsername = null;
	private static String usernameNoAt = null;
	private static String defaultPassword = null;
	private static String base64;
	private static User authenticatedUser = null;
	private static boolean authenticated = false;
	private static int requests = 0;
	
	public static RequestBuilder newRequest(Method httpMethod, String url) {
		requests += 1;
		return newCacheManagerRequest(httpMethod, url);
	}
	
	public static int incrementalRequests() {
		int count = requests;
		requests = 0;
		return count;
	}
	
	public static RequestBuilder newCacheManagerRequest(Method httpMethod, String url) {
		if(!authenticated) {
			GWT.log("Attempts to build a request when no credentials present");
			return null;
		}
		RequestBuilder request = new RequestBuilder(httpMethod, url);
		request.setTimeoutMillis(35000);
		request.setHeader("Authorization", base64);
		request.setHeader("If-Modified-Since", "01 Jan 1970 00:00:00 GMT");
		//request.setUser(defaultUsername);
		//request.setPassword(defaultPassword);
		return request;
	}
	
	public static RequestBuilder request(Method httpMethod, String url, String user, String secret) {
		
		RequestBuilder request = new RequestBuilder(httpMethod, url);
		//request.setHeader("Authorization", "Basic " + Base64.encode(user + ":" + secret));
		request.setUser(user.replace("@", ".at-."));
		request.setPassword(secret);
		return request;
	}

	/**
	 * @return the defaultUsername
	 */
	public static String getDefaultUsername() {
		return defaultUsername;
	}


	/**
	 * @return the defaultPassword
	 */
	public static String getDefaultPassword() {
		return defaultPassword;
	}


	/**
	 * @return the authenticated
	 */
	public static boolean isAuthenticated() {
		return authenticated;
	}
	
	public static User getUser() {
		return authenticatedUser;
	}

	public static void setCredentials(String username, String password, User user) {
		authenticatedUser = user;
		defaultUsername = username;
		usernameNoAt = username.replace("@", ".at-.");
		defaultPassword = password;
		base64 = "Basic " + Base64.encode(usernameNoAt + ":" + password);
		authenticated = true;
	}
	
	public static class Base64 {

	    private static final String etab =
	        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";

	    
	    public static String encode(String data) {
	        StringBuffer out = new StringBuffer();
	        
	        int i = 0;
	        int r = data.length();
	        while (r > 0) {
	            byte d0, d1, d2;
	            byte e0, e1, e2, e3;
	            
	            d0 = (byte) data.charAt(i++); --r;
	            e0 = (byte) (d0 >>> 2);
	            e1 = (byte) ((d0 & 0x03) << 4);
	            
	            if (r > 0) {
	                d1 = (byte) data.charAt(i++); --r;
	                e1 += (byte) (d1 >>> 4);
	                e2 = (byte) ((d1 & 0x0f) << 2);
	            }
	            else {
	                e2 = 64;
	            }
	            
	            if (r > 0) {
	                d2 = (byte) data.charAt(i++); --r;
	                e2 += (byte) (d2 >>> 6);
	                e3 = (byte) (d2 & 0x3f);
	            }
	            else {
	                e3 = 64;
	            }
	            out.append(etab.charAt(e0));
	            out.append(etab.charAt(e1));
	            out.append(etab.charAt(e2));
	            out.append(etab.charAt(e3));
	        }

	        return out.toString();
	    }
	}
	
	

}
