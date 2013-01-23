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


import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.EntitySubclass;
import com.googlecode.objectify.annotation.Unindex;

import n3phele.service.model.Action;
import n3phele.service.model.Context;
import n3phele.service.model.SignalKind;
import n3phele.service.model.core.User;

@EntitySubclass
@XmlRootElement(name = "CountDownAction")
@XmlType(name = "CountDownAction", propOrder = { "count" })
@Unindex
@Cache
public class CountDownAction extends Action {
	final private static java.util.logging.Logger log = java.util.logging.Logger.getLogger(CountDownAction.class.getName()); 
	private int count = 99;
	
	public CountDownAction() {}
	
	protected CountDownAction(User owner, String name,
			Context context) {
		super(owner.getUri(), name, context);
	}

	@Override
	public boolean call() throws Exception {
		log.warning(this.name+" Call "+count);
		ActionLogger.name(this).info("Call "+count);
		if(this.getContext().getValue("arg").equals("throw"+count--)) {
			throw new IllegalArgumentException();
		}
		return count <= 0;
	}

	@Override
	public void cancel() {
		log.warning(this.name+" Cancel");
		count=1000;
		
	}

	@Override
	public void dump() {
		log.warning(this.name+" Dump");
		count=2000;
		
	}

	@Override
	public void init() throws Exception {
		log.warning("Init "+this.getContext().getValue("arg"));
		count = 5;
		if(this.getContext().getValue("arg").equals("throwInit")) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void signal(SignalKind kind, String assertion) {
		log.warning(this.name+" Signal "+kind+":"+assertion);
		
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
		CountDownAction other = (CountDownAction) obj;
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
