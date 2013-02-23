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
package n3phele.client.presenter;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class CommandListPlace extends Place {

	private String placeName;

	public CommandListPlace(String token) {
		this.placeName = token;
	}

	public String getPlaceName() {
		return placeName;
	}

	@Prefix("command")
	public static class Tokenizer implements PlaceTokenizer<CommandListPlace> {

		@Override
		public String getToken(CommandListPlace place) {
			return place.getPlaceName();
		}

		@Override
		public CommandListPlace getPlace(String token) {
			return new CommandListPlace(token);
		}

	}
}