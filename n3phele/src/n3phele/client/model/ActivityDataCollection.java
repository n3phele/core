package n3phele.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.google.gwt.core.client.JavaScriptObject;

public class ActivityDataCollection extends Entity {

	protected ActivityDataCollection() {
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
	
	public final List<ActivityData> getElements(){
		return JsList.asList(getStringElements());
	}
	
//	public final List<ActivityData> getElements() {
//		JavaScriptObject jsa = getStringElements();
//		List<ActivityData> list = new ArrayList<ActivityData>();
//		System.out.println("jsa: " + jsa);
//		String[] costs = jsa.toString().split(",");
//		for (int i = 0; i < costs.length; i++) {
//			list.add(ActivityData.asActivityData(jsa[i]));
//		}
//		return list;
//	}

	public static final native Collection<ActivityDataCollection> asCollection(String assumedSafe) /*-{
		return eval("(" + assumedSafe + ")");
		// return JSON.parse(assumedSafe);
	}-*/;

	public static final native List<ActivityData> asList(String assumedSafe) /*-{
		return eval("(" + assumedSafe + ")");
		// return JSON.parse(assumedSafe);
	}-*/;

	public static final native ActivityDataCollection asActivityDataCollection(String assumedSafe) /*-{
		return eval("(" + assumedSafe + ")");
		// return JSON.parse(assumedSafe);
	}-*/;
}
