package n3phele.client.model;

import java.util.HashMap;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

public class Context extends HashMap<String, Variable> {
	private static final long serialVersionUID = 1L;

	public JSONValue toJSON() {
	JsArray<Variable> x = JsArray.createArray().cast();
		int i = 0;
		for(Variable v : values()) {
			x.set(i++, v);
		}
		JSONObject context = new JSONObject();
		context.put("Variable", new JSONArray(x));
		return context;
	}
}
