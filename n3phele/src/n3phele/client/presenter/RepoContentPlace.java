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

public class RepoContentPlace extends Place {

	private String placeName;
	private String prefix;

	public RepoContentPlace(String token) {
		this.placeName = "";
		this.prefix = "";
		if(!isNullOrBlank(token)) {
			String[] bits = token.split("::");
			if(bits != null && bits.length > 0) {
				if(bits.length > 0) {
					this.placeName = isNullOrBlank(bits[0])?"":bits[0];
				}
				if(bits.length > 1) {
					this.prefix = isNullOrBlank(bits[1])?"":bits[1];
				}
			}
		}
	}
	
	public RepoContentPlace(String uri, String prefix) {
		this.placeName = isNullOrBlank(uri)?"":uri;
		this.prefix = isNullOrBlank(prefix)?"":prefix;
	}

	public String getPlaceName() {
		return placeName;
	}
	
	public String getPrefix() {
		return this.prefix;
	}
	
	

	@Prefix("repositoryContent")
	public static class Tokenizer implements PlaceTokenizer<RepoContentPlace> {

		@Override
		public String getToken(RepoContentPlace place) {
			return place.getPlaceName()+"::"+place.getPrefix();
		}

		@Override
		public RepoContentPlace getPlace(String token) {
			return new RepoContentPlace(token);
		}

	}
	private boolean isNullOrBlank(String s) {
		return s==null || s.isEmpty() || s.equals("null");
	}
	
}