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

import com.google.gwt.place.shared.Place;
import com.google.gwt.resources.client.ImageResource;

public class MenuItem extends IconText  {

	private String place;
	private Class<? extends Place> clazz;
	public MenuItem(ImageResource icon, String text, String placeToken, Class<? extends Place> place) {
		super(icon, text);
		this.place = placeToken;
		this.clazz = place;
	}
	
	public MenuItem(ImageResource icon, String text, String placeToken) {
		this(icon, text, placeToken, Place.class);
	}
	/**
	 * @return the place
	 */
	public String getPlaceToken() {
		return this.place;
	}

	/**
	 * @param place the place to set
	 */
	public void setPlaceToken(String place) {
		this.place = place;
	}
	/**
	 * @return the clazz
	 */
	public Class<? extends Place> getPlaceClass() {
		return this.clazz;
	}
	
	

}
