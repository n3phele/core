package n3phele.client.model;

import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;

public class CloudProcessSummary extends Entity {
	protected CloudProcessSummary() {}

	/**
	 * @return the state
	 */
	public native final String getState() /*-{
		return this.state;
	}-*/;

	/**
	 * @return the actionType
	 */
	public native final String getActionType() /*-{
		return this.actionType;
	}-*/;
	/**
	 * @return the narrative
	 */
	public final List<Narrative> getNarrative() {
		JavaScriptObject jsa = narrative();
		return JsList.asList(jsa);
	}
	private native final JavaScriptObject narrative() /*-{
	var array = [];
	if(this.narrative != undefined && this.narrative!=null) {
		if(this.narrative.length==undefined) {
			array[0] = this.narrative;
		} else {
			array = this.narrative;
		}
	}
	return array;

	}-*/;
	


	public static final native Collection<CloudProcessSummary> asCollection(String assumedSafe) /*-{
	return eval("("+assumedSafe+")");
	// return JSON.parse(assumedSafe);
	}-*/;
	public static final native CloudProcessSummary asCloudProcessSummary(String assumedSafe) /*-{
	return eval("("+assumedSafe+")");
	// return JSON.parse(assumedSafe);
	}-*/;
	
}
