/**
 * @author Nigel Cook
 *
 * (C) Copyright 2010-2011. All rights reserved.
 */
package n3phele.client.presenter;


import java.util.HashMap;
import java.util.List;

import n3phele.client.AppPlaceHistoryMapper;
import n3phele.client.CacheManager;
import n3phele.client.ClientFactory;
import n3phele.client.model.Account;
import n3phele.client.model.Activity;
import n3phele.client.model.ActivityData;
import n3phele.client.model.ActivityDataCollection;
import n3phele.client.model.Collection;
import n3phele.client.model.CostsCollection;

import n3phele.client.presenter.helpers.AuthenticatedRequestFactory;
import n3phele.client.view.AccountHyperlinkView;

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
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class AccountHyperlinkActivity extends AbstractActivity {
	private final String accountUri;
	private final ClientFactory factory;
	private AccountHyperlinkView display;
	private Account account = null;
	private List<ActivityData> vsList;
	private Collection<ActivityData> vsCol;
	private final AppPlaceHistoryMapper historyMapper;
	private String accountCollection;
	private String virtualServerCollection;
	private final CacheManager cacheManager;
	private EventBus eventBus;
	private HandlerRegistration handlerRegistration;
	private HashMap<ActivityData, Activity> activityPerVS = null;
	private List<Double> pricesQuery;
	


	public AccountHyperlinkActivity(String accountUri, ClientFactory factory) {
		this.factory = factory;
		this.historyMapper = factory.getHistoryMapper();
		this.accountUri = accountUri;
		this.display = factory.getAccountHyperlinkView(accountUri);
		this.cacheManager = factory.getCacheManager();
		this.eventBus = factory.getEventBus();
		this.accountCollection = URL.encode(factory.getCacheManager().ServiceAddress + "account");
		String id = accountUri.substring(accountUri.lastIndexOf("/") + 1);
		this.virtualServerCollection = URL.encode(factory.getCacheManager().ServiceAddress );
		this.virtualServerCollection += id;
		
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		getAccountList();
		this.eventBus = eventBus;
		handlerRegistration(eventBus);
		display.setPresenter(this);
		panel.setWidget(display);
		display.requestChartData("24hours");
		display.onModuleLoad();
		this.initProcessUpdate();
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

	public void getChartData(String time) {
		if (time.equals("24hours")) {
			getProcessByDay(1);
		} else if (time.equals("7days")) {
			getProcessByDay(7);
		} else if (time.equals("30days")) {
			getProcessByDay(30);
		}

		display.setChartData(pricesQuery);
	}
	/*
	 * ------------- Data Handling -------------
	 */
	private void getProcessByDay(int day) {
		final String url = accountUri + "/lastcompleted/" + day + "/get";
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
						display.onModuleLoad();
					
					} else {
						GWT.log("Couldn't retrieve JSON (" + response.getStatusText() + ")");
					}
				}
			});
		} catch (RequestException e) {
			GWT.log("Couldn't retrieve JSON " + e.getMessage());
		}
		getRunningProcess();
	}

	private void getRunningProcess() {
		final String url = accountUri + "/runningprocess/get";
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
						display.setDisplayList(list);
					} else {
						GWT.log("Couldn't retrieve JSON (" + response.getStatusText() + ")");
					}
				}
			});
		} catch (RequestException e) {
			GWT.log("Couldn't retrieve JSON " + e.getMessage());
		}

	}


	public void updateActivity(ActivityData vs, Activity activity) {
		if (activityPerVS == null)
			return;
		activityPerVS.put(vs, activity);
		display.refresh(this.vsList, this.activityPerVS);
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
			// displayError("Couldn't retrieve JSON "+e.getMessage());
		}
	}

	public void getAccount(List<Account> list) {
		if (list == null || list.size() == 0)
			return;
		else
			for (Account account : list)
				if (account.getUri().equals(accountUri))
					updateAccount(account);
	}

	

	public interface AccountListUpdateEventHandler extends EventHandler {
		void onMessageReceived(AccountListUpdate commandListUpdate);
	}

	public static class AccountListUpdate extends GwtEvent<AccountListUpdateEventHandler> {
		public static Type<AccountListUpdateEventHandler> TYPE = new Type<AccountListUpdateEventHandler>();

		public AccountListUpdate() {
		}

		@Override
		public com.google.gwt.event.shared.GwtEvent.Type<AccountListUpdateEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(AccountListUpdateEventHandler handler) {
			handler.onMessageReceived(this);
		}
	}

	public void onSelect(ActivityData selected) {
		History.newItem(historyMapper.getToken(new ProcessPlace(selected.getUriTopLevel())));
	}

	/*
	 * ---------------- Timer Definition ----------------
	 */
	private void initProcessUpdate() {
		// setup timer to refresh list automatically

		Timer refreshTimer = new Timer() {
			public void run() {
				if (display.isAttached()) {
					updateCall();
				}else this.cancel();
			}
		};
		refreshTimer.scheduleRepeating(300000);
	}
	public void initTimerDelete(){
		final List<ActivityData>  param =  display.getVsData();
		Timer refreshTimer = new Timer() {
			public void run() {
				
				if(param == null || param != display.getVsData()){
					param.removeAll(display.getVsData());
					for (ActivityData activityData : param) {
						display.getVsData().remove(activityData);
					}
					updateCall();
					this.cancel();
				}else{
					updateCall();
					
				}
			}

			
		};
		refreshTimer.scheduleRepeating(5000);
	}
	private void updateCall() {
		getRunningProcess();
		display.onModuleLoad();
		
	}
}