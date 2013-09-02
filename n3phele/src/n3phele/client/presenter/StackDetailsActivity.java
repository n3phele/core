/**
 * @author Nigel Cook
 * @author Douglas Tondin 
 * @author Leonardo Amado
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import n3phele.client.AppPlaceHistoryMapper;
import n3phele.client.CacheManager;
import n3phele.client.ClientFactory;
import n3phele.client.model.Account;
import n3phele.client.model.AssimilateVMAction;
import n3phele.client.model.CloudProcess;
import n3phele.client.model.Stack;
import n3phele.client.model.StackServiceAction;
import n3phele.client.presenter.helpers.AuthenticatedRequestFactory;
import n3phele.client.view.StackDetailsView;


import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class StackDetailsActivity extends AbstractActivity {
	private EventBus eventBus;
	private final AppPlaceHistoryMapper historyMapper;
	private final StackDetailsView display;
	private List<CloudProcess> accountList = null;
	private final CacheManager cacheManager;
	private HandlerRegistration handlerRegistration;	
	private HashMap<String, String> cloudIP = null;

	protected final PlaceController placeController;
	private String stackId = "-1";
	private StackServiceAction stackAction = null;
	private Stack stack = null;
	private List<CloudProcess> listCloudProcess = new ArrayList<CloudProcess>();
	
	public StackDetailsActivity(String url , ClientFactory factory) {
		this.historyMapper = factory.getHistoryMapper();
		this.display = factory.getStackDetailsView();
		String []splitedToken = url.split("#");
		stackId = splitedToken[1];
		url = splitedToken[0].trim();
		getAction(url);
		this.cacheManager = factory.getCacheManager();
		this.placeController = factory.getPlaceController();
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		cloudIP = new HashMap<String, String>();
		this.eventBus = eventBus;
		handlerRegistration(eventBus);		
		panel.setWidget(display);
		display.setDisplayList(this.accountList);		
	}

	@Override
	public String mayStop() {
		return null;
	}
	@Override
	public void onCancel() {
		unregister();
	}
	@Override
	public void onStop() {
		this.display.setDisplayList(null);
		unregister();
	}

	public void goToPrevious() {
		History.back();
	}
	public void handlerRegistration(EventBus eventBus) {
		this.handlerRegistration = this.eventBus.addHandler(AccountListUpdate.TYPE, new AccountListUpdateEventHandler() {
			@Override
			public void onMessageReceived(AccountListUpdate event) {
				//TODO update here?
			}
		});
		CacheManager.EventConstructor change = new CacheManager.EventConstructor() {
			@Override
			public AccountListUpdate newInstance(String key) {
				return new AccountListUpdate();
			}
		};
		cacheManager.register(cacheManager.ServiceAddress + "account", "accountList", change);


	}

	protected void unregister() {
		cacheManager.unregister(cacheManager.ServiceAddress + "account", "accountList");
	}

	protected void updateAccountList(List<CloudProcess> list) {
		this.accountList = list;
		display.refresh(this.listCloudProcess, this.cloudIP);
	}

	protected void updateCloudIP(String name, String ip) {
		if(cloudIP == null) return;
		cloudIP.put(name, ip);
		display.refresh(this.listCloudProcess, this.cloudIP);
	}


	
	/*
	 * -------------
	 * Data Handling
	 * -------------
	 */

	
	public void getAction(String uri) {
		// Send request to server and catch any errors.
		RequestBuilder builder = AuthenticatedRequestFactory.newRequest(RequestBuilder.GET,uri);
		try {
			Request request = builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					GWT.log("Got error");
				}

				public void onResponseReceived(Request request, Response response) {
					GWT.log("Got reply");
					if (200 == response.getStatusCode()) {
						stackAction = StackServiceAction.asAction(response.getText());
						for(Stack s : stackAction.getStackList()){
							if( s.getId().equals(stackId)){
								stack = s;
								break;
							}
								
						}
						if(stack.getVms().size() > 0){
							for(String str : stack.getVms()){
								 getProcess(str);
							}
						}
					} 
				}

			});
		} catch (RequestException e) {
			GWT.log("Got error");
		}
	}
	
	public void getProcess(String processUri) {
		// Send request to server and catch any errors.
		RequestBuilder builder = AuthenticatedRequestFactory.newRequest(RequestBuilder.GET, processUri);
		try {
			Request request = builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					// displayError("Couldn't retrieve JSON "+exception.getMessage());
				}

				public void onResponseReceived(Request request, Response response) {
					GWT.log("Got reply");
					if (200 == response.getStatusCode()) {
						CloudProcess process = CloudProcess.asCloudProcess(response.getText());
					    listCloudProcess.add(process);
					    display.setDisplayList(listCloudProcess);
					    getAssimilateAction(process.getName(), process.getAction());
					    
					} else {

					}
				}

			});
		} catch (RequestException e) {
			//displayError("Couldn't retrieve JSON "+e.getMessage());
		}
	}
	
	public void getAssimilateAction(String name, String uri) {
		// Send request to server and catch any errors.
		RequestBuilder builder = AuthenticatedRequestFactory.newRequest(RequestBuilder.GET,uri);
		final String fname = name;
		try {
			Request request = builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					GWT.log("Got error");
				}
				public void onResponseReceived(Request request, Response response) {
					GWT.log("Got reply");
					if (200 == response.getStatusCode()) {
						AssimilateVMAction action = AssimilateVMAction.asAction(response.getText());
						updateCloudIP(fname, action.getTargetIP());
					} 
				}

			});
		} catch (RequestException e) {
			GWT.log("Got error");
		}
	}
	
	
	public void onSelect(Account selected) {
		History.newItem(historyMapper.getToken(new AccountHyperlinkPlace(selected.getUri())));
	}
	

	/*
	 * ----------------
	 * Event Definition
	 * ----------------
	 */

	public interface AccountListUpdateEventHandler extends EventHandler {
		void onMessageReceived(AccountListUpdate commandListUpdate);
	}

	public static class AccountListUpdate extends GwtEvent<AccountListUpdateEventHandler> {
		public static Type<AccountListUpdateEventHandler> TYPE = new Type<AccountListUpdateEventHandler>();
		public AccountListUpdate() {}
		@Override
		public com.google.gwt.event.shared.GwtEvent.Type<AccountListUpdateEventHandler> getAssociatedType() {
			return TYPE;
		}
		@Override
		protected void dispatch(AccountListUpdateEventHandler handler) {
			handler.onMessageReceived(this);
		}
	}


}
