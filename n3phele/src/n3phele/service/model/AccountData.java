package n3phele.service.model;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import n3phele.service.model.core.Entity;

@XmlRootElement(name="AccountData")
@XmlType(name="AccountData", propOrder={"accountName","cost","actives","cloud","uriAccount"})
public class AccountData extends Entity{
	private String accountName;
	private String cost;
	private String actives;
	private String cloud;
	private String uriAccount;

	public AccountData(String accountName, String cost, String actives, String cloud,String uriAccount){
		this.actives = actives;
		this.accountName = accountName;
		this.cost = cost;
		this.cloud = cloud;
		this.uriAccount = uriAccount;
	}
	
	public AccountData(){
		
	}

	public String getUriAccount() {
		return this.uriAccount;
	}

	public void setUriAccount(String uriAccount) {
		this.uriAccount = uriAccount;
	}
	
	public String getCloud() {
		return this.cloud;
	}

	public void setCloud(String cloud) {
		this.cloud = cloud;
	}

	public String getAccountName() {
		return this.accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getCost() {
		return this.cost;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}

	public String getActives() {
		return this.actives;
	}
	
	public void setActives(String actives) {
		this.actives = actives;
	}

	@Override
	public String toString() {
		return "AccountData [accountName=" + this.accountName + ", cost=" + this.cost + ", actives=" + this.actives + ", cloud=" + this.cloud + "]";
	}

}
