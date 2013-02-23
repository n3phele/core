package n3phele.client.model;

import com.google.gwt.core.client.JavaScriptObject;

public class CommandCloudAccount extends JavaScriptObject {
	protected CommandCloudAccount() {}

	/**
	 * @return the accountName
	 */
	public native final String getAccountName() /*-{
		return this.accountName;
	}-*/;
	/**
	 * @return the cloudName
	 */
	public native final String getImplementation() /*-{
		return this.implementation;
	}-*/;
	/**
	 * @return the accountUri
	 */
	public native final String getAccountUri() /*-{
		return this.accountUri;
	}-*/;
	
	
	
}
