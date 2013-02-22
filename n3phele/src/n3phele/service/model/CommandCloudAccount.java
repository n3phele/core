package n3phele.service.model;

import java.net.URI;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import n3phele.service.model.core.Helpers;

@XmlRootElement(name="CommandCloudAccount")
@XmlType(name="CommandCloudAccount", propOrder={"accountName", "cloudName", "accountUri"})
public class CommandCloudAccount {
	private String accountName;
	private String cloudName;
	private String accountUri;
	
	public CommandCloudAccount() {}
	
	/**
	 * @param accountName
	 * @param cloudName
	 * @param accountUri
	 */
	public CommandCloudAccount(String accountName, String cloudName,
			URI accountUri) {
		super();
		this.accountName = accountName;
		this.cloudName = cloudName;
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
	 * @return the cloudName
	 */
	public String getCloudName() {
		return cloudName;
	}
	/**
	 * @param cloudName the cloudName to set
	 */
	public void setCloudName(String cloudName) {
		this.cloudName = cloudName;
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
