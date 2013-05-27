package n3phele.service.model;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import n3phele.service.model.core.Entity;

@XmlRootElement(name="ActivityData")
@XmlType(name="ActivityData", propOrder={"uriTopLevel","cost","age","nameTop"})
public class ActivityData extends Entity{
	private String uriTopLevel;
	private String cost;
	private String age;
	private String nameTop;
	
	public ActivityData(String uri,String name, String uriTopLevel, String cost, String age, String nameTop) {
		this.uri = uri;
		this.name = name;
		this.age = age;
		this.uriTopLevel = uriTopLevel;
		this.cost = cost;
		this.nameTop = nameTop;
	}
	
	public ActivityData(){
		
	}


	public String getNameTop() {
		return this.nameTop;
	}

	public void setNameTop(String nameTop) {
		this.nameTop = nameTop;
	}

	public String getUriTopLevel() {
		return this.uriTopLevel;
	}

	public void setUriTopLevel(String uriTopLevel) {
		this.uriTopLevel = uriTopLevel;
	}

	public String getCost() {
		return this.cost;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}

	public String getAge() {
		return this.age;
	}
	
	public void setAge(String age) {
		this.age = age;
	}

	@Override
	public String toString() {
		return "ActivityData [uriTopLevel=" + this.uriTopLevel + ", cost=" + this.cost + ", age=" + this.age + ", nameTop=" + this.nameTop + "]";
	}

}
