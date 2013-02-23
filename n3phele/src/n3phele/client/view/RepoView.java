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

import java.util.HashMap;

import n3phele.client.N3phele;
import n3phele.client.model.Repository;
import n3phele.client.model.User;
import n3phele.client.presenter.RepoActivity;
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
import com.google.gwt.user.client.ui.SimpleCheckBox;
import com.google.gwt.user.client.ui.Widget;

public class RepoView extends WorkspaceVerticalPanel {
	final private FlexTable table;
	private Repository repository;
	private TextBox name;
	private TextBox description;
	private TextBox authId;
	private PasswordTextBox password;
	private PasswordTextBox confirmPassword;
	private Button cancel;
	private Button save;
	private RepoActivity presenter;
	private TextBox location;
	private ListBox kind;
	private HashMap<String, Integer> kindMap;
//	private final String kinds[] = new String[]{"S3", "ftp", "http"};
	private final String kinds[] = new String[]{"S3", "Swift"};
	private TextBox root;
	private Label lblNewLabel_8;
	private SimpleCheckBox isPublic;
	private Widget nameValid;
	private ValidInputIndicatorWidget locationUrlSupplied;
	private ValidInputIndicatorWidget kindSelected;
	private ValidInputIndicatorWidget basePathSupplied;
	private ValidInputIndicatorWidget gotAuthId;
	private ValidInputIndicatorWidget passwordTextSupplied;
	private ValidInputIndicatorWidget passwordConfirmSupplied;
	private Widget errorsOnPage;
	public RepoView() {
		super(new MenuItem(N3phele.n3pheleResource.repositoryIcon(), "Repository", null));
		table = new FlexTable();
		table.setCellPadding(10);
		
		ChangeHandler update = new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				validateRepositorySpecification(true);
			}};
		KeyUpHandler keyup = new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				validateRepositorySpecification(true);
			}

			};
		this.repository = null;
		Label lblNewLabel = new Label("Name");
		table.setWidget(0, 0, lblNewLabel);
		nameValid = new ValidInputIndicatorWidget("Text value required", false);
		table.setWidget(0, 1, nameValid);
		
		name = new TextBox();
		name.setVisibleLength(30);
		name.addChangeHandler(update);
		name.addKeyUpHandler(keyup);
		table.setWidget(0, 2, name);
		
		Label lblNewLabel_1 = new Label("Description");
		table.setWidget(1, 0, lblNewLabel_1);
		
		description = new TextBox();
		description.setVisibleLength(30);
		description.addChangeHandler(update);
		table.setWidget(1, 2, description);
		
		Label lblNewLabel_2 = new Label("Location URL");
		table.setWidget(2, 0, lblNewLabel_2);
	//	locationUrlSupplied = new ValidInputIndicatorWidget("http, https or ftp URL required", false);
		locationUrlSupplied = new ValidInputIndicatorWidget("http or https URL required", false);
		table.setWidget(2, 1, locationUrlSupplied);
		
		location = new TextBox();
		location.setVisibleLength(30);
		location.addChangeHandler(update);
		location.addKeyUpHandler(keyup);
		table.setWidget(2, 2, location);
		
		Label lblNewLabel_3 = new Label("Kind");
		table.setWidget(3, 0, lblNewLabel_3);
		kindSelected = new ValidInputIndicatorWidget("Kind selection required.", false);
		table.setWidget(3, 1, kindSelected);
		
		kind = new ListBox(false);
		kindMap = new HashMap<String,Integer>();
		for(int i=0; i < kinds.length; i++) {
			kind.addItem(kinds[i]);
			kindMap.put(kinds[i],i);
		}
		kind.addChangeHandler(update);
		table.setWidget(3, 2, kind);
		kind.addChangeHandler(update);
		kind.addKeyUpHandler(keyup);

		
		Label lblNewLabel_4 = new Label("Base path");
		table.setWidget(4, 0, lblNewLabel_4);
		basePathSupplied = new ValidInputIndicatorWidget("bucket or other base path required", false);
		table.setWidget(4, 1, basePathSupplied);
		
		root = new TextBox();
		root.setVisibleLength(30);
		root.addChangeHandler(update);
		root.addKeyUpHandler(keyup);
		table.setWidget(4, 2, root);
		
		Label lblNewLabel_5 = new Label("Authentication Id");
		table.setWidget(5, 0, lblNewLabel_5);
		gotAuthId = new ValidInputIndicatorWidget("id required", false);
		table.setWidget(5, 1, gotAuthId);
		
		authId = new TextBox();
		authId.setVisibleLength(30);
		authId.addChangeHandler(update);
		authId.addKeyUpHandler(keyup);
		table.setWidget(5, 2, authId);
		
		Label lblNewLabel_6 = new Label("New password");
		table.setWidget(6, 0, lblNewLabel_6);
		passwordTextSupplied = new ValidInputIndicatorWidget("Password text required", false);
		table.setWidget(6, 1, passwordTextSupplied);
		
		password = new PasswordTextBox();
		password.setVisibleLength(30);
		password.addChangeHandler(update);
		password.addKeyUpHandler(keyup);
		table.setWidget(6, 2, password);
		
		Label lblNewLabel_7 = new Label("Confirm Password");
		table.setWidget(7, 0, lblNewLabel_7);
		passwordConfirmSupplied = new ValidInputIndicatorWidget("Matching password text required", false);
		table.setWidget(7, 1, passwordConfirmSupplied);
		
		confirmPassword = new PasswordTextBox();
		confirmPassword.setVisibleLength(30);
		confirmPassword.addChangeHandler(update);
		confirmPassword.addKeyUpHandler(keyup);
		table.setWidget(7, 2, confirmPassword);
		
		lblNewLabel_8 = new Label("Accessible to all n3phele user?");
		table.setWidget(8, 0, lblNewLabel_8);
		
		isPublic = new SimpleCheckBox();
		table.setWidget(8, 1, isPublic);
		
		cancel = new Button("cancel",  new ClickHandler() {
          public void onClick(ClickEvent event) {
            do_cancel();
          }
        });

		table.setWidget(9, 3, cancel);
		
		save = new Button("save",  new ClickHandler() {
	          public void onClick(ClickEvent event) {
	              do_save();
	            }
	          });
		table.setWidget(9, 2, save);
		table.getFlexCellFormatter().setHorizontalAlignment(9, 3, HasHorizontalAlignment.ALIGN_RIGHT);
		errorsOnPage = new ValidInputIndicatorWidget("check for missing or invalid parameters marked with this icon", false);
		table.setWidget(9, 1, errorsOnPage);

		for(int i=0; i < 8; i++) {
			table.getFlexCellFormatter().setColSpan(i, 2, 3);
			table.getFlexCellFormatter().setVerticalAlignment(i, 0, HasVerticalAlignment.ALIGN_MIDDLE);
			table.getFlexCellFormatter().setHorizontalAlignment(i, 0, HasHorizontalAlignment.ALIGN_RIGHT);
			table.getFlexCellFormatter().setVerticalAlignment(i, 2, HasVerticalAlignment.ALIGN_MIDDLE);
			table.getFlexCellFormatter().setHorizontalAlignment(i, 2, HasHorizontalAlignment.ALIGN_LEFT);
	
		}
		table.getColumnFormatter().setWidth(1, "18px");
		table.getColumnFormatter().setWidth(4, "16px");
		table.setCellPadding(1);
		table.setCellSpacing(5);
		this.add(table);
	}
	
	public void do_save() {
		this.presenter.onSave(repository, name.getText(), 
				description.getText().trim(), location.getText().trim(), 
				kinds[kind.getSelectedIndex()], root.getText().trim(), isPublic.getValue(),  authId.getText().trim(), password.getText().trim());
	}
	
	public void do_cancel() {
		setData(null);
		this.presenter.goToPrevious();
	}
	
	public void setData(Repository repository) {
		this.repository = repository;
		if(repository != null) {
			name.setText(repository.getName());
			description.setText(repository.getDescription());
			root.setText(repository.getRoot());
			location.setText(repository.getTarget());
			if(kindMap.containsKey(repository.getKind())) {
				kind.setSelectedIndex(kindMap.get(repository.getKind()));
			} else {
				kind.setSelectedIndex(-1);
			}
			isPublic.setValue(repository.isPublic());
			validateRepositorySpecification(true);
		} else {
			name.setText("");
			description.setText("");
			root.setText("");
			location.setText("");
			authId.setText("");
			password.setText("");
			confirmPassword.setText("");
		}
	}

	public void setPresenter(RepoActivity presenter) {
		this.presenter = presenter;
	}

	public void refresh(User newUser) {
		
	}
	


	private boolean validateRepositorySpecification(boolean isValid) {
		if(repository == null) return false;
		boolean nameValid = (name.getText()!= null && name.getText().length()!=0);
		this.nameValid.setVisible(!nameValid);
		boolean kindSelected = kind.getSelectedIndex() >=0;
		this.kindSelected.setVisible(!kindSelected);
//		boolean gotLocation = location.getText()!= null && location.getText().matches("^(ftp|http|https):\\/\\/(\\w+:{0,1}\\w*@)?(\\S+)(:[0-9]+)?(\\/|\\/([\\w#!:.?+=&%@!\\-\\/]))?$");
		boolean gotLocation = location.getText()!= null && location.getText().matches("^(http|https):\\/\\/(\\w+:{0,1}\\w*@)?(\\S+)(:[0-9]+)?(\\/|\\/([\\w#!:.?+=&%@!\\-\\/]))?$");
		this.locationUrlSupplied.setVisible(!gotLocation);
		boolean gotBase = root.getText()!= null && root.getText().trim().length()!=0;
		this.basePathSupplied.setVisible(!gotBase);
		isValid = isValid && nameValid
					&& kindSelected  && gotLocation && gotBase;
		boolean gotId =  authId.getText() != null && authId.getText().length() != 0;
		boolean gotPassword = password.getText() != null && password.getText().length() != 0;
		boolean gotConfirm = confirmPassword.getText() != null && confirmPassword.getText().length() != 0;
		if(repository.getUri() == null || repository.getUri().length()==0) {
			this.gotAuthId.setVisible(!gotId);
			this.passwordTextSupplied.setVisible(!gotPassword);
			this.passwordConfirmSupplied.setVisible(!(gotConfirm && password.getText().equals(confirmPassword.getText())));
			isValid = isValid && gotId && gotPassword && gotConfirm && password.getText().equals(confirmPassword.getText());
		} else {
			if(gotPassword || gotConfirm || gotId ) {
				this.gotAuthId.setVisible(!gotId);
				this.passwordTextSupplied.setVisible(!gotPassword);
				this.passwordConfirmSupplied.setVisible(!(gotConfirm && password.getText().equals(confirmPassword.getText())));
				isValid = isValid && gotPassword && gotConfirm && password.getText().equals(confirmPassword.getText()) && gotId;
			} else {
				this.gotAuthId.setVisible(false);
				this.passwordTextSupplied.setVisible(false);
				this.passwordConfirmSupplied.setVisible(false);
			}
		}
		this.errorsOnPage.setVisible(!isValid);
		this.save.setEnabled(isValid);
		return isValid;
	}



}
