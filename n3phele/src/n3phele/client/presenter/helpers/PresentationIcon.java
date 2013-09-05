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
package n3phele.client.presenter.helpers;

import n3phele.client.N3phele;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;

public class PresentationIcon {
	public static String getIconImageHtml(String url) {
		Image image = getIconImage(url);
		image.setPixelSize(32, 32);
		return image.toString();
	}
	
	@SuppressWarnings("deprecation")
	public static Image getIconImage(String url) {
		ImageResource icon = N3phele.n3pheleResource.scriptIcon();

		if(url.equals("stackIcon")) {
			icon = N3phele.n3pheleResource.stackIcon();
		}
		
		if(url==null || url.length()==0) {
			
	
		} else if(url.startsWith("http://www.n3phele.com")|| url.startsWith("https://www.n3phele.com/icons/")) {
			if(url.endsWith("qiimeIcon")) {
				icon = N3phele.n3pheleResource.qiimeIcon();
			}  else if(url.endsWith("fileCopy")) {
				icon = N3phele.n3pheleResource.fileCopyIcon();
			} else if(url.endsWith("unzip")) {
				icon = N3phele.n3pheleResource.unzipIcon();
			} else if(url.endsWith("untar")) {
				icon = N3phele.n3pheleResource.untarIcon();
			} else if(url.endsWith("espritTree")) {
				icon = N3phele.n3pheleResource.treeIcon();
			} else if(url.endsWith("concatenate")) {
				icon = N3phele.n3pheleResource.concatenateIcon();
			} else if(url.equals("stackIcon")) {				
				icon = N3phele.n3pheleResource.stackIcon();
			} else if(url.endsWith("jujuIcon")){
				icon = N3phele.n3pheleResource.jujuIcon();
			}
			
			return new Image(icon.getURL());
		} else if(!url.endsWith(".png")) {
			return new Image(icon.getURL());
		}
		return new Image(url);
	}

}
