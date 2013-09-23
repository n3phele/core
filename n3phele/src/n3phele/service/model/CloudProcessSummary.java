package n3phele.service.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import n3phele.service.core.NotFoundException;
import n3phele.service.model.core.Entity;
import n3phele.service.model.core.Helpers;
import n3phele.service.rest.impl.NarrativeResource;

@XmlRootElement(name="CloudProcessSummary")
@XmlType(name="CloudProcessSummary", propOrder={"state", "narrative", "costPerHour", "epoch", "start", "complete"})
public class CloudProcessSummary extends Entity {
	private ActionState state = ActionState.NEWBORN;
	private Collection<Narrative> narrative;
	private double costPerHour;
	private Date epoch;
	private Date start;
	private Date complete;

	public CloudProcessSummary() {}

	public CloudProcessSummary(CloudProcess full) {
		this.name = full.getName();
		this.uri = Helpers.URItoString(full.getUri());
		this.isPublic = full.getPublic();
		this.owner = full.getOwner().toString();
		this.state = full.getState();
		this.costPerHour = full.getCostPerHour();
		this.epoch = full.getEpoch();
		this.start = full.getStart();
		this.complete = full.getComplete();

		this.narrative = new ArrayList<Narrative>();
		try {
			this.narrative.add(NarrativeResource.dao.getLastNarrative(full.getUri()));
		} catch (NotFoundException e) {

		}
	}

	/**
	 * @return the state
	 */
	@XmlElement
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
	@XmlElement
	public Collection<Narrative> getNarrative() {
		return narrative;
	}

	/**
	 * @param narrative the narrative to set
	 */
	public void setNarrative(Collection<Narrative> narrative) {
		this.narrative = narrative;
	}

	@XmlElement
	public double getCostPerHour() {
		return this.costPerHour;
	}

	public void setCostPerHour(double costPerHour) {
		this.costPerHour = costPerHour;
	}

	@XmlElement
	public Date getStart() {
		return this.start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	@XmlElement
	public Date getComplete() {
		return this.complete;
	}

	public void setComplete(Date complete) {
		this.complete = complete;
	}

	@XmlElement
	public Date getEpoch() {
		return this.epoch;
	}

	public void setEpoch(Date epoch) {
		this.epoch = epoch;
	}

}