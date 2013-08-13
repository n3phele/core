/**
 * @author Nigel Cook
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
import java.util.List;

import n3phele.client.N3phele;
import n3phele.client.model.CloudProcessSummary;
import n3phele.client.model.Narrative;
import n3phele.client.presenter.AbstractCloudProcessActivity;
import n3phele.client.presenter.AbstractServiceActivity;
import n3phele.client.presenter.helpers.AuthenticatedRequestFactory;
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
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
public class ServiceListView extends WorkspaceVerticalPanel {
	private static final int PAGESIZE = 15;
	private ActivityStatusList cellTable;
	private List<CloudProcessSummary> data = null;
	private AbstractServiceActivity presenter;
	
	private ActionDialogBox<CloudProcessSummary> dialog = null;
	public ServiceListView() {
		super(new MenuItem(N3phele.n3pheleResource.activityIcon(), "Service List", null));
		
		HorizontalPanel heading = new HorizontalPanel();
		heading.setWidth("500px");
		heading.setStyleName(N3phele.n3pheleResource.css().sectionPanelHeader());
		add(heading);
		
		heading.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		
		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
	    SimplePager simplePager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		heading.add(simplePager);
		heading.setCellHorizontalAlignment(simplePager, HorizontalPanel.ALIGN_CENTER);

		this.cellTable = new ActivityStatusList();
		this.cellTable.setWidth("100%");
		TextColumn<CloudProcessSummary> narrative = new TextColumn<CloudProcessSummary>(){

			@Override
			public String getValue(CloudProcessSummary process) {
				String result = "";
				List<Narrative> narrative = process.getNarrative();
				if(narrative != null && narrative.size() > 0) {
					result = narrative.get(narrative.size()-1).getText();
				}

				return result;
			}};
			this.cellTable.addColumn(narrative);
			this.cellTable.setColumnWidth(narrative, "55%");
			Column<CloudProcessSummary, CloudProcessSummary> cancelColumn = new Column<CloudProcessSummary, CloudProcessSummary>(
					 new CancelButtonCell<CloudProcessSummary>(new Delegate<CloudProcessSummary>() {

						@Override
						public void execute(CloudProcessSummary value) {
							if(value != null) {
								cellTable.getSelectionModel().setSelected(value, false);
								getDialog(value).show();
							}
						}}, "cancel activity")) {
				@Override
				public CloudProcessSummary getValue(CloudProcessSummary object) {
					String status = object.getState();
					if(status == null || status.equalsIgnoreCase("COMPLETE") || status.equalsIgnoreCase("FAILED") ||
							status.equalsIgnoreCase("CANCELLED")) {
							return null;	
					}
					return object;
				}
			};
			cellTable.addColumn(cancelColumn);
			cellTable.setColumnWidth(cancelColumn, "26px");
		//cellTable.setSize("455px", "");
		this.add(cellTable);
		cellTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
		final SingleSelectionModel<CloudProcessSummary> selectionModel = new SingleSelectionModel<CloudProcessSummary>();
	    cellTable.setSelectionModel(selectionModel);
	    selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
	      public void onSelectionChange(SelectionChangeEvent event) {
	        CloudProcessSummary selected = selectionModel.getSelectedObject();
	        if (selected != null) {
	          if(presenter != null) {
	        	 presenter.onSelect(selected);
	        	// selectionModel.setSelected(selected, false);
	          }
	         
	        }
	      }
	    });
	    
	    /*
	     * Add Table paging
	     */
	    simplePager.setDisplay(cellTable);
		simplePager.setPageSize(PAGESIZE);
		cellTable.addRangeChangeHandler(new RangeChangeEvent.Handler(){

			/* (non-Javadoc)
			 * @see com.google.gwt.view.client.RangeChangeEvent.Handler#onRangeChange(com.google.gwt.view.client.RangeChangeEvent)
			 */
			@Override
			public void onRangeChange(RangeChangeEvent event) {
				Range range = cellTable.getVisibleRange();
				int start = range.getStart();

//				if(data == null || (data.size() < start) ){
					GWT.log("Fetch "+start);
					presenter.refresh(start);
//				} else {
//					if(length+start > data.size())
//						length = data.size()-start;
//					GWT.log("data available start="+start);
//					grid.setRowData(start, chunk(data.subList(start, start+length)));
//				}
			}
	    	
	    });
		this.add(cellTable);	

	    
	    
	    
	}
	
	/* (non-Javadoc)
	 * @see n3phele.client.view.CloudProcessView#setDisplayList(java.util.List)
	 */
	
	public void setDisplayList(List<CloudProcessSummary> processList, int start, int max) {
		if(processList == null)
			processList = new ArrayList<CloudProcessSummary>();
		this.cellTable.setRowCount(max, true);
		this.cellTable.setRowData(start, data=processList);
		//N3phele.checkSize();
	}



	/* (non-Javadoc)
	 * @see n3phele.client.view.CloudProcessView#refresh(int, n3phele.client.model.CloudProcessSummary)
	 */
	
	public void refresh(int i, CloudProcessSummary update) {
		this.cellTable.setRowData(i, data.subList(i, i+1));
	}
	
	protected ActionDialogBox<CloudProcessSummary> getDialog(CloudProcessSummary item) {
		if(dialog == null) {
			dialog = new ActionDialogBox<CloudProcessSummary>("Activity Terminate Confirmation",
					"No", "Yes", new Delegate<CloudProcessSummary>(){

						@Override
						public void execute(CloudProcessSummary object) {
							kill(object.getUri());
							
						}});
			 dialog.setGlassEnabled(true);
			 dialog.setAnimationEnabled(true);

		}
		dialog.setValue("Terminate processing of running activity \""+item.getName()+"\"?", item);
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
		          } else {
		        	  Window.alert("Couldn't delete (" + response.getStatusText() + ")");
		          }
		        }
		      });
		    } catch (RequestException e) {
		    	Window.alert("Couldn't delete "+e.getMessage());
		    
		    }
	}

	
	public int getPageSize() {
		return PAGESIZE;
	}

	public AbstractServiceActivity getPresenter() {
		return this.presenter;
	}

	public void setPresenter(AbstractServiceActivity presenter) {
		this.presenter = presenter;
	}

}
