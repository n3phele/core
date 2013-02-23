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


import com.google.gwt.cell.client.AbstractSafeHtmlCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class IconTextCell<T extends IconText> extends AbstractSafeHtmlCell<T>  {
	  /**
	   * Constructs an cell combining an Icon put text label with a set icon size. The
	   * supplied icons are scaled to the specified widthxheight to create a uniform presentation.
	   * 
	   */
	  public IconTextCell(int iconWidth, int iconHeight) {
	    super(new IconTextSafeHtmlRenderer<T>(iconWidth, iconHeight, 5) );
	  }
	  
	  public IconTextCell(int iconWidth, int iconHeight, int widthPadding) {
		    super(new IconTextSafeHtmlRenderer<T>(iconWidth, iconHeight, widthPadding) );
		  }


	  @Override
	  public void render(Context context, SafeHtml value, SafeHtmlBuilder sb) {
	    if (value != null) {
	      sb.append(value);
	    }
	  }
}
