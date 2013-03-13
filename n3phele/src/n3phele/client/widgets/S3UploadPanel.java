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
package n3phele.client.widgets;

import n3phele.client.N3phele;
import n3phele.client.presenter.RepoContentActivity;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellWidget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;

/**
 * @author Nigel Cook
 *
 * (C) Copyright 2010. All rights reserved.
 * 
 *
 */
public class S3UploadPanel extends FormPanel implements UploadPanel {
	private static S3UploadPanel instance = null;
	private PopupPanel uploadPopup;
	private FlexTable table;
	private HorizontalPanel header;
	private PushButton quitWidget;
	private TextBox filename;
	private HTML destination;
	private FlowPanel flow;
	private Hidden key;
	private Hidden awsAccessKeyId;
	private Hidden acl;
	private Hidden policy;
	private Hidden signature;
	private Hidden contentType;
	private RepoContentActivity activity;
	private String path;
	private String canonicalName;
	private Button upload;
	private FileUpload fileUpload;
	private Hidden success_action;
	private boolean cancelled = true;
	
	protected S3UploadPanel() {
		this.setWidth("500px");
		flow = new FlowPanel();
		this.add(flow);
		this.addStyleName(N3phele.n3pheleResource.css().workspacePanel());
		Cell<MenuItem> cell = new IconTextCell<MenuItem>(32,32);
		header = new HorizontalPanel();
		header.setWidth("100%");
		header.addStyleName(N3phele.n3pheleResource.css().workspacePanelHeader());
		header.add(new CellWidget<MenuItem>(cell, new MenuItem(N3phele.n3pheleResource.uploadIcon(), "File Upload", null)));
		flow.add(header);
		quitWidget = new PushButton(new Image(N3phele.n3pheleResource.dialog_close()), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				cancelled = true;
				S3UploadPanel.this.uploadPopup.hide();
			}});

		quitWidget.setWidth(N3phele.n3pheleResource.dialog_close().getWidth()+4+"px");
		quitWidget.setHeight(N3phele.n3pheleResource.dialog_close().getHeight()+4+"px");
		
		header.add(quitWidget);
		header.setCellHorizontalAlignment(quitWidget, HorizontalPanel.ALIGN_RIGHT);
		header.setCellVerticalAlignment(quitWidget, HorizontalPanel.ALIGN_TOP);
	    this.setEncoding(FormPanel.ENCODING_MULTIPART);
	    this.setMethod(FormPanel.METHOD_POST);
		key = new Hidden("key");
		flow.add(key);
		awsAccessKeyId = new Hidden("AWSAccessKeyId");
		flow.add(awsAccessKeyId);
		acl = new Hidden("acl", "private");
		flow.add(acl);
		success_action = new Hidden("success_action_status", "201");
		flow.add(success_action);
		policy = new Hidden("policy");
		flow.add(policy);
		signature = new Hidden("signature");
		flow.add(signature);
		contentType = new Hidden("Content-Type");
		flow.add(contentType);
		table = new FlexTable();
	    flow.add(table);

		table.setWidth("490px");
		fileUpload = new FileUpload();
		fileUpload.setName("file");
		fileUpload.setWidth("100%");
		fileUpload.addChangeHandler(new ChangeHandler(){

			@Override
			public void onChange(ChangeEvent event) {
				filename.setText(getFilename(fileUpload.getFilename()));
				upload.setEnabled(fileUpload.getFilename()!=null && fileUpload.getFilename().length()>0);
				
			}});
		InlineHTML step1 = new InlineHTML("Step 1:");
		step1.setStyleName(N3phele.n3pheleResource.css().repoContentElementName());
		table.setWidget(0, 0, step1);
		InlineHTML step1Text = new InlineHTML("Choose file to upload");
		step1Text.setStyleName(N3phele.n3pheleResource.css().repoContentElementName());
		table.setWidget(0, 1, step1Text);
		table.setWidget(0, 2, fileUpload);
		InlineHTML step2 = new InlineHTML("Step 2:");
		step2.setStyleName(N3phele.n3pheleResource.css().repoContentElementName());
		table.setWidget(1, 0, step2);
		InlineHTML step2Text =  new InlineHTML("Set Destination");
		step2Text.setStyleName(N3phele.n3pheleResource.css().repoContentElementName());
		table.setWidget(1, 1, step2Text);
		destination = new HTML("/");
		table.setWidget(1, 2, destination);
		destination.setStyleName(N3phele.n3pheleResource.css().repoContentUploadPath());
		filename = new TextBox();
		table.setWidget(1, 3, filename );
		

		
		upload = new Button("upload");
		upload.addClickHandler(new ClickHandler(){


			@Override
			public void onClick(ClickEvent event) {
				canonicalName = path;
				if(path == null || path.length()==0) {
					canonicalName = filename.getText().trim();
				} else {
					canonicalName = path + filename.getText().trim();;
				}
				if(activity != null) {
					upload.setEnabled(false);
					activity.getSignature(canonicalName);
				}
			}});
		table.setWidget(2, 3, upload);
		
		this.addSubmitHandler(new SubmitHandler() {
			@Override
			public void onSubmit(SubmitEvent event) {
				GWT.log("submit ..."+event.toString());
				
			}});
		this.addSubmitCompleteHandler(new SubmitCompleteHandler() {

			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				cancelled = false;
				S3UploadPanel.this.uploadPopup.hide();
				activity.uploadComplete(canonicalName);
			}
		});
		flow.add(upload);

		table.getFlexCellFormatter().setColSpan(0, 2, 2);
		table.getFlexCellFormatter().setWidth(0, 0, "10%");
		table.getFlexCellFormatter().setWidth(0, 2, "70%");
//		table.getFlexCellFormatter().setWidth(1, 2, "200px");
		table.getFlexCellFormatter().setWidth(1, 3, "30%");
	}
		
	/* (non-Javadoc)
	 * @see n3phele.client.widgets.UploadPanel#setSignature(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void setSignature(String url, String destination, String keyId, String base64Policy, String sign, String content) {
		//if(activity != null && this.destination.equals(canonicalName))
		this.setAction(url);
		key.setValue(destination);
		awsAccessKeyId.setValue(keyId);
		policy.setValue(base64Policy);
		signature.setValue(sign);
		contentType.setValue(content);
		this.submit();
	}
	/**
	 * @param node
	 * @param uploadPopup
	 * @return
	 */
	public static UploadPanel getInstance(PopupPanel uploadPopup, String destination, RepoContentActivity activity) {
		if(instance == null)
			instance = new S3UploadPanel();
		instance.uploadPopup = uploadPopup;
		instance.destination.setText(destination);
		instance.destination.setTitle(destination);
		instance.path = destination;
		instance.setActivity(activity);
		instance.canonicalName = null;
		instance.upload.setEnabled(instance.fileUpload.getFilename()!=null && instance.fileUpload.getFilename().length()>0);
		instance.cancelled = true;
		return instance;
	}
	
	private void setActivity(RepoContentActivity activity) {
		this.activity = activity;
		
	}

	private String getFilename(String canonicalName) {
		if(canonicalName == null || canonicalName.trim().length()==0)
			return null;

		int index = Math.max(canonicalName.lastIndexOf("/"), canonicalName.lastIndexOf("\\"));
		return canonicalName.substring(index+1);
	}
	
	/* (non-Javadoc)
	 * @see n3phele.client.widgets.UploadPanel#isCancelled()
	 */
	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}
	
}
