/**
 * @author Nigel Cook
 * @author Douglas Tondin 
 * @author Leonardo Amado
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
import java.util.HashMap;
import java.util.List;
import n3phele.client.N3phele;
import n3phele.client.model.Account;
import n3phele.client.model.CloudProcess;
import n3phele.client.presenter.StackDetailsActivity;
import n3phele.client.presenter.helpers.AuthenticatedRequestFactory;
import n3phele.client.resource.ClickableCellTableResource;
import n3phele.client.widgets.ActionDialogBox;
import n3phele.client.widgets.MenuItem;
import n3phele.client.widgets.WorkspaceVerticalPanel;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Window;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

public class StackDetailsView extends WorkspaceVerticalPanel {
	private CellTable<CloudProcess> cellTable;
	private List<CloudProcess> data = null;
	private StackDetailsActivity presenter = null;
	private static ClickableCellTableResource resource = null;
	private HashMap<String, String> cloudIP = null; 
	public StackDetailsView() {
		super(new MenuItem(N3phele.n3pheleResource.serviceIcon(), "Stack Details", null));			

		if(resource ==null)
			resource = GWT.create(ClickableCellTableResource.class);

		cellTable = new CellTable<CloudProcess>(15, resource);
		cellTable.setSize("455px", "");

		TextColumn<CloudProcess> nameColumn = new TextColumn<CloudProcess>() {
			@Override
			public String getValue(CloudProcess item) {
				String result = "";
				if(item != null){
					result += item.getName();
				}
				return result;
			}
		};
		cellTable.addColumn(nameColumn, "Name");
		cellTable.setColumnWidth(nameColumn, "120px");

		TextColumn<CloudProcess> hoursColumn = new TextColumn<CloudProcess>() {
			@Override
			public String getValue(CloudProcess item) {
				String result = "";
				if(item != null){
					result += "US$" + item.getCost();
				}	
				return result;
			}
		};
		cellTable.addColumn(hoursColumn, "Cost");
		cellTable.setColumnWidth(hoursColumn, "100px");

		TextColumn<CloudProcess> activeColumn = new TextColumn<CloudProcess>() {
			@Override
			public String getValue(CloudProcess item) {
				String result = "";
				if(cloudIP!= null){
					if(cloudIP.get(item.getName()) != null){
						result = cloudIP.get(item.getName()).trim();
					}
				}
				return result;
			}
		};
		cellTable.addColumn(activeColumn, "IP");
		cellTable.setColumnWidth(activeColumn, "80px");

		// Add a selection model to handle user selection.
		final SingleSelectionModel<CloudProcess> selectionModel = new SingleSelectionModel<CloudProcess>();
		cellTable.setSelectionModel(selectionModel);
		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			public void onSelectionChange(SelectionChangeEvent event) {
				CloudProcess selected = selectionModel.getSelectedObject();
				if (selected != null) {
					if(presenter != null) {
						//presenter.onSelect(selected);
					}
				}
			}
		});	
		
		cellTable.setTableLayoutFixed(true);
		cellTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
		this.add(cellTable);

	}

	public void setDisplayList(List<CloudProcess> list) {
		if(list == null)
			list = new ArrayList<CloudProcess>();
		this.cellTable.setRowCount(list.size(), true);
		this.cellTable.setRowData(data=list);
	}

	public void setPresenter(StackDetailsActivity stackDetailsActivity) {
		this.presenter = stackDetailsActivity;

	}

	public void refresh(List<CloudProcess> newProgressList, HashMap<String, String> cloudIP) {
		this.cloudIP = cloudIP;
		setDisplayList(newProgressList);
	}


	public void refresh(int i, CloudProcess update) {
		this.cellTable.setRowData(i, data.subList(i, i+1));
	}

	public void refreshCostPerAccount(HashMap<String, String> cloudIP) {
		this.cloudIP = cloudIP;
	}	
}
