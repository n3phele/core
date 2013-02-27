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

import n3phele.client.AppPlaceHistoryMapper;
import n3phele.client.CacheManager;
import n3phele.client.ClientFactory;
import n3phele.client.N3phele;
import n3phele.client.model.CloudProcess;
import n3phele.client.model.CloudProcessSummary;
import n3phele.client.presenter.AbstractCloudProcessActivity.ProcessUpdate;
import n3phele.client.presenter.AbstractCloudProcessActivity.ProcessUpdateEventHandler;
import n3phele.client.presenter.helpers.AuthenticatedRequestFactory;
import n3phele.client.presenter.helpers.ProcessUpdateHelper;
import n3phele.client.view.ProcessView;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;

public class ProcessActivity extends AbstractActivity {
	private final String processUri;
	private final ProcessView display;
	private CloudProcess process = null;
	private final CacheManager cacheManager;
	private final EventBus eventBus;
	private HandlerRegistration itemUpdateHandlerRegistration;
	private Timer refreshTimer = null;
	private AppPlaceHistoryMapper historyMapper;
	public ProcessActivity(String processUri, ClientFactory factory) {
		this.processUri = processUri;
		this.display = factory.getProcessView();
		this.cacheManager = factory.getCacheManager();
		this.eventBus = factory.getEventBus();
		this.historyMapper = factory.getHistoryMapper();
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		display.setPresenter(this);
		panel.setWidget(display);
		display.setData(this.process);
		handlerRegistration(eventBus);
		initProcessUpdate();
		getProcess();

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
		this.display.setData(null);
	}

	protected void clearLHS() {
		IsWidget w = N3phele.basePanel.getLeftHandside();
	}

	protected void updateProcess(CloudProcess process) {
		this.process = process;
		display.setData(this.process);
	}
	
	
	public void goToPrevious() {
		History.back();
	}


	
	/*
	 * Data Handling
	 * -------------
	 */
	

	public void getProcess() {
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
						updateProcess(process);
					} else {

					}
				}

			});
		} catch (RequestException e) {
			//displayError("Couldn't retrieve JSON "+e.getMessage());
		}
	}
	
	private void initProcessUpdate() {
		// setup timer to refresh list automatically
		refreshTimer = new Timer() {
			public void run()
			{
				if(process != null) {
					String status = process.getState();
					if(!"COMPLETE".equals(status) && !"FAILED".equals(status) && !"CANCELLED".equals(status)) {
						//int update = updateProgressCounter(process);
						//if(update != process.getPercentx10Complete()) {
							//progress.setPercentagex10Complete(update);
							display.refresh(process);
						//}
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

	private int updateProcessCounter(CloudProcessSummary process) {
		return ProcessUpdateHelper.updateProcess(process);
	}
	

	

	
	/*
	 * Event Definition
	 * ----------------
	 */
	

	public void handlerRegistration(EventBus eventBus) {
		this.itemUpdateHandlerRegistration = this.eventBus.addHandler(ProcessUpdate.TYPE, new ProcessUpdateEventHandler() {
			@Override
			public void onMessageReceived(ProcessUpdate event) {
				if(event.getKey().equals(processUri))
						getProcess();
			}
		});
	}
	
	

	protected void unregister() {
		this.itemUpdateHandlerRegistration.removeHandler();
	}

	public String getToken(String processUri) {
		return historyMapper.getToken(getPlace(processUri));
	}
	
	public com.google.gwt.place.shared.Place getPlace(String uri) {
		if(uri != null) {
			if(uri.contains("/process/")) {
				return new ProcessPlace(uri);
			} else if(uri.contains("/command/")) {
				return new CommandPlace(uri);
			} 
		}
		return Place.NOWHERE;
	}

}
