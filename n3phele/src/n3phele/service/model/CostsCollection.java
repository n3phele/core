package n3phele.service.model;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import n3phele.service.model.core.Entity;

@XmlRootElement(name="CostsCollection")
@XmlType(name="Costs", propOrder={"total", "elements"})
public class CostsCollection extends Entity{
	private int total;
	private List<Double> elements;
	
	public CostsCollection(List<Double> e) {
		elements = e;
		total = e.size();
	}
	
	public CostsCollection(){
		
	}
	
	public int getTotal() {
		return this.total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public List<Double> getElements() {
		return this.elements;
	}
	public void setElements(List<Double> elements) {
		this.elements = elements;
	}
}
