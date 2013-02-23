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
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class ValidInputIndicatorCell extends AbstractCell<String> {
	  
	private final String iconImage;
	public ValidInputIndicatorCell(ImageResource icon) {

		this.iconImage = AbstractImagePrototype.create(icon).getHTML();
	}

	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context,
			String value, SafeHtmlBuilder sb) {
		if(value != null) {
			boolean visible = value.startsWith("+");
			value = value.substring(1);
			String html = iconImage.replace("<img ", "<img title=\""+value+"\" ");
			if(!visible) {
				html = html.replace("style='", "style='margin-bottom:-5px; display:none; ");
			} else {
				html = html.replace("style='", "style='margin-bottom:-5px; ");
			}
			sb.append(SafeHtmlUtils.fromTrustedString(html));
		} else {
			sb.append(SafeHtmlUtils.fromTrustedString(iconImage));
		}
	}
}
