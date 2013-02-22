package n3phele.service.model;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import n3phele.service.model.core.Entity;
import n3phele.service.model.core.Helpers;
import n3phele.service.rest.impl.NarrativeResource;

@XmlRootElement(name="CloudProcessSummary")
@XmlType(name="CloudProcess", propOrder={"state", "actionType", "narrative"})
public class CloudProcessSummary extends Entity {
	private ActionState state = ActionState.NEWBORN;
	private String actionType;
	private Narrative[] narrative;
	
	public CloudProcessSummary(CloudProcess full) {
		this.name = full.getName();
		this.uri = Helpers.URItoString(full.getUri());
		this.state = full.getState();
		this.actionType = full.getActionType();
		this.narrative = new Narrative[] {NarrativeResource.dao.getLastNarrative(full.getUri()) };
	}

	/**
	 * @return the state
	 */
	public ActionState getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(ActionState state) {
		this.state = state;
	}

	/**
	 * @return the actionType
	 */
	public String getActionType() {
		return actionType;
	}

	/**
	 * @param actionType the actionType to set
	 */
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	/**
	 * @return the narrative
	 */
	public Narrative[] getNarrative() {
		return narrative;
	}

	/**
	 * @param narrative the narrative to set
	 */
	public void setNarrative(Narrative[] narrative) {
		this.narrative = narrative;
	}
	
}