/**
 * @author Nigel Cook
 *
 * (C) Copyright 2010-2011. All rights reserved.
 */
package n3phele.service.model.core;

import java.net.URI;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="ExecutionFactoryCreateRequest")
@XmlType(name="ExecutionFactoryCreateRequest", propOrder={"name", "description", "location", "notification", "accessKey", "encryptedSecret", "owner", "idempotencyKey", "locationId", "ipaddress"})
public class ExecutionFactoryAssimilateRequest {
	public String name;
	public String description;
	public URI location;
	public URI notification;
	public String accessKey;
	public String encryptedSecret;
	public URI owner;
	public String idempotencyKey;
	public String locationId;
	public String ipaddress;
	
	public ExecutionFactoryAssimilateRequest() {}
	
}
