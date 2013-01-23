package n3phele.service.model;

import java.util.HashMap;
import java.util.List;

public class Context extends HashMap<String, Variable> {
	private static final long serialVersionUID = 1L;
	
	public String getValue(String name) {
		Variable v = this.get(name);
		if(v != null)
			return v.getValue();
		else
			return null;
	}
	
	public boolean putValue(String name, String value) {
		Variable v = this.get(name);
		boolean created = false;
		if(v == null) {
			v = new Variable();
			v.setName(name);
			created = true;
		}
		v.setType(VariableType.String);
		v.setValue(value);
		this.put(name, v);
		return created;
	}
	
	public boolean putValue(String name, Action value) {
		Variable v = this.get(name);
		boolean created = false;
		if(v == null) {
			v = new Variable();
			v.setName(name);
			created = true;
		}
		v.setType(VariableType.Action);
		v.setValue(value.getUri().toString());
		this.put(name, v);
		return created;
	}
	
	
	public boolean putValue(String name, long value) {
		Variable v = this.get(name);
		boolean created = false;
		if(v == null) {
			v = new Variable();
			v.setName(name);
			created = true;
		}
		v.setType(VariableType.Long);
		v.setValue(Long.toString(value));
		this.put(name, v);
		return created;
	}
	
	
	public boolean putValue(String name, double value) {
		Variable v = this.get(name);
		boolean created = false;
		if(v == null) {
			v = new Variable();
			v.setName(name);
			created = true;
		}
		v.setType(VariableType.Double);
		v.setValue(Double.toString(value));
		this.put(name, v);
		return created;
	}
	
	public boolean putValue(String name, boolean value) {
		Variable v = this.get(name);
		boolean created = false;
		if(v == null) {
			v = new Variable();
			v.setName(name);
			created = true;
		}
		v.setType(VariableType.Boolean);
		v.setValue(Boolean.toString(value));
		this.put(name, v);
		return created;
	}
	
	public boolean putValue(String name, List<String> value) {
		Variable v = this.get(name);
		boolean created = false;
		if(v == null) {
			v = new Variable();
			v.setName(name);
			created = true;
		}
		v.setType(VariableType.List);
		v.setValue(value);
		this.put(name, v);
		return created;
	}
	
	public long getLongValue(String name) {
		Variable v = this.get(name);
		if(v != null && !v.getValue().isEmpty()) {
			if(v.getType() == VariableType.Long) {
				return Long.valueOf(v.getValue());
			} else if(v.getType() == VariableType.Double) {
				return Double.valueOf(v.getValue()).longValue();
			} else if(v.getType() == VariableType.Boolean) {
				return Boolean.valueOf(v.getValue())? 1 : 0;
			}
		}
		return 0;
	}
	
	public int getIntegerValue(String name) {
		return (int) getLongValue(name);
	}
	
	public double getDoubleValue(String name) {
		Variable v = this.get(name);
		if(v != null && !v.getValue().isEmpty()) {
			if(v.getType() == VariableType.Long) {
				return (double) Long.valueOf(v.getValue());
			} else if(v.getType() == VariableType.Double) {
				return Double.valueOf(v.getValue());
			} else if(v.getType() == VariableType.Boolean) {
				return Boolean.valueOf(v.getValue())? 1 : 0;
			}
		}
		return 0;
	}
	
	public float getFloatValue(String name) {
		return (float) getDoubleValue(name);
	}
	
	public boolean getBooleanValue(String name) {
		Variable v = this.get(name);
		if(v != null && !v.getValue().isEmpty()) {
			if(v.getType() == VariableType.Long) {
				return  Long.valueOf(v.getValue()) != 0;
			} else if(v.getType() == VariableType.Double) {
				return Double.valueOf(v.getValue()) != 0;
			} else if(v.getType() == VariableType.Boolean) {
				return Boolean.valueOf(v.getValue());
			} else if(v.getType() == VariableType.String) {
				return Boolean.valueOf(v.getValue());
			}
		}
		return false;
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
		} else{
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

}
