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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import n3phele.client.N3phele;
import n3phele.client.model.CloudProcess;
import n3phele.client.model.Narrative;
import n3phele.client.presenter.ActivityPlace;
import n3phele.client.presenter.ProgressActivity;
import n3phele.client.resource.NarrativeListCellTableResource;
import n3phele.client.widgets.IconText;
import n3phele.client.widgets.IconTextCell;
import n3phele.client.widgets.MenuItem;
import n3phele.client.widgets.WorkspaceVerticalPanel;

import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.CellWidget;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;


public class ProgressView extends WorkspaceVerticalPanel {
	final private FlexTable table;
	final private CellTable<Narrative> narrativeTable;
	private ProgressActivity presenter;
	private CloudProcess process;
	private Column<Narrative,ImageResource> state;
	private Label name;
	private CellWidget<IconText> iconStatus;
	private Hyperlink command;
	private Label description;
	private CellWidget<Date> startdate;
	private CellWidget<Date> completedate;
	private Label duration;
	private HashMap<String, ImageResource> statusVizualization;
	private String barUrl;
	private static NarrativeListCellTableResource resource=null;
	public ProgressView() {
		super(new MenuItem(N3phele.n3pheleResource.activityIcon(), "Activity", null));
		table = new FlexTable();
		table.setCellPadding(2);


		this.add(table);
		//table.setSize("478px", "260px");

		Label lblNewLabel_4 = new Label("name");
		table.setWidget(0, 0, lblNewLabel_4);

		name = new Label("");
		table.setWidget(0, 1, name);

		iconStatus = new CellWidget<IconText>(new IconTextCell<IconText>(32,32,15));
//		{
//			@Override
//			public IconText getValue(Progress process) {
//				String status = process.getStatus();
//				ImageResource icon = statusVizualization.get(status);
//				if(icon != null) return new IconText(icon, process.getName());
//				return new IconText(getTemplate().statusBar(getPercentComplete(process), barUrl ), process.getName()); // make process bar
//			}
//		};


		table.setWidget(0, 2, iconStatus);

		Label lblNewLabel = new Label("running");
		table.setWidget(1, 0, lblNewLabel);

		command = new Hyperlink("","");
		table.setWidget(1, 1, command);

		description = new Label("-description-");
		table.setWidget(1, 2, description);

		Label lblNewLabel_3 = new Label("started");
		table.setWidget(2, 0, lblNewLabel_3);

		startdate = new CellWidget<Date>(new DateCell(DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT)));

		table.setWidget(2, 1, startdate);
		table.getFlexCellFormatter().setColSpan(2, 1, 2);

		Label lblNewLabel_6 = new Label("completed");
		table.setWidget(3, 0, lblNewLabel_6);

