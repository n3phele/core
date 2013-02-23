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

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.UIObject;

public class StyledButtonCell<C> extends ActionCell<C> {
	private String style;
	final private String tip;
	private String text = null;
	private Element eventTarget = null;
	private Context context;
	private Element parent;
	private C value;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.gwt.cell.client.ActionCell#onBrowserEvent(com.google.gwt.cell
	 * .client.Cell.Context, com.google.gwt.dom.client.Element,
	 * java.lang.Object, com.google.gwt.dom.client.NativeEvent,
	 * com.google.gwt.cell.client.ValueUpdater)
	 */
	@Override
	public void onBrowserEvent(Context context, Element parent, C value,
			NativeEvent event, ValueUpdater<C> valueUpdater) {
		Element target = event.getEventTarget().cast();
		Element me = parent.getFirstChildElement();
		if (!me.isOrHasChild(target)) {
			return;
		}
		this.eventTarget = target;
		this.context = context;
		this.parent = parent;
		this.value = value;
		super.onBrowserEvent(context, parent, value, event, valueUpdater);
	}
	
	private static class UIObjectWrapper extends UIObject {
		public UIObjectWrapper(Element element) {
			setElement(element);
		}
	}
	
	
	public UIObject getEventTarget() {
		return new UIObjectWrapper(this.eventTarget);
	}
	
	
	public StyledButtonCell(
			com.google.gwt.cell.client.ActionCell.Delegate<C> delegate,
			String style, String tip) {
		super("", delegate);
		this.style = style;
		this.tip = tip;
	}
	
	public StyledButtonCell(
			StyledButtonDelegate<C> delegate,
			String style, String tip) {
		super("", delegate);
		this.style = style;
		this.tip = tip;
		delegate.setCell(this);
	}

	@Override
	public void render(Context context, C value, SafeHtmlBuilder sb) {
		if (value != null) {
			sb.appendHtmlConstant("<button type=\"button\" class=\""
					+ this.style + "\""+(tip!=null?"title=\""+this.tip+"\" ":"")+" tabindex=\"-1\">"+(this.text==null?"":this.text)+"</button>");
		} else {
			//sb.appendHtmlConstant("<span></span>");
			sb.appendHtmlConstant("<button type=\"button\" class=\""
					+ this.style + "\" style=\"visibility: hidden;\" tabindex=\"-1\">"+(this.text==null?"":this.text)+"</button>");

		}
	}
	
	public void setText(String text) {
		this.text = SafeHtmlUtils.htmlEscape(text);
	}

	public static abstract class StyledButtonDelegate<C> implements com.google.gwt.cell.client.ActionCell.Delegate<C> {
		private StyledButtonCell<C> cell;
		public void setCell(StyledButtonCell<C> cell) {
			this.cell = cell;
		}
		public StyledButtonCell<C> getCell() {
			return this.cell;
		}
	}


	/**
	 * @param style the new cell style
	 */
	public void setStyle(String style) {
		this.style = style;
	}


	/**
	 * 
	 */
	public void redraw() {
		this.setValue(this.context, this.parent, this.value);
		
	}
}
