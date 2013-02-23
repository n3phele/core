package n3phele.client.model;

import java.util.Date;

import n3phele.client.presenter.helpers.SafeDate;
import n3phele.client.model.Activity;

public class VirtualServer extends Entity {
	protected VirtualServer() {
	}

	public static final native String getDescription() /*-{
		return this.description;
	}-*/;

	public final Date getCreated() {
		return SafeDate.parse(created());
	}

	protected final native String created() /*-{
		return this.created;
	}-*/;
	
	public final native String getStatus() /*-{
		return this.status;
	}-*/;

	public final Date getEndDate() {
		return SafeDate.parse(endDate());
	}

	protected final native String endDate() /*-{
		return this.endDate;
	}-*/;

	public final native String getActivity() /*-{
		return this.activity;
	}-*/;

	/*
	 * public final String getPrice(){ return price(); }
	 */

	public final native String getPrice() /*-{
		return this.price;
	}-*/;

	public static final native VirtualServerCollection<VirtualServer> asCollection(
			String assumedSafe) /*-{
		return eval("(" + assumedSafe + ")");
		// return JSON.parse(assumedSafe);
	}-*/;

	public static final native VirtualServer asVirtualServer(String assumedSafe) /*-{
		return eval("(" + assumedSafe + ")");
		// return JSON.parse(assumedSafe);
	}-*/;
	
	public static final native String CloudURI() /*-{
	return this.clouduri;
}-*/;
	

}
