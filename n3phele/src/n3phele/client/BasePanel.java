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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import n3phele.client.model.User;
import n3phele.client.presenter.helpers.AuthenticatedRequestFactory;
import n3phele.client.resource.MenuTreeResource;
import n3phele.client.widgets.MainMenuTreeViewModel;
import n3phele.client.widgets.MenuItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.CellTree.Resources;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.VerticalPanel;

public class BasePanel extends FlexTable implements AcceptsOneWidget {
	public HTML basePanelName;
	private HorizontalPanel menu;
	private VerticalPanel lhs;
	private Image n3phele = null;
	private HTML siteText;
	private CellTree navigation;
	private boolean gotDecorations = false;
	private Map<Class<? extends Place>, MenuItem> menuMap = new HashMap<Class<? extends Place>, MenuItem>();
	private MainMenuTreeViewModel menuModel;
	private n3phele.client.ClientFactory clientFactory;
	private LayoutPanel layout;
	private Image displayingIcon = null;
	private boolean displayingN3phele = false;

	public BasePanel(n3phele.client.ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		this.basePanelName = new HTML();
		this.basePanelName.addStyleName(N3phele.n3pheleResource.css().gwtHTMLBasePanelName());
		this.basePanelName.setHTML("n3phele");
		this.basePanelName.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);

		this.menu = new HorizontalPanel();
		this.menu.setSize("100%", "100%");
		this.menu.addStyleName(N3phele.n3pheleResource.css().panelMenu());
		this.setWidget(0, 0, this.menu);
		
		this.getCellFormatter().setAlignment(0, 0, 
				HasHorizontalAlignment.ALIGN_LEFT, 
				HasVerticalAlignment.ALIGN_MIDDLE);
		this.getFlexCellFormatter().setColSpan(0, 0, 2);
		this.getFlexCellFormatter().setHeight(0, 0, "40px");
		this.menu.add(basePanelName);
		
		this.lhs = new VerticalPanel();
		this.lhs.setWidth("200px");
		this.setWidget(1, 0, this.lhs);
		this.getFlexCellFormatter().setAlignment(1, 0, 
				HasHorizontalAlignment.ALIGN_RIGHT, 
				HasVerticalAlignment.ALIGN_TOP);
		
		this.getCellFormatter().setAlignment(1, 1, 
				HasHorizontalAlignment.ALIGN_LEFT, 
				HasVerticalAlignment.ALIGN_TOP);
		
