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
package n3phele.client;

import n3phele.client.presenter.AccountActivity;
import n3phele.client.presenter.AccountHyperlinkActivity;
import n3phele.client.presenter.AccountHyperlinkPlace;
import n3phele.client.presenter.AccountListActivity;
import n3phele.client.presenter.AccountListPlace;
import n3phele.client.presenter.AccountPlace;
import n3phele.client.presenter.ActivityDashboardActivity;
import n3phele.client.presenter.ActivityDashboardPlace;
import n3phele.client.presenter.ActivityListActivity;
import n3phele.client.presenter.ActivityListPlace;
import n3phele.client.presenter.ActivityPlace;
import n3phele.client.presenter.ActivtiyActivity;
import n3phele.client.presenter.CommandActivity;
import n3phele.client.presenter.CommandGridListPlace;
import n3phele.client.presenter.CommandListActivity;
import n3phele.client.presenter.CommandListPlace;
import n3phele.client.presenter.CommandPlace;
import n3phele.client.presenter.LoginActivity;
import n3phele.client.presenter.LoginPlace;
import n3phele.client.presenter.ProgressActivity;
import n3phele.client.presenter.ProgressPlace;
import n3phele.client.presenter.RepoActivity;
import n3phele.client.presenter.RepoContentActivity;
import n3phele.client.presenter.RepoContentPlace;
import n3phele.client.presenter.RepoListActivity;
import n3phele.client.presenter.RepoListPlace;
import n3phele.client.presenter.RepoPlace;
import n3phele.client.presenter.UserActivity;
import n3phele.client.presenter.UserPlace;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

public class AppActivityMapper implements ActivityMapper {

	private ClientFactory clientFactory;
	
	public AppActivityMapper(ClientFactory clientFactory) {
		super();
		this.clientFactory = clientFactory;
	}
	
	@Override
	public Activity getActivity(Place place) {
		GWT.log("Place called: " + place);
		N3phele.basePanel.updateMenu(place);
		if(place instanceof ActivityListPlace) {
			return new ActivityListActivity("activityList", clientFactory);
		} else if(place instanceof LoginPlace) {
			return new LoginActivity("login", clientFactory);
		} else if(place instanceof ActivityDashboardPlace) {
			return new ActivityDashboardActivity("activityDashboard", clientFactory);
		} else if(place instanceof CommandListPlace) {
			return new CommandListActivity("command", clientFactory);
		} else if(place instanceof CommandGridListPlace) {
			return new CommandListActivity("commandGrid", clientFactory);
		} else if(place instanceof CommandPlace) {
			return new CommandActivity("command-detail", ((CommandPlace) place).getPlaceName(), clientFactory);
		} else if(place instanceof UserPlace) {
			return new UserActivity(((UserPlace) place).getPlaceName(), clientFactory);
		} else if(place instanceof AccountPlace) {
			return new AccountActivity(((AccountPlace) place).getPlaceName(), clientFactory);
		} else if(place instanceof AccountListPlace) {
			return new AccountListActivity(((AccountListPlace) place).getPlaceName(), clientFactory);
		} else if(place instanceof AccountHyperlinkPlace) {
			return new AccountHyperlinkActivity(((AccountHyperlinkPlace) place).getPlaceName(), clientFactory);
		} else if(place instanceof RepoListPlace) {
			return new RepoListActivity(((RepoListPlace) place).getPlaceName(), clientFactory);
		} else if(place instanceof RepoPlace) {
			return new RepoActivity(((RepoPlace) place).getPlaceName(), clientFactory);
		} else if(place instanceof RepoContentPlace) {
			return new RepoContentActivity(((RepoContentPlace)place).getPlaceName(), ((RepoContentPlace) place).getPrefix(), clientFactory);
		}else if(place instanceof ProgressPlace) {
			return new ProgressActivity(((ProgressPlace) place).getPlaceName(), clientFactory);
		}	else if(place instanceof ActivityPlace) {
			return new ActivtiyActivity("activity-detail", 
					((ActivityPlace) place).getPlaceName(), clientFactory);
		}
		return null;
	}

}
