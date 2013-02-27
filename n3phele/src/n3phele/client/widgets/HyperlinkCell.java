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

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Hyperlink;

public class HyperlinkCell extends AbstractCell<Hyperlink> { 
	public HyperlinkCell() {
		super("click");
	}
	/* (non-Javadoc)
	 * @see com.google.gwt.cell.client.AbstractCell#onBrowserEvent(com.google.gwt.cell.client.Cell.Context, com.google.gwt.dom.client.Element, java.lang.Object, com.google.gwt.dom.client.NativeEvent, com.google.gwt.cell.client.ValueUpdater)
	 */
	@Override
	public void onBrowserEvent(Context context, Element parent,
			Hyperlink value, NativeEvent event,
			ValueUpdater<Hyperlink> valueUpdater) {
		super.onBrowserEvent(context, parent, value, event, valueUpdater);
		if(valueUpdater != null) {
			if("click".equals(event.getType())) {
				valueUpdater.update(value);
			}
		}
	}

	@Override 
	public void render(com.google.gwt.cell.client.Cell.Context context, 
			Hyperlink h, SafeHtmlBuilder sb) { 
		h.setWidth("100%"); 
		sb.append(SafeHtmlUtils.fromTrustedString(h.toString())); 

	} 
} 



