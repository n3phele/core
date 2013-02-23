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

import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

public class ActionDialogBox<C> extends DialogBox {
	private String message;
	final private String noText;
	final private String yesText;
	final private Delegate<C> onYesClick;
	final private HTML details;
	private C value = null;
	public ActionDialogBox(String title, String noText, String yesText, Delegate<C> onYesClick) {
		FlexTable dialogContents = new FlexTable();
		dialogContents.setCellPadding(15);
		this.setTitle(title);
		this.setText(title);
		this.noText = noText;
		this.yesText = yesText;
		this.onYesClick = onYesClick;
			    
		this.setWidget(dialogContents);
	
	    // Add some text to the top of the dialog
	    details = new HTML(this.message);
	    dialogContents.setWidget(0,0,details);
	    dialogContents.getFlexCellFormatter().setColSpan(0,0,2);
	    Button closeButton = new Button(
	            this.noText, new ClickHandler() {
	              public void onClick(ClickEvent event) {
	                hide();
	              }
	            });
	    dialogContents.setWidget(1,1,closeButton);
	    Button doitButton = new Button(
	            this.yesText, new ClickHandler() {
	              public void onClick(ClickEvent event) {
	            	ActionDialogBox.this.onYesClick.execute(value);
	                hide();
	              }
	            });
	    dialogContents.setWidget(1,0,doitButton);
	    dialogContents.getFlexCellFormatter().setHorizontalAlignment(1, 0, HasHorizontalAlignment.ALIGN_RIGHT);
	    dialogContents.getFlexCellFormatter().setHorizontalAlignment(1, 1, HasHorizontalAlignment.ALIGN_LEFT);

	}
	
	public void setValue(String prompt, C value) {
		this.message = prompt;
		this.details.setHTML(this.message);
		this.value = value;
	}
}
