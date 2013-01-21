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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlRootElement(name="TypedParameter")
@XmlType(name="TypedParameter", propOrder={"name", "description", "type", "value", "defaultValue", "optional"})
public class TypedParameter implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private String description;
	private ParameterType type;
	private String value;
	private String defaultValue;
	private boolean isOptional = false;
	public static Map<String, TypedParameter> asMap(List<TypedParameter> executionParameters) {
		Map<String, TypedParameter> result = new HashMap<String, TypedParameter>();
		if(executionParameters != null) {
			for(TypedParameter t : executionParameters) {
				result.put(t.name, t);
			}
		}
		return result;
	}
	
	public static ArrayList<TypedParameter> asList(Map<String, TypedParameter>map) {
		ArrayList<TypedParameter> result = new ArrayList<TypedParameter>(map.size());
		result.addAll(map.values());
		return result;
	}
	
	/**
	 * @param name
	 * @param description
	 * @param type
	 * @param value
	 * @param defaultValue
	 */
	public TypedParameter(String name, String description, ParameterType type,
			String value, String defaultValue) {
		super();
		this.name = name;
		setDescription(description);
		this.type = type;
		setValue(value);
		setDefaultValue(defaultValue);
	}
	
	/**
	 * @param p
	 */
	public TypedParameter(TypedParameter p) {
		super();
		this.name = p.name;
		this.description = p.description;
		this.type = p.type;
		this.value = p.value;
		this.defaultValue = p.defaultValue;
	}
	
	public static List<TypedParameter> cloneOf(TypedParameter[] params) {
		List<TypedParameter> result = new ArrayList<TypedParameter>(params.length);
		for(TypedParameter p : params) {
			result.add(new TypedParameter(p));
		}
		return result;
	}
	
	public TypedParameter() {
	}

	public TypedParameter withName(String name) {
		this.name = name;
		return this;
	}
	
	public TypedParameter withDescription(String description) {
		setDescription(description);
		return this;
	}
	
	public TypedParameter ofType(ParameterType type) {
		this.type = type;
		return this;
	}
	
	public TypedParameter withValue(String value) {
		setValue(value);
		this.type = ParameterType.String;
		return this;
	}
	
	public TypedParameter withValue(Long value) {
		setValue(Long.toString(value));
		this.type = ParameterType.Long;
		return this;
	}
	
	public TypedParameter withValue(Integer value) {
		setValue(Long.toString(value));
		this.type = ParameterType.Long;
		return this;
	}
	
	public TypedParameter withValue(Boolean value) {
		setValue(Boolean.toString(value));
		this.type = ParameterType.Boolean;
		return this;
	}
	
	public TypedParameter withValue(Double value) {
		setValue(Double.toString(value));
		this.type = ParameterType.Double;
		return this;
	}
	
	public TypedParameter withDefault(String value) {
		setDefaultValue(value);
		this.type = ParameterType.String;
		return this;
	}
	
	public TypedParameter withDefault(Long value) {
		setDefaultValue(Long.toString(value));
		this.type = ParameterType.Long;
		return this;
	}
	
	public TypedParameter withDefault(Boolean value) {
		setDefaultValue(Boolean.toString(value));
		this.type = ParameterType.Boolean;
		return this;
	}
	
	public TypedParameter withDefault(Double value) {
		setDefaultValue(Double.toString(value));
		this.type = ParameterType.Double;
		return this;
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
	 * @return the description
	 */
	public String getDescription() {
		return (description==null)?null:description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = (description==null)?null:description;
	}
	/**
	 * @return the type
	 */
	public ParameterType getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(ParameterType type) {
		this.type = type;
	}
	/**
	 * @return the value, obscured if of type secret
	 */
	public String getValue() {
		if(value != null && this.type == ParameterType.Secret)
			return "***********";
		else
			return (value==null)?null:value;
	}
	
	public String valueOf() {
		String result = value();
		if(result == null) result = defaultValue();
		return result;
	}
	/**
	 * @return the value
	 */
	public String value() {
		return (value==null)?null:value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = (value==null)?null:value;
	}
	/**
	 * @return the defaultValue, obscured if of type secret
	 */
	public String getDefaultValue() {
		if(defaultValue != null && this.type == ParameterType.Secret)
			return "***********";
		else
			return (defaultValue==null)?null:defaultValue;
	}
	
	/**
	 * @return the defaultValue
	 */
	public String defaultValue() {
		return (defaultValue==null)?null:defaultValue;
	}
	
	/**
	 * @param defaultValue the defaultValue to set
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = (defaultValue==null)?null:defaultValue;
	}
	/**
	 * @return the isOptional
	 */
	public boolean isOptional() {
		return this.isOptional;
	}
	
	/**
	 * @return the isOptional
	 */
	public boolean getOptional() {
		return this.isOptional;
	}

	/**
	 * @param isOptional the isOptional to set
	 */
	public void setOptional(boolean isOptional) {
		this.isOptional = isOptional;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String
				.format("TypedParameter [name=%s, description=%s, type=%s, value=%s, defaultValue=%s, isOptional=%s]",
						name, description, type, value, defaultValue,
						isOptional);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((defaultValue == null) ? 0 : defaultValue.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + (isOptional ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		TypedParameter other = (TypedParameter) obj;
		if (defaultValue == null) {
			if (other.defaultValue != null)
				return false;
		} else if (!defaultValue.equals(other.defaultValue))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (isOptional != other.isOptional)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type != other.type)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

}
