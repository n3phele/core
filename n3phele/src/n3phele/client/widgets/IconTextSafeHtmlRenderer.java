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


import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.text.shared.SafeHtmlRenderer;

public class IconTextSafeHtmlRenderer<T extends IconText> implements SafeHtmlRenderer<T> {
	  interface Template extends SafeHtmlTemplates {	    
		    @Template("<div style=\"height:{1}px;width:100%;position:relative;padding-left:{2}px;zoom:1;\">" +
		    		"<div style=\"position:absolute;left:0px;top:50%;height:{1}px;width:{0}px;margin-top:-{5}px;\" >" +
		    		"{3}" +
		    		"</div>" +
		    		"<div style=\"line-height:{1}px; overflow: hidden; white-space:nowrap;\">{4}</div></div>")
		    SafeHtml iconText(int width, int height, int textIndent, SafeHtml Image, SafeHtml text, int halfHeight);
		}
	  
	  private static Template template;
	  private int iconWidth;
	  private int iconHeight;
	  private int padding;
	 
	 public IconTextSafeHtmlRenderer(int width, int height, int padding) {
		 this.iconWidth = width;
		 this.iconHeight = height;
		 this.padding = padding;
		 if (template == null) {
	        template = GWT.create(Template.class);
	     } 
	 }
	@Override
	public SafeHtml render(IconText object) {
		//object.getImageHtml();
		if(object == null)
			return SafeHtmlUtils.fromTrustedString("<div></div>");
		else {
			if(object.hasImageHtml()) {
				return template.iconText(this.iconWidth, this.iconHeight, this.iconWidth+padding, 
						object.getImageHtml(), object.getTextHtml(), 1 + (int) Math.round(iconHeight / 2.0));
			} else {
				String s = "<img src=\""+object.getUrl()+"\" style=\"width:100%;height:100%;no-repeat 0px 0px\" border=\"0\"/>";
				return template.iconText(this.iconWidth, this.iconHeight, this.iconWidth+this.padding, 
						SafeHtmlUtils.fromTrustedString(s), object.getTextHtml(), 1 + (int) Math.round(iconHeight / 2.0));
			}
		}
	}

	@Override
	public void render(IconText object, SafeHtmlBuilder builder) {
		builder.append(render(object));
		
	}
	  
}