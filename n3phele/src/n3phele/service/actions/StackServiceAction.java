package n3phele.service.actions;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import n3phele.service.core.NotFoundException;
import n3phele.service.model.Action;
import n3phele.service.model.CloudProcess;
import n3phele.service.model.Context;
import n3phele.service.model.Relationship;
import n3phele.service.model.SignalKind;
import n3phele.service.model.Stack;
import n3phele.service.model.core.Credential;
import n3phele.service.model.core.Entity;
import n3phele.service.model.core.User;
import n3phele.service.rest.impl.ActionResource;
import n3phele.service.rest.impl.CloudProcessResource;

import com.google.appengine.api.datastore.Text;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Embed;
import com.googlecode.objectify.annotation.EntitySubclass;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Serialize;
import com.googlecode.objectify.annotation.Unindex;

@EntitySubclass(index = true)
@XmlRootElement(name = "StackServiceAction")
@XmlType(name = "StackServiceAction", propOrder = { "serviceDescription", "stacks", "relationships" })
@Unindex
@Cache
public class StackServiceAction extends ServiceAction {
	// generates the id of stacks
	private long stackNumber;
	private String serviceDescription;

	private List<String> adopted = new ArrayList<String>();
	@Embed
	private List<Stack> stacks = new ArrayList<Stack>();
	@Embed
	private List<Relationship> relationships = new ArrayList<Relationship>();

	public StackServiceAction() {
		stackNumber = 0;
	}

	public StackServiceAction(String description, String name, User owner, Context context) {
		super(owner, name, context);
		this.serviceDescription = description;
		stackNumber = 0;
	}

	/*
	 * Automatic Generated Methods
	 */
	@Override
	public String getDescription() {
		return "StackService " + this.getName();
	}

	public String getServiceDescription() {

		return this.serviceDescription;
	}

	public void setServiceDescription(String description) {
		this.serviceDescription = description;
	}

	public List<Stack> getStacks() {
		return this.stacks;
	}

	public void setStacks(List<Stack> stacks) {
		this.stacks = stacks;
	}

	public boolean addStack(Stack stack) {
		if(stack.getId() == -1)
		stack.setId(this.getNextStackNumber());
		return stacks.add(stack);
	}

	public List<Relationship> getRelationships() {
		return this.relationships;
	}

	public void setRelationships(List<Relationship> relationships) {
		this.relationships = relationships;
	}

	public boolean addRelationhip(Relationship relation) {
		return relationships.add(relation);
	}

	public long getNextStackNumber() {
		long id = stackNumber;
		stackNumber++;
		return id;
	}

	@Override
	public void cancel() {
		log.info("Cancelling " + stacks.size() + " stacks");
		for (Stack stack : stacks) {
			for (URI uri : stack.getVms()) {
				try {
					processLifecycle().cancel(uri);
				} catch (NotFoundException e) {
					log.severe("Not found: " + e.getMessage());
				}
			}
		}
		for (String vm : adopted) {
			try {
				processLifecycle().cancel(URI.create(vm));
			} catch (NotFoundException e) {
				log.severe("Not found: " + e.getMessage());
			}
		}
	}

	@Override
	public void dump() {
		log.info("Dumping " + stacks.size() + " stacks");
		for (Stack stack : stacks) {
			for (URI uri : stack.getVms()) {
				try {
					processLifecycle().dump(uri);
				} catch (NotFoundException e) {
					log.severe("Not found: " + e.getMessage());
				}
			}
		}
		for (String vm : adopted) {
			try {
				processLifecycle().cancel(URI.create(vm));
			} catch (NotFoundException e) {
				log.severe("Not found: " + e.getMessage());
			}
		}
	}

	@Override
	public String toString() {
		return "StackServiceAction [description=" + this.serviceDescription + ", stacks=" + this.stacks + ", relationships=" + this.relationships + ", idStack=" + this.stackNumber + ", context=" + this.context + ", name=" + this.name + ", uri=" + this.uri + ", owner=" + this.owner + ", isPublic="
				+ this.isPublic + "]";
	}

	@Override
	public void signal(SignalKind kind, String assertion) throws NotFoundException {
		boolean isStacked = false;
		Stack stacked = null;
		for (Stack s : stacks) {
			if (s.getDeployProcess() == null)
				continue;
			if (s.getDeployProcess().equals(assertion)) {
				isStacked = true;
				stacked = s;
				break;
			}
		}

		boolean isAdopted = this.adopted.contains(assertion);
		log.info("Signal " + kind + ":" + assertion);
		switch (kind) {
		case Adoption:
			URI processURI = URI.create(assertion);
			try {
				CloudProcess child = CloudProcessResource.dao.load(processURI);
				Action action = ActionResource.dao.load(child.getAction());
				log.info("Adopting child " + child.getName() + " " + child.getClass().getSimpleName());
				this.adopted.add(assertion);
				if (action instanceof AssimilateAction) {
					for (Stack s : stacks) {
						if (s.getId() == action.getContext().getLongValue("stackId")) {
							s.addVm(child.getUri());
						}
					}
				}
			} catch (Exception e) {
				log.info("Assertion is not a cloudProcess");
			}
			break;
		case Cancel:
			if (isStacked) {
				stacks.remove(stacked);
			} else if (isAdopted) {
				adopted.remove(assertion);
			}
			break;
		case Event:
			// URI processURI2 = URI.create(assertion);
			// CloudProcess p = CloudProcessResource.dao.load(processURI2);
			// for(Stack s: this.getStacks()){
			// if(s.getDeployProcess() == null) continue;
			// if(s.getDeployProcess().equals(p.getParent().toString())){
			// s.addVm(p.getUri());
			// }
			// }
			// log.warning("Ignoring event "+assertion);
			// break;
		case Failed:
			if (isStacked) {
				stacks.remove(stacked);
			} else if (isAdopted) {
				adopted.remove(assertion);
			}
			break;
		case Ok:
			log.info(assertion + " ok");
			break;
		default:
			return;
		}
		ActionResource.dao.update(this);
	}

}
