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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import n3phele.client.AppPlaceHistoryMapper;
import n3phele.client.CacheManager;
import n3phele.client.ClientFactory;
import n3phele.client.model.CloudProcessSummary;
import n3phele.client.model.Collection;
import n3phele.client.presenter.helpers.AuthenticatedRequestFactory;
import n3phele.client.presenter.helpers.ProcessUpdateHelper;
import n3phele.client.view.CloudProcessView;

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
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public abstract class AbstractCloudProcessActivity extends AbstractActivity {

	protected final String name;
	protected final CacheManager cacheManager;
	protected EventBus eventBus;
	protected final PlaceController placeController;
	protected final CloudProcessView display;
	private List<CloudProcessSummary> cloudProcessSummaryList = null;
	private HandlerRegistration handlerRegistration;
	private Set<String> interests = new HashSet<String>();
	private final String collectionUrl;
	private AppPlaceHistoryMapper historyMapper;
	private HandlerRegistration itemUpdateHandlerRegistration;
	private Timer refreshTimer = null;
	private int start;
	private int total;
	private boolean countAll;
	private int max;
	private int pageSize;

	public AbstractCloudProcessActivity(String name, ClientFactory factory, CloudProcessView activityView) {
		this(name, factory, activityView, false);
	}
	
	
	
	
	public AbstractCloudProcessActivity(String name, ClientFactory factory, CloudProcessView activityView, boolean countAll) {
		super();
		this.name = name;
		this.cacheManager = factory.getCacheManager();
		this.placeController = factory.getPlaceController();
		this.display = activityView;
		this.historyMapper = factory.getHistoryMapper();
		this.collectionUrl = URL.encode(cacheManager.ServiceAddress + "process");
		this.total = 0;
		this.countAll = countAll;
	}
	
	
	public interface ProcessListUpdateEventHandler extends EventHandler {
		void onMessageReceived(ProcessListUpdate event);
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		this.eventBus = eventBus;
		handlerRegistration(eventBus);
		start = 0;
		max = 0;
		pageSize = display.getPageSize();
		display.setDisplayList(cloudProcessSummaryList, start, max);
		display.setPresenter(this);
		panel.setWidget(display);
		
		initData();
		initProcessUpdate();
	}
	
	public void handlerRegistration(EventBus eventBus) {
		this.handlerRegistration = this.eventBus.addHandler(ProcessListUpdate.TYPE, new ProcessListUpdateEventHandler() {
			@Override
			public void onMessageReceived(ProcessListUpdate event) {
				refresh(start);
			}
		});
		this.itemUpdateHandlerRegistration = this.eventBus.addHandler(ProcessUpdate.TYPE, new ProcessUpdateEventHandler() {
			@Override
			public void onMessageReceived(ProcessUpdate event) {
				refresh(event.getKey());
			}
		});
	}

	private void initProcessUpdate() {
		// setup timer to refresh list automatically
		Timer refreshTimer = new Timer() {
			public void run()
			{
				if(cloudProcessSummaryList != null) {
					for(int i=0; i < cloudProcessSummaryList.size(); i++) {
						CloudProcessSummary summary = cloudProcessSummaryList.get(i);
						String state = summary.getState();
						if(!"COMPLETE".equals(state) && !"FAILED".equals(state) && !"CANCELLED".equals(state)) {
							//int update = updateProgress(summary);
							// if(update != summary.getPercentx10Complete()) {
								//progress.setPercentagex10Complete(update);
								display.refresh(i, summary);
							//}
						}
					}
				}
			}
		};
		refreshTimer.scheduleRepeating(30000);
		
	}
	
	private void killRefreshTimer() {
		if(refreshTimer != null) {
			refreshTimer.cancel();
			refreshTimer = null;
			
		}
	}

	private int updateProgress(CloudProcessSummary process) {
		return ProcessUpdateHelper.updateProcess(process);
	}

	@Override
	public String mayStop() {
	    return null;
	}

	@Override
	public void onCancel() {
		killRefreshTimer();
		unregister();
	}

	@Override
	public void onStop() {
		killRefreshTimer();
		unregister();
	}

	public void goTo(Place place) {
		this.placeController.goTo(place);
	}
	
	public void onSelect(CloudProcessSummary selected) {
		History.newItem(historyMapper.getToken(new ProcessPlace(selected.getUri())));
	}

	protected void initData() {
		CacheManager.EventConstructor change = new CacheManager.EventConstructor() {
			@Override
			public ProcessListUpdate newInstance(String key) {
				return new ProcessListUpdate();
			}
		};
		cacheManager.register(cacheManager.ServiceAddress + "process", this.name, change);
		this.refresh(start);
	}

	protected void unregister() {
		cacheManager.unregister(cacheManager.ServiceAddress + "process", this.name);
		for(String s : interests) {
			cacheManager.unregister(s, this.name);
		}
		//this.handlerRegistration.removeHandler();
	}

	protected void updateData(String uri, List<CloudProcessSummary> update, int max) {
		
		this.total = max;
		
		GWT.log("Max activities is "+max);
		if(update != null) {
			CacheManager.EventConstructor constructor = new CacheManager.EventConstructor() {
				@Override
				public ProcessUpdate newInstance(String key) {
					return new ProcessUpdate(key);
				}
			};
			Set<String> nonInterests = new HashSet<String>();
			nonInterests.addAll(interests);
			for(CloudProcessSummary p : update) {
				if(!interests.contains(p.getUri())) {
					cacheManager.register(p.getUri(), this.name, constructor);
					interests.add(p.getUri());
				} else {
					nonInterests.remove(p.getUri());
				}
			}
			for(String outOfView : nonInterests) {
				cacheManager.unregister(outOfView, this.name);
				interests.remove(outOfView);
			}
		}
		cloudProcessSummaryList = update;
		display.setDisplayList(cloudProcessSummaryList, start, max);
	}

	protected void updateData(String uri, CloudProcessSummary update) {
		if(update != null) {
			for(int i=0; i < cloudProcessSummaryList.size(); i++) {
				CloudProcessSummary p = cloudProcessSummaryList.get(i);
				if(p.getUri().equals(update.getUri())) {
					cloudProcessSummaryList.set(i, update);
					display.refresh(i, update);
					return;
				}
			}
		}
	}

	public void refresh(int start) {
	
		String url = buildUrlForProcesses(start);		
		this.start = start;
		
		final int total = this.total;
		
		// Send request to server and catch any errors.
		RequestBuilder builder = AuthenticatedRequestFactory.newRequest(RequestBuilder.GET, url);
		try {
			builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					GWT.log("Couldn't retrieve JSON " + exception.getMessage());
				}
	
				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) {
						GWT.log("got cloudProcess");
						Collection<CloudProcessSummary> c = CloudProcessSummary.asCollection(response.getText());
							
						int collectionSize = total;
						if(collectionSize == 0)
						{
							collectionSize = c.getTotal();
						}
						updateData(c.getUri(), c.getElements(), collectionSize);
						
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

	protected String buildUrlForProcesses(int start) {
		boolean count = false;
		
		//If wants to get the total count of elements then
		if(countAll)
		{	//If already don't have a total number or is zero, will ask again
			if(this.total == 0) count = true;
		}
		
		String url = collectionUrl ;
		url += "?summary=true&start="+start+"&end="+(start+pageSize);
		
		//if needs to count all existent, ask for it in the url
		if(count)
		{			
			url += "&count=true";
		}
		return url;
	}

	protected void refresh(String key) {
	
		String url = URL.encode(key+"?summary=true");
		// Send request to server and catch any errors.
		RequestBuilder builder = AuthenticatedRequestFactory.newRequest(RequestBuilder.GET, url);
		try {
			builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					GWT.log("Couldn't retrieve JSON " + exception.getMessage());
				}
	
				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) {
						CloudProcessSummary p = CloudProcessSummary.asCloudProcessSummary(response.getText());
						updateData(p.getUri(), p);
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

	public static class ProcessListUpdate extends GwtEvent<ProcessListUpdateEventHandler> {
		public static Type<ProcessListUpdateEventHandler> TYPE = new Type<ProcessListUpdateEventHandler>();
		public ProcessListUpdate() {}
		@Override
		public com.google.gwt.event.shared.GwtEvent.Type<ProcessListUpdateEventHandler> getAssociatedType() {
			return TYPE;
		}
		@Override
		protected void dispatch(ProcessListUpdateEventHandler handler) {
			handler.onMessageReceived(this);
		}
	}
	public interface ProcessUpdateEventHandler extends EventHandler {
		void onMessageReceived(ProcessUpdate event);
	}
	
	public static class ProcessUpdate extends GwtEvent<ProcessUpdateEventHandler> {
		public static Type<ProcessUpdateEventHandler> TYPE = new Type<ProcessUpdateEventHandler>();
		private final String key;
		public ProcessUpdate(String key) {this.key = key;}
		@Override
		public com.google.gwt.event.shared.GwtEvent.Type<ProcessUpdateEventHandler> getAssociatedType() {
			return TYPE;
		}
		@Override
		protected void dispatch(ProcessUpdateEventHandler handler) {
			handler.onMessageReceived(this);
		}
		public String getKey() { return this.key; }
	}

}