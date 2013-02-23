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

import n3phele.client.CacheManager;
import n3phele.client.ClientFactory;
import n3phele.client.N3phele;
import n3phele.client.model.Progress;
import n3phele.client.presenter.AbstractActivityProgressActivity.ProgressUpdate;
import n3phele.client.presenter.AbstractActivityProgressActivity.ProgressUpdateEventHandler;
import n3phele.client.presenter.helpers.AuthenticatedRequestFactory;
import n3phele.client.presenter.helpers.ProgressUpdateHelper;
import n3phele.client.view.ProgressView;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;

public class ProgressActivity extends AbstractActivity {
	private final String progressUri;
	private final ProgressView display;
	private Progress progress = null;
	private final CacheManager cacheManager;
	private final EventBus eventBus;
	private HandlerRegistration itemUpdateHandlerRegistration;
	private Timer refreshTimer = null;
	public ProgressActivity(String progressUri, ClientFactory factory) {
		this.progressUri = progressUri;
		this.display = factory.getProgressView();
		this.cacheManager = factory.getCacheManager();
		this.eventBus = factory.getEventBus();
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		display.setPresenter(this);
		panel.setWidget(display);
		display.setData(this.progress);
		handlerRegistration(eventBus);
		initProgressUpdate();
		getProgress();

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

	protected void updateProgress(Progress progress) {
		this.progress = progress;
		display.setData(this.progress);
	}
	
	
	public void goToPrevious() {
		History.back();
	}


	
	/*
	 * Data Handling
	 * -------------
	 */
	

	public void getProgress() {
		// Send request to server and catch any errors.
		RequestBuilder builder = AuthenticatedRequestFactory.newRequest(RequestBuilder.GET, progressUri);
		try {
			Request request = builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					// displayError("Couldn't retrieve JSON "+exception.getMessage());
				}

				public void onResponseReceived(Request request, Response response) {
					GWT.log("Got reply");
					if (200 == response.getStatusCode()) {
						Progress progress = Progress.asProgress(response.getText());
						updateProgress(progress);
					} else {

					}
				}

			});
		} catch (RequestException e) {
			//displayError("Couldn't retrieve JSON "+e.getMessage());
		}
	}
	
	private void initProgressUpdate() {
		// setup timer to refresh list automatically
		refreshTimer = new Timer() {
			public void run()
			{
				if(progress != null) {
					String status = progress.getStatus();
					if(!"COMPLETE".equals(status) && !"FAILED".equals(status) && !"CANCELLED".equals(status)) {
						int update = updateProgressCounter(progress);
						if(update != progress.getPercentx10Complete()) {
							//progress.setPercentagex10Complete(update);
							display.refresh(progress);
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

	private int updateProgressCounter(Progress progress) {
		return ProgressUpdateHelper.updateProgress(progress);
	}
	

	

	
	/*
	 * Event Definition
	 * ----------------
	 */
	

	public void handlerRegistration(EventBus eventBus) {
		this.itemUpdateHandlerRegistration = this.eventBus.addHandler(ProgressUpdate.TYPE, new ProgressUpdateEventHandler() {
			@Override
			public void onMessageReceived(ProgressUpdate event) {
				if(event.getKey().equals(progressUri))
						getProgress();
			}
		});
	}
	
	

	protected void unregister() {
		this.itemUpdateHandlerRegistration.removeHandler();
	}

}
