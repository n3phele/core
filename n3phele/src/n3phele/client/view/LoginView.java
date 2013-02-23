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

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.DecoratorPanel;

public class LoginView extends Composite {

    // LoginView ids. These MUST match the ids in TeamScape.html!
    private static final String LOGINFORM_ID = "loginForm";
   // private static final String LOGINBUTTON_ID = "loginSubmit";
    private static final String USERNAME_ID = "loginUsername";
    private static final String PASSWORD_ID = "loginPassword";

    //private final EventBus mEventBus;
    private final FormPanel form;
    private DecoratorPanel decoratorPanel;
    private HTML errmsg;
    private final String homeUrl;
    private final boolean altMethod = true;
    public LoginView(String homeUrl) {
    	this.homeUrl = homeUrl;

    	if(altMethod) {
    		form = FormPanel.wrap(Document.get().getElementById(LOGINFORM_ID), true);
    		form.addSubmitHandler(new SubmitHandler(){

				@Override
				public void onSubmit(SubmitEvent event) {
			        setMessage("checking your identity and credentials.");
			    	N3phele.authenticate(getUsername(), getPassword(), LoginView.this);
				}});
    	} else {
//	        // Get a handle to the form and set its action to our jsni method
//	        form = FormPanel.wrap(Document.get().getElementById(LOGINFORM_ID), false);
//	        form.setAction("javascript:__gwt_login()");
//	
//	        // Get the submit button for text localization
//	        @SuppressWarnings("unused")
//			ButtonElement submit = (ButtonElement) Document.get().getElementById(LOGINBUTTON_ID);
//	
//	        // Now, inject the jsni method for handling the form submit
//	        injectLoginFunction(this);
    	}

        
        this.decoratorPanel = new DecoratorPanel();
        VerticalPanel v = new VerticalPanel();
        this.decoratorPanel.add(v); 
        Button register = new Button("<u>register</u>", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				new NewUserView(LoginView.this.homeUrl+"user").show();		
			}});
        Button forgot = new Button("<u>forgot password</u>", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				new ForgotPasswordView(LoginView.this.homeUrl+"user/reset").show();		
			}});
        HTML h = new HTML();
        h.setHTML("<h1 style=\"text-indent:10px\">n3phele login</h1><p style=\"border-style:solid;border-width:1px;border-color:#4f81bd\"></p>");
        v.add(h);
        errmsg = new HTML();
        errmsg.setHeight("100px");
		errmsg.setWordWrap(true);
		errmsg.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        v.add(errmsg);
        register.setStyleName(N3phele.n3pheleResource.css().registerButton());
        forgot.setStyleName(N3phele.n3pheleResource.css().registerButton());
        v.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_DEFAULT);
        v.add(form);
        v.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        v.add(register);
        v.add(forgot);

        initWidget(this.decoratorPanel);
        

    }

//    // This is our JSNI method that will be called on form submit
//    private native void injectLoginFunction(LoginView view) /*-{
//        $wnd.__gwt_login = function(){
//        view.@n3phele.client.view.LoginView::doLogin()();
//        }
//    }-*/;
//
//    // This is our internal method that is called from the JSNI method
//    private void doLogin() {
//        setMessage("checking your identity and credentials");
//    	N3phele.authenticate(getUsername(), getPassword(), this);
//    }


    public final String getPassword() {
        return ((InputElement) Document.get().getElementById(PASSWORD_ID)).getValue();
    }

 
    public final String getUsername() {
        return ((InputElement) Document.get().getElementById(USERNAME_ID)).getValue();
    }

	public void setMessage(String text) {
		errmsg.setText(text);
	}
	
	public void clear() {
		((InputElement) Document.get().getElementById(PASSWORD_ID)).setValue("");
		//form.setVisible(false);
		//remove(form);
		//this.setVisible(false);
		
	}

}