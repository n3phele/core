/**
 * @author Nigel Cook
 * @author Douglas Tondin
 *
 * (C) Copyright 2010-2013. Nigel Cook. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * 
 * Licensed under the terms described in LICENSE file that accompanied this code, (the "License"); you may not use this file
 * except in compliance with the License. 
 * 
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on 
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the 
 *  specific language governing permissions and limitations under the License.
 */
package n3phele.client.presenter;

import n3phele.client.model.Stack;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class StackDetailsPlace extends Place { 
	private String placeName;

	public StackDetailsPlace(String token) {
	
		this.placeName = token;
	}

	public String getPlaceName() {
		return placeName;
	}

	@Prefix("stackDetails")
	public static class Tokenizer implements PlaceTokenizer<StackDetailsPlace> {

		@Override
		public String getToken(StackDetailsPlace place) { 
			return place.getPlaceName();
		}

		@Override
		public StackDetailsPlace getPlace(String token) {
			return new StackDetailsPlace(token);
		}
	
	}
	
}