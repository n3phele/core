/**
 * @author Nigel Cook
 *
 * (C) Copyright 2010-2011. All rights reserved.
 */
package n3phele.client.presenter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import n3phele.client.AppActivityMapper;
import n3phele.client.AppPlaceHistoryMapper;
import n3phele.client.CacheManager;
import n3phele.client.ClientFactory;
import n3phele.client.model.Account;
import n3phele.client.model.Activity;
import n3phele.client.model.CloudProcessSummary;
import n3phele.client.model.Collection;
import n3phele.client.model.CostsCollection;
import n3phele.client.model.VirtualServerCollection;

import n3phele.client.model.VirtualServer;
import n3phele.client.presenter.helpers.AuthenticatedRequestFactory;
import n3phele.client.view.AccountHyperlinkView;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
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
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class AccountHyperlinkActivity extends AbstractActivity {
	private final String accountUri;
	private final ClientFactory factory;
	private AccountHyperlinkView display;
	private Account account = null;
	private List<VirtualServer> vsList;
	private VirtualServerCollection<VirtualServer> vsCol;
	private final AppPlaceHistoryMapper historyMapper;
	private String accountCollection;
	private String virtualServerCollection;
	private final CacheManager cacheManager;
	private EventBus eventBus;
	private HandlerRegistration handlerRegistration;
	private HashMap<VirtualServer, Activity> activityPerVS = null;
	private List<Double> pricesQuery;

	public AccountHyperlinkActivity(String accountUri, ClientFactory factory) {
		this.factory = factory;
		this.historyMapper = factory.getHistoryMapper();
		this.accountUri = accountUri;

		this.display = factory.getAccountHyperlinkView(accountUri);
		this.cacheManager = factory.getCacheManager();
		this.eventBus = factory.getEventBus();
		this.accountCollection = URL.encode(factory.getCacheManager().ServiceAddress + "account");
		String id = accountUri.substring(accountUri.lastIndexOf("/")+1);
		this.virtualServerCollection = URL.encode(factory.getCacheManager().ServiceAddress + "virtualServers/account/");
		this.virtualServerCollection += id;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		getVSList();
		getAccountList();
		this.eventBus = eventBus;
		handlerRegistration(eventBus);
		display.setPresenter(this);
		panel.setWidget(display);
		//display.setDisplayList(null);
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

	protected void updateVSList(VirtualServerCollection<VirtualServer> list) {
		vsCol = list;
		this.vsList = new ArrayList<VirtualServer>(list.getElements());
		for (VirtualServer vs : list.getElements()) {
			if(vs.getEndDate() != null)
				vsList.remove(vs);
		}
		activityPerVS = new HashMap<VirtualServer, Activity>(vsList.size());
		for(int i=0; i<vsList.size(); i++){
			if(vsList.get(i).getActivity() != null)
				getActivity(vsList.get(i));
		}
		display.refresh(vsList, activityPerVS);
	}

	protected void updateAccount(Account account) {
		this.account = account;
		display.setData(this.account);
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

	public void getChartData(String time){
			if(time.equals("24hours")){
				getProcessByDay(1);			
			}
			else if(time.equals("7days")){
				getProcessByDay(7);			
			}
			else if(time.equals("30days")){
				getProcessByDay(30);
			}
		
		display.setChartData(pricesQuery);	
	}
	private void getProcessByDay(int day){
		final String url = account.getUri() +"/lastcompleted/" + day+"/get";
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
						pricesQuery = result.getElements();
						display.setChartData(pricesQuery);
						display.updateChartTable();
						
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
		

	
	public void updatetActivity(VirtualServer vs, Activity activity){
		if(activityPerVS == null) return;
		activityPerVS.put(vs, activity);
		display.refresh(this.vsList, this.activityPerVS);
	}

	/*
	 * -------------
	 * Data Handling
	 * -------------
	 */

	public void getActivity(final VirtualServer vs){
		String uri = vs.getActivity();
			// Send request to server and catch any errors.
			RequestBuilder builder = AuthenticatedRequestFactory.newRequest(RequestBuilder.GET, uri);
			try {
				Request request = builder.sendRequest(null, new RequestCallback() {
					public void onError(Request request, Throwable exception) {
						// displayError("Couldn't retrieve JSON "+exception.getMessage());
					}

					public void onResponseReceived(Request request, Response response) {
						GWT.log("Got reply");
						if (200 == response.getStatusCode()) {
							Activity activity = Activity.asActivity(response.getText());
							updatetActivity(vs, activity);
						} else {
						}
					}

				});
			} catch (RequestException e) {
				//displayError("Couldn't retrieve JSON "+e.getMessage());
			}
	}

	public void getVSList(){
		// Send request to server and catch any errors.
		RequestBuilder builder = AuthenticatedRequestFactory.newRequest(RequestBuilder.GET, virtualServerCollection);
		try {
			Request request = builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					// displayError("Couldn't retrieve JSON "+exception.getMessage());
				}

				public void onResponseReceived(Request request, Response response) {
					GWT.log("Got reply");
					if (200 == response.getStatusCode()) {
						VirtualServerCollection<VirtualServer> vs = VirtualServer.asCollection(response.getText());
						updateVSList(vs);
					} else {

					}
				}

			});
		} catch (RequestException e) {
			//displayError("Couldn't retrieve JSON "+e.getMessage());
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
						getAccount(account.getElements());
					} else {

					}
				}

			});
		} catch (RequestException e) {
			//displayError("Couldn't retrieve JSON "+e.getMessage());
		}
	}


	public void getAccount(List<Account> list){
		if(list == null || list.size() == 0)
			return;
		else
			for(Account account : list)
				if(account.getUri().equals(accountUri))
					updateAccount(account);
	}

	/*public void getAccount() {
		// Send request to server and catch any errors.
		RequestBuilder builder = AuthenticatedRequestFactory.newRequest(RequestBuilder.GET, accountUri);
		try {
			Request request = builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					// displayError("Couldn't retrieve JSON "+exception.getMessage());
				}

				public void onResponseReceived(Request request, Response response) {
					GWT.log("Got reply");
					if (200 == response.getStatusCode()) {
						Account account = Account.asAccount(response.getText());
						updateAccount(account);
					} else {

					}
				}

			});
		} catch (RequestException e) {
			//displayError("Couldn't retrieve JSON "+e.getMessage());
		}
	}*/


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

	public void onSelect(Activity selected) {
		History.newItem(historyMapper.getToken(new ProcessPlace(selected.getProgress())));
	}

}