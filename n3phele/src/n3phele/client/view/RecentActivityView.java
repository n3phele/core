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

import java.util.ArrayList;
import java.util.List;

import n3phele.client.model.CloudProcessSummary;
import n3phele.client.presenter.AbstractCloudProcessActivity;
import n3phele.client.widgets.SectionPanel;

import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

public class RecentActivityView extends SectionPanel implements CloudProcessView {
	private ActivityStatusList cellTable;
	private List<CloudProcessSummary> data = null;
	private AbstractCloudProcessActivity presenter;
	public RecentActivityView() {
		super();
		//setSize("100%", "100%");
		this.cellTable = new ActivityStatusList();
		this.setWidget(this.cellTable);
		this.setHeading("Recent activities");
		final SingleSelectionModel<CloudProcessSummary> selectionModel = new SingleSelectionModel<CloudProcessSummary>();
	    cellTable.setSelectionModel(selectionModel);
	    selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
	      public void onSelectionChange(SelectionChangeEvent event) {
	        CloudProcessSummary selected = selectionModel.getSelectedObject();
	        if (selected != null) {
	          if(presenter != null) {
	        	 presenter.onSelect(selected);
	        	 selectionModel.setSelected(selected, false);
	          }
	        }
	      }
	    });
		
	}
	@Override
	public void setDisplayList(List<CloudProcessSummary> processList, int start, int max) {
		if(processList == null)
			processList = new ArrayList<CloudProcessSummary>();
		this.cellTable.setRowCount(processList.size(), true);
		this.cellTable.setRowData(data=processList);
	}
	@Override
	public void setPresenter(AbstractCloudProcessActivity presenter) {
		this.presenter = presenter;
		
	}

	@Override
	public void refresh(int i, CloudProcessSummary update) {
		this.cellTable.setRowData(i, data.subList(i, i+1));
	}

	@Override
	public int getPageSize() {
		return 8;
	}
	
}
