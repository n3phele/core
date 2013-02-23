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
import java.util.HashMap;
import java.util.List;


import n3phele.client.N3phele;
import n3phele.client.model.Account;

import n3phele.client.presenter.AccountListActivity;
import n3phele.client.presenter.helpers.AuthenticatedRequestFactory;
import n3phele.client.resource.ClickableCellTableResource;
import n3phele.client.widgets.ActionDialogBox;
import n3phele.client.widgets.CancelButtonCell;
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
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Window;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

public class AccountListView extends WorkspaceVerticalPanel {
	private CellTable<Account> cellTable;
	private List<Account> data = null;
	private AccountListActivity presenter = null;
	private ActionDialogBox<Account> dialog;
	private static ClickableCellTableResource resource = null;
	private double cost = 0.0;
	private HashMap<Account, Double> costPerAccount = null;
	private HashMap<Account, Integer> vsPerAccount = null;
	public AccountListView() {
		super(new MenuItem(N3phele.n3pheleResource.accountIcon(), "Accounts", null),
				new MenuItem(N3phele.n3pheleResource.accountAddIcon(), "create a new account", "account:null"));

		if(resource ==null)
			resource = GWT.create(ClickableCellTableResource.class);

		cellTable = new CellTable<Account>(15, resource);
		cellTable.setSize("455px", "");

		TextColumn<Account> nameColumn = new TextColumn<Account>() {
			@Override
			public String getValue(Account item) {
				String result = "";
				if(item != null){
					result += item.getName();
				}
				return result;
			}
		};
		cellTable.addColumn(nameColumn, "Name");
		cellTable.setColumnWidth(nameColumn, "120px");

		TextColumn<Account> hoursColumn = new TextColumn<Account>() {
			@Override
			public String getValue(Account item) {
				String result = "";
				if(item != null){
					if(costPerAccount != null && costPerAccount.containsKey(item))
						result += "US$" + (double)Math.round(costPerAccount.get(item) * 1000) / 1000;
					else
						result += "US$" + 0.0;
				}
				return result;
			}
		};
		cellTable.addColumn(hoursColumn, "Last 24 hours");
		cellTable.setColumnWidth(hoursColumn, "100px");

		TextColumn<Account> activeColumn = new TextColumn<Account>() {
			@Override
			public String getValue(Account item) {
				String result = "";
				if(item != null){
					if(vsPerAccount != null && vsPerAccount.containsKey(item)){
						int value = vsPerAccount.get(item);
						result += value;
					}
					else
						result += 0;
				}
				return result;
			}
		};
		cellTable.addColumn(activeColumn, "Active");
		cellTable.setColumnWidth(activeColumn, "80px");


		TextColumn<Account> cloudColumn = new TextColumn<Account>() {
			@Override
			public String getValue(Account item) {
				String result = "";
				if(item != null) {
					result = item.getCloudName();
				}
				return result;
			}
		};
		cellTable.addColumn(cloudColumn, "Cloud");
		cellTable.setColumnWidth(cloudColumn, "120px");


		// Add a selection model to handle user selection.
		final SingleSelectionModel<Account> selectionModel = new SingleSelectionModel<Account>();
		cellTable.setSelectionModel(selectionModel);
		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			public void onSelectionChange(SelectionChangeEvent event) {
				Account selected = selectionModel.getSelectedObject();
				if (selected != null) {
					if(presenter != null) {
						presenter.onSelect(selected);
					}
				}
			}
		});

		Column<Account, Account> cancelColumn = new Column<Account, Account>(
				new CancelButtonCell<Account>(new Delegate<Account>() {

					@Override
					public void execute(Account value) {
						if(value != null) {
							cellTable.getSelectionModel().setSelected(value, false);
							getDialog(value).show();
						}
					}}, "delete account")) {
			@Override
			public Account getValue(Account object) {
				return object;
			}
		};
		cellTable.addColumn(cancelColumn);
		cellTable.setColumnWidth(cancelColumn, "20px");
		
		cellTable.setTableLayoutFixed(true);
		cellTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
		this.add(cellTable);

	}

	public void setDisplayList(List<Account> list) {
		if(list == null)
			list = new ArrayList<Account>();
		this.cellTable.setRowCount(list.size(), true);
		this.cellTable.setRowData(data=list);
	}

	public void setPresenter(AccountListActivity accountListActivity) {
		this.presenter = accountListActivity;

	}

	public void refresh(List<Account> newProgressList, HashMap<Account, Double> costPerAccount, HashMap<Account, Integer> vsPerAccount) {
		this.costPerAccount = costPerAccount;
		this.vsPerAccount = vsPerAccount;
		setDisplayList(newProgressList);
	}


	public void refresh(int i, Account update) {
		this.cellTable.setRowData(i, data.subList(i, i+1));
	}

	public void refreshCostPerAccount(HashMap<Account, Double> cost) {
		this.costPerAccount = cost;
	}

	public void set24HoursCost(double cost){
		this.cost = cost;
	}

	protected ActionDialogBox<Account> getDialog(Account item) {
		if(dialog == null) {
			dialog = new ActionDialogBox<Account>("Account Removal Confirmation",
					"No", "Yes", new Delegate<Account>(){

				@Override
				public void execute(Account object) {
					kill(object.getUri());

				}});
			dialog.setGlassEnabled(true);
			dialog.setAnimationEnabled(true);

		}
		dialog.setValue("Remove cloud account \""+item.getName()+"\" from n3phele?<p>" +
				"The account setup on the cloud will not be affected.", item);
		dialog.center();
		return dialog;
	}

	private void kill(String uri) {
		String url = uri;
		// Send request to server and catch any errors.
		RequestBuilder builder = AuthenticatedRequestFactory.newRequest(RequestBuilder.DELETE, url);

		try {
			@SuppressWarnings("unused")
			Request request = builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					Window.alert("Couldn't delete "+exception.getMessage());
				}

				public void onResponseReceived(Request request, Response response) {
					if (204 == response.getStatusCode()) {
						if(AccountListView.this.presenter != null)
							AccountListView.this.presenter.getAccountList();
					} else {
						Window.alert("Couldn't delete (" + response.getStatusText() + ")");
					}
				}
			});
		} catch (RequestException e) {
			Window.alert("Couldn't delete "+e.getMessage());

		}

	}

}
