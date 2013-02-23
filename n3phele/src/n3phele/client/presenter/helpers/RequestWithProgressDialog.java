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
package n3phele.client.presenter.helpers;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

public class RequestWithProgressDialog  {
	
	protected final RequestBuilder rb;
	protected Request request=null;
	protected int retries;
	private String message;
	private HTML alert;
	private HTML details;
	private DialogBox working;
	public RequestWithProgressDialog(RequestBuilder rb, String message, int retries) {
		this.rb = rb;
		this.retries = retries;
		this.message = message;
	}
	

	public Request sendRequest(final String requestData, final RequestCallback callback)throws RequestException {
		request = rb.sendRequest(requestData, new RequestCallback() {
			@Override
			public void onResponseReceived(Request request, Response response) {
				hideLoadingMessage();
				callback.onResponseReceived(request, response);
				
			}

			@Override
			public void onError(Request request, Throwable exception) {
				GWT.log(exception.toString(), exception);
				if (retries-- <= 0) {
					alert("Failure "+exception.toString());
					callback.onError(request, exception);
				} else {
					try {
						alert("Failure "+exception.toString()+" retrying..");
						request = sendRequest(requestData, callback);
					} catch (RequestException e) {
						this.onError(request, e);
					}
				}
			}
		});
		if(request != null) createLoadingMessage();
		return request;
	}

	public void onError(Request request, Throwable exception) {
		alert("Processing failed:"+exception.toString());
	}
	
	public void cancel() {
		if(request != null) {
			request.cancel();
		}
		hideLoadingMessage();
	}
	
	public void hideLoadingMessage() {
        working.hide();
	}
	
	public void createLoadingMessage() {
		working = new DialogBox();
		FlexTable dialogContents = new FlexTable();
		dialogContents.setCellPadding(15);    
		working.setWidget(dialogContents);
	
	    // Add some text to the top of the dialog
	    details = new HTML(this.message);
	    alert = new HTML();
	    dialogContents.setWidget(0,0,details);
	    dialogContents.getFlexCellFormatter().setColSpan(0,0,2);
	    dialogContents.setWidget(1,0,alert);
	    dialogContents.getFlexCellFormatter().setColSpan(1,0,2);
	    Button closeButton = new Button(
	            "cancel", new ClickHandler() {
	              public void onClick(ClickEvent event) {
	                RequestWithProgressDialog.this.cancel();
	              }
	            });
	    dialogContents.setWidget(2,1,closeButton);
	    dialogContents.getFlexCellFormatter().setHorizontalAlignment(2, 1, HasHorizontalAlignment.ALIGN_LEFT);
	    working.setGlassEnabled(true);
	    working.setAnimationEnabled(true);
	    working.center();
	    working.show();

	}
	
	public void alert(String msg) {
		this.alert.setHTML(this.alert.getHTML()+"<p>"+msg);
	}
	
	
}
