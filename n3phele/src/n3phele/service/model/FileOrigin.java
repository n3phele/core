package n3phele.service.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
@XmlRootElement(name="FileOrigin")
@XmlType(name="Task", propOrder={"canonicalName", "length", "modified", "process"})
public class FileOrigin {
	
	@Id private String canonicalName;
	private String process;
	private long length;
	private Date modified;

	public FileOrigin(String canonicalName, long length, Date modified, String process) {
		this.canonicalName = createKey(canonicalName);
		this.process = process;
		this.length = length;
		this.modified = modified;
	}

	public static String createKey(String canonicalPath) {
		String path = canonicalPath;
		if(path.length()> 255) {
			String hash = Long.toString(path.hashCode());
			path = hash+path.substring((path.length()+hash.length())-255);
		}
		
		return path;
	}

	/**
	 * @return the canonicalName
	 */
	public String getCanonicalName() {
		return canonicalName;
	}

	/**
	 * @param canonicalName the canonicalName to set
	 */
	public void setCanonicalName(String canonicalName) {
		this.canonicalName = canonicalName;
	}

	/**
	 * @return the process
	 */
	public String getProcess() {
		return process;
	}

	/**
	 * @param process the process to set
	 */
	public void setProcess(String process) {
		this.process = process;
	}

	/**
	 * @return the length
	 */
	public long getLength() {
		return length;
	}

	/**
	 * @param length the length to set
	 */
	public void setLength(long length) {
		this.length = length;
	}

	/**
	 * @return the modified
	 */
	public Date getModified() {
		return modified;
	}

	/**
	 * @param modified the modified to set
	 */
	public void setModified(Date modified) {
		this.modified = modified;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String
				.format("FileOrigin [canonicalName=%s, process=%s, length=%s, modified=%s]",
						canonicalName, process, length, modified);
	}
	
	
}
