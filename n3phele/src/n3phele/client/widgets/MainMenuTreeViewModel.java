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


import java.util.List;

import com.google.gwt.user.client.History;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;

public class MainMenuTreeViewModel implements TreeViewModel {

	private final SingleSelectionModel<MenuItem> selectionModel;
	private final ListDataProvider<MenuItem> dataProvider;

	public MainMenuTreeViewModel(List<MenuItem> list) {
		this.selectionModel = new SingleSelectionModel<MenuItem>();
		this.dataProvider = new ListDataProvider<MenuItem>(list);
		this.selectionModel.addSelectionChangeHandler(new Handler() {

			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				MenuItem item = MainMenuTreeViewModel.this.selectionModel.getSelectedObject();
				if(item!= null && item.getPlaceToken() != null) {
					History.newItem(item.getPlaceToken());
				}
			}});
		
	}
	

	@Override
	public <T> NodeInfo<?> getNodeInfo(T value) {
		if(value == null) {

			return new DefaultNodeInfo<MenuItem>(dataProvider, new IconTextCell<MenuItem>(20,20), selectionModel, null);
//		} else if(value instanceof TypeThatHasChildren) {
			/* Need a composite for the renderer that combines a small icon and a string */
//			return new DefaultNodeInfo<String>(dataProvider, new TextCell());
			/* If a component has children, we return a list of them here */
		} else
			return null;
		
	}

	@Override
	public boolean isLeaf(Object value) {
		if (value==null) return false;
		if (value instanceof MenuItem) {
			return false;
		}
		return false;
	}

	public SingleSelectionModel<MenuItem> getSelectionModel() {
		return this.selectionModel;
	}
}
