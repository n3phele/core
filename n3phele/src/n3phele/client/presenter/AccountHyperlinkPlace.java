/**
 * @author Nigel Cook
 *
 * (C) Copyright 2010-2011. All rights reserved.
 */
package n3phele.client.presenter;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class AccountHyperlinkPlace extends Place {

	private String placeName;

	public AccountHyperlinkPlace(String token) {
		this.placeName = token;
	}

	public String getPlaceName() {
		return placeName;
	}

	@Prefix("accountInfo")
	public static class Tokenizer implements PlaceTokenizer<AccountHyperlinkPlace> {

		@Override
		public String getToken(AccountHyperlinkPlace place) {
			return place.getPlaceName();
		}

		@Override
		public AccountHyperlinkPlace getPlace(String token) {
			return new AccountHyperlinkPlace(token);
		}

	}
}