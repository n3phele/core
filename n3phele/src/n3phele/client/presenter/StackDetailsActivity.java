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

import n3phele.client.AppPlaceHistoryMapper;
import n3phele.client.CacheManager;
import n3phele.client.ClientFactory;
import n3phele.client.model.Account;
import n3phele.client.model.ActivityData;
import n3phele.client.model.ActivityDataCollection;
import n3phele.client.model.CloudProcess;
import n3phele.client.model.Collection;
import n3phele.client.model.CostsCollection;
import n3phele.client.model.Stack;
import n3phele.client.model.StackServiceAction;
import n3phele.client.model.VirtualServerCollection;
import n3phele.client.model.VirtualServer;
import n3phele.client.presenter.helpers.AuthenticatedRequestFactory;
import n3phele.client.view.AccountListView;
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
	private List<Account> accountList = null;
	private final CacheManager cacheManager;
	private String accountCollection;
	private final String virtualServerCollection;
	private HandlerRegistration handlerRegistration;
	private VirtualServerCollection<VirtualServer> vsCol = null;
	private HashMap<Account, Double> costPerAccount = null;
	private HashMap<Account, Integer> vsPerAccount = null;
	private int runningHours = 0;
	private int runningMinutes = 0;
	protected final PlaceController placeController;

	public StackDetailsActivity(String url ,String id, ClientFactory factory) {
		System.out.println("ID: " + id);
		this.historyMapper = factory.getHistoryMapper();
		this.display = factory.getStackDetailsView();
		
		
//		for(String s: stack.getVms()){
//			getProcess(s);
//		}
		this.cacheManager = factory.getCacheManager();
		this.accountCollection = URL.encode(factory.getCacheManager().ServiceAddress + "account");
		this.virtualServerCollection = URL.encode(factory.getCacheManager().ServiceAddress + "virtualServers/account/");
		this.placeController = factory.getPlaceController();
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		this.eventBus = eventBus;
		handlerRegistration(eventBus);
		//display.setPresenter(this);
		panel.setWidget(display);
		display.setDisplayList(this.accountList);
		//		getClouds();
		getAccountList();
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
				getAccountList();
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

	protected void updateAccountList(List<Account> list) {
		this.accountList = list;
		for(int i=0; i<accountList.size(); i++){
			runningHours = 0;
			runningMinutes = 0;
			//getVSList(accountList.get(i));
			//getRunningProcess(accountList.get(i));
			getProcessByDay(accountList.get(i));
		}
		display.refresh(this.accountList, this.costPerAccount, this.vsPerAccount);
	}

	protected void updateCostPerAccount(Account account, double cost) {
		if(costPerAccount == null) return;
		costPerAccount.put(account, cost);
		display.refresh(this.accountList, this.costPerAccount, this.vsPerAccount);
	}


	public void updateVsPerAccount(Account account, int cont){
		vsPerAccount.put(account, cont);
		display.refresh(this.accountList, this.costPerAccount, this.vsPerAccount);
	}
	
	/*
	 * -------------
	 * Data Handling
	 * -------------
	 */

	public void getVSList(final Account account){
		String uri = virtualServerCollection + account.getUri().substring(account.getUri().lastIndexOf("/")+1);
		// Send request to server and catch any errors.
		RequestBuilder builder = AuthenticatedRequestFactory.newRequest(RequestBuilder.GET, uri);
		try {
			Request request = builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					// displayError("Couldn't retrieve JSON "+exception.getMessage());
				}

				public void onResponseReceived(Request request, Response response) {
					GWT.log("Got reply");
					int cont = 0;
					if (200 == response.getStatusCode()) {
						VirtualServerCollection<VirtualServer> virtualServerCollection = VirtualServer.asCollection(response.getText());
						//updateCostPerAccount(account, virtualServerCollection.dayCost());
						for(VirtualServer vs : virtualServerCollection.getElements()){
							//updateTimePerAccount(account, vs);
							if(vs.getStatus().equalsIgnoreCase("running")) cont++;
						}
						updateVsPerAccount(account, cont);
					} else {

					}
				}

			});
		} catch (RequestException e) {
			//displayError("Couldn't retrieve JSON "+e.getMessage());
		}
	}
	
	private void processList(final Account account) {
		final String url = account.getUri() + "/runningprocess/get";
		RequestBuilder builder = AuthenticatedRequestFactory.newRequest(RequestBuilder.GET, url);
		try {
			builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					GWT.log("Couldn't retrieve JSON " + exception.getMessage());
				}

				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) {
						GWT.log("Got reply");
						ActivityDataCollection result = ActivityDataCollection.asActivityDataCollection(response.getText());
						result.getStringElements();
						List<ActivityData> list = result.getElements();
						vsPerAccount.put(account, list.size());
						updateVsPerAccount(account, list.size());
					} else {
						GWT.log("Couldn't retrieve JSON (" + response.getStatusText() + ")");
					}
				}
			});
		} catch (RequestException e) {
			GWT.log("Couldn't retrieve JSON " + e.getMessage());
		}

	}
	
	private void getProcessByDay(final Account account) {
		final String url = account.getUri() + "/totalCost24Hour";
		RequestBuilder builder = AuthenticatedRequestFactory.newRequest(RequestBuilder.GET, url);
		try {
			builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					GWT.log("Couldn't retrieve JSON " + exception.getMessage());
				}

				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) {
						GWT.log("Got reply");
						CostsCollection result = CostsCollection.asCostsCollection(response.getText());
						List<Double> value = result.getElements();
						double d = value.get(0);
						costPerAccount.put(account, d);		
						updateCostPerAccount(account, d); 
					} else {
						GWT.log("Couldn't retrieve JSON (" + response.getStatusText() + ")");
					}
				}
			});
		} catch (RequestException e) {
			GWT.log("Couldn't retrieve JSON " + e.getMessage());
		}
	}

	public void getAccountList() {
		// Send request to server and catch any errors.
		RequestBuilder builder = AuthenticatedRequestFactory.newRequest(RequestBuilder.GET, accountCollection);
		try {
			Request request = builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					// displayError("Couldn't retrieve JSON "+exception.getMessage());
				}

				public void onResponseReceived(Request request, Response response) {
					GWT.log("Got reply");
					if (200 == response.getStatusCode()) {
						Collection<Account> account = Account.asCollection(response.getText());
						costPerAccount = new HashMap<Account, Double>(account.getElements().size());
						vsPerAccount = new HashMap<Account, Integer>(account.getElements().size());
						for(Account acc : account.getElements()){
							//getRunningProcess(acc);
							
						}
						
						updateAccountList(account.getElements());
					} else {

					}
				}

			});
		} catch (RequestException e) {
			//displayError("Couldn't retrieve JSON "+e.getMessage());
		}
	}

	//REST CALLS
	
	public void getAction() {
		// Send request to server and catch any errors.
		RequestBuilder builder = AuthenticatedRequestFactory.newRequest(RequestBuilder.GET,"");
		try {
			Request request = builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					GWT.log("Got error");
				}

				public void onResponseReceived(Request request, Response response) {
					GWT.log("Got reply");
					if (200 == response.getStatusCode()) {
						StackServiceAction stackAction = StackServiceAction.asAction(response.getText());
						//display.setStackAction(stackAction);
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
						//TODO do something in the view
					} else {

					}
				}

			});
		} catch (RequestException e) {
			//displayError("Couldn't retrieve JSON "+e.getMessage());
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
