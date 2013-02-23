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
import n3phele.client.view.LoginView;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class LoginActivity extends AbstractActivity {
	private final String name;
	private EventBus eventBus;
	private final PlaceController placeController;
	private final LoginView display;
	public LoginActivity(String name, ClientFactory factory) {
		this.name = name;
		this.placeController = factory.getPlaceController();
		this.display = factory.getLoginView();
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		this.eventBus = eventBus;

		// display.setPresenter(this);
		panel.setWidget(display);

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
		
	}
	
	/*
	 * Data Handling
	 * -------------
	 */
	




	
	/*
	 * Event Definition
	 * ----------------
	 */
	


}

