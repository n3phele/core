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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import n3phele.client.AppPlaceHistoryMapper;
import n3phele.client.CacheManager;
import n3phele.client.ClientFactory;
import n3phele.client.model.Collection;
import n3phele.client.model.Command;
import n3phele.client.model.Context;
import n3phele.client.presenter.helpers.AuthenticatedRequestFactory;
import n3phele.client.view.CommandListGridView;
import n3phele.client.view.CommandListViewInterface;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class CommandListActivity extends AbstractActivity {

	protected final String name;
	protected final CacheManager cacheManager;
	protected EventBus eventBus;
	protected final PlaceController placeController;
	protected final CommandListViewInterface display;
	private List<Command> commandList = null;
	private HandlerRegistration handlerRegistration;
	private Set<String> interests = new HashSet<String>();
	private final String collectionUrl;
	private HandlerRegistration itemUpdateHandlerRegistration;
	private AppPlaceHistoryMapper historyMapper;
	private final int PAGESIZE;
	private int lastStart;
	private boolean lastPreferred;
	private String lastSearch;
	protected boolean knowTags = false;
	private HashSet<String> tags = new HashSet<String>();

	public CommandListActivity(String name, ClientFactory factory, String commandCollectionArgs, CommandListViewInterface view) {
		super();
		this.name = name;
		this.cacheManager = factory.getCacheManager();
		this.placeController = factory.getPlaceController();
		this.display = view;
		this.collectionUrl = URL.encode(cacheManager.ServiceAddress + "command"+commandCollectionArgs);
		this.historyMapper = factory.getHistoryMapper();
		this.PAGESIZE = view.getPageSize();
	}
	
	public CommandListActivity(String name, ClientFactory factory) {
		this(name, factory, "?summary=true", 
				name=="command"?factory.getCommandListView():factory.getCommandListGridView());
	}
	
	
	public interface CommandListUpdateEventHandler extends EventHandler {
		void onMessageReceived(CommandListUpdate commandListUpdate);
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		this.display.setPresenter(this);
		this.eventBus = eventBus;
		handlerRegistration(eventBus);
		//display.setDisplayList(commandList);
		panel.setWidget(display);

		initData();
	}
	
	public void handlerRegistration(EventBus eventBus) {
		this.handlerRegistration = this.eventBus.addHandler(CommandListUpdate.TYPE, new CommandListUpdateEventHandler() {
			@Override
			public void onMessageReceived(CommandListUpdate event) {
				fetch(lastStart, lastSearch, lastPreferred,null);
			}
		});
		
	}


	@Override
	public String mayStop() {
	    return null;
	}

	@Override
	public void onCancel() {
		//this.display.setPresenter(null);
		unregister();
	}

	@Override
	public void onStop() {
		//this.display.setPresenter(null);
		unregister();
	}
	
	public void onSelect(Command command) {
		//goTo(new CommandPlace(command.getUri()));
		History.newItem(historyMapper.getToken(new CommandPlace(command.getUri())));
	}
	
	public String getToken(Command command) {
		return historyMapper.getToken(new CommandPlace(command.getUri()));
	}

	public void goTo(Place place) {
		this.placeController.goTo(place);
	}

	protected void initData() {
		this.fetch(lastStart=0, lastSearch=null, lastPreferred=!knowTags,null);
		CacheManager.EventConstructor change = new CacheManager.EventConstructor() {
			@Override
			public CommandListUpdate newInstance(String key) {
				return new CommandListUpdate();
			}
		};
		cacheManager.register(cacheManager.ServiceAddress + "command", this.name, change);		
	}

	protected void unregister() {
		GWT.log("unregister");
		cacheManager.unregister(cacheManager.ServiceAddress + "command", this.name);
		for(String s : interests) {
			cacheManager.unregister(s, this.name);
		}
		//this.handlerRegistration.removeHandler();
		//this.itemUpdateHandlerRegistration.removeHandler();
	}

	protected void updateData(String uri, List<Command> update, int start, int total) {
		display.setDisplayList(update, start, total);
	}
		
	public void fetch(final int start, String search, boolean preferred, List<String> list) {
		GWT.log("Command fetch start="+start+" search="+search+" preferred="+preferred);
		lastStart = start;
		String url = collectionUrl+"&start="+start+"&end="+(start+PAGESIZE)+"&preferred="+preferred;
		if(list == null){
			list = new ArrayList<String>();
			list.add("alltags");
			list.add("untagged");
		}
		if(!isBlankOrNull(search))
			url += "&search="+URL.encodeQueryString(search);
		// Send request to server and catch any errors.
		for(String s : list)
		{
			url += "&tags=" + s;
		}
		url = URL.encode(url);
		RequestBuilder builder = AuthenticatedRequestFactory.newRequest(RequestBuilder.GET, url);
		try {
			 Request msg = builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					Window.alert("Request error " + exception.getMessage());
				}
	
				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) {
						Collection<Command> c = Command.asCollection(response.getText());
						updateData(c.getUri(), c.getElements(), start, c.getTotal());
						//knowTags = true;						
						for(Command command :c.getElements()){
							if(command.getTags() != null)
								tags.addAll(command.getTags());
						}
						if(display instanceof CommandListGridView){
							((CommandListGridView) display).updateTags(tags);
						}
						knowTags = true;
					} else {
						Window.alert("Response error " + response.getStatusText());
					}
				}
			});
		} catch (RequestException exception) {
			Window.alert("Request exception " + exception.getMessage());
		}
	}

	public static class CommandListUpdate extends GwtEvent<CommandListUpdateEventHandler> {
		public static Type<CommandListUpdateEventHandler> TYPE = new Type<CommandListUpdateEventHandler>();
		public CommandListUpdate() {}
		@Override
		public com.google.gwt.event.shared.GwtEvent.Type<CommandListUpdateEventHandler> getAssociatedType() {
			return TYPE;
		}
		@Override
		protected void dispatch(CommandListUpdateEventHandler handler) {
			handler.onMessageReceived(this);
		}
	}

	private boolean isBlankOrNull(String s) {
		return s==null || s.length()==0;
	}
	

}