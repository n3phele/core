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
package n3phele.client;

import n3phele.client.presenter.AccountHyperlinkPlace;
import n3phele.client.presenter.AccountListPlace;
import n3phele.client.presenter.AccountPlace;
import n3phele.client.presenter.ActivityDashboardPlace;
import n3phele.client.presenter.ActivityListPlace;
import n3phele.client.presenter.ActivityPlace;
import n3phele.client.presenter.CommandGridListPlace;
import n3phele.client.presenter.CommandPlace;
import n3phele.client.presenter.CommandListPlace;
import n3phele.client.presenter.LoginPlace;
import n3phele.client.presenter.ProgressPlace;
import n3phele.client.presenter.RepoContentPlace;
import n3phele.client.presenter.RepoListPlace;
import n3phele.client.presenter.RepoPlace;
import n3phele.client.presenter.UserPlace;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

@WithTokenizers({ActivityListPlace.Tokenizer.class, LoginPlace.Tokenizer.class, 
	ActivityDashboardPlace.Tokenizer.class, CommandListPlace.Tokenizer.class, CommandGridListPlace.Tokenizer.class,
	CommandPlace.Tokenizer.class,
	UserPlace.Tokenizer.class, AccountPlace.Tokenizer.class, AccountListPlace.Tokenizer.class, AccountHyperlinkPlace.Tokenizer.class,
	RepoListPlace.Tokenizer.class, RepoPlace.Tokenizer.class, RepoContentPlace.Tokenizer.class, 
	ProgressPlace.Tokenizer.class, 
	ActivityPlace.Tokenizer.class})
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper {

}
