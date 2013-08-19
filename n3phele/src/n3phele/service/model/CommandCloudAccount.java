package n3phele.service.model;

import java.net.URI;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import n3phele.service.model.core.Entity;
import n3phele.service.model.core.Helpers;

@XmlRootElement(name="CommandCloudAccount")
@XmlType(name="CommandCloudAccount", propOrder={"accountName", "implementation", "accountUri"})
public class CommandCloudAccount extends Entity{
	private String accountName;
	private String implementation;
	private String accountUri;
	
	public CommandCloudAccount() {}
	
	/**
	 * @param accountName
	 * @param implementation
	 * @param accountUri
	 */
	public CommandCloudAccount(String implementation, String accountName, 
			URI accountUri) {
		super();
		this.accountName = accountName;
		this.implementation = implementation;
		this.accountUri = Helpers.URItoString(accountUri);
	}





	/**
	 * @return the accountName
	 */
	public String getAccountName() {
		return accountName;
	}
	/**
	 * @param accountName the accountName to set
	 */
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	/**
	 * @return the implementation
	 */
	public String getImplementation() {
		return implementation;
	}
	/**
	 * @param implementation the implementation to set
	 */
	public void setImplementation(String implementation) {
		this.implementation = implementation;
	}
	/**
	 * @return the accountUri
	 */
	public URI getAccountUri() {
		return Helpers.stringToURI(accountUri);
	}
	/**
	 * @param accountUri the accountUri to set
	 */
	public void setAccountUri(URI accountUri) {
		this.accountUri = Helpers.URItoString(accountUri);
	}
	
	
	
	
	
}
