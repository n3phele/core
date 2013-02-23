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

import java.util.List;

import n3phele.client.AppPlaceHistoryMapper;
import n3phele.client.CacheManager;
import n3phele.client.ClientFactory;
import n3phele.client.model.Repository;
import n3phele.client.model.Collection;
import n3phele.client.presenter.helpers.AuthenticatedRequestFactory;
import n3phele.client.view.RepoListView;

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
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class RepoListActivity extends AbstractActivity {
	private EventBus eventBus;
	private final AppPlaceHistoryMapper historyMapper;
	private final RepoListView display;
	private List<Repository> repositoryList = null;
	private String repositoryCollection;
	private HandlerRegistration handlerRegistration;
	private final CacheManager cacheManager;
	public RepoListActivity(String repositoryUri, ClientFactory factory) {
		this.cacheManager = factory.getCacheManager();
		this.historyMapper = factory.getHistoryMapper();
		this.display = factory.getRepositoryListView();
		this.repositoryCollection = URL.encode(factory.getCacheManager().ServiceAddress + "repository");
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		this.eventBus = eventBus;
		handlerRegistration(eventBus);
		display.setPresenter(this);
		panel.setWidget(display);
		display.setDisplayList(this.repositoryList);
		getRepositoryList();

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
		unregister();
	}
	public void handlerRegistration(EventBus eventBus) {
		this.handlerRegistration = this.eventBus.addHandler(RepoListUpdate.TYPE, new RepoListUpdateEventHandler() {
			@Override
			public void onMessageReceived(RepoListUpdate event) {
				getRepositoryList();
			}
		});
		CacheManager.EventConstructor change = new CacheManager.EventConstructor() {
			@Override
			public RepoListUpdate newInstance(String key) {
				return new RepoListUpdate();
			}
		};
		cacheManager.register(cacheManager.ServiceAddress + "repository", "repoList", change);

		
	}

	protected void unregister() {
		cacheManager.unregister(cacheManager.ServiceAddress + "repository", "repoList");
	}

	protected void updateRepositoryList(List<Repository> list) {
		this.repositoryList = list;
		display.refresh(this.repositoryList);
	}
	
	
	public void goToPrevious() {
		History.back();
	}


	
	/*
	 * Data Handling
	 * -------------
	 */
	

	public void getRepositoryList() {
		// Send request to server and catch any errors.
		RequestBuilder builder = AuthenticatedRequestFactory.newRequest(RequestBuilder.GET, repositoryCollection);
		try {
			Request request = builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					// displayError("Couldn't retrieve JSON "+exception.getMessage());
				}

				public void onResponseReceived(Request request, Response response) {
					GWT.log("Got reply");
					if (200 == response.getStatusCode()) {
						Collection<Repository> repository = Repository.asCollection(response.getText());
						updateRepositoryList(repository.getElements());
					} else {

					}
				}

			});
		} catch (RequestException e) {
			//displayError("Couldn't retrieve JSON "+e.getMessage());
		}
	}
	
	public void onSelect(Repository selected) {
		History.newItem(historyMapper.getToken(new RepoContentPlace(selected.getUri())));
	}
	
	
	/*
	 * Event Definition
	 * ----------------
	 */
	
	public interface RepoListUpdateEventHandler extends EventHandler {
		void onMessageReceived(RepoListUpdate commandListUpdate);
	}
	public static class RepoListUpdate extends GwtEvent<RepoListUpdateEventHandler> {
		public static Type<RepoListUpdateEventHandler> TYPE = new Type<RepoListUpdateEventHandler>();
		public RepoListUpdate() {}
		@Override
		public com.google.gwt.event.shared.GwtEvent.Type<RepoListUpdateEventHandler> getAssociatedType() {
			return TYPE;
		}
		@Override
		protected void dispatch(RepoListUpdateEventHandler handler) {
			handler.onMessageReceived(this);
		}
	}
}
