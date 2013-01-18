package n3phele.service.actions.tasks;
/**
 * (C) Copyright 2010-2013. Nigel Cook. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * 
 * Licensed under the terms described in LICENSE file that accompanied this code, (the "License"); you may not use this file
 * except in compliance with the License. 
 * 
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on 
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the 
 *  specific language governing permissions and limitations under the License.
 */

import java.util.HashMap;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.EntitySubclass;
import com.googlecode.objectify.annotation.Unindex;

import n3phele.service.model.Action;
import n3phele.service.model.SignalKind;
import n3phele.service.model.core.User;

@EntitySubclass
@XmlRootElement(name = "LogAction")
@XmlType(name = "LogAction", propOrder = { "count" })
@Unindex
@Cache
public class LogAction extends Action {
	final private static java.util.logging.Logger log = java.util.logging.Logger.getLogger(LogAction.class.getName()); 
	private int count = 5;
	
	public LogAction() {}
	
	protected LogAction(User owner, String name,
			HashMap<String, String> context) {
		super(owner, name, context);
	}

	@Override
	public boolean call() throws Exception {
		log.warning("Call "+count--);
		return count <= 0;
	}

	@Override
	public void cancel() {
		log.warning("Cancel");
		
	}

	@Override
	public void dump() {
		log.warning("Dump");
		
	}

	@Override
	public void init() throws Exception {
		log.warning("Init "+this.getContext().get("arg"));
		ActionLogger.name(this).info(this.getContext().get("arg"));
	}

	@Override
	public void signal(SignalKind kind, String assertion) {
		log.warning("Signal "+assertion);
		
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + count;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		LogAction other = (LogAction) obj;
		if (count != other.count)
			return false;
		return true;
	}

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param count the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("LogAction [count=%s, %s]", count,
				super.toString());
	}

	
	
}
