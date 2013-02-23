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
package n3phele.client.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import n3phele.client.N3phele;
import n3phele.client.model.Account;
import n3phele.client.model.Cloud;
import n3phele.client.model.User;
import n3phele.client.presenter.AccountActivity;
import n3phele.client.widgets.MenuItem;
import n3phele.client.widgets.ValidInputIndicatorWidget;
import n3phele.client.widgets.WorkspaceVerticalPanel;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;

public class AccountView extends WorkspaceVerticalPanel {
	final private FlexTable table;
	private Account account;
	private TextBox name;
	private TextBox description;
	private TextBox cloudId;
	private TextBox secret;
	//private PasswordTextBox confirmPassword;
	private Button cancel;
	private Button save;
	private AccountActivity presenter;
	private final ListBox cloud = new ListBox(false);
	private final Map<String,Integer> cloudMap = new HashMap<String,Integer>();
	private final List<String> uriMap = new ArrayList<String>();
	private final ValidInputIndicatorWidget nameValid;
	private final ValidInputIndicatorWidget cloudSelected;
	private final ValidInputIndicatorWidget gotCloudId;
	private final ValidInputIndicatorWidget secretTextSupplied;
	//private final ValidInputIndicatorWidget passwordConfirmSupplied;
	private final ValidInputIndicatorWidget errorsOnPage;
	public AccountView() {
		super(new MenuItem(N3phele.n3pheleResource.accountIcon(), "Cloud Account", null));
		table = new FlexTable();
		table.setCellPadding(10);
		
		ChangeHandler update = new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				validateAccount(true);
			}};
			
		KeyUpHandler keyup = new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				validateAccount(true);
			}

			};
		
		this.account = null;
		Label lblNewLabel = new Label("Name");
		table.setWidget(0, 0, lblNewLabel);
		nameValid = new ValidInputIndicatorWidget("Text value required", false);
		table.setWidget(0, 1, nameValid);
		
		name = new TextBox();
		name.setVisibleLength(40);
		name.addChangeHandler(update);
		name.addKeyUpHandler(keyup);
		table.setWidget(0, 2, name);
		
		Label lblNewLabel_1 = new Label("Description");
		table.setWidget(1, 0, lblNewLabel_1);
		
		description = new TextBox();
		description.setVisibleLength(40);
		description.addChangeHandler(update);
		table.setWidget(1, 2, description);
		
		Label lblNewLabel_2 = new Label("on Cloud");
		table.setWidget(2, 0, lblNewLabel_2);
		
		cloudSelected = new ValidInputIndicatorWidget("Cloud selection required", false);
		table.setWidget(2, 1, cloudSelected);
		
		cloud.addItem("--loading--");
		cloud.addChangeHandler(update);
		table.setWidget(2, 2, cloud);
		
		Label lblNewLabel_3 = new Label("Cloud Id");
		table.setWidget(3, 0, lblNewLabel_3);
		
		gotCloudId = new ValidInputIndicatorWidget("Cloud id required", false);
		table.setWidget(3, 1, gotCloudId);
		cloudId = new TextBox();
		cloudId.setVisibleLength(40);
		cloudId.addChangeHandler(update);
		cloudId.addKeyUpHandler(keyup);
		table.setWidget(3, 2, cloudId);
		
		Label lblNewLabel_4 = new Label("Cloud Secret");
		table.setWidget(4, 0, lblNewLabel_4);
		
		secretTextSupplied = new ValidInputIndicatorWidget("Secret text required", false);
		table.setWidget(4, 1, secretTextSupplied);
		secret = new TextBox();
		secret.setVisibleLength(40);
		secret.addChangeHandler(update);
		secret.addKeyUpHandler(keyup);
		table.setWidget(4, 2, secret);
		
		/*
		Label lblNewLabel_5 = new Label("Confirm Password");
		table.setWidget(5, 0, lblNewLabel_5);
		passwordConfirmSupplied = new ValidInputIndicatorWidget("Matching password text required", false);
		table.setWidget(5, 1, passwordConfirmSupplied);
		confirmPassword = new PasswordTextBox();
		confirmPassword.setVisibleLength(40);
		confirmPassword.addChangeHandler(update);
		confirmPassword.addKeyUpHandler(keyup);
		table.setWidget(5, 2, confirmPassword);
		*/
		
		cancel = new Button("cancel",  new ClickHandler() {
          public void onClick(ClickEvent event) {
            do_cancel();
          }
        });

		table.setWidget(6, 3, cancel);
		
		
		save = new Button("save",  new ClickHandler() {
	          public void onClick(ClickEvent event) {
	              do_save();
	            }
	          });
		table.setWidget(6, 2, save);
		
		errorsOnPage = new ValidInputIndicatorWidget("check for missing or invalid parameters marked with this icon", false);
		table.setWidget(6, 1, errorsOnPage);
		table.getFlexCellFormatter().setHorizontalAlignment(6, 3, HasHorizontalAlignment.ALIGN_CENTER);
		
		
		for(int i=0; i < 6; i++) {
			table.getFlexCellFormatter().setColSpan(i, 2, 2);
			table.getFlexCellFormatter().setVerticalAlignment(i, 0, HasVerticalAlignment.ALIGN_MIDDLE);
			table.getFlexCellFormatter().setHorizontalAlignment(i, 0, HasHorizontalAlignment.ALIGN_RIGHT);
//			table.getFlexCellFormatter().setVerticalAlignment(i, 2, HasVerticalAlignment.ALIGN_MIDDLE);
//			table.getFlexCellFormatter().setHorizontalAlignment(i, 2, HasHorizontalAlignment.ALIGN_LEFT);
	
		}
		table.getColumnFormatter().setWidth(0, "25%");
		table.getColumnFormatter().setWidth(1, "18px");
		table.getColumnFormatter().setWidth(4, "16px");
		table.setCellPadding(1);
		table.setCellSpacing(5);
		this.add(table);
	}
	
	public void do_save() {
		this.presenter.onSave(account, name.getText(), 
				description.getText().trim(), uriMap.get(cloud.getSelectedIndex()), cloudId.getText().trim(), secret.getText().trim());
	}
	
	public void do_cancel() {
		setData(null);
		this.presenter.goToPrevious();
	}
	
	public void setData(Account account) {
		this.account = account;
		if(account != null) {
			name.setText(account.getName());
			description.setText(account.getDescription());
			if(cloudMap.containsKey(account.getCloud())) {
				cloud.setSelectedIndex(cloudMap.get(account.getCloud()));
			} else {
				if(cloudMap.isEmpty() || (account.getCloud()!=null && account.getCloud().length()!=0))
					cloud.setSelectedIndex(-1);
				else
					cloud.setSelectedIndex(0);
			}
			validateAccount(true);
		} else {
			name.setText("");
			description.setText("");
			cloudId.setText("");
			secret.setText("");
			//confirmPassword.setText("");
			validateAccount(true);
		}
	}

	public void setPresenter(AccountActivity presenter) {
		this.presenter = presenter;
	}

	public void refresh(User newUser) {
		
	}
	
	public void setClouds(List<Cloud> list) {
		uriMap.clear();
		cloudMap.clear();
		cloud.clear();
		if(list != null) {
			int i=0;
			for(Cloud c : list) {
				uriMap.add(c.getUri());
				cloudMap.put(c.getUri(),i++);
				cloud.addItem(c.getName());
			}
			if(account != null && cloudMap.containsKey(account.getCloud())) {
				cloud.setSelectedIndex(cloudMap.get(account.getCloud()));
			} else {
				cloud.setSelectedIndex(-1);
			}
		}
		validateAccount(true);
	}

	private boolean validateAccount(boolean isValid) {
		if(account == null) return false;
		boolean nameValid = (name.getText()!= null && name.getText().length()!=0);
		this.nameValid.setVisible(!nameValid);
		boolean cloudValid = cloud.getSelectedIndex() >=0 && cloudMap.size() > 0;
		this.cloudSelected.setVisible(!cloudValid);
		isValid = isValid && nameValid && cloudValid;
		boolean gotId =  cloudId.getText() != null && cloudId.getText().length() != 0;
		boolean gotPassword = secret.getText() != null && secret.getText().length() != 0;
		//boolean gotConfirm = confirmPassword.getText() != null && confirmPassword.getText().length() != 0;
		if(account.getUri() == null || account.getUri().length() == 0) {
			this.gotCloudId.setVisible(!gotId);
			this.secretTextSupplied.setVisible(!gotPassword);
			//this.passwordConfirmSupplied.setVisible(!(gotConfirm && password.getText().equals(confirmPassword.getText())));
			isValid = isValid && gotId && gotPassword; //&& gotConfirm && password.getText().equals(confirmPassword.getText());
		} else {
			if(gotPassword || gotId ) {
				this.gotCloudId.setVisible(!gotId);
				this.secretTextSupplied.setVisible(!gotPassword);
				//this.passwordConfirmSupplied.setVisible(!(gotConfirm && password.getText().equals(confirmPassword.getText())));
				isValid = isValid && gotPassword; //&& gotConfirm && password.getText().equals(confirmPassword.getText()) && gotId;
			} else {
				this.gotCloudId.setVisible(false);
				this.secretTextSupplied.setVisible(false);
				//this.passwordConfirmSupplied.setVisible(false);
			}
		}
		this.errorsOnPage.setVisible(!isValid);
		this.save.setEnabled(isValid);
		return isValid;
	}
}
