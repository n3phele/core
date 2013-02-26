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

import n3phele.client.model.CloudProcessSummary;
import n3phele.client.presenter.AbstractCloudProcessActivity;

import com.google.gwt.user.client.ui.IsWidget;

public interface CloudProcessView extends IsWidget {

	public abstract void setDisplayList(List<CloudProcessSummary> processList, int start, int max);

	public abstract void setPresenter(AbstractCloudProcessActivity presenter);

	public abstract void refresh(int i, CloudProcessSummary update);
	
	public abstract int getPageSize();

}