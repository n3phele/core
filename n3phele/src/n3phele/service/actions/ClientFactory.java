/**
 * @author Nigel Cook
 *
 * (C) Copyright 2010-2011. All rights reserved.
 */
package n3phele.service.actions;

import java.util.ArrayList;
import java.util.List;

import com.sun.jersey.api.client.Client;

/** Provides JAXB client reuse
 * @author Nigel Cook
 *
 */
public class ClientFactory {
	private static ClientFactory instance = null;
	private List<Client> pool;
	
	protected ClientFactory() {} {
		pool = new ArrayList<Client>();
	}
	
	private Client borrow() {
		Client client = null;
		synchronized (pool) {
			if(!pool.isEmpty())
				client = pool.remove(0);
		}
		if(client == null) {
			client = Client.create();
		} else {
			client.removeAllFilters();
		}
		return client;
	}
	
	private void payback(Client client) {
		synchronized (pool) {
			pool.add(client);
		}
	}
	
	public static Client create() {
		if(instance == null) {
			instance = new ClientFactory();
		}
		return instance.borrow();
	}
	
	public static void give(Client client) {
		if(instance == null) {
			instance = new ClientFactory();
		}
		instance.payback(client);
	}

}
