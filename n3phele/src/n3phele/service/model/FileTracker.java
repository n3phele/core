package n3phele.service.model;

import java.net.URI;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import n3phele.service.model.core.Helpers;

@XmlRootElement(name="FileTracker")
@XmlType(name="FileTracker", propOrder={"name", "localName", "repo", "process"})

public class FileTracker {
	private String name;
	private String localName;
	private String repo;	// repo://root/path/path
	private String process;	// uri of action that created file
	
	public FileTracker() {}
	
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the repo
	 */
	public URI getRepo() {
		return Helpers.stringToURI(repo);
	}
	/**
	 * @param repo the repo to set
	 */
	public void setRepo(URI repo) {
		this.repo = Helpers.URItoString(repo);
	}
	/**
	 * @return the process
	 */
	public URI getProcess() {
		return Helpers.stringToURI(process);
	}
	/**
	 * @param process the process to set
	 */
	public void setProcess(URI process) {
		this.process = Helpers.URItoString(process);
	}

	/**
	 * @return the localName
	 */
	public String getLocalName() {
		return localName;
	}


	/**
	 * @param localName the localName to set
	 */
	public void setLocalName(String localName) {
		this.localName = localName;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String
				.format("FileTracker [name=%s, localName=%s, repo=%s, process=%s, toString()=%s]",
						name, localName, repo, process, super.toString());
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((localName == null) ? 0 : localName.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((process == null) ? 0 : process.hashCode());
		result = prime * result + ((repo == null) ? 0 : repo.hashCode());
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
		FileTracker other = (FileTracker) obj;
		if (localName == null) {
			if (other.localName != null)
				return false;
		} else if (!localName.equals(other.localName))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (process == null) {
			if (other.process != null)
				return false;
		} else if (!process.equals(other.process))
			return false;
		if (repo == null) {
			if (other.repo != null)
				return false;
		} else if (!repo.equals(other.repo))
			return false;
		return true;
	}
	
}

