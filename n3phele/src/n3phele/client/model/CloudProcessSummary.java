package n3phele.client.model;

import java.util.Date;
import java.util.List;

import n3phele.client.presenter.helpers.SafeDate;

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
	
	public final Date getComplete() {
		return SafeDate.parse(complete());
	}
	

	public native final String complete() /*-{
		return this.complete;
	}-*/;
	
	
	public native final String epoch() /*-{
		return this.epoch;
	}-*/;
	private native final String getCost2() /*-{
		return this.costPerHour;
	}-*/;
	public final double getCost(){
		return Double.parseDouble(getCost2());
	};
	
	public final Date getEpoch() {
		return SafeDate.parse(epoch());
	}

	public static final native Collection<CloudProcessSummary> asCollection(String assumedSafe) /*-{
		return eval("("+assumedSafe+")");
	// return JSON.parse(assumedSafe);
	}-*/;
	public static final native CloudProcessSummary asCloudProcessSummary(String assumedSafe) /*-{
		return eval("("+assumedSafe+")");
	// return JSON.parse(assumedSafe);
	}-*/;
	
}
