package n3phele.client.model;

import com.google.gwt.core.client.JavaScriptObject;

public class CommandCloudAccount extends Entity{
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
	
	public static final native Collection<CommandCloudAccount> asCollection(String assumedSafe) /*-{
		return eval("("+assumedSafe+")");
		// return JSON.parse(assumedSafe);
	}-*/;
	
}
