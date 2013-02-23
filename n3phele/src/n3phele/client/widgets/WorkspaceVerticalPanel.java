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

import com.google.gwt.cell.client.Cell;
import com.google.gwt.user.cellview.client.CellWidget;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;

public class WorkspaceVerticalPanel extends FlowPanel {
	CellWidget<MenuItem> header;
	protected List<Hyperlink> headerExtras;
	final protected HorizontalPanel strip;
	public WorkspaceVerticalPanel(MenuItem content, MenuItem... extras) {
		super();
		this.setWidth("500px");
		this.addStyleName(N3phele.n3pheleResource.css().workspacePanel());
		Cell<MenuItem> cell = new IconTextCell<MenuItem>(32,32);
		strip = new HorizontalPanel();
		this.add(strip);
		strip.setWidth("100%");
		strip.addStyleName(N3phele.n3pheleResource.css().workspacePanelHeader());
		header = new CellWidget<MenuItem>(cell, content);
		header.addStyleName(N3phele.n3pheleResource.css().workspacePanelHeader());
		strip.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		strip.add(header);
		strip.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		for(MenuItem m : extras) {
			if(headerExtras == null)
				headerExtras = new ArrayList<Hyperlink>(extras.length);
			Hyperlink h = createHyperlink(m);
			headerExtras.add(h);
			h.addStyleName(N3phele.n3pheleResource.css().workspacePanelMenuItem());
			strip.add(h);
		}
	}
	
	protected Hyperlink createHyperlink(MenuItem m) {
		String html = "<img style='border:none; vertical-align:bottom; margin:-2px; padding-right:2px;' width=20 height=20 src='"+m.getUrl()+"'/>"+m.getText();
		Hyperlink h = new Hyperlink(html, true, m.getPlaceToken());
		return h;
	}
}
