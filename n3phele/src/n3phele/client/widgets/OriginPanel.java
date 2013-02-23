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
import n3phele.client.presenter.helpers.FileSizeRenderer;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.cellview.client.CellWidget;
import com.google.gwt.user.client.ui.DateLabel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ValueLabel;

public class OriginPanel extends FlowPanel {
	private static OriginPanel instance = null;
	private PopupPanel originPopup;
	private FlexTable table;
	private InlineLabel repoPath;
	private FileSizeLabel fileSize;
	private Image outOfDate;

	private DateLabel lastModified;
	private InlineHyperlink activity;
	private HorizontalPanel header;
	private PushButton quitWidget;
	private FileNode data;
	private Origin origin;
	private InlineLabel activityLabel;
	
	protected OriginPanel() {
		this.setWidth("100%");
		this.addStyleName(N3phele.n3pheleResource.css().workspacePanel());
		Cell<MenuItem> cell = new IconTextCell<MenuItem>(32,32);
		header = new HorizontalPanel();
		header.setWidth("100%");
		header.addStyleName(N3phele.n3pheleResource.css().workspacePanelHeader());
		header.add(new CellWidget<MenuItem>(cell, new MenuItem(N3phele.n3pheleResource.dataSetIcon(), "Origin", null)));
		this.add(header);
		quitWidget = new PushButton(new Image(N3phele.n3pheleResource.dialog_close()), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				GWT.log("Hide popup");
				OriginPanel.this.originPopup.hide();
			}});

		quitWidget.setWidth(N3phele.n3pheleResource.dialog_close().getWidth()+4+"px");
		quitWidget.setHeight(N3phele.n3pheleResource.dialog_close().getHeight()+4+"px");
		
		header.add(quitWidget);
		header.setCellHorizontalAlignment(quitWidget, HorizontalPanel.ALIGN_RIGHT);
		header.setCellVerticalAlignment(quitWidget, HorizontalPanel.ALIGN_TOP);
		table = new FlexTable();
		repoPath = new InlineLabel("");
		table.setWidget(1, 1, repoPath);
		
		fileSize = new FileSizeLabel();
		table.setWidget(2, 1, fileSize);
		table.setWidget(2, 2, new InlineLabel("Last modified"));
		lastModified = new DateLabel(DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM));
		table.setWidget(2, 3, lastModified);
		
		outOfDate = new Image(N3phele.n3pheleResource.outOfSyncIcon());
		table.setWidget(2, 0, outOfDate);
		outOfDate.setVisible(false);
		table.getCellFormatter().setHeight(2, 0, "32");
		table.getCellFormatter().setWidth(2, 0, "32");
		
		activityLabel = new InlineLabel("");
		table.setWidget(0, 1, activityLabel);
		activity = new InlineHyperlink("", false, "");
		table.setWidget(0, 2, activity);
		

		table.getCellFormatter().setHorizontalAlignment(2, 2, HasHorizontalAlignment.ALIGN_LEFT);
		table.getFlexCellFormatter().setColSpan(0, 2, 2);
		table.getCellFormatter().setHorizontalAlignment(0, 2, HasHorizontalAlignment.ALIGN_LEFT);
		table.getFlexCellFormatter().setColSpan(1, 1, 3);
		table.getCellFormatter().setHorizontalAlignment(2, 1, HasHorizontalAlignment.ALIGN_LEFT);
		// table.getFlexCellFormatter().setColSpan(1, 1, 4);
		FlexTableHelper.fixRowSpan(table);
		this.add(table);
	}
	
	public void setFileNode(FileNode data) {
		this.data = data;
		if(this.data != null) {
			String name = this.data.getPath();
			if(name != null && name.length()!=0) {
//				name += "/";
			} else {
				name = "";
			}
			name += this.data.getName();
			repoPath.setText(name);
			lastModified.setValue(this.data.getModified());
			lastModified.setVisible(this.data.getModified()!=null);
			fileSize.setValue(this.data.getSize());
			fileSize.setVisible(this.data.getSize() != 0);
		} else {
			repoPath.setText(null);
			lastModified.setValue(null);
			fileSize.setValue(null);
			activityLabel.setText(null);
			activity.setVisible(false);
		}
	}
	
	/**
	 * @param node
	 * @param origin
	 */
	public void setOrigin(Origin data) {
		this.origin = data;
		if(origin.getActivity() != null) {
			activity.setTargetHistoryToken("activityDetail:"+origin.getActivity());
			activityLabel.setText("Output from");
			activity.setText(origin.getActivityName());
			activity.setVisible(true);
		} else {
			activityLabel.setText("uploaded");
			activity.setVisible(false);
		}
		
		
	}

	private static class FileSizeLabel extends ValueLabel<Long> {

		  public FileSizeLabel() {
		    super(new FileSizeRenderer());
		  }
	}
		
	/**
	 * @param node
	 * @param originPopup
	 * @return
	 */
	public static OriginPanel getInstance(PopupPanel originPopup) {
		if(instance == null)
			instance = new OriginPanel();
		instance.originPopup = originPopup;
		instance.setFileNode(null);
		return instance;
	}


}
