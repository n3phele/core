/**
 * @author Nigel Cook
 *
 * (C) Copyright 2010-2011. All rights reserved.
 */
package n3phele.service.model.core;

import java.net.URI;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="ExecutionFactoryCreateRequest")
@XmlType(name="ExecutionFactoryCreateRequest", propOrder={"name", "description", "location", "parameters", "notification", "accessKey", "encryptedSecret", "owner", "idempotencyKey"})
public class ExecutionFactoryCreateRequest {
	public String name;
	public String description;
	public URI location;
	public ArrayList<NameValue> parameters; 
	public URI notification;
	public String accessKey;
	public String encryptedSecret;
	public URI owner;
	public String idempotencyKey;
	
	public ExecutionFactoryCreateRequest() {}
	
}
