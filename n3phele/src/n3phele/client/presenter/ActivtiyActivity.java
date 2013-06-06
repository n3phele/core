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
package n3phele.client.presenter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import n3phele.client.ClientFactory;
import n3phele.client.model.Activity;
import n3phele.client.model.Command;
import n3phele.client.model.TypedParameter;
import n3phele.client.presenter.helpers.AuthenticatedRequestFactory;
import n3phele.client.view.CommandDetailView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;

public class ActivtiyActivity extends CommandActivity {
	private Activity activity;
	private final String activityUri;
	/**
	 * @param name
	 * @param uri
	 * @param factory
	 * @param view
	 * @param activity
	 */
	public ActivtiyActivity(String name, ClientFactory factory,
			CommandDetailView view, String activity) {
		super(name, activity, factory, view);
		this.activityUri = activity;
	}
	
	public ActivtiyActivity(String name, String activityUri, ClientFactory factory) {
		this(name, factory, factory.getActivityCommandView(), activityUri);
	}
	
//	@Override
//	protected void initData() {
//		this.refreshActivity(this.activityUri);
//	}
	

	
	protected void refreshActivity(String key) {
		
		String url = key;
		// String url = objectUri;
		// Send request to server and catch any errors.
		RequestBuilder builder = AuthenticatedRequestFactory.newRequest(RequestBuilder.GET, url);
		try {
			builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					GWT.log("Couldn't retrieve JSON " + exception.getMessage());
				}
	
				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) {
						ActivtiyActivity.this.activity = Activity.asActivity(response.getText());
						ActivtiyActivity.this.objectUri = activity.getCommand();
						ActivtiyActivity.super.initData();
					} else {
						GWT.log("Couldn't retrieve JSON ("
								+ response.getStatusText() + ")");
					}
				}
			});
		} catch (RequestException e) {
			GWT.log("Couldn't retrieve JSON " + e.getMessage());
		}
	}
	
	@Override
	protected void updateData(String uri, Command update) {
		Map<String, String> topLevel = new HashMap<String, String>();

		List<TypedParameter> executionParamters = update.getExecutionParameters();
		for(int i=0; i < executionParamters.size(); i++) {
			TypedParameter p = executionParamters.get(i);
			if(p.getName() != null && p.getName().startsWith("$")) {
				executionParamters.remove(i--);
				p.setName(p.getName().substring(1));
				GWT.log("toplevel "+p.getName()+" "+p.getValue()+" "+p.getDefaultValue());
				topLevel.put(p.getName(), isBlankOrNull(p.getValue())?p.getDefaultValue():p.getValue());
			}
		}
		if(executionParamters.size() == 0) {
			update.getExecutionParameters().clear();
		}
		super.updateData(uri, update);
		this.display.setJobName(topLevel.get("name"));
		this.display.setNotify(Boolean.valueOf(topLevel.get("notify")));
		this.display.setSelectedImplementation(topLevel.get("account"));
	}
	
	private boolean isBlankOrNull(String s) {
		return s==null || s.isEmpty();
	}
	
//	protected void merge(Command command, Activity activity) {
//		if(command==null || activity==null) 
//			return;
//		List<NameValue> specifiedParams = activity.getParameters();
//		Map<String,String>paramMap = new HashMap<String,String>();
//		if(specifiedParams != null) {
//			for(NameValue nv : specifiedParams) {
//				paramMap.put(nv.getKey(), nv.getValue());
//			}
//		}
//		
//		List<FileSpecification> specifiedInputs = activity.getInputs();
//		Map<String,FileSpecification>inputMap = new HashMap<String,FileSpecification>();
//		if(inputMap != null) {
//			for(FileSpecification fs : specifiedInputs) {
//				inputMap.put(fs.getName(), fs);
//			}
//		}
//		
//		List<FileSpecification> specifiedOutputs = activity.getOutputs();
//		Map<String,FileSpecification>outputMap = new HashMap<String,FileSpecification>();
//		if(outputMap != null) {
//			for(FileSpecification fs : specifiedOutputs) {
//				outputMap.put(fs.getName(), fs);
//			}
//		}
//		/*
//		 * Update parameters
//		 */
//		if(command.getExecutionParameters() != null) {
//			for(TypedParameter p : command.getExecutionParameters()) {
//				String value = paramMap.get(p.getName());
//				if(!isNullOrBlank(value)) {
//					if(!isSame(value, p.getDefaultValue())) {
//						p.setValue(value);
//					}	
//				}
//			}
//		}
//		/*
//		 * Update input files
//		 */
//		if(command.getInputFiles() != null) {
//			for(FileSpecification p : command.getInputFiles()) {
//				FileSpecification value = inputMap.get(p.getName());
//				if(value != null) {
//					p.setDescription(value.getDescription());
//					p.setFilename(value.getFilename());
//					p.setRepository(value.getRepository());
//				}	
//			}
//		}
//		/*
//		 * Update output files
//		 */
//		if(command.getOutputFiles() != null) {
//			for(FileSpecification p : command.getOutputFiles()) {
//				FileSpecification value = outputMap.get(p.getName());
//				if(value != null) {
//					p.setDescription(value.getDescription());
//					p.setFilename(value.getFilename());
//					p.setRepository(value.getRepository());
//				}	
//			}
//		}
//	}
	protected boolean isNullOrBlank(String x) {
		return x==null || x.length()==0;
	}
	
	protected boolean isSame(String a, String b) {
		return isNullOrBlank(a)? isNullOrBlank(b) : a.equals(b);
	}
}
