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

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

/**
 * A {@link Cell} used to render a checkbox. The value of the checkbox may be
 * toggled using the ENTER key as well as via mouse click.
 */
public class SensitiveCheckBoxCell extends CheckboxCell {

  private static final SafeHtml INPUT_DISABLED = SafeHtmlUtils.fromSafeConstant("<input type=\"checkbox\" disabled=disabled tabindex=\"-1\"/>");

  

public SensitiveCheckBoxCell() {
	super();
}



/**
 * @param dependsOnSelection
 * @param handlesSelection
 */
public SensitiveCheckBoxCell(boolean dependsOnSelection,
		boolean handlesSelection) {
	super(dependsOnSelection, handlesSelection);
}



@Override
  public void render(Context context, Boolean value, SafeHtmlBuilder sb) {
    // Get the view data.
    Object key = context.getKey();
    Boolean viewData = getViewData(key);
    if (viewData != null && viewData.equals(value)) {
      clearViewData(key);
      viewData = null;
    }

    if (value == null) {
    	sb.append(INPUT_DISABLED);
    } else {
    	super.render(context, value, sb);
    }
  }
}
