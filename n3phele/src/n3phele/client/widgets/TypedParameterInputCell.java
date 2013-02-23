/**
 * @author Nigel Cook
 *
 * (C) Copyright 2010-2012. Nigel Cook. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * 
 * Licensed under the terms described in LICENSE file that accompanied this code, (the "License"); you may not use this file
 * except in compliance with the License. 
 * 
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on 
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the 
 *  specific language governing permissions and limitations under the License.
 */
package n3phele.client.widgets;

import java.util.ArrayList;
import java.util.List;

import n3phele.client.N3phele;
import n3phele.client.model.TypedParameter;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.TextInputCell;

public class TypedParameterInputCell extends CompositeCell<TypedParameter>{
	private final List<HasCell<TypedParameter, ?>> list;
	public TypedParameterInputCell(List<HasCell<TypedParameter, ?>> list) {
		super(list);
		this.list = list;
	}
	
	public List<HasCell<TypedParameter, ?>> getCompositeCells() {
		return this.list;
	}
	
	public static TypedParameterInputCell create() {

		final TextInputCell value = new TextInputCell();
		final ValidInputIndicatorCell valid = new ValidInputIndicatorCell(N3phele.n3pheleResource.inputErrorIcon());
	
		HasCell<TypedParameter,String> valueHasCell = new HasCell<TypedParameter,String>() {

			@Override
			public Cell<String> getCell() {
				return value;
			}

			@Override
			public FieldUpdater<TypedParameter, String> getFieldUpdater() {
				return new FieldUpdater<TypedParameter, String>() {

					@Override
					public void update(int index, TypedParameter object, String value) {
						
					}
					
				};
			}

			@Override
			public String getValue(TypedParameter object) {
				return object.getValue();
			}
			
		};
		HasCell<TypedParameter,String> validHasCell = new HasCell<TypedParameter,String>() {

			@Override
			public Cell<String> getCell() {
				return valid;
			}

			@Override
			public FieldUpdater<TypedParameter, String> getFieldUpdater() {
				return new FieldUpdater<TypedParameter, String>() {

					@Override
					public void update(int index, TypedParameter object, String value) {
						validateTypedParameter(object, value);
					}
				};
			}

			@Override
			public String getValue(TypedParameter object) {
				return "some random text";
				//return null;
			}
			
		};
		List<HasCell<TypedParameter,?>> arg = new ArrayList<HasCell<TypedParameter,?>>(2);
		arg.add(validHasCell);
		arg.add(valueHasCell);

		return new TypedParameterInputCell(arg);
	}
	
	private static boolean validateTypedParameter(TypedParameter object, String value) {
		boolean error = false;
		String type = object.getType();
  	  if(type.equals("Long")) {
  		  try {
  			  Long.valueOf(value.trim());
  		  } catch (Exception e) {
  			  error = true;
  		  }
  	  } else if(type.equals("Double")) {
  		  try {
  			  Double.valueOf(value);
  		  } catch (Exception e) {
  			  error = true;
  		  }
  		  
  	  } else if(type.equals("Boolean")) {
  		  try {
  			  Boolean.valueOf(value.trim());
  		  } catch (Exception e) {
  			  error = true;
  		  }
  		  
  	  } 
  	  return !error;
	}
}
