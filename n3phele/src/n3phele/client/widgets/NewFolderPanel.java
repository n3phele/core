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

import java.util.ArrayList;
import java.util.List;

import n3phele.client.N3phele;
import n3phele.client.model.Repository;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellWidget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;

public class NewFolderPanel extends FlowPanel {
	private static NewFolderPanel instance = null;
	private PopupPanel popup;
	private FlexTable table;
	private HorizontalPanel header;
	private PushButton quitWidget;
	final private TextBox folderName;
	final private Button ok;
	String name;
	
	protected NewFolderPanel() {
		this.addStyleName(N3phele.n3pheleResource.css().workspacePanel());
		Cell<MenuItem> cell = new IconTextCell<MenuItem>(32,32);
		header = new HorizontalPanel();
		header.setWidth("100%");
		header.addStyleName(N3phele.n3pheleResource.css().workspacePanelHeader());
		header.add(new CellWidget<MenuItem>(cell, new MenuItem(N3phele.n3pheleResource.folderAddedIcon(), "New Folder", null)));
		this.add(header);
		quitWidget = new PushButton(new Image(N3phele.n3pheleResource.dialog_close()), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				GWT.log("Hide popup");
				NewFolderPanel.this.name = null;
				NewFolderPanel.this.popup.hide();
			}});

		quitWidget.setWidth(N3phele.n3pheleResource.dialog_close().getWidth()+4+"px");
		quitWidget.setHeight(N3phele.n3pheleResource.dialog_close().getHeight()+4+"px");
		
		header.add(quitWidget);
		header.setCellHorizontalAlignment(quitWidget, HorizontalPanel.ALIGN_RIGHT);
		header.setCellVerticalAlignment(quitWidget, HorizontalPanel.ALIGN_TOP);

		table = new FlexTable();
		
		table.setWidget(0, 0, new Label("name"));
		table.setWidget(0, 1, folderName = new TextBox());
		table.setWidget(1, 0, ok = new Button("add"));
		ok.setTitle("Adds folder name placeholder to browser view.\r\r" +
				"Actual folder will be created in the repository during command execution.\r" +
				"Unused placeholders are automatically delelted.");
		ok.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				NewFolderPanel.this.name = NewFolderPanel.this.folderName.getText();
				NewFolderPanel.this.popup.hide();
			}
		});

		this.add(table);
	}
	
	public void clearName() {
		this.folderName.setText(null);
		name = null;
	}
	public String getFolderName() {
		if(this.name != null) {
			if(this.name.trim().length()==0)
				this.name = null;
		}
		return this.name;
	}
		
	/**
	 * @param node
	 * @param newFolderPopup
	 * @return
	 */
	public static NewFolderPanel getInstance(PopupPanel newFolderPopup, Repository repository) {
		if(instance == null)
			instance = new NewFolderPanel();
		instance.popup = newFolderPopup;
		List<Repository> repos = new ArrayList<Repository>(1);
		repos.add(repository);
		return instance;
	}

}
