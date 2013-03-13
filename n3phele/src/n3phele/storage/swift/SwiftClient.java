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

import java.io.ByteArrayInputStream;
import java.io.SequenceInputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRootElement;

import n3phele.service.core.NotFoundException;
import n3phele.storage.swift.Access.Token;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.client.filter.LoggingFilter;


public class SwiftClient {
	private final static Client client;
	static {
		client = Client.create();
		client.addFilter(new LoggingFilter(Logger.getLogger("swift")));
	}
	private WebResource resource;
	private Token token;
	private String endpoint;
	
	public URI getTarget() {
		return URI.create(endpoint);
	}
	
	public SwiftClient(Access access, String region) {
		endpoint = findEndpointURL(access.getServiceCatalog(), "object-store", region, "public");
		resource = client.resource(endpoint);
		token = access.getToken();
		
		
		resource.addFilter(new ClientFilter(){

			@Override
			public ClientResponse handle(ClientRequest cr)
					throws ClientHandlerException {
				cr.getHeaders().putSingle("X-Auth-Token", token.getId());
				return getNext().handle(cr);
			}});
	}

	public static String findEndpointURL(List<Access.Service> serviceCatalog, String type, String region, String facing) {
		for(Access.Service service : serviceCatalog) {
			if(type.equals(service.getType())) {
				for(Access.Service.Endpoint endpoint : service.getEndpoints()) {
					if(region == null || region.equals(endpoint.getRegion())) {
						if(endpoint.getPublicURL() != null && facing.equals("public")) {
							return endpoint.getPublicURL();
						} else if(endpoint.getInternalURL() != null && facing.equals("internal")) {
							return endpoint.getInternalURL();
						} else if(endpoint.getAdminURL() != null && facing.equals("admin")) {
							return endpoint.getAdminURL();
						}
					}
				}
			}
		}
		throw new NotFoundException("endpoint url not found");
	}
	
	public boolean createContainer(String containerName, String... args) {
		Builder builder = resource.path(containerName).getRequestBuilder();
		if(args != null && args.length!=0) {
			for(int i=1; i < args.length; i+=2) {
				builder = builder.header(args[i-1], args[i]);
			}
		}
		ClientResponse response = builder.put(ClientResponse.class);
		return response.getStatus() == 201;
		
	}
	
	
	public ClientResponse getObjectMetadata(String containerName,
			String filename) throws NotFoundException {
		ClientResponse response = resource.path(containerName).path(filename).accept(MediaType.WILDCARD_TYPE).head();
		if(response.getStatus() != 200)
			throw new NotFoundException("Container "+containerName+" object "+filename);
		return response;
	}
	
	public ClientResponse getContainerMetadata(String containerName) throws NotFoundException {
		ClientResponse response = resource.path(containerName).accept(MediaType.WILDCARD_TYPE).head();
		if(response.getStatus() != 204)
			throw new NotFoundException("Container "+containerName);
		return response;
	}

	public boolean removeObject(String containerName, String file) {
		ClientResponse response = resource.path(containerName).path(file).delete(ClientResponse.class);
		return response.getStatus() == 204;
	}
	List<SwiftObject> listObjects(String container, Map<String, String> params) {
		
		WebResource target = resource.path(container);
		/*
		 * I hate this so much
		 */
		target.addFilter(new ClientFilter(){

			@Override
			public ClientResponse handle(ClientRequest cr)
					throws ClientHandlerException {
				ClientResponse resp = getNext().handle(cr);
				SequenceInputStream in = new SequenceInputStream(new ByteArrayInputStream("{\"objectList\":".getBytes()), resp.getEntityInputStream());
				resp.setEntityInputStream(new SequenceInputStream(in, new ByteArrayInputStream("}".getBytes())));
				
				return resp;
			}});


		for(String filter : new String[]{"prefix","delimiter","path","marker", "limit"}) {
			if(params.get(filter) != null) {
				target = target.queryParam(filter, params.get(filter));
			}
		}
		ObjectListWrapper olw = target.accept(MediaType.APPLICATION_JSON).get(ObjectListWrapper.class);
		if(olw == null)
			return null;
		else
			return olw.getObjectList();
	}
	
	@XmlRootElement
	public static class ObjectListWrapper {
		public ObjectListWrapper() {}
		
		public List<SwiftObject> objectList;
		
		List<SwiftObject> getObjectList() {
			return this.objectList;
		}
	}
	
}
