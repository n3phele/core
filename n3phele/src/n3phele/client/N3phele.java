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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootPanel;

import n3phele.client.model.User;
import n3phele.client.presenter.AccountListPlace;
import n3phele.client.presenter.ActivityDashboardActivity;
import n3phele.client.presenter.ActivityListPlace;
import n3phele.client.presenter.CommandGridListPlace;
import n3phele.client.presenter.LoginPlace;
import n3phele.client.presenter.RepoListPlace;
import n3phele.client.presenter.helpers.AuthenticatedRequestFactory;
import n3phele.client.resource.N3pheleResource;
import n3phele.client.view.LoginView;
import n3phele.client.widgets.MenuItem;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class N3phele implements EntryPoint {

	public static final N3pheleResource n3pheleResource = GWT.create(N3pheleResource.class);
	public static BasePanel basePanel = null;
	public static FlexTable base = null;
	private final static Place defaultPlace =  new CommandGridListPlace("home");
	private static ClientFactory clientFactory = null;
	private static ActivityMapper activityMapper = null;
	private static ActivityManager activityManager = null;
	private static AppPlaceHistoryMapper historyMapper = null;
	private static PlaceHistoryHandler historyHandler = null;
	private static Activity lhs = null;

	/**
	 * @wbp.parser.entryPoint
	 */
	public void onModuleLoad() {

		StyleInjector.inject(n3pheleResource.css().getText());
		n3pheleResource.css().ensureInjected();
		if (clientFactory == null)
			clientFactory = GWT.create(ClientFactory.class);
		if (basePanel == null) {
			basePanel = new BasePanel(clientFactory);
			base = new FlexTable();
			RootPanel.get().add(base);
			base.addStyleName(N3phele.n3pheleResource.css().baseBackground());
			base.getRowFormatter().addStyleName(0, n3pheleResource.css().baseBackgroundRow());
			
			Window.addResizeHandler(new ResizeHandler() {
				public void onResize(ResizeEvent event) {
					int height = event.getHeight();
					int width = event.getWidth();
					base.setHeight(height + "px");
					base.setWidth(width + "px");
				}
			});
			//
			base.setWidget(0,0,basePanel);
			base.setSize(Window.getClientWidth()+"px", Window.getClientHeight() + "px");
			base.setBorderWidth(0);

			base.getCellFormatter().setAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_TOP);
		}

		if (activityMapper == null)
			activityMapper = new AppActivityMapper(clientFactory);
		if (activityManager == null) {
			activityManager = new ActivityManager(activityMapper,
					clientFactory.getEventBus());
			activityManager.setDisplay(basePanel);
		}

		if (!AuthenticatedRequestFactory.isAuthenticated()) {
			clientFactory.getPlaceController().goTo(
					new LoginPlace("authenticate"));
		} else {
			initHistory();
			basePanel.enableDecorations(initMainMenu());
			initLHS();
			historyHandler.handleCurrentHistory();

		}
	}
	
	private static List<MenuItem> initMainMenu() {
		List<MenuItem> result = new ArrayList<MenuItem>();
		
		result.add(new MenuItem(N3phele.n3pheleResource.repositoryIcon(), "Files",
				historyMapper.getToken(new RepoListPlace(null)), RepoListPlace.class));
		result.add(new MenuItem(N3phele.n3pheleResource.commandIcon(), "Commands", 
				historyMapper.getToken(new CommandGridListPlace(null)), CommandGridListPlace.class));
		result.add(new MenuItem(N3phele.n3pheleResource.activityIcon(), "Activity History", 
				historyMapper.getToken(new ActivityListPlace(null)), ActivityListPlace.class));
		result.add(new MenuItem(N3phele.n3pheleResource.accountIcon(), "Accounts", 
				historyMapper.getToken(new AccountListPlace(null)), AccountListPlace.class));
		
		return result;
	}

	public static void initLHS() {
		lhs = new ActivityDashboardActivity("dashboard", clientFactory);
		lhs.start(new AcceptsOneWidget() {
			
			@Override
			public void setWidget(IsWidget w) {
				basePanel.setLeftHandside(w);
				
			}
		}, clientFactory.getEventBus());
	}

	@SuppressWarnings("deprecation")
	public static void initHistory() {
		if (historyMapper == null)
			historyMapper = GWT.create(AppPlaceHistoryMapper.class);
		if (historyHandler == null) {
			historyHandler = new PlaceHistoryHandler(historyMapper);
			historyHandler.register(clientFactory.getPlaceController(),
					clientFactory.getEventBus(), defaultPlace);
		}

	}

	private static class Callback implements RequestCallback {
		@SuppressWarnings("unused")
		private String username;
		private String password;
		private LoginView view;

		public Callback(String username, String password, LoginView view) {
			this.username = username;
			this.password = password;
			this.view = view;
		}

		public void onError(Request request, Throwable exception) {
			view.setMessage("Login failure\n"+exception.getMessage());
		}

		public void onResponseReceived(Request request, Response response) {
			GWT.log("Got reply");
			if (200 == response.getStatusCode()) {
				User user = User.asUser(response.getText());
				AuthenticatedRequestFactory.setCredentials(user.getName(),
						password, user);

				view.clear();
				initHistory();
				basePanel.enableDecorations(initMainMenu());
				initLHS();
				historyHandler.handleCurrentHistory();
				clientFactory.getCacheManager().start();
			} else {
				view.setMessage("Login failure.\n Status ("
						+response.getStatusCode()+")"
						+response.getStatusText()+"\n"
						+response.getText());
			}
		}

	}

	public static void authenticate(String username, String password,
			LoginView loginView) {
		clientFactory.getCacheManager().test(username, password,
				new Callback(username, password, loginView));

	}
	
	public static void checkSize() {
		base.setSize(Window.getClientWidth()+"px", Window.getClientHeight()-1 + "px");
		base.setSize(Window.getClientWidth()+"px", Window.getClientHeight() + "px");
	}
}