		completedate = new CellWidget<Date>(new DateCell(DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT)));
		table.setWidget(3, 1, completedate);
		table.getFlexCellFormatter().setColSpan(3, 1, 2);

		Label lblNewLabel_7 = new Label("estimated duration", true);
		table.setWidget(4, 0, lblNewLabel_7);

		duration = new Label(".duration");
		table.setWidget(4, 1, duration);
		table.getFlexCellFormatter().setColSpan(4, 1, 2);
		if(resource==null)
			resource = GWT.create(NarrativeListCellTableResource.class);
		narrativeTable = new CellTable<Narrative>(15, resource);
		this.add(narrativeTable);
		narrativeTable.setWidth("100%", true);

		final Map<String,ImageResource> mapper = new HashMap<String,ImageResource>();
		mapper.put("info",N3phele.n3pheleResource.narrativeInfo());
		mapper.put("warning",N3phele.n3pheleResource.narrativeWarning());
		mapper.put("error",N3phele.n3pheleResource.narrativeError());
		state = new Column<Narrative,ImageResource>(new ImageResourceCell()) {
			@Override
			public ImageResource getValue(Narrative object) {
				return mapper.get(object.getState());
			}
		};
		state.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		state.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		narrativeTable.addColumn(state);
		narrativeTable.setColumnWidth(state,"8%");

		Column<Narrative, Date> date = new Column<Narrative, Date>(new DateCell(DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT))) {
			@Override
			public Date getValue(Narrative object) {
				return object.getStamp();
			}
		};
		narrativeTable.addColumn(date);
		narrativeTable.setColumnWidth(date,"15%");

		TextColumn<Narrative> id = new TextColumn<Narrative>() {
			@Override
			public String getValue(Narrative object) {
				return object.getTag();
			}
		};
		id.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		id.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);

		narrativeTable.addColumn(id);
		narrativeTable.setColumnWidth(id,"20%");

		TextColumn<Narrative> msg = new TextColumn<Narrative>() {
			@Override
			public String getValue(Narrative object) {
				return object.getText();
			}
		};
		msg.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		msg.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);

		narrativeTable.addColumn(msg);
		narrativeTable.setColumnWidth(msg,"60%");
	}



	public void do_cancel() {
		setData(null);
		this.presenter.goToPrevious();
	}

	public void setData(CloudProcess process) {
		this.process = process;
		if(statusVizualization == null) {
			statusVizualization = new HashMap<String, ImageResource>();
			statusVizualization.put("COMPLETE",N3phele.n3pheleResource.completedIcon());
			statusVizualization.put("FAILED",N3phele.n3pheleResource.FailedIcon());
			statusVizualization.put("CANCELLED",N3phele.n3pheleResource.cancelledIcon());
			statusVizualization.put("INIT",N3phele.n3pheleResource.initIcon());
			statusVizualization.put("BLOCKED",N3phele.n3pheleResource.blockedIcon());
			barUrl = new Image(N3phele.n3pheleResource.barBackground()).getUrl();
		}
		if(this.process != null) {
			ActivityPlace place = new ActivityPlace(this.process.getUri());
			this.name.setText(this.process.getName());
			this.command = new Hyperlink("this.process.getCommand()xxx",  place.getToken());
			table.setWidget(1, 1, this.command);
			this.startdate.setValue(this.process.getStart());
			this.completedate.setValue(this.process.getComplete());
			this.description.setText("this.process.getDescription()xxx");
			this.duration.setText(durationText(12345)); //FIXME
			
			this.iconStatus.setValue(getIconText(this.process));
			this.narrativeTable.setRowData(this.process.getNarrative()); 
			N3phele.checkSize();
		} else {

		}
	}
	
	protected String durationText(long duration) {
		StringBuilder body = new StringBuilder();
		if (duration >= 60 * 60 * 24) {
			double days = Math.round((duration * 10.0) / (60 * 60 * 24))/10.0;
			body.append(Double.toString(days));
			body.append(days > 1 ? " days" : " day");
		} else if (duration > 60 * 60) {
			double hours = Math.round(10 * duration
					/ (60 * 60.0)) / 10.0;
			body.append(Double.toString(hours));
			body.append(hours > 1.0 ? " hours" : " hour");
		} else if (duration > 60) {
			double minutes = Math.round(10 * duration
					/ (60.0)) / 10.0;
			body.append(Double.toString(minutes));
			body.append(minutes > 1.0 ? " minutes" : " minute");
		} else {
			long seconds = duration;
			body.append(Long.toString(seconds));
			body.append(seconds > 1 ? " seconds" : " second");
		} 
		return body.toString();
	}
	protected IconText getIconText(CloudProcess process) {
		String status = process.getState();
		ImageResource icon = statusVizualization.get(status);
		if(icon != null) return new IconText(icon, status);
		return new IconText(getTemplate().statusBar(getPercentComplete(process), barUrl ), status); // make process bar
	}
	
	

	public void setPresenter(ProgressActivity presenter) {
		this.presenter = presenter;
	}

	public void refresh(CloudProcess process) {
		setData(process);
	}



	public interface StatusCellSafeHTMLTemplate extends SafeHtmlTemplates {
		@Template("<div style=\"margin-top:6px;height:10px;width:40px;cursor:default;border:thin #7ba5d5 solid;\">"
				+ "<div style=\"height:10px;width:{0}%; background-image: url({1}); \">"
				+ "</div></div>")
				SafeHtml statusBar(double percentage, String image);

	}

	private static StatusCellSafeHTMLTemplate template;

	private StatusCellSafeHTMLTemplate getTemplate() {
		if (template == null) {
			template = GWT.create(StatusCellSafeHTMLTemplate.class);
		}
		return template;
	}

	public double getPercentComplete(CloudProcess process) {
		//return ProgressUpdateHelper.updateProcess(process)/10.0;
		return 0;
	}	


}
