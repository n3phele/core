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

import n3phele.client.N3phele;
import n3phele.client.model.Repository;
import n3phele.client.presenter.RepoListActivity;
import n3phele.client.presenter.helpers.AuthenticatedRequestFactory;
import n3phele.client.resource.ClickableCellTableResource;
import n3phele.client.widgets.ActionDialogBox;
import n3phele.client.widgets.CancelButtonCell;
import n3phele.client.widgets.MenuItem;
import n3phele.client.widgets.WorkspaceVerticalPanel;

import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;

public class RepoListView extends WorkspaceVerticalPanel {
	private CellTable<Repository> cellTable;
	private List<Repository> data = null;
	private RepoListActivity repositoryListActivity = null;
	private ActionDialogBox<Repository> dialog=null;
	private static ClickableCellTableResource resource = null;
	public RepoListView() {
		super(new MenuItem(N3phele.n3pheleResource.repositoryIcon(), "File Repositories", null),
				new MenuItem(N3phele.n3pheleResource.repositoryAddIcon(), "create a new repository", "repository:"));

		if(resource ==null)
			resource = GWT.create(ClickableCellTableResource.class);
		
		cellTable = new CellTable<Repository>(15, resource);

		cellTable.setSize("455px", "");
		//this.scrollPanel.add(this.cellTable);
		TextColumn<Repository> nameColumn = new TextColumn<Repository>() {
			@Override
			public String getValue(Repository item) {
				String result = "";
				if(item != null)
					return item.getName();
				return result;
			}
		};
		cellTable.addColumn(nameColumn, "Name");
		
		TextColumn<Repository> descriptionColumn = new TextColumn<Repository>() {
			@Override
			public String getValue(Repository item) {
				String result = "";
				if(item != null)
					return item.getDescription();
				return result;
			}
		};
		cellTable.addColumn(descriptionColumn, "Description");
		
		
		 // Add a selection model to handle user selection.
	    final SingleSelectionModel<Repository> selectionModel = new SingleSelectionModel<Repository>();
	    cellTable.setSelectionModel(selectionModel);
	    selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
	      public void onSelectionChange(SelectionChangeEvent event) {
	        Repository selected = selectionModel.getSelectedObject();
	        if (selected != null) {
	          if(repositoryListActivity != null) {
	        	 repositoryListActivity.onSelect(selected);
	          }
	        }
	      }
	    });
	    cellTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
		this.add(cellTable);
		
		Column<Repository, Repository> cancelColumn = new Column<Repository, Repository>(
				new CancelButtonCell<Repository>(new Delegate<Repository>() {

					@Override
					public void execute(Repository value) {
						if(value != null) {
							cellTable.getSelectionModel().setSelected(value, false);
							getDialog(value).show();
						}
					}},"remove repository")) {
			@Override
			public Repository getValue(Repository object) {
				return object;
			}
		};
		cellTable.addColumn(cancelColumn);
		cellTable.setColumnWidth(cancelColumn, "26px");
	}
	
	final private static List<Repository> nullList = new ArrayList<Repository>(0);
	public void setDisplayList(List<Repository> list) {
		if(list == null) {
			list = nullList;
		}
		this.data = list;
		this.cellTable.setRowData(this.data);
	}

	public void setPresenter(RepoListActivity repositoryListActivity) {
		this.repositoryListActivity = repositoryListActivity;
		
	}

	public void refresh(List<Repository> newProgressList) {
		setDisplayList(newProgressList);
	}


	public void refresh(int i, Repository update) {
		this.cellTable.setRowData(i, data.subList(i, i+1));
	}
	protected ActionDialogBox<Repository> getDialog(Repository item) {
		if(dialog == null) {
			dialog = new ActionDialogBox<Repository>("Remove Repository Confirmation",
					"No", "Yes", new Delegate<Repository>(){

						@Override
						public void execute(Repository object) {
							kill(object.getUri());
							
						}});
			 dialog.setGlassEnabled(true);
			 dialog.setAnimationEnabled(true);

		}
		dialog.setValue("Remove repository \""+item.getName()+"\"?<p>" +
				"The contents of the repository store will not be affected.", item);
		dialog.center();
		return dialog;
	}
	
	private void kill(String uri) {
		 String url = uri;
		 // Send request to server and catch any errors.
		    RequestBuilder builder = AuthenticatedRequestFactory.newRequest(RequestBuilder.DELETE, url);

		    try {
		      builder.sendRequest(null, new RequestCallback() {
		        public void onError(Request request, Throwable exception) {
		        	Window.alert("Couldn't delete "+exception.getMessage());
		        }

		        public void onResponseReceived(Request request, Response response) {
		          if (204 == response.getStatusCode()) {
		        	  if(RepoListView.this.repositoryListActivity != null)
							RepoListView.this.repositoryListActivity.getRepositoryList();
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
