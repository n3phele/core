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

import n3phele.client.ClientFactory;
import n3phele.client.N3phele;
import n3phele.client.model.User;
import n3phele.client.presenter.helpers.AuthenticatedRequestFactory;
import n3phele.client.view.UserView;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
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

public class UserActivity extends AbstractActivity {
	private final String name;
	private EventBus eventBus;
	private final PlaceController placeController;
	private final UserView display;
	private final String userUrl;
	private User user = null;
	private final boolean isSelf;
	public UserActivity(String name, ClientFactory factory) {
		if(name == null || name.length()==0) {
			name = AuthenticatedRequestFactory.getUser().getName();
		}
		if(isSelf = name.equals(AuthenticatedRequestFactory.getUser().getName())) {
			this.user = AuthenticatedRequestFactory.getUser();
		}
		this.name = name;
		this.placeController = factory.getPlaceController();
		this.display = factory.getUserView();
		userUrl = URL.encode(factory.getCacheManager().ServiceAddress + "user");
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		this.eventBus = eventBus;

		display.setPresenter(this);
		panel.setWidget(display);
		display.setData(this.user);

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
		//this.display.setData(null);
	}
	
	public void onSave(User user, String email, String firstName, String lastName, String password) {
		updateUserDetails(user.getUri(), email, firstName, lastName, password);
	}
	
	private void updateUserDetails(String url, String email, String firstName, String lastName, final String password) {

		// Send request to server and catch any errors.
		RequestBuilder builder = AuthenticatedRequestFactory.newRequest(RequestBuilder.POST, url);
		builder.setHeader("Content-type", "application/x-www-form-urlencoded");
		StringBuilder args = new StringBuilder();
		args.append("email=");
		args.append(URL.encodeQueryString(email));
		args.append("&firstName=");
		args.append(URL.encodeQueryString(firstName));
		args.append("&lastName=");
		args.append(URL.encodeQueryString(lastName));
		if(password != null && password.length() > 0) {
			args.append("&secret=");
			args.append(URL.encodeQueryString(password));
		}
		try {
			Request request = builder.sendRequest(args.toString(), new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					// displayError("Couldn't retrieve JSON "+exception.getMessage());
				}

				public void onResponseReceived(Request request, Response response) {
					GWT.log("Got reply");
					if (200 == response.getStatusCode()) {
						if(isSelf) {
							User user = User.asUser(response.getText());
							String pw = password;
							if(pw==null || pw.length()==0) {
								pw = AuthenticatedRequestFactory.getDefaultPassword();
							}
							AuthenticatedRequestFactory.setCredentials(user.getName(), pw, user);
							N3phele.basePanel.updateUser(user);
						}
						goToPrevious();
					} else {
						Window.alert("Update failure: "+response.getStatusText()+" "+response.getText());
					}
				}

			});
		} catch (RequestException e) {
			Window.alert("Update exception: "+e.toString());
		}
	}
	
	
	
	public void goToPrevious() {
		History.back();
	}	
	
}
