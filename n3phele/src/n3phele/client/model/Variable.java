package n3phele.client.model;

import java.util.Arrays;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.shared.GWT;

public class Variable extends JavaScriptObject {
	
	protected Variable() {}
	
	public static Variable newInstance(String name, String typeString, String value) {
		Variable a = createObject().cast();
		a.init(name,  typeString, value);
		GWT.log(a.getName());
		GWT.log(a.getType());
		GWT.log(""+Arrays.asList(a.getValue()));
		return a;
	}
	
	public static Variable newInstance(FileSpecification fileSpec) {
		Variable a = createObject().cast();
		a.init(fileSpec.getName(), "File", fileSpec.getRepository());
		return a;
	}
	
	private native final void init(String name, String type, String value) /*-{
		this.name = name;
		this.type = type;
		this.value = [value];
	}-*/;

	/**
	 * @return the name
	 */
	public native final String getName() /*-{
		return this.name;
	}-*/;

	/**
	 * @return the type
	 */
	public native final String getType()  /*-{
		return this.type;
	}-*/;

	/**
	 * @return the value
	 */
	
	public native final JsArrayString getValue() /*-{
		return this.value;
	}-*/;
}
