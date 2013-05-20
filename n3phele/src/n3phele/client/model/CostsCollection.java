package n3phele.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.google.gwt.core.client.JavaScriptObject;

public class CostsCollection extends Entity {

	protected CostsCollection() {
	}

	public native final int getTotal() /*-{
		return this.total;
	}-*/;

	public native final JavaScriptObject getStringElements() /*-{
		var array = [];
		if (this.elements != undefined && this.elements != null) {
			if (this.elements.length == undefined) {
				array[0] = this.elements;
			} else {
				array = this.elements;
			}
		}
		return array;
	}-*/;
	
	public final List<Double> getElements() {
		JavaScriptObject jsa = getStringElements();
		List<Double> list = new ArrayList<Double>();
		String[] costs = jsa.toString().split(",");
		for (int i = 0; i < costs.length; i++) {
			list.add(Double.parseDouble(costs[i]));
		}
		return list;
	}

	public static final native Collection<CostsCollection> asCollection(String assumedSafe) /*-{
		return eval("(" + assumedSafe + ")");
		// return JSON.parse(assumedSafe);
	}-*/;

	public static final native List<Double> asList(String assumedSafe) /*-{
		return eval("(" + assumedSafe + ")");
		// return JSON.parse(assumedSafe);
	}-*/;

	public static final native CostsCollection asCostsCollection(String assumedSafe) /*-{
		return eval("(" + assumedSafe + ")");
		// return JSON.parse(assumedSafe);
	}-*/;
}
