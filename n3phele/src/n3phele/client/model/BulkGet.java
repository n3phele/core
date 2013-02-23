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
package n3phele.client.model;

import n3phele.client.presenter.helpers.AuthenticatedRequestFactory;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;

public class BulkGet {
	static void get(BulkGetRequest bulk, RequestCallback callback) {
		String url = "https://n3phele.appspot.com/resources/";
	    url = URL.encode(url);
		 // Send request to server and catch any errors.
	    RequestBuilder builder = AuthenticatedRequestFactory.newRequest(RequestBuilder.POST, url);
	    builder.setHeader("Content-type", "application/x-www-form-urlencoded");

	    try {
	      Request request = builder.sendRequest(bulk.toString(), callback);
	    } catch (RequestException e) {
	      //displayError("Couldn't retrieve JSON "+e.getMessage());
	    }
	}
	
	static ObjectBag decode(Response response) {
	if (200 == response.getStatusCode()) {
    	ObjectBag bag =  ObjectBag.parse(response.getText());
    	return bag;
      }
	return null;
	}

}
