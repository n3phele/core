/**
 * @author Nigel Cook
 *
 * (C) Copyright 2010-2012. Nigel Cook. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * 
 * Licensed under the terms described in LICENSE file that accompanied this code, (the "License"); you may not use this file
 * except in compliance with the License. 
 * 
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on 
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the 
 *  specific language governing permissions and limitations under the License.
 */
package n3phele.service.model;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlRootElement
@XmlType(name="ChangeGroup", propOrder={"stamp", "change"})
public class ChangeGroup {
	private long stamp;
	private ArrayList<Change> change;
	
	public ChangeGroup() {}
	
	/**
	 * @return the stamp
	 */
	public long getStamp() {
		return stamp;
	}
	/**
	 * @param stamp the stamp to set
	 */
	public void setStamp(long stamp) {
		this.stamp = stamp;
	}
	/**
	 * @return the change
	 */
	public ArrayList<Change> getChange() {
		return change;
	}
	/**
	 * @param change the change to set
	 */
	public void setChange(ArrayList<Change> change) {
		this.change = change;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final int maxLen = 20;
		return String.format(
				"ChangeGroup [stamp=%s, change=%s]",
				this.stamp,
				this.change != null ? this.change.subList(0,
						Math.min(this.change.size(), maxLen)) : null);
	}

}
