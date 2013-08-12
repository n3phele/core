package n3phele.client.model;

import java.util.List;

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
	

	public static final native Collection<ActivityDataCollection> asCollection(String assumedSafe) /*-{
		return eval("(" + assumedSafe + ")");
	}-*/;

	public static final native List<ActivityData> asList(String assumedSafe) /*-{
		return eval("(" + assumedSafe + ")");
	}-*/;

	public static final native ActivityDataCollection asActivityDataCollection(String assumedSafe) /*-{
		return eval("(" + assumedSafe + ")");
	}-*/;
}
