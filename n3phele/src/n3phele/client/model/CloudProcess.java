package n3phele.client.model;

import java.util.Date;
import java.util.List;

import n3phele.client.presenter.helpers.SafeDate;

import com.google.gwt.core.client.JavaScriptObject;

public class CloudProcess extends Entity {
	protected CloudProcess() {}
	
	/**
	 * @return the state
	 */
	public native final String getState() /*-{
		return this.state;
	}-*/;
	
	/**
	 * @return the action
	 */
	public native final String getAction() /*-{
		return this.action;
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
	

	/**
	 * @return the description
	 */
	public native final String getDescription()  /*-{
		return this.description;
	}-*/;
	
	/**
	 * @return the start
	 */
	public final Date getStart() {
		return SafeDate.parse(start());
	}
	
	/**
	 * @return the start
	 */
	private native final String start() /*-{
		return this.start;
	}-*/;
	
	/**
	 * @return the completion date
	 */
	public final Date getComplete() {
		return SafeDate.parse(complete());
	}
	
	/**
	 * @return the completion date
	 */
	public native final String complete() /*-{
		return this.complete;
	}-*/;
	public native final String getCost2() /*-{
		return this.costPerHour;
	}-*/;
	public final double getCost(){
		return Double.parseDouble(getCost2());
	};
	
	public static final native CloudProcess asCloudProcess(String assumedSafe) /*-{
		return eval("("+assumedSafe+")");
		// return JSON.parse(assumedSafe);
	}-*/;
}
