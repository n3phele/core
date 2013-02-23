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

import n3phele.client.N3phele;
import n3phele.client.model.User;
import n3phele.client.presenter.UserActivity;
import n3phele.client.widgets.MenuItem;
import n3phele.client.widgets.ValidInputIndicatorWidget;
import n3phele.client.widgets.WorkspaceVerticalPanel;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;

public class UserView extends WorkspaceVerticalPanel {
	final private FlexTable table;
	private User user;
	private TextBox email;
	private TextBox firstName;
	private TextBox lastName;
	private PasswordTextBox password;
	private PasswordTextBox confirmPassword;
	private Button cancel;
	private Button save;
	private UserActivity presenter;
	private ValidInputIndicatorWidget emailValid;
	private ValidInputIndicatorWidget firstNameValid;
	private ValidInputIndicatorWidget lastNameValid;
	private ValidInputIndicatorWidget passwordTextSupplied;
	private ValidInputIndicatorWidget passwordConfirmSupplied;
	private ValidInputIndicatorWidget errorsOnPage;
	public UserView() {
		super(new MenuItem(N3phele.n3pheleResource.profileIcon(), "User Profile", null));
		table = new FlexTable();
		table.setCellPadding(10);
		
		ChangeHandler update = new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				validateUser(true);
			}};
		KeyUpHandler keyup = new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				validateUser(true);
			}

			};
		
		this.user = null;
		Label lblNewLabel = new Label("Email");
		table.setWidget(0, 0, lblNewLabel);
		emailValid = new ValidInputIndicatorWidget("Email address required", false);
		table.setWidget(0, 1, emailValid);
		
		email = new TextBox();
		email.setVisibleLength(30);
		email.addChangeHandler(update);
		email.addKeyUpHandler(keyup);
		table.setWidget(0, 2, email);
		
		Label lblNewLabel_1 = new Label("First Name");
		table.setWidget(1, 0, lblNewLabel_1);
		firstNameValid = new ValidInputIndicatorWidget("Text value required", false);
		table.setWidget(1, 1, firstNameValid);
		
		firstName = new TextBox();
		firstName.setVisibleLength(30);
		firstName.addChangeHandler(update);
		firstName.addKeyUpHandler(keyup);
		table.setWidget(1, 2, firstName);
		
		Label lblNewLabel_2 = new Label("Last Name");
		table.setWidget(2, 0, lblNewLabel_2);
		lastNameValid = new ValidInputIndicatorWidget("Text value required", false);
		table.setWidget(2, 1, lastNameValid);
		
		lastName = new TextBox();
		lastName.setVisibleLength(30);
		lastName.addChangeHandler(update);
		lastName.addKeyUpHandler(keyup);
		table.setWidget(2, 2, lastName);
		
		Label lblNewLabel_3 = new Label("New password");
		table.setWidget(3, 0, lblNewLabel_3);
		passwordTextSupplied = new ValidInputIndicatorWidget("Password text required", false);
		table.setWidget(3, 1, passwordTextSupplied);
		
		password = new PasswordTextBox();
		password.setVisibleLength(30);
		password.addChangeHandler(update);
		password.addKeyUpHandler(keyup);
		table.setWidget(3, 2, password);
		
		Label lblNewLabel_4 = new Label("Confirm Password");
		table.setWidget(4, 0, lblNewLabel_4);
		passwordConfirmSupplied = new ValidInputIndicatorWidget("Matching password text required", false);
		table.setWidget(4, 1, passwordConfirmSupplied);

		
		confirmPassword = new PasswordTextBox();
		confirmPassword.setVisibleLength(30);
		confirmPassword.addChangeHandler(update);
		confirmPassword.addKeyUpHandler(keyup);
		table.setWidget(4, 2, confirmPassword);
		
		cancel = new Button("cancel",  new ClickHandler() {
          public void onClick(ClickEvent event) {
            do_cancel();
          }
        });

		table.setWidget(5, 3, cancel);
		
		save = new Button("save",  new ClickHandler() {
	          public void onClick(ClickEvent event) {
	              do_save();
	            }
	          });
		table.setWidget(5, 2, save);
		errorsOnPage = new ValidInputIndicatorWidget("check for missing or invalid parameters marked with this icon", false);
		table.setWidget(5, 1, errorsOnPage);
		table.getFlexCellFormatter().setHorizontalAlignment(5, 3, HasHorizontalAlignment.ALIGN_RIGHT);

		for(int i=0; i < 5; i++) {
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
		this.presenter.onSave(user, email.getText(), 
				firstName.getText().trim(), lastName.getText().trim(), password.getText().trim());
	}
	
	public void do_cancel() {
		//setData(null);
		this.presenter.goToPrevious();
	}
	
	public void setData(User user) {
		this.user = user;
		if(user != null) {
			email.setText(user.getName());
			firstName.setText(user.getFirstName());
			lastName.setText(user.getLastName());
			validateUser(true);
		} else {
			email.setText("");
			firstName.setText("");
			lastName.setText("");
			password.setText("");
			confirmPassword.setText("");
			validateUser(false);
		}
	}

	public void setPresenter(UserActivity presenter) {
		this.presenter = presenter;
	}

	public void refresh(User newUser) {
		
	}

	private boolean validateUser(boolean isValid) {
		boolean gotEmail = (email.getText().equals("root") || email.getText().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$"));
		this.emailValid.setVisible(!gotEmail);
		boolean gotFirstName = firstName.getText() != null && firstName.getText().length() != 0;
		this.firstNameValid.setVisible(!gotFirstName);
		boolean gotLastName = lastName.getText() != null && lastName.getText().length() != 0;
		this.lastNameValid.setVisible(!gotLastName);
		isValid = isValid && gotEmail && gotFirstName && gotLastName;
		boolean gotPassword = password.getText() != null && password.getText().length() != 0;
		boolean gotConfirm = confirmPassword.getText() != null && confirmPassword.getText().length() != 0;
		if(user.getUri() == null || user.getUri().length() == 0) {
			this.passwordTextSupplied.setVisible(!gotPassword);
			this.passwordConfirmSupplied.setVisible(!(gotConfirm && password.getText().equals(confirmPassword.getText())));
			isValid = isValid && gotPassword && gotConfirm && password.getText().equals(confirmPassword.getText());
		} else {
			if(gotPassword || gotConfirm ) {
				this.passwordTextSupplied.setVisible(!gotPassword);
				this.passwordConfirmSupplied.setVisible(!(gotConfirm && password.getText().equals(confirmPassword.getText())));
				isValid = isValid && gotPassword && gotConfirm && password.getText().equals(confirmPassword.getText());
			} else {
				this.passwordTextSupplied.setVisible(false);
				this.passwordConfirmSupplied.setVisible(false);
			}
		}
		
		this.errorsOnPage.setVisible(!isValid);
		this.save.setEnabled(isValid);
		return isValid;
	}

}
