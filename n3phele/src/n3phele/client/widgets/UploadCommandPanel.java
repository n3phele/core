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
import n3phele.client.presenter.CommandListActivity;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellWidget;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;


public class UploadCommandPanel extends FormPanel {
	private static UploadCommandPanel instance = null;
	private PopupPanel uploadPopup;
	private FlexTable table;
	private HorizontalPanel header;
	private PushButton quitWidget;
	private FlowPanel flow;

	private Button upload;
	private FileUpload fileUpload;
	private boolean cancelled = true;
	
	protected UploadCommandPanel() {
		this.setWidth("500px");
		flow = new FlowPanel();
		this.add(flow);
		this.addStyleName(N3phele.n3pheleResource.css().workspacePanel());
		Cell<MenuItem> cell = new IconTextCell<MenuItem>(32,32);
		header = new HorizontalPanel();
		header.setWidth("100%");
		header.addStyleName(N3phele.n3pheleResource.css().workspacePanelHeader());
		header.add(new CellWidget<MenuItem>(cell, new MenuItem(N3phele.n3pheleResource.uploadIcon(), "Command Definition Import", null)));
		flow.add(header);
		quitWidget = new PushButton(new Image(N3phele.n3pheleResource.dialog_close()), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				cancelled = true;
				UploadCommandPanel.this.uploadPopup.hide();
			}});

		quitWidget.setWidth(N3phele.n3pheleResource.dialog_close().getWidth()+4+"px");
		quitWidget.setHeight(N3phele.n3pheleResource.dialog_close().getHeight()+4+"px");
		
		header.add(quitWidget);
		header.setCellHorizontalAlignment(quitWidget, HorizontalPanel.ALIGN_RIGHT);
		header.setCellVerticalAlignment(quitWidget, HorizontalPanel.ALIGN_TOP);
	    this.setEncoding(FormPanel.ENCODING_MULTIPART);
	    this.setMethod(FormPanel.METHOD_POST);
	    this.setAction("/resources/command/import");

		table = new FlexTable();
	    flow.add(table);

		table.setWidth("490px");
		fileUpload = new FileUpload();
		fileUpload.setName("file");
		fileUpload.setWidth("100%");
		fileUpload.addChangeHandler(new ChangeHandler(){

			@Override
			public void onChange(ChangeEvent event) {
				upload.setEnabled(isValid());
			}});

		InlineHTML step1Text = new InlineHTML("Choose command definition file");
		step1Text.setStyleName(N3phele.n3pheleResource.css().repoContentElementName());
		table.setWidget(0, 1, step1Text);
		table.setWidget(0, 2, fileUpload);

		upload = new Button("import");
		upload.addClickHandler(new ClickHandler(){


			@Override
			public void onClick(ClickEvent event) {
				if(isValid()) {
					GWT.log("submiting ...");
					submit();
				}
				upload.setEnabled(false);
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
				UploadCommandPanel.this.uploadPopup.hide();
				Window.alert(event.getResults()==null?"An error occurred":event.getResults().replaceAll("<[Pp][Rr][Ee][^>]*>", "").replaceAll("</[Pp][Rr][Ee]>", ""));
			}
		});
		flow.add(upload);

		table.getFlexCellFormatter().setColSpan(0, 2, 2);
		table.getFlexCellFormatter().setWidth(0, 0, "10%");
		table.getFlexCellFormatter().setWidth(0, 2, "70%");
	}
		
	/**
	 * @param node
	 * @param uploadPopup
	 * @return
	 */
	public static UploadCommandPanel getInstance(PopupPanel uploadPopup, CommandListActivity presenter) {
		if(instance == null)
			instance = new UploadCommandPanel();
		instance.uploadPopup = uploadPopup;

		instance.upload.setEnabled(instance.isValid());
		instance.cancelled = true;
		return instance;
	}
	
	public boolean isCancelled() {
		return this.cancelled;
	}

	private boolean isValid() {
		return this.fileUpload.getFilename()!=null && this.fileUpload.getFilename().length() != 0 && this.fileUpload.getFilename().toLowerCase().endsWith(".n");
	}
}
