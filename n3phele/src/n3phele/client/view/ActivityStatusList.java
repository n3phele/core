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

import java.util.HashMap;
import java.util.Map;

import n3phele.client.N3phele;
import n3phele.client.model.CloudProcessSummary;
import n3phele.client.presenter.helpers.ProgressUpdateHelper;
import n3phele.client.resource.ClickableCellTableResource;
import n3phele.client.widgets.IconText;
import n3phele.client.widgets.IconTextCell;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Image;

public class ActivityStatusList extends CellTable<CloudProcessSummary> {
	private static Map<String, ImageResource> statusVizualization = null;
	private static String barUrl; 
	private static ClickableCellTableResource resource = GWT.create(ClickableCellTableResource.class);
	public ActivityStatusList() {
		super(15, resource);
		if(statusVizualization == null) {
			statusVizualization = new HashMap<String, ImageResource>();
			statusVizualization.put("COMPLETE",N3phele.n3pheleResource.completedIcon());
			statusVizualization.put("FAILED",N3phele.n3pheleResource.FailedIcon());
			statusVizualization.put("CANCELLED",N3phele.n3pheleResource.cancelledIcon());
			statusVizualization.put("INIT",N3phele.n3pheleResource.initIcon());
			statusVizualization.put("BLOCKED",N3phele.n3pheleResource.blockedIcon());
			barUrl = new Image(N3phele.n3pheleResource.barBackground()).getUrl();
		}
		Column<CloudProcessSummary, IconText> activityColumn = new Column<CloudProcessSummary, IconText>(new IconTextCell<IconText>(20,20,25)) {
			@Override
			public IconText getValue(CloudProcessSummary process) {
				String status = process.getState();
				ImageResource icon = statusVizualization.get(status);
				if(icon != null) return new IconText(icon, process.getName());
				return new IconText(getTemplate().statusBar(getPercentComplete(process), barUrl ), process.getName()); // make progress bar
			}
		};
		this.addColumn(activityColumn);
		this.setWidth("100%", true);
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
	
	public double getPercentComplete(CloudProcessSummary process) {
		return ProgressUpdateHelper.updateProcess(process)/10.0;
	}	
	
	
}
