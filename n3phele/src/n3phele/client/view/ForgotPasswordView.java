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
import com.google.gwt.user.client.ui.TextBox;

public class ForgotPasswordView extends DialogBox {
	final private FlexTable table;
	private TextBox email;
	private TextBox firstName;
	private TextBox lastName;
	private Button cancel;
	private Button save;
	private final String signupUrl;
	private ValidInputIndicatorWidget emailValid;
	private ValidInputIndicatorWidget firstNameValid;
	private ValidInputIndicatorWidget lastNameValid;

	private ValidInputIndicatorWidget errorsOnPage;

	public ForgotPasswordView(String signupUrl) {
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
			}};
		KeyUpHandler keyup = new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				validateUser(true);
			}

			};
		
		HTML heading = new HTML("<i><u>User Password Reset</u></i>");
		table.setWidget(0,0, heading);
		
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
		

		
		cancel = new Button("cancel",  new ClickHandler() {
          public void onClick(ClickEvent event) {
            do_cancel();
          }
        });


		table.setWidget(4, 3, cancel);
		table.getFlexCellFormatter().setHorizontalAlignment(9, 0, HasHorizontalAlignment.ALIGN_RIGHT);
		
		save = new Button("reset",  new ClickHandler() {
	          public void onClick(ClickEvent event) {
	              do_save();
	            }
	          });
		table.setWidget(4, 2, save);
		table.getFlexCellFormatter().setHorizontalAlignment(9, 2, HasHorizontalAlignment.ALIGN_RIGHT);
		save.setEnabled(false);
		errorsOnPage = new ValidInputIndicatorWidget("check for missing or invalid parameters marked with this icon", true);
		table.setWidget(4, 1, errorsOnPage);
		table.getFlexCellFormatter().setHorizontalAlignment(9, 3, HasHorizontalAlignment.ALIGN_RIGHT);

		for(int i=1; i < 4; i++) {
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
		createUser(this.signupUrl, email.getText(), 
				firstName.getText().trim(), lastName.getText().trim());
		hide();
	}
	
	private void createUser(String url, final String email, String firstName, String lastName) {

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

		try {
			@SuppressWarnings("unused")
			Request request = builder.sendRequest(args.toString(), new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					Window.alert("User create error "+exception.getMessage());
				}

				public void onResponseReceived(Request request, Response response) {
					GWT.log("Got reply");
					if (201 == response.getStatusCode()) {
						Window.alert("User "+email+" password reset. Check your email for details.");
					} else {
						Window.alert("User password reset failure "+response.getStatusText()+"\n"+
								response.getText());
					}
				}

			});
		} catch (RequestException e) {
			Window.alert("User password reset exception "+e.getMessage());
		}
	}
	
	public void do_cancel() {
		setData(null);
		hide();
	}
	
	public void setData(User user) {
		if(user != null) {
			email.setText(user.getName());
			firstName.setText(user.getFirstName());
			lastName.setText(user.getLastName());
		} else {
			email.setText("");
			firstName.setText("");
			lastName.setText("");
		}
	}

	
	private boolean validateUser(boolean isValid) {
		boolean gotEmail = (email.getText().equals("root") || email.getText().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$"));
		this.emailValid.setVisible(!gotEmail);
		boolean gotFirstName = firstName.getText() != null && firstName.getText().length() != 0;
		this.firstNameValid.setVisible(!gotFirstName);
		boolean gotLastName = lastName.getText() != null && lastName.getText().length() != 0;
		this.lastNameValid.setVisible(!gotLastName);
		isValid = isValid && gotEmail && gotFirstName && gotLastName;
		this.errorsOnPage.setVisible(!isValid);
		this.save.setEnabled(isValid);
		return isValid;
	}

}