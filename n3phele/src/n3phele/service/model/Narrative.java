package n3phele.service.model;

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

import java.net.URI;
import java.text.DateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import n3phele.service.rest.impl.CloudProcessResource;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Unindex;

@XmlRootElement(name="Narrative")
@XmlType(name = "Narrative", propOrder = { "stamp", "state", "processUri", "tag", "text"}) 
@Entity
@Unindex
public class Narrative {
	@Id protected Long id;
	protected String tag;
	@Index protected long process;
	@Index protected long rootProcess;
	@Index protected String group;
	@Index protected Date stamp;
	protected NarrativeLevel state = NarrativeLevel.undefined;
	protected String text;
	private final static DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);

	
	public Narrative() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String
				.format("Narrative [id=%s, tag=%s, process=%s, rootProcess=%s, stamp=%s, state=%s, text=%s]",
						this.id, this.tag, this.process, this.rootProcess,
						this.stamp, this.state, this.text);
	}
	/**
	 * @return the id
	 */
	@XmlTransient
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the tag
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * @param tag the tag to set
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}

	/**
	 * @return the stamp
	 */
	public Date getStamp() {
		return stamp;
	}

	/**
	 * @param stamp the stamp to set
	 */
	public void setStamp(Date stamp) {
		this.stamp = stamp;
	}

	/**
	 * @return the state
	 */
	public NarrativeLevel getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(NarrativeLevel state) {
		this.state = state;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return this.text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		if(text == null || text.length() <= MAXMESSAGE)
			this.text = text;
		else {
			this.text = text.substring(0, MAXMESSAGE-3)+"...";
		}
	}
	static final int MAXMESSAGE=500*1024;

	/**
	 * @return the process
	 */
	@XmlTransient
	public long getProcess() {
		return this.process;
	}

	/**
	 * @param process the process to set
	 */
	public void setProcess(long process) {
		this.process = process;
	}
	
	/**
	 * @return the rootProcess
	 */
	@XmlTransient
	public long getRootProcess() {
		return this.rootProcess;
	}

	/**
	 * @param process the root process to set
	 */
	public void setRootProcess(long process) {
		this.rootProcess = process;
	}
	
	/**
	 * @return the log group
	 */
	@XmlTransient
	public URI getGroup() {
		if(this.group == null)
			return null;
		else
			return URI.create(CloudProcessResource.dao.path.toString()+"/"+this.group);
	}

	/**
	 * @param process the root process to set
	 */
	public void setGroup(URI process) {
		this.group = process==null?null:process.getPath().substring(process.getPath().lastIndexOf("/")+1);
	}
	
	@XmlElement(name="processUri")
	public String getProcessUri() {
		String uri = CloudProcessResource.dao.path.toString()+"/"+rootProcess;
		if(rootProcess != process)
			uri += "_"+process;
		return uri;
	}
	
	public void setProcessUri(URI processUri) {
		String uri = processUri.toString();
		String ids = uri.substring(uri.lastIndexOf("/")+1);
		long root;
		long id;

		int split = ids.indexOf('_');
		if(split == -1) {
			id = root = Long.valueOf(ids);
		} else {
			root = Long.valueOf(ids.substring(0,split));
			id = Long.valueOf(ids.substring(split+1));
		}
		this.process = id;
		this.rootProcess = root;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
		result = prime * result + (int) (this.process ^ (this.process >>> 32));
		result = prime * result
				+ (int) (this.rootProcess ^ (this.rootProcess >>> 32));
		result = prime * result
				+ ((this.stamp == null) ? 0 : this.stamp.hashCode());
		result = prime * result
				+ ((this.state == null) ? 0 : this.state.hashCode());
		result = prime * result
				+ ((this.tag == null) ? 0 : this.tag.hashCode());
		result = prime * result
				+ ((this.text == null) ? 0 : this.text.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Narrative other = (Narrative) obj;
		if (this.id == null) {
			if (other.id != null)
				return false;
		} else if (!this.id.equals(other.id))
			return false;
		if (this.process != other.process)
			return false;
		if (this.rootProcess != other.rootProcess)
			return false;
		if (this.stamp == null) {
			if (other.stamp != null)
				return false;
		} else if (!this.stamp.equals(other.stamp))
			return false;
		if (this.state != other.state)
			return false;
		if (this.tag == null) {
			if (other.tag != null)
				return false;
		} else if (!this.tag.equals(other.tag))
			return false;
		if (this.text == null) {
			if (other.text != null)
				return false;
		} else if (!this.text.equals(other.text))
			return false;
		return true;
	}

	
}
