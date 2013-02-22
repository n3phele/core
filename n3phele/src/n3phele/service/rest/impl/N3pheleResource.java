package n3phele.service.rest.impl;
/**
 * (C) Copyright 2010-2013. Nigel Cook. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * 
 * Licensed under the terms described in LICENSE file that accompanied this code, (the "License"); you may not use this file
 * except in compliance with the License. 
 * 
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on 
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the 
 *  specific language governing permissions and limitations under the License.
 */
import java.util.logging.Logger;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import n3phele.service.model.ChangeGroup;
import n3phele.service.model.ChangeManager;
import n3phele.service.model.Root;
import n3phele.service.model.core.User;

@Path("/")
public class N3pheleResource {
	private static Logger log = Logger.getLogger(N3pheleResource.class.getName()); 


	@Context UriInfo uriInfo;
	@Context SecurityContext securityContext;
	
	public N3pheleResource() {
	}
	
	

	@GET
	@Produces({"application/json"})
	@RolesAllowed("authenticated")
	public Root list( @QueryParam("since") Long since) {
		User user = UserResource.toUser(securityContext);
		log.warning("getN3phele entered since "+since+" for user "+user.getName());
		Root root = new Root();

		if(since == null || since == 0) { 
			since = ChangeManager.factory().initCache();
		}
		ChangeGroup changes = ChangeManager.factory().getChanges(since, user.getUri(), user.isAdmin());
		if(changes != null) {
			root.setStamp(changes.getStamp());
			root.setCacheAvailable(true); 
			root.setChangeGroup(changes);
			if(changes.getChange() != null) 
				root.setChangeCount(changes.getChange().size());
		} else {
			root.setStamp(ChangeManager.factory().initCache());
		}
		log.info("Change is "+root.getChangeGroup());
		return root;
	}
}

