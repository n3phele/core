package n3phele.service.model;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import n3phele.service.core.NotFoundException;
import n3phele.service.model.core.Entity;
import n3phele.service.model.core.Helpers;
import n3phele.service.rest.impl.NarrativeResource;

@XmlRootElement(name="CloudProcessSummary")
@XmlType(name="CloudProcess", propOrder={"state", "narrative"})
public class CloudProcessSummary extends Entity {
	private ActionState state = ActionState.NEWBORN;
	private Narrative[] narrative;
	
	public CloudProcessSummary() {}
	
	public CloudProcessSummary(CloudProcess full) {
		this.name = full.getName();
		this.uri = Helpers.URItoString(full.getUri());
		this.isPublic = full.getPublic();
		this.owner = full.getOwner().toString();
		this.state = full.getState();
		try {
			this.narrative = new Narrative[] {NarrativeResource.dao.getLastNarrative(full.getUri()) };
		} catch (NotFoundException e) {
			this.narrative = new Narrative[0];
		}
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