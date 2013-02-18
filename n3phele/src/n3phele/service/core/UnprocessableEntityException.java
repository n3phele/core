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
package n3phele.service.core;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class UnprocessableEntityException extends WebApplicationException {
	private static final long serialVersionUID = 1L;

	/**
     * Create a HTTP 422 Unprocessable Entity exception.
     */
    public UnprocessableEntityException() {
        super(Response.status(422).build());
    }

    /**
     * Create a HTTP 422 (Unprocessable Entity) exception.
     * @param message the description of the semantic error associated with the 422 response.
     */
    public UnprocessableEntityException(String message) {
        super(Response.status(422).
                entity(message).type("text/plain").build());
        myDetailMessage = message;
    }
    
    private String myDetailMessage;
    
    public String getMessage() {
    	return myDetailMessage;
    }
}