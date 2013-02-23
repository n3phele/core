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
import n3phele.client.model.Collection;
import n3phele.client.model.Progress;
import n3phele.client.presenter.helpers.AuthenticatedRequestFactory;
import n3phele.client.presenter.helpers.ProgressUpdateHelper;
import n3phele.client.view.ActivityView;

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

public abstract class AbstractActivityProgressActivity extends AbstractActivity {

	protected final String name;
	protected final CacheManager cacheManager;
	protected EventBus eventBus;
	protected final PlaceController placeController;
	protected final ActivityView display;
	private List<Progress> progressList = null;
	private HandlerRegistration handlerRegistration;
	private Set<String> interests = new HashSet<String>();
	private final String collectionUrl;
	private AppPlaceHistoryMapper historyMapper;
	private HandlerRegistration itemUpdateHandlerRegistration;
	private Timer refreshTimer = null;
	private int start;
	private int max;
	private int pageSize;

	public AbstractActivityProgressActivity(String name, ClientFactory factory, ActivityView activityView) {
		super();
		this.name = name;
		this.cacheManager = factory.getCacheManager();
		this.placeController = factory.getPlaceController();
		this.display = activityView;
		this.historyMapper = factory.getHistoryMapper();
		this.collectionUrl = URL.encode(cacheManager.ServiceAddress + "progress");
	}
	
	
	public interface ProgressListUpdateEventHandler extends EventHandler {
		void onMessageReceived(ProgressListUpdate event);
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		this.eventBus = eventBus;
		handlerRegistration(eventBus);
		start = 0;
		max = 0;
		pageSize = display.getPageSize();
		display.setDisplayList(progressList, start, max);
		display.setPresenter(this);
		panel.setWidget(display);
		
		initData();
		initProgressUpdate();
	}
	
	public void handlerRegistration(EventBus eventBus) {
		this.handlerRegistration = this.eventBus.addHandler(ProgressListUpdate.TYPE, new ProgressListUpdateEventHandler() {
			@Override
			public void onMessageReceived(ProgressListUpdate event) {
				refresh(start);
			}
		});
		this.itemUpdateHandlerRegistration = this.eventBus.addHandler(ProgressUpdate.TYPE, new ProgressUpdateEventHandler() {
			@Override
			public void onMessageReceived(ProgressUpdate event) {
				refresh(event.getKey());
			}
		});
	}

	private void initProgressUpdate() {
		// setup timer to refresh list automatically
		Timer refreshTimer = new Timer() {
			public void run()
			{
				if(progressList != null) {
					for(int i=0; i < progressList.size(); i++) {
						Progress progress = progressList.get(i);
						String status = progress.getStatus();
						if(!"COMPLETE".equals(status) && !"FAILED".equals(status) && !"CANCELLED".equals(status)) {
							int update = updateProgress(progress);
							if(update != progress.getPercentx10Complete()) {
								//progress.setPercentagex10Complete(update);
								display.refresh(i, progress);
							}
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

	private int updateProgress(Progress progress) {
		return ProgressUpdateHelper.updateProgress(progress);
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
	
	public void onSelect(Progress selected) {
		History.newItem(historyMapper.getToken(new ProgressPlace(selected.getUri())));
	}

	protected void initData() {
		CacheManager.EventConstructor change = new CacheManager.EventConstructor() {
			@Override
			public ProgressListUpdate newInstance(String key) {
				return new ProgressListUpdate();
			}
		};
		cacheManager.register(cacheManager.ServiceAddress + "progress", this.name, change);
		this.refresh(start);
	}

	protected void unregister() {
		cacheManager.unregister(cacheManager.ServiceAddress + "progress", this.name);
		for(String s : interests) {
			cacheManager.unregister(s, this.name);
		}
		//this.handlerRegistration.removeHandler();
	}

	protected void updateData(String uri, List<Progress> update, int max) {
		GWT.log("Max activities is "+max);
		if(update != null) {
			CacheManager.EventConstructor constructor = new CacheManager.EventConstructor() {
				@Override
				public ProgressUpdate newInstance(String key) {
					return new ProgressUpdate(key);
				}
			};
			Set<String> nonInterests = new HashSet<String>();
			nonInterests.addAll(interests);
			for(Progress p : update) {
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
		progressList = update;
		display.setDisplayList(progressList, start, max);
	}

	protected void updateData(String uri, Progress update) {
		if(update != null) {
			for(int i=0; i < progressList.size(); i++) {
				Progress p = progressList.get(i);
				if(p.getUri().equals(update.getUri())) {
					progressList.set(i, update);
					display.refresh(i, update);
					return;
				}
			}
		}
	}

	public void refresh(int start) {
	
		String url = collectionUrl;
		url += "?summary=true&start="+start+"&end="+(start+pageSize-1);
		this.start = start;
		// Send request to server and catch any errors.
		RequestBuilder builder = AuthenticatedRequestFactory.newRequest(RequestBuilder.GET, url);
		try {
			builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					GWT.log("Couldn't retrieve JSON " + exception.getMessage());
				}
	
				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) {
						GWT.log("got progress");
						Collection<Progress> c = Progress.asCollection(response.getText());
						updateData(c.getUri(), c.getElements(), c.getTotal());
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
						Progress p = Progress.asProgress(response.getText());
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

	public static class ProgressListUpdate extends GwtEvent<ProgressListUpdateEventHandler> {
		public static Type<ProgressListUpdateEventHandler> TYPE = new Type<ProgressListUpdateEventHandler>();
		public ProgressListUpdate() {}
		@Override
		public com.google.gwt.event.shared.GwtEvent.Type<ProgressListUpdateEventHandler> getAssociatedType() {
			return TYPE;
		}
		@Override
		protected void dispatch(ProgressListUpdateEventHandler handler) {
			handler.onMessageReceived(this);
		}
	}
	public interface ProgressUpdateEventHandler extends EventHandler {
		void onMessageReceived(ProgressUpdate event);
	}
	
	public static class ProgressUpdate extends GwtEvent<ProgressUpdateEventHandler> {
		public static Type<ProgressUpdateEventHandler> TYPE = new Type<ProgressUpdateEventHandler>();
		private final String key;
		public ProgressUpdate(String key) {this.key = key;}
		@Override
		public com.google.gwt.event.shared.GwtEvent.Type<ProgressUpdateEventHandler> getAssociatedType() {
			return TYPE;
		}
		@Override
		protected void dispatch(ProgressUpdateEventHandler handler) {
			handler.onMessageReceived(this);
		}
		public String getKey() { return this.key; }
	}

}