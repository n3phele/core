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
import n3phele.client.model.Repository;
import n3phele.client.presenter.helpers.AuthenticatedRequestFactory;
import n3phele.client.view.RepoView;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class RepoActivity extends AbstractActivity {
	private final String repositoryUri;
	private EventBus eventBus;
	private final PlaceController placeController;
	private final RepoView display;
	private Repository repo = null;
	private final CacheManager cacheManager;
	public RepoActivity(String repositoryUri, ClientFactory factory) {
		this.cacheManager = factory.getCacheManager();
		this.repositoryUri = repositoryUri;
		this.placeController = factory.getPlaceController();
		this.display = factory.getRepoView();	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		this.eventBus = eventBus;

		display.setPresenter(this);
		panel.setWidget(display);
		if(repositoryUri == null || repositoryUri.length()==0 || repositoryUri.equals("null")) {
			this.repo =  JavaScriptObject.createObject().<Repository> cast();
			display.setData(this.repo);
		} else {
			display.setData(this.repo);
			getRepo();
		}


	}
	
	@Override
	public String mayStop() {
	    return null;
	}
	@Override
	public void onCancel() {
		
	}
	@Override
	public void onStop() {
		this.display.setData(null);
	}
	/**
	 * @param repo
	 * @param name
	 * @param description
	 * @param target
	 * @param kind
	 * @param root
	 * @param cloudId
	 * @param password
	 */
	public void onSave(Repository repo, String name, String description,
			String target, String kind, String root, boolean isPublic, String authId, String password) {
		updateRepoDetails(repo.getUri(), name, description, target, kind, root, isPublic, authId, 
				password);
	}


	protected void updateRepo(Repository item) {
		this.repo = item;
		display.setData(this.repo);
	}
	
	
	public void goToPrevious() {
		History.back();
	}


	
	/*
	 * Data Handling
	 * -------------
	 */
	

	public void getRepo() {
		// Send request to server and catch any errors.
		RequestBuilder builder = AuthenticatedRequestFactory.newRequest(RequestBuilder.GET, repositoryUri);
		try {
			Request request = builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					// displayError("Couldn't retrieve JSON "+exception.getMessage());
				}

				public void onResponseReceived(Request request, Response response) {
					GWT.log(response.getStatusCode()+" "+response.getText());
					if (200 == response.getStatusCode()) {
						Repository repo = Repository.asRepository(response.getText());
						updateRepo(repo);		
					} else {
						Window.alert("Update failure: "+response.getStatusText()+" "+response.getText());
					}
				}

			});
		} catch (RequestException e) {
			Window.alert("Update exception: "+e.toString());
		}
	}
	
	
	private void updateRepoDetails(String url, String name, String description,
			String target, String kind, String root, boolean isPublic, String authId, String password) {

		if(url==null || url.trim().length()==0) {
			url = cacheManager.ServiceAddress+"repository";
		}
		// Send request to server and catch any errors.
		RequestBuilder builder = AuthenticatedRequestFactory.newRequest(RequestBuilder.POST, url);
		builder.setHeader("Content-type", "application/x-www-form-urlencoded");
		StringBuilder args = new StringBuilder();
		args.append("name=");
		args.append(URL.encodeQueryString(name));
		if(description != null && description.length() !=0) {
			args.append("&description=");
			args.append(URL.encodeQueryString(description));
		}
		args.append("&target=");
		args.append(URL.encodeQueryString(target));
		args.append("&kind=");
		args.append(URL.encodeQueryString(kind));
		args.append("&root=");
		args.append(URL.encodeQueryString(root));
		args.append("&isPublic=");
		args.append(URL.encode(isPublic?"true":"false"));
		if(password != null && password.length() > 0) {
			args.append("&repositoryId=");
			args.append(URL.encodeQueryString(authId));
			args.append("&secret=");
			args.append(URL.encodeQueryString(password));
		}
		GWT.log(args.toString());
		GWT.log(authId);
		GWT.log(password);
		try {
			Request request = builder.sendRequest(args.toString(), new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					// displayError("Couldn't retrieve JSON "+exception.getMessage());
				}

				public void onResponseReceived(Request request, Response response) {
					GWT.log("Got reply");
					if (200 == response.getStatusCode()) {
						goToPrevious();
					} else if (201 == response.getStatusCode()) {
						goToPrevious();
					} else {
						Window.alert("Repository update error "+response.getStatusText()+" "+response.getText());
					}
				}

			});
		} catch (RequestException e) {
			//displayError("Couldn't retrieve JSON "+e.getMessage());
		}
	}



	public void onSelect(Repository selected) {
		// TODO Auto-generated method stub
		
	}

	
	/*
	 * Event Definition
	 * ----------------
	 */
	


}
