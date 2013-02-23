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

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;

import n3phele.client.N3phele;
import n3phele.client.model.Command;
import n3phele.client.presenter.CommandListActivity;
import n3phele.client.presenter.CommandPlace;
import n3phele.client.widgets.HyperlinkCell;
import n3phele.client.widgets.MenuItem;
import n3phele.client.widgets.WorkspaceVerticalPanel;

import com.google.gwt.user.client.ui.Hyperlink;

public class CommandListView extends WorkspaceVerticalPanel implements CommandListViewInterface {
	private CellTable<Command> cellTable;
	final static List<Command> nullList = new ArrayList<Command>();
	private List<Command> data = nullList;
	private CommandListActivity commandActivity = null;
	public CommandListView() {
		super(new MenuItem(N3phele.n3pheleResource.commandIcon(), "Commands", null));
		cellTable = new CellTable<Command>();
		cellTable.setSize("455px", "");

//		TextColumn<Command> nameColumn = new TextColumn<Command>() {
//			@Override
//			public String getValue(Command command) {
//				String result = "";
//				if(command != null)
//					return command.getName();
//				return result;
//			}
//		};
//		cellTable.addColumn(nameColumn, "Name");

		Column<Command,Hyperlink> nameColumn = new Column<Command,Hyperlink>(new HyperlinkCell()) {

			@Override
			public Hyperlink getValue(Command command) {
				if(command == null)
					return null;
				String name = command.getName();
				String historyToken = commandActivity.getToken(command);
				return new Hyperlink(name, historyToken);
			}
		
		};

		nameColumn.setFieldUpdater(new FieldUpdater<Command, Hyperlink>() {
			@Override
			public void update(int index, Command object, Hyperlink value) {
				CommandListView.this.commandActivity.goTo(new CommandPlace(object.getUri()));
			}
		});
		cellTable.addColumn(nameColumn, "Name");
		
		TextColumn<Command> descriptionColumn = new TextColumn<Command>() {
			@Override
			public String getValue(Command command) {
				String result = "";
				if(command != null)
					return command.getDescription();
				return result;
			}
		};
		cellTable.addColumn(descriptionColumn, "Description");
//		 // Add a selection model to handle user selection.
//	    final SingleSelectionModel<Command> selectionModel = new SingleSelectionModel<Command>();
//	    cellTable.setSelectionModel(selectionModel);
//	    selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
//	      public void onSelectionChange(SelectionChangeEvent event) {
//	        Command selected = selectionModel.getSelectedObject();
//	        if (selected != null) {
//	          if(commandActivity != null) {
//	        	 commandActivity.onSelect(selected);
//	          }
//	        }
//	      }
//	    });

	    cellTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
		this.add(cellTable);
		
	}
	

	/* (non-Javadoc)
	 * @see n3phele.client.view.CommandListViewI#setDisplayList(java.util.List)
	 */
	public void setDisplayList(List<Command> commandList) {
		if(commandList == null)
			commandList = nullList;
		this.cellTable.setRowCount(commandList.size(), true);
		this.cellTable.setRowData(data=commandList);
		N3phele.checkSize();
	}


	/* (non-Javadoc)
	 * @see n3phele.client.view.CommandListViewI#setPresenter(n3phele.client.presenter.CommandListActivity)
	 */
	public void setPresenter(CommandListActivity presenter) {
		this.commandActivity = presenter;
		
	}

	/* (non-Javadoc)
	 * @see n3phele.client.view.CommandListViewI#refresh(java.util.List)
	 */
	public void refresh(List<Command> newProgressList) {
		setDisplayList(newProgressList);
	}


	/* (non-Javadoc)
	 * @see n3phele.client.view.CommandListViewI#refresh(int, n3phele.client.model.Command)
	 */
	public void refresh(int i, Command update) {
		this.cellTable.setRowData(i, data.subList(i, i+1));
	}


	@Override
	public void setDisplayList(List<Command> commandList, int start, int max) {
		setDisplayList(commandList);
	}


	@Override
	public int getPageSize() {
		return 100;
	}
	
}
