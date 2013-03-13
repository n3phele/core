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
import n3phele.client.model.FileNode;
import n3phele.client.model.Origin;

import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;

public class FileDetailsPanel extends FlowPanel {
	private static FileDetailsPanel instance = null;
	private PopupPanel popup;
	private FlexTable table;
	private HorizontalPanel header;
	private FileNode node;
	private Label size;
	private Label date;
	private InlineHyperlink activity;
	private InlineLabel modifiedLabel;
	private InlineLabel originLabel;
	private PushButton quitWidget;
	private Image icon;
	private Label banner;
	private Button makePublic;
	private Button makePrivate;
	private Button delete;
	private HorizontalPanel buttonBar;
	private Delegate<FileNode> deleteHander;
	private Delegate<FileNode> makePublicHandler;
	private Delegate<FileNode> makePrivateHandler;
	final private static InlineLabel uploaded = new InlineLabel("upload");
	
	protected FileDetailsPanel(Delegate<FileNode> deleteHandler, Delegate<FileNode> makePublicHandler, Delegate<FileNode> makePrivateHandler) {
		super();
		this.deleteHander = deleteHandler;
		this.makePublicHandler = makePublicHandler;
		this.makePrivateHandler = makePrivateHandler;
		this.addStyleName(N3phele.n3pheleResource.css().workspacePanel());;
		header = new HorizontalPanel();
		header.setSpacing(3);
		header.addStyleName(N3phele.n3pheleResource.css().workspacePanelHeader());
		header.add(icon = new Image(N3phele.n3pheleResource.fileIcon()));
		header.add(banner = new InlineLabel("File details"));
		quitWidget = new PushButton(new Image(N3phele.n3pheleResource.dialog_close()), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				GWT.log("Hide popup");
				FileDetailsPanel.this.popup.hide();
			}});
		
		quitWidget.setPixelSize(N3phele.n3pheleResource.dialog_close().getWidth(),N3phele.n3pheleResource.dialog_close().getHeight()+4 );
		
		header.add(quitWidget);
		header.setCellHorizontalAlignment(icon, HorizontalPanel.ALIGN_LEFT);
		header.setCellHorizontalAlignment(banner, HorizontalPanel.ALIGN_LEFT);
		header.setCellHorizontalAlignment(quitWidget, HorizontalPanel.ALIGN_RIGHT);
		header.setCellVerticalAlignment(quitWidget, HorizontalPanel.ALIGN_TOP);
		header.setWidth("100%");
		this.add(header);

		table = new FlexTable();
		
		table.setWidget(0, 1, size = new InlineLabel());
		table.setWidget(1, 0, modifiedLabel = new InlineLabel("Modified"));
		table.setWidget(1, 1, date = new InlineLabel());
		table.setWidget(2, 0, originLabel = new InlineLabel("Created by"));
		table.setWidget(2, 1, activity = new InlineHyperlink("", false, ""));
		originLabel.setVisible(false);
		modifiedLabel.setVisible(false);
		activity.setVisible(false);
		this.add(table);
		buttonBar = new HorizontalPanel();
		buttonBar.setWidth("100%");
		this.add(buttonBar);
		delete = new Button("Delete", new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				FileDetailsPanel.this.deleteHander.execute(FileDetailsPanel.this.node);
				
			}});
		makePublic = new Button("Make Public", new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				FileDetailsPanel.this.makePublicHandler.execute(FileDetailsPanel.this.node);
				
			}});
		makePrivate = new Button("Make Private", new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				FileDetailsPanel.this.makePrivateHandler.execute(FileDetailsPanel.this.node);
				
			}});
		
	}

	final static DateTimeFormat dateFormater = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM);
	public void setFileNode(FileNode node) {
		this.node = node;
		if(node == null) {
			banner.setText("file details.");
			size.setText("");
			date.setText("");
			originLabel.setVisible(false);
			modifiedLabel.setVisible(false);
			activity.setVisible(false);
			table.setWidget(2, 1, activity);
			buttonBar.clear();
		} else if(node.getMime() != null && node.getMime().endsWith("Folder")) {
			boolean isPublic = node.getMime().endsWith("PublicFolder");
			banner.setText((isPublic?"Public Folder ":"Folder ")+node.getName());
			icon.setUrl(isPublic?N3phele.n3pheleResource.publicFolder().getSafeUri():N3phele.n3pheleResource.folderIcon().getSafeUri());
			size.setText("");
			date.setText("");
			originLabel.setVisible(false);
			modifiedLabel.setVisible(false);
			activity.setVisible(false);
			table.setWidget(2, 1, activity);
			buttonBar.clear();
			buttonBar.add(delete);
			buttonBar.add(isPublic? makePrivate : makePublic);
			buttonBar.setCellHorizontalAlignment(delete, HorizontalPanel.ALIGN_LEFT);
			buttonBar.setCellHorizontalAlignment(isPublic? makePrivate : makePublic, HorizontalPanel.ALIGN_RIGHT);
			
		} else if(node.getMime() != null && node.getMime().endsWith("Placeholder")) {
			banner.setText("New Folder "+node.getName());
			icon.setUrl(N3phele.n3pheleResource.folderAddedIcon().getSafeUri());

			size.setText("");
			date.setText("");
			originLabel.setVisible(false);
			modifiedLabel.setVisible(false);
			activity.setVisible(false);
			table.setWidget(2, 1, activity);
			buttonBar.clear();
		} else {
			banner.setText(node.getName());
			icon.setUrl(N3phele.n3pheleResource.fileIcon().getSafeUri());
			size.setText(fileSizeToString(node.getSize()));
			if(node.getModified() != null) {
				date.setText(dateFormater.format(node.getModified()));
				modifiedLabel.setVisible(true);
			} else {
				date.setText("");
				modifiedLabel.setVisible(false);
			}
			activity.setText(" ");
			table.setWidget(2, 1, activity);
			activity.setVisible(false);
			originLabel.setVisible(true);
			buttonBar.clear();
			buttonBar.add(delete);
		}
	}
	public FileNode getNode() {
		return this.node;
	}
		
	/**
	 * @param node
	 * @param newFolderPopup
	 * @return
	 */
	public static FileDetailsPanel getInstance(PopupPanel fileDetailPopup, FileNode node, 
			Delegate<FileNode> deleteHandler, Delegate<FileNode> makePublicHandler, Delegate<FileNode> makePrivateHandler) {
		if(instance == null)
			instance = new FileDetailsPanel(deleteHandler, makePublicHandler, makePrivateHandler);
		instance.popup = fileDetailPopup;
		instance.setFileNode(node);
		return instance;
	}
	
	private static String fileSizeToString(Long size) {
	    if (size == null) {
		      return "";
	    }
	    double result = (double)size;
	    String formatted;
	    if(size < 1024) {
	    	formatted =  size+" B";
	    } else if(size < 1048576) {
	    	result = Math.round(result/102.4);
	    	formatted = Double.toString(result/10)+" KB";
	    } else if(size < 1073741824) {
	    	result = Math.round(result/104857.6);
	    	formatted = Double.toString(result/10)+" MB";
	    } else {
	    	result = Math.round(result/107374182.4);
	    	formatted = Double.toString(result/10)+" GB";
	    }
	    return formatted;
	}
	
	public boolean isShowing(FileNode node) {
		if(node == this.node)
			return true;
		if(node == null || this.node == null)
			return false;
		return safeEquals(node.getName(), this.node.getName());
	}
	
	private boolean safeEquals(Object a, Object b) {
		if(a == b) return true;
		if(a == null || b == null) return false;
		return a.equals(b);
	}
	public void setOrigin(FileNode node, Origin data) {
		if(this.popup.isShowing()) {
			if(node.getName().equals(this.node.getName())) {
				if(data != null && data.getProcess() != null) {
					boolean outOfSync = (data.getModified() != null && !data.getModified().equals(node.getModified()));
					if(outOfSync) {
						long origin = data.getModified().getTime();
						long file = node.getModified().getTime();
						if(((origin/1000) == (file/1000)) && ((origin % 100) == 0 || (file % 100) == 0)) {
							outOfSync = false; // rounding error in time transfer
						}
					}
					if(outOfSync) {
						icon.setUrl(N3phele.n3pheleResource.outOfSyncIcon().getSafeUri());
					}
					activity.setTargetHistoryToken("process:"+data.getProcess());
					if(outOfSync)
						activity.setText(data.getProcessName()+" on "+dateFormater.format(data.getModified()));
					else
						activity.setText(data.getProcessName());
					activity.setVisible(true);
				} else {
					table.setWidget(2, 1, uploaded);
					activity.setVisible(false);
				}
			}
		}
		
	}
}
