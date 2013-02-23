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

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import n3phele.client.model.Command;
import n3phele.client.presenter.CommandListActivity;

public interface CommandListViewInterface extends IsWidget {

	public void setDisplayList(List<Command> commandList, int start, int max);

	public void setPresenter(CommandListActivity presenter);
	
	//public void setDisplayList(List<Command> commandList);

	//public void refresh(List<Command> newProgressList);

	//public void refresh(int i, Command update);
	
	public int getPageSize();

}