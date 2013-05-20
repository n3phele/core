package n3phele.service.model;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import n3phele.service.model.core.Entity;

@XmlRootElement(name="ActivityDataCollection")
@XmlType(name="Collection", propOrder={"total", "elements"})
public class ActivityDataCollection extends Entity{
	private int total;
	private List<ActivityData> elements;
	
	public ActivityDataCollection(List<ActivityData> e) {
		elements = e;
		total = e.size();
	}
	
	public ActivityDataCollection(){
		
	}
	
	public int getTotal() {
		return this.total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public List<ActivityData> getElements() {
		return this.elements;
	}
	public void setElements(List<ActivityData> elements) {
		this.elements = elements;
	}
}
