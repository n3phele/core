/**
 * @author Nigel Cook
 *
 * (C) Copyright 2010-2011. All rights reserved.
 */
package n3phele.service.model.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="NameValue")
@XmlType(name="NameValue", propOrder={"key", "value"})
public class NameValue {
	private String key;
	private String value;
	public NameValue() {}
	public NameValue(String key, String value) {
		this.key = key;
		this.value = value;
	}
	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}
	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	static public HashMap<String,String> toMap(List<NameValue> list) {
		HashMap<String,String> result = new HashMap<String,String>();
		for(NameValue i : list) {
			result.put(i.getKey(), i.getValue());
		}
		return result;
	}
	
	static public ArrayList<NameValue> toList(Map<String,String> map) {
		if(map == null) return new ArrayList<NameValue>();
		ArrayList<NameValue> result = new ArrayList<NameValue>(map.size());
		for(Entry<String, String> i : map.entrySet()) {
			result.add(new NameValue(i.getKey(), i.getValue()));
		}
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("NameValue [key=%s, value=%s]", key,
				value);
	}
	/* FIXME 
	public static ArrayList<NameValue> from(List<TypedParameter> executionParameters) {
		ArrayList<NameValue> result = null;
		if(executionParameters != null) {
			result = new ArrayList<NameValue>(executionParameters.size());
			for(TypedParameter t : executionParameters) {
				NameValue pa = new NameValue(t.getName(), t.value());
				result.add(pa);
			}
		}
		return result;
	} */
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		NameValue other = (NameValue) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
}

