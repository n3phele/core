package n3phele.service.model;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import n3phele.service.model.core.Helpers;

public class Context extends HashMap<String, Variable> {
	private static final long serialVersionUID = 1L;
	
	public String getValue(String name) {
		Variable v = this.get(name);
		if(v != null)
			return v.value();
		else
			return null;
	}
	
	public boolean putValue(String name, String value) {
		if(Helpers.isBlankOrNull(name)) throw new IllegalArgumentException("Null context name");
		Variable v = new Variable();
		v.setName(name);
		boolean created = this.containsKey(name);
		v.setType(VariableType.String);
		v.setValue(value);
		this.put(name, v);
		return created;
	}
	
	public boolean putSecretValue(String name, String value) {
		if(Helpers.isBlankOrNull(name)) throw new IllegalArgumentException("Null context name");
		Variable v = new Variable();
		v.setName(name);
		boolean created = this.containsKey(name);
		v.setType(VariableType.Secret);
		v.setValue(value);
		this.put(name, v);
		return created;
	}
	
	public boolean putValue(String name, Action value) {
		if(Helpers.isBlankOrNull(name)) throw new IllegalArgumentException("Null context name");
		Variable v = new Variable();
		v.setName(name);
		boolean created = this.containsKey(name);
		v.setType(VariableType.Action);
		v.setValue(value.getUri().toString());
		this.put(name, v);
		return created;
	}
	
	
	public boolean putValue(String name, long value) {
		if(Helpers.isBlankOrNull(name)) throw new IllegalArgumentException("Null context name");
		Variable v = new Variable();
		v.setName(name);
		boolean created = this.containsKey(name);
		v.setType(VariableType.Long);
		v.setValue(Long.toString(value));
		this.put(name, v);
		return created;
	}
	
	
	public boolean putValue(String name, double value) {
		if(Helpers.isBlankOrNull(name)) throw new IllegalArgumentException("Null context name");
		Variable v = new Variable();
		v.setName(name);
		boolean created = this.containsKey(name);
		v.setType(VariableType.Double);
		v.setValue(Double.toString(value));
		this.put(name, v);
		return created;
	}
	
	public boolean putValue(String name, boolean value) {
		if(Helpers.isBlankOrNull(name)) throw new IllegalArgumentException("Null context name");
		Variable v = new Variable();
		v.setName(name);
		boolean created = this.containsKey(name);
		v.setType(VariableType.Boolean);
		v.setValue(Boolean.toString(value));
		this.put(name, v);
		return created;
	}
	
	public boolean putValue(String name, List<String> value) {
		if(Helpers.isBlankOrNull(name)) throw new IllegalArgumentException("Null context name");
		Variable v = new Variable();
		v.setName(name);
		boolean created = this.containsKey(name);
		v.setType(VariableType.List);
		v.setValue(value);
		this.put(name, v);
		return created;
	}
	
	public boolean putValue(String name, URI[] value) {
		if(Helpers.isBlankOrNull(name)) throw new IllegalArgumentException("Null context name");
		Variable v = new Variable();
		v.setName(name);
		boolean created = this.containsKey(name);
		v.setType(VariableType.List);
		v.setValue(value);
		this.put(name, v);
		return created;
	}
	
	public boolean putValue(String name, Map<String,String> value) {
		if(Helpers.isBlankOrNull(name)) throw new IllegalArgumentException("Null context name");
		Variable v = new Variable();
		v.setName(name);
		boolean created = this.containsKey(name);
		v.setType(VariableType.FileList);
		v.setValue(value);
		this.put(name, v);
		return created;
	}
	
	public boolean putActionValue(String name, URI action) {
		if(Helpers.isBlankOrNull(name)) throw new IllegalArgumentException("Null context name");
		Variable v = new Variable();
		v.setName(name);
		boolean created = this.containsKey(name);
		v.setType(VariableType.Action);
		v.setValue(action.toString());
		this.put(name, v);
		return created;
	}
	
	public boolean putValue(String name, URI uri) {
		if(Helpers.isBlankOrNull(name)) throw new IllegalArgumentException("Null context name");
		Variable v = new Variable();
		v.setName(name);
		boolean created = this.containsKey(name);
		String scheme = uri.getScheme();
		if("http".equals(scheme) || "https".equals(scheme)) {
			v.setType(VariableType.Object);
		} else {
			v.setType(VariableType.File);
		}
		v.setValue(uri.toString());
		this.put(name, v);
		return created;
	}

	
	public long getLongValue(String name) {
		Variable v = this.get(name);
		if(v != null && !v.value().isEmpty()) {
			if(v.getType() == VariableType.Long) {
				return Long.valueOf(v.value());
			} else if(v.getType() == VariableType.Double) {
				return Double.valueOf(v.value()).longValue();
			} else if(v.getType() == VariableType.Boolean) {
				return Boolean.valueOf(v.value())? 1 : 0;
			}
		}
		return 0;
	}
	
	public int getIntegerValue(String name) {
		return (int) getLongValue(name);
	}
	
	public double getDoubleValue(String name) {
		Variable v = this.get(name);
		if(v != null && !v.value().isEmpty()) {
			if(v.getType() == VariableType.Long) {
				return (double) Long.valueOf(v.value());
			} else if(v.getType() == VariableType.Double) {
				return Double.valueOf(v.value());
			} else if(v.getType() == VariableType.Boolean) {
				return Boolean.valueOf(v.value())? 1 : 0;
			}
		}
		return 0;
	}
	
	public float getFloatValue(String name) {
		return (float) getDoubleValue(name);
	}
	
	public boolean getBooleanValue(String name) {
		Variable v = this.get(name);
		if(v != null && !v.value().isEmpty()) {
			if(v.getType() == VariableType.Long) {
				return  Long.valueOf(v.value()) != 0;
			} else if(v.getType() == VariableType.Double) {
				return Double.valueOf(v.value()) != 0;
			} else if(v.getType() == VariableType.Boolean) {
				return Boolean.valueOf(v.value());
			} else if(v.getType() == VariableType.String) {
				return Boolean.valueOf(v.value());
			}
		}
		return false;
	}
	
	public URI getFileValue(String name) {
		Variable v = this.get(name);
		if(v != null && !v.value().isEmpty()) {
			if(v.getType() == VariableType.File) {
				return  Helpers.stringToURI(v.value());
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public void putObjectValue(String name, Object arg) {
		if(arg instanceof String) {
			putValue(name, (String)arg);
		} else if (arg instanceof Long) {
			putValue(name, (Long)arg);
		} else if (arg instanceof Double) {
			putValue(name, (Double)arg);
		} else if (arg instanceof Boolean) {
			putValue(name, (Boolean)arg);
		} else if (arg instanceof List) {
			putValue(name, (List<String>)arg);
		} else if (arg instanceof Map) {
			putValue(name, (Map<String,String>)arg);
		}else {
			putValue(name, arg.toString());
		}
	}
	
	public Object getObjectValue(String name) {
		Variable v = this.get(name);
		if(v == null) 
			return null;
		else
			return v.getExpressionObject();
	}

	public List<String> getListValue(String name) {
		Variable v = get(name);
		return (Arrays.asList(v.getValue()));
	}

	public URI getURIValue(String name) {
		Variable v = this.get(name);
		if(v != null && !v.value().isEmpty()) {
			if(v.getType() == VariableType.Object || v.getType() == VariableType.File || v.getType() == VariableType.Action) {
				return URI.create(v.value());
			} 
		}
		return null;
	}
}