		this.n3phele = new Image(N3phele.n3pheleResource.loginGraphic().getURL());
		this.n3phele.setPixelSize(N3phele.n3pheleResource.loginGraphic().getWidth()*250/N3phele.n3pheleResource.loginGraphic().getHeight(),250);
		this.lhs.add(this.n3phele);
		this.lhs.setCellHorizontalAlignment(this.n3phele, VerticalPanel.ALIGN_CENTER);
		
	}
	
	public void setLeftHandIcon(Image icon) {
		if(icon == null) {
			if(displayingN3phele)
				return;
			if(displayingIcon != null)
				layout.remove(displayingIcon);
			displayingN3phele = true;
			displayingIcon = null;
			int scaledWidth = 100;
			if(this.siteText == null) {
				GWT.log("nephele="+N3phele.n3pheleResource.profile().getURL());
				GWT.log("nephele_alt="+N3phele.n3pheleResource.profile_alt().getURL());
				this.n3phele = new Image(N3phele.n3pheleResource.profile_alt().getURL());
				int scaledHeight = N3phele.n3pheleResource.profile_alt().getHeight()*scaledWidth/N3phele.n3pheleResource.profile_alt().getWidth();
				this.n3phele.setPixelSize(scaledWidth, scaledHeight );
				this.siteText = new HTML("<b>Welcome!</b><br>Cloud computing made easy");
				this.siteText.setSize((200-scaledWidth)+"px", 
						scaledHeight+"px");
				this.siteText.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			}
			this.n3phele.setPixelSize(scaledWidth, 109 );
//			GWT.log("nephele_alt="+this.n3phele.getUrl());
			layout.add(this.n3phele);
			layout.setWidgetLeftWidth(this.n3phele, 0.0, Unit.PX, scaledWidth, Unit.PX);
			layout.setWidgetTopHeight(this.n3phele, 0.0, Unit.PX, 109, Unit.PX);
			layout.add(this.siteText);
			layout.setWidgetRightWidth(this.siteText, 0.0, Unit.PX, (200-scaledWidth), Unit.PX);
		} else {
			if(displayingN3phele ) {
				layout.remove(this.n3phele);
				layout.remove(this.siteText);
			}
			if(displayingIcon != null)
				layout.remove(displayingIcon);
			displayingN3phele = false;
			displayingIcon = icon;
			int scaledHeight = 109;
			int scaledWidth = icon.getWidth()*scaledHeight/icon.getHeight();
			icon.setPixelSize(scaledWidth, scaledHeight );
			layout.add(icon);
		}
	}

	public void enableDecorations(List<MenuItem> initMainMenu) {
		if(!gotDecorations) {
			gotDecorations = true;
			this.lhs.clear();
			this.getCellFormatter().setAlignment(1, 0, 
					HasHorizontalAlignment.ALIGN_CENTER, 
					HasVerticalAlignment.ALIGN_TOP);
			layout = new LayoutPanel();
			layout.setWidth("200px");
			setLeftHandIcon(null);
	
//			int scaledHeight = 109;
//			int scaledFacebookHeight = N3phele.n3pheleResource.facebook().getHeight()*32/N3phele.n3pheleResource.facebook().getWidth();
//			ImageAnchor facebook = new ImageAnchor();
//			facebook.setResource(N3phele.n3pheleResource.facebook(), 32, scaledFacebookHeight);
//			facebook.setHref("http://www.facebook.com/pages/N3phele/180516518659652");
////			Image facebook = new Image(N3phele.n3pheleResource.facebook().getURL());
////			facebook.setPixelSize(32,scaledFacebookHeight );
//			layout.add(facebook);
//			layout.setWidgetTopHeight(facebook, scaledHeight+10.0, Unit.PX, scaledFacebookHeight, Unit.PX);
			layout.setHeight(100+"px");
			this.lhs.add(layout);

			if(initMainMenu != null) {
				for(MenuItem item : initMainMenu) {
					menuMap.put(item.getPlaceClass(), item);
				}
			}
			menuModel = new MainMenuTreeViewModel(initMainMenu);
			Resources res = GWT.create(MenuTreeResource.class);
			navigation = new CellTree(menuModel, null, res );
			navigation.setAnimationEnabled(true);
			
			this.lhs.add(new HTML("<hr align=left width=100%>"));
			this.lhs.add(navigation);
			this.lhs.add(new HTML("<hr align=left width=100%>"));

			
			VerticalPanel stack = new VerticalPanel();
			this.username = new HTML();
			this.username.addStyleName(N3phele.n3pheleResource.css().gwtHTMLBasePanelUsername());
			updateUser(AuthenticatedRequestFactory.getUser());
			stack.add(this.username);
			
			this.hprlnkEditProfile = new Hyperlink("edit profile", false, "user:");
			this.hprlnkEditProfile.addStyleName(N3phele.n3pheleResource.css().gwtHyperlinkBasePanelEditProfile());
			stack.add(this.hprlnkEditProfile);
			stack.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
			this.menu.add(stack);
			this.menu.setCellHorizontalAlignment(stack, HasHorizontalAlignment.ALIGN_RIGHT);
			
		}
		
	}


	private IsWidget view=null;
	@Override
	public void setWidget(IsWidget w) {
		if(view != null) {
			this.remove(view);
		}
		view = w;
		if(view != null) {
			this.setWidget(1,1,this.view.asWidget());
		}
	}
	private IsWidget lhsView=null;
	private HTML username;
	private Hyperlink hprlnkEditProfile;
	public void setLeftHandside(IsWidget w) {
		if(lhsView != null) {
			this.lhs.remove(lhsView);
		}
		lhsView = w;
		if(lhsView != null) {
			this.lhs.add(lhsView);
			this.lhs.setCellWidth(lhsView.asWidget(), "200px");
		}
		
	}
	
	public IsWidget getLeftHandside() {
		return lhsView;
	}
	
	public void updateUser(User user) {
		String nameText = user.getFirstName()+" "+user.getLastName();
		this.username.setHTML(nameText+ " <i>"+user.getName()+"</i>");
	}
	
	public void updateMenu(Place place) {
		if(menuMap.containsKey(place.getClass())) {
			MenuItem item = menuMap.get(place.getClass());
			item.setPlaceToken(clientFactory.getHistoryMapper().getToken(place));
			this.menuModel.getSelectionModel().setSelected(item, true);
		} else {
			if(this.menuModel != null) {
				MenuItem selected = this.menuModel.getSelectionModel().getSelectedObject();
				if(selected != null)
					this.menuModel.getSelectionModel().setSelected(selected, false);
			}
		}
	}
}

