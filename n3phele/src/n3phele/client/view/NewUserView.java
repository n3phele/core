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


import n3phele.client.model.User;
import n3phele.client.widgets.ValidInputIndicatorWidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class NewUserView extends DialogBox {
	final private FlexTable table;
	private TextBox email;
	private TextBox firstName;
	private TextBox lastName;
	private PasswordTextBox password;
	private PasswordTextBox confirmPassword;
	private Button cancel;
	private Button save;
	// private PasswordTextBox confirmSecret;
	private final String signupUrl;
	private ValidInputIndicatorWidget emailValid;
	private ValidInputIndicatorWidget firstNameValid;
	private ValidInputIndicatorWidget lastNameValid;
	private ValidInputIndicatorWidget passwordTextSupplied;
	private ValidInputIndicatorWidget passwordConfirmSupplied;
	private ValidInputIndicatorWidget errorsOnPage;
	// private Widget confirmSupplied;

	public NewUserView(String signupUrl) {
		this.signupUrl = signupUrl;
		this.setGlassEnabled(true);
		this.setAnimationEnabled(true);

		table = new FlexTable();
		table.setCellPadding(1);
		table.setCellSpacing(5);

		ChangeHandler update = new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				save.setEnabled(validateUser(true));
			}
		};
		KeyUpHandler keyup = new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				validateUser(true);
			}

		};

		HTML heading = new HTML("<i><u>New User Registration</u></i>");
		table.setWidget(0, 0, heading);

		Label lblNewLabel = new Label("Email");
		table.setWidget(1, 0, lblNewLabel);
		emailValid = new ValidInputIndicatorWidget("Email address required", true);
		table.setWidget(1, 1, emailValid);

		email = new TextBox();
		email.setVisibleLength(30);
		email.addChangeHandler(update);
		email.addKeyUpHandler(keyup);
		table.setWidget(1, 2, email);

		Label lblNewLabel_1 = new Label("First Name");
		table.setWidget(2, 0, lblNewLabel_1);
		firstNameValid = new ValidInputIndicatorWidget("Text value required", true);
		table.setWidget(2, 1, firstNameValid);

		firstName = new TextBox();
		firstName.setVisibleLength(30);
		firstName.addChangeHandler(update);
		firstName.addKeyUpHandler(keyup);
		table.setWidget(2, 2, firstName);

		Label lblNewLabel_2 = new Label("Last Name");
		table.setWidget(3, 0, lblNewLabel_2);
		lastNameValid = new ValidInputIndicatorWidget("Text value required", true);
		table.setWidget(3, 1, lastNameValid);

		lastName = new TextBox();
		lastName.setVisibleLength(30);
		lastName.addChangeHandler(update);
		lastName.addKeyUpHandler(keyup);
		table.setWidget(3, 2, lastName);

		Label lblNewLabel_3 = new Label("New password");
		table.setWidget(4, 0, lblNewLabel_3);
		passwordTextSupplied = new ValidInputIndicatorWidget("Password text required", true);
		table.setWidget(4, 1, passwordTextSupplied);

		password = new PasswordTextBox();
		password.setVisibleLength(30);
		password.addChangeHandler(update);
		password.addKeyUpHandler(keyup);
		table.setWidget(4, 2, password);
		
		Label lblNewLabel_4 = new Label("Confirm Password");
		table.setWidget(5, 0, lblNewLabel_4);
		passwordConfirmSupplied = new ValidInputIndicatorWidget("Matching password text required", true);
		table.setWidget(5, 1, passwordConfirmSupplied);

		confirmPassword = new PasswordTextBox();
		confirmPassword.setVisibleLength(30);
		confirmPassword.addChangeHandler(update);
		confirmPassword.addKeyUpHandler(keyup);
		table.setWidget(5, 2, confirmPassword);

		cancel = new Button("cancel", new ClickHandler() {
			public void onClick(ClickEvent event) {
				do_cancel();
			}
		});

		table.setWidget(10, 3, cancel);
		// table.getFlexCellFormatter().setHorizontalAlignment(10, 0,
		// HasHorizontalAlignment.ALIGN_RIGHT);

		save = new Button("register", new ClickHandler() {
			public void onClick(ClickEvent event) {
				do_save();
			}
		});
		table.setWidget(10, 2, save);
		// table.getFlexCellFormatter().setHorizontalAlignment(10, 2,
		// HasHorizontalAlignment.ALIGN_RIGHT);
		save.setEnabled(false);
		errorsOnPage = new ValidInputIndicatorWidget("check for missing or invalid parameters marked with this icon", true);
		table.setWidget(10, 1, errorsOnPage);
		// table.getFlexCellFormatter().setHorizontalAlignment(10, 3,
		// HasHorizontalAlignment.ALIGN_RIGHT);

		for (int i = 1; i < 10; i++) {
			table.getFlexCellFormatter().setColSpan(i, 2, 3);
			table.getFlexCellFormatter().setVerticalAlignment(i, 0, HasVerticalAlignment.ALIGN_MIDDLE);
			table.getFlexCellFormatter().setHorizontalAlignment(i, 0, HasHorizontalAlignment.ALIGN_RIGHT);
			table.getFlexCellFormatter().setVerticalAlignment(i, 2, HasVerticalAlignment.ALIGN_MIDDLE);
			table.getFlexCellFormatter().setHorizontalAlignment(i, 2, HasHorizontalAlignment.ALIGN_LEFT);

		}

		table.getColumnFormatter().setWidth(1, "18px");
		table.getColumnFormatter().setWidth(4, "18px");
		table.getFlexCellFormatter().setColSpan(0, 0, 4);
		table.getFlexCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
		this.add(table);
		this.center();
	}


	public void do_save() {
		createUser(this.signupUrl, email.getText(), firstName.getText().trim(), 
				lastName.getText().trim(), password.getText().trim());
		hide();
	}

	private void createUser(String url, final String email, String firstName, String lastName, String password) {

		// Send request to server and catch any errors.
		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, url);
		builder.setUser("signup");
		builder.setPassword("newuser");
		builder.setHeader("Content-type", "application/x-www-form-urlencoded");

		StringBuilder args = new StringBuilder();
		args.append("email=");
		args.append(URL.encodeQueryString(email));
		args.append("&firstName=");
		args.append(URL.encodeQueryString(firstName));
		args.append("&lastName=");
		args.append(URL.encodeQueryString(lastName));
		if (password != null && password.length() > 0) {
			args.append("&secret=");
			args.append(URL.encodeQueryString(password));
		}

		try {
			@SuppressWarnings("unused")
			Request request = builder.sendRequest(args.toString(), new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					Window.alert("User create error " + exception.getMessage());
				}

				public void onResponseReceived(Request request, Response response) {
					GWT.log("Got reply");
					if (201 == response.getStatusCode()) {
						Window.alert("User " + email + " created.");
					} else {
						Window.alert("User create failure " + response.getStatusText() + "\n" + response.getText());
					}
				}

			});
		} catch (RequestException e) {
			Window.alert("Account create exception " + e.getMessage());
		}
	}

	public void do_cancel() {
		setData(null);
		hide();
	}

	public void setData(User user) {
		if (user != null) {
			email.setText(user.getName());
			firstName.setText(user.getFirstName());
			lastName.setText(user.getLastName());
		} else {
			email.setText("");
			firstName.setText("");
			lastName.setText("");
			password.setText("");
			confirmPassword.setText("");
			// confirmSecret.setText("");
		}
	}

	private boolean validateUser(boolean isValid) {
		boolean gotEmail = (email.getText().equals("root") || email.getText().matches(
				"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$"));
		this.emailValid.setVisible(!gotEmail);
		boolean gotFirstName = firstName.getText() != null && firstName.getText().length() != 0;
		this.firstNameValid.setVisible(!gotFirstName);
		boolean gotLastName = lastName.getText() != null && lastName.getText().length() != 0;
		this.lastNameValid.setVisible(!gotLastName);
		isValid = isValid && gotEmail && gotFirstName && gotLastName;

		boolean gotPassword = password.getText() != null && password.getText().length() != 0;
		boolean gotConfirm = confirmPassword.getText() != null && confirmPassword.getText().length() != 0;
		this.passwordTextSupplied.setVisible(!gotPassword);
		this.passwordConfirmSupplied.setVisible(!(gotConfirm && password.getText().equals(confirmPassword.getText())));
		isValid = isValid && gotPassword && gotConfirm && password.getText().equals(confirmPassword.getText());

		// boolean gotEC2Confirm = confirmSecret.getText() != null &&
		// confirmSecret.getText().length() != 0 &&
		// secret.getText().equals(confirmSecret.getText());
		// this.confirmSupplied.setVisible(!gotEC2Confirm);
		this.errorsOnPage.setVisible(!isValid);
		this.save.setEnabled(isValid);
		return isValid;
	}

}