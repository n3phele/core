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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import n3phele.service.model.core.Helpers;

@XmlRootElement(name = "Variable")
@XmlType(name = "Variable", propOrder = {"name", "type", "value" })
public class Variable {
	private String name;
	private VariableType type;
	private String[] value = new String[] { null };
	
	public Variable() {}

	public Variable(String name, long value) {
		this.name = name;
		this.type = VariableType.Long;
		this.value = new String[] {Long.toString(value)};
	}
	
	public Variable(String name, int value) {
		this.name = name;
		this.type = VariableType.Long;
		this.value = new String[] {Long.toString(value)};
	}
	
	public Variable(String name, boolean value) {
		this.name = name;
		this.type = VariableType.Boolean;
		this.value = new String[] {Boolean.toString(value)};
	}
	
	public Variable(String name, float value) {
		this.name = name;
		this.type = VariableType.Double;
		this.value = new String[] { Double.toString(value)};
	}
	
	public Variable(String name, double value) {
		this.name = name;
		this.type = VariableType.Double;
		this.value = new String[] { Double.toString(value)};
	}
	
	public Variable(String name, String value) {
		this.name = name;
		this.type = VariableType.String;
		this.value =new String[] { value };
	}
	
	public Variable(String name, URI value) {
		this.name = name;
		URI uri = (URI) value;
		String scheme = uri.getScheme();
		if("http".equals(scheme) || "https".equals(scheme)) {
			this.type = VariableType.Object;
		} else {
			this.type = VariableType.File;
		}
		this.value =new String[] { value.toString() };
	}
	
	public Variable(String name, Map<String,String> value) {
		this.name = name;
		this.type = VariableType.FileList;
		this.setValue(value);
	}

	
	@SuppressWarnings("unchecked")
	public Variable(String name, Object value) {
		this.name = name;	
		if(value instanceof List) {
			this.type = VariableType.List;
			this.value = ((List<String>)value).toArray(new String[((List<String>)value).size()]);
		} else if(value instanceof Action) {
			this.type = VariableType.Action;
			this.value = new String[] { ((Action)value).getUri().toString() };
		} else if(value instanceof Map) {
			this.type = VariableType.FileList;
			this.setValue((Map<String,String>)value);
		}else {
			this.value = new String[] { value.toString() };
			if(value instanceof Long) {
				this.type = VariableType.Long;
			} else if(value instanceof String) {
				this.type = VariableType.String;
			}else if(value instanceof Double) {
				this.type = VariableType.Double;
			} else if(value instanceof Boolean) {
				this.type = VariableType.Boolean;
			} else  if(value instanceof URI) {
				URI uri = (URI) value;
				String scheme = uri.getScheme();
				if("http".equals(scheme) || "https".equals(scheme)) {
					this.type = VariableType.Object;
				} else {
					this.type = VariableType.File;
				}
			} else {
				this.type = VariableType.String;	
			}
		}
	}


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
	 * @return the type
	 */
	public VariableType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(VariableType type) {
		this.type = type;
	}

	/**
	 * @return the value
	 */
	public String[] getValue() {
		if(type == VariableType.Secret) {
			return new String[] { "**********" };
		}
		return this.value;
	}
	
	/**
	 * @return the value
	 */
	public String value() {
		if(type == VariableType.List)
			return Arrays.toString(value);
		else
			return value[0];
	}
	
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = new String[] {value};
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String[] value) {
		this.value = value;
	}
	
	/**
	 * @param value the value to set
	 */
	public void setValue(List<String> value) {
		this.value = value.toArray(new String[value.size()]);
	}
	
	/**
	 * @param value the value to set
	 */
	public void setValue(URI[] value) {
		String [] result = new String[value.length];
		for(int i=0; i < value.length; i++) {
			result[i] = Helpers.URItoString(value[i]);
		}
		this.value = result;
	}
	
	/**
	 * @param value the value to set
	 */
	public void setValue(Map<String,String> value) {
		List<String> linear = new ArrayList<String>();
		for(Entry<String, String> e : ((Map<String,String>)value).entrySet()) {
			linear.add(e.getKey()+":"+e.getValue());
		}
		this.value = linear.toArray(new String[linear.size()]);
	}
	
	public Object getExpressionObject() {
		switch(type) {

		case Boolean:
			return Boolean.valueOf(this.value[0]);
		case Double:
			return Double.valueOf(this.value[0]);
		case Long:
			return Long.valueOf(this.value[0]);
		case Secret:
			return "******";
		case Object:
		case Action:
		case File:
			return Helpers.stringToURI(this.value[0]);
		case FileList:
			Map<String,String> fileList = new HashMap<String,String>();
			for(String combo : this.value){
				String[] parts = combo.split(":");
				fileList.put(parts[0], parts[1]);
			}
			return fileList;
		case List:
			return new ArrayList<String>(Arrays.asList(this.value));
		case String:
			default:
			return this.value[0];
		}
	}

	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("Variable [name=%s, type=%s, value=%s]", name,
				type, Arrays.toString(value));
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + Arrays.hashCode(value);
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
		Variable other = (Variable) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type != other.type)
			return false;
		if (!Arrays.equals(value, other.value))
			return false;
		return true;
	}

}
