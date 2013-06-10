package n3phele.service.actions;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import n3phele.service.model.Action;
import n3phele.service.model.Context;
import n3phele.service.model.Relationship;
import n3phele.service.model.Stack;
import n3phele.service.model.core.Credential;
import n3phele.service.model.core.Entity;
import n3phele.service.model.core.User;

import com.google.appengine.api.datastore.Text;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Embed;
import com.googlecode.objectify.annotation.EntitySubclass;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Serialize;
import com.googlecode.objectify.annotation.Unindex;
@EntitySubclass
@XmlRootElement(name="StackServiceAction")
@XmlType(name="StackServiceAction", propOrder={"idStack","description","stacks","relationships"})
@Unindex
@Cache
public class StackServiceAction extends ServiceAction {
	private String description;
	
	@Embed private List<Stack> stacks = new ArrayList<Stack>();
	@Embed private List<Relationship> relationships = new ArrayList<Relationship>();
	private long idStack;
	

	public StackServiceAction(){}
	
	public StackServiceAction(String description,String name, User owner,Context context){
		super(owner, name, context);
		this.description = description;
		idStack = 0;
	}


	/*
	 * Automatic Generated Methods
	 */
	public String getDescription() {
		
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Stack> getStacks() {
		return this.stacks;
	}
	public void setStacks(List<Stack> stacks) {
		this.stacks = stacks;
	}
	
	public boolean addStack(Stack stack){
		stack.setId(idStack);
		idStack++;
		return stacks.add(stack);
	}
	public List<Relationship> getRelationships() {
		return this.relationships;
	}

	public void setRelationships(List<Relationship> relationships) {
		this.relationships = relationships;
	}
	public boolean addRelationhip(Relationship relation){
		return relationships.add(relation);
	}

	@Override
	public String toString() {
		return "StackServiceAction [description=" + this.description + ", stacks=" + this.stacks + ", relationships=" + this.relationships + ", idStack=" + this.idStack + ", context=" + this.context + ", name=" + this.name + ", uri=" + this.uri + ", owner=" + this.owner + ", isPublic="
				+ this.isPublic + "]";
	}
}
