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

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;

public class IconText {
	private ImageResource image;
	private String text;
	private SafeHtml safeHTML=null;
	private SafeHtml url=null;
	
	public IconText(ImageResource image, String text) {
		this.image = image;
		this.text = text;
	}
	
	public IconText(SafeHtml image, String text) {
		this.url = image;
		this.image = null;
		this.text = text;
	}
	/**
	 * @return the image
	 */
	public ImageResource getImage() {
		return image;
	}
	/**
	 * @param image the image to set
	 */
	public void setImage(ImageResource image) {
		if(image != null) {
			if(image.equals(this.image))
				return;
		}
		this.image = image;
		this.safeHTML = null;
	}
	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}
	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		if(text != null) {
			if(text.equals(this.text)){
				return;
			}
		}
		this.text = text;
		this.safeHTML = null;
	}
	
	public SafeHtml getTextHtml() {
		if(this.safeHTML == null) {
			if(this.text != null) {
				this.safeHTML = SafeHtmlUtils.fromString(this.text);
			} else {
				this.safeHTML = SafeHtmlUtils.fromString("");
			}
		}
		return this.safeHTML;
	}
	
	public SafeHtml getImageHtml() {
		if(this.image != null && this.url == null) {
			String s = AbstractImagePrototype.create(image).getHTML().replace("<img", "<img width=100% height=100%");//.replace(image.getHeight()+"px", "100%");
			this.url = SafeHtmlUtils.fromTrustedString(s);
		}
		return this.url;
	}
	
	public String getUrl() {
		if(image == null) return null;
		return new Image(image).getUrl();
		
	}
	
	public boolean hasImageHtml() {
		return image == null & url != null;
	}

}
