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

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.HTML;

public class SectionPanel extends FlowPanel {
	private final HTML html;
	private final FlexTable header;
	private int buttons = 0;
	public SectionPanel(String text) {
		this.header = new FlexTable();
		this.header.addStyleName(N3phele.n3pheleResource.css().sectionPanelHeader());
		this.addStyleName(N3phele.n3pheleResource.css().sectionPanel());
		this.setWidth("100%");
		this.html = new HTML(text, true);
		this.html.addStyleName(N3phele.n3pheleResource.css().gwtHTMLSectionPanel());
		this.header.setHeight("1.5em");
		this.header.setWidth("100%");
		this.header.setWidget(0,0,this.html);
		this.header.getCellFormatter().setWidth(0, 0, "70%");
		add(this.header);
	}
	
	public void addButton(IsWidget w) {
		buttons += 1;
		this.header.setWidget(0,buttons,w.asWidget());
		this.header.getFlexCellFormatter().setHorizontalAlignment(0, buttons, HasHorizontalAlignment.ALIGN_RIGHT);
		w.asWidget().setStyleName(N3phele.n3pheleResource.css().sectionPanelHeaderButton());
	}
	
	public SectionPanel() {
		this("SectionPanel");
	}

	public void setHeading(String title) {
		this.html.setHTML(title);
	}
	
	private IsWidget view=null;
	public void setWidget(IsWidget w) {
		if(view != null) {
			this.remove(view);
		}
		view = w;
		if(view != null) {
			this.add(this.view);
		}
	}
}
