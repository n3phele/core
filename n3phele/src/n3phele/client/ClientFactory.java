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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.PlaceController;

import n3phele.client.presenter.AccountHyperlinkActivity;
import n3phele.client.resource.N3pheleResource;
import n3phele.client.view.AccountHyperlinkView;
import n3phele.client.view.AccountListView;
import n3phele.client.view.AccountView;
import n3phele.client.view.ActivityCommandView;
import n3phele.client.view.CommandDetailView;
import n3phele.client.view.CommandListGridView;
import n3phele.client.view.CommandListView;
import n3phele.client.view.ActivityListView;
import n3phele.client.view.CompleteAccountView;
import n3phele.client.view.LoginView;
import n3phele.client.view.ProgressView;
import n3phele.client.view.RecentActivityView;
import n3phele.client.view.RepoContentView;
import n3phele.client.view.RepoListView;
import n3phele.client.view.RepoView;
import n3phele.client.view.UserView;

public class ClientFactory {

	private final EventBus eventBus;
	private PlaceController placeController=null;
	private final CacheManager cacheManager;
	public N3pheleResource n3pheleResource = null;
	private AppPlaceHistoryMapper historyMapper;
	private ActivityListView detailedActivityView=null;
	private LoginView loginView=null;
	private RecentActivityView recentActivity = null;
	private CommandListView commandListView = null;
	private CommandDetailView commandDetailView = null;
	private CompleteAccountView completeAccountView = null;
	private UserView userView = null;
	private AccountView accountView = null;
	private AccountListView accountListView = null;
	private AccountHyperlinkView accountHyperlinkView = null;
	private RepoListView repoListView = null;
	private RepoView repoView = null;
	private ProgressView progressView = null;
	private ActivityCommandView activityCommandView = null;
	private RepoContentView repoContentView;
	private CommandListGridView commandListGridView;
	
	public ClientFactory() {
		eventBus = new SimpleEventBus();
		cacheManager = new CacheManager(eventBus);
	}

	/**
	 * @return the eventBus
	 */
	public EventBus getEventBus() {
		return eventBus;
	}

	/**
	 * @return the placeController
	 */
	public PlaceController getPlaceController() {
		if(placeController == null) placeController = new PlaceController(getEventBus());
		return placeController;
	}

	/**
	 * @return the cacheManager
	 */
	public CacheManager getCacheManager() {
		return cacheManager;
	}

	/**
	 * @return the n3pheleResource
	 */
	public N3pheleResource getN3pheleResource() {
		if(n3pheleResource == null) n3pheleResource = GWT.create(N3pheleResource.class);
		return n3pheleResource;
	}
	
	
	
	/**
	 * @return the historyMapper
	 */
	public AppPlaceHistoryMapper getHistoryMapper() {
		if(historyMapper == null) historyMapper = GWT.create(AppPlaceHistoryMapper.class);
		return this.historyMapper;
	}

	/**
	 * @return the loginView
	 */
	public LoginView getLoginView() {
		if(loginView == null) loginView = new LoginView(getCacheManager().ServiceAddress);
		return loginView;
	}
	
	/**
	 * @return the detailedActivityView
	 */
	public ActivityListView getDetailedActivityView() {
		if(detailedActivityView == null) detailedActivityView = new ActivityListView();
		return detailedActivityView;
	}
	
	/**
	 * @return the recentActivityView
	 */
	public RecentActivityView getRecentActivityView() {
		if(recentActivity == null) recentActivity = new RecentActivityView();
		return recentActivity;
	}

	/**
	 * @return the commandListView
	 */
	public CommandListView getCommandListView() {
		if(commandListView == null) commandListView = new CommandListView();
		return this.commandListView;
	}
	
	/**
	 * @return the commandListView
	 */
	public CommandListGridView getCommandListGridView() {
		if(commandListGridView == null) commandListGridView = new CommandListGridView();
		return this.commandListGridView;
	}

	public CommandDetailView getCommandDetailView() {
		if(commandDetailView == null) commandDetailView = new CommandDetailView();
		return this.commandDetailView;
	}

	public UserView getUserView() {
		if(userView == null) userView = new UserView();
		return this.userView;
	}

	public AccountView getAccountView() {
		if(accountView == null) accountView = new AccountView();
		return this.accountView;
	}
	
	public CompleteAccountView getCompleteAccountView() {
		if(completeAccountView == null) completeAccountView = new CompleteAccountView();
		return this.completeAccountView;
	}

	public AccountListView getAccountListView() {
		if(accountListView == null) accountListView = new AccountListView();
		return this.accountListView;
	}
	
	public AccountHyperlinkView getAccountHyperlinkView(String accountUri) {
		if(accountHyperlinkView == null) 
			accountHyperlinkView = new AccountHyperlinkView(accountUri);
		return this.accountHyperlinkView;
	}

	public RepoListView getRepositoryListView() {
		if(repoListView == null) repoListView = new RepoListView();
		return this.repoListView;
	}

	public RepoView getRepoView() {
		if(repoView == null) repoView = new RepoView();
		return this.repoView;
	}
	
	public RepoContentView getRepoContentView() {
		if(repoContentView == null) repoContentView = new RepoContentView();
		return this.repoContentView;
	}

	public ProgressView getProgressView() {
		if(progressView == null) progressView = new ProgressView();
		return this.progressView;
	}
	
	/**
	 * @return
	 */
	public ActivityCommandView getActivityCommandView() {
		if(activityCommandView == null) activityCommandView = new ActivityCommandView();
		return this.activityCommandView;
	}

}
