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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import n3phele.client.CacheManager;
import n3phele.client.ClientFactory;
import n3phele.client.N3phele;
import n3phele.client.model.Collection;
import n3phele.client.model.Command;
import n3phele.client.model.Context;
import n3phele.client.model.FileNode;
import n3phele.client.model.RepoListResponse;
import n3phele.client.model.Repository;
import n3phele.client.model.ValidationResponse;
import n3phele.client.presenter.helpers.AuthenticatedRequestFactory;
import n3phele.client.presenter.helpers.PresentationIcon;
import n3phele.client.view.CommandDetailView;
import n3phele.client.widgets.FileNodeBrowser;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class CommandActivity extends AbstractActivity {

	protected final String name;
	protected final CacheManager cacheManager;
	protected EventBus eventBus;
	protected final PlaceController placeController;
	protected final CommandDetailView display;
	protected Command command = null;
	private HandlerRegistration itemUpdateHandlerRegistration;
	protected String objectUri;
	private final String repoListUrl;
//	private final String cloudListUrl;
//	private final String accountListUrl;
	private Map<String,Map<String,FileNode>> placeholderMap=null;


	public CommandActivity(String name, String uri, ClientFactory factory,  
						   CommandDetailView view) {
		super();
		this.name = name;
		this.cacheManager = factory.getCacheManager();
		this.placeController = factory.getPlaceController();
		this.display = view;
		this.objectUri = uri;
		this.repoListUrl = URL.encode(cacheManager.ServiceAddress + "repository");
//		this.cloudListUrl = URL.encode(cacheManager.ServiceAddress + "cloud");
//		this.accountListUrl = URL.encode(cacheManager.ServiceAddress + "account");
	}
	
	public CommandActivity(String name, String uri, ClientFactory factory) {
		this(name, uri, factory, factory.getCommandDetailView());
	}
	

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		this.eventBus = eventBus;
		handlerRegistration(eventBus);
		display.setPresenter(this);
		display.setData(command);
		panel.setWidget(display);
		
		initData();
	}
	
	public void handlerRegistration(EventBus eventBus) {
		
//		this.itemUpdateHandlerRegistration = this.eventBus.addHandler(CommandUpdate.TYPE, new CommandUpdateEventHandler() {
//			@Override
//			public void onMessageReceived(CommandUpdate event) {
//				refresh(event.getKey());
//			}
//		});
	}


	@Override
	public String mayStop() {
	    return null;
	}

	@Override
	public void onCancel() {
		unregister();
		N3phele.basePanel.setLeftHandIcon(null);
	}

	@Override
	public void onStop() {
		unregister();
		N3phele.basePanel.setLeftHandIcon(null);
	}
	
	public void goToPrevious() {
		History.back();
	}

	protected void initData() {
//		CacheManager.EventConstructor constructor = new CacheManager.EventConstructor() {
//			@Override
//			public CommandUpdate newInstance(String key) {
//				return new CommandUpdate(key);
//			}
//		};
//		cacheManager.register(objectUri, this.name, constructor);
		this.refresh(objectUri);

		this.getRepos();
//		this.getClouds();
//		this.getAccounts();
	}

	protected void unregister() {
//		cacheManager.unregister(objectUri, this.name);
		display.setPresenter(null);
	}


	protected void updateData(String uri, Command update) {
		this.command = update;
		if(update != null) {
			this.display.refresh(update);
			N3phele.basePanel.setLeftHandIcon(PresentationIcon.getIconImage(update.getIcon()));
		}
	}
	
	protected void updateRepository(List<Repository> update) {
		this.display.setRepositories(update);
	}
	
//	protected void updateClouds(List<Cloud> update) {
//		this.display.setClouds(update);
//	}
	
//	protected void updateAccounts(List<Account> update) {
//		this.display.setAccounts(update);
//	}

	protected void refresh(String key) {
	
		String url = key;
		// String url = objectUri;
		// Send request to server and catch any errors.
		RequestBuilder builder = AuthenticatedRequestFactory.newRequest(RequestBuilder.GET, url);
		try {
			builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					GWT.log("Couldn't retrieve JSON " + exception.getMessage());
				}
	
				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) {
						Command p = Command.asCommand(response.getText());
						updateData(p.getUri(), p);
					} else {
						GWT.log("Couldn't retrieve JSON ("
								+ response.getStatusText() + ")");
					}
				}
			});
		} catch (RequestException e) {
			GWT.log("Couldn't retrieve JSON " + e.getMessage());
		}
	}
	
	protected void getRepos() {
		
		String url = repoListUrl;
		// Send request to server and catch any errors.
		RequestBuilder builder = AuthenticatedRequestFactory.newRequest(RequestBuilder.GET, url);
		try {
			builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					GWT.log("Couldn't retrieve JSON " + exception.getMessage());
				}
	
				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) {
						Collection<Repository> r = Repository.asCollection(response.getText());
						updateRepository(r.getElements());
					} else {
						GWT.log("Couldn't retrieve JSON ("
								+ response.getStatusText() + ")");
					}
				}
			});
		} catch (RequestException e) {
			GWT.log("Couldn't retrieve JSON " + e.getMessage());
		}
	}
	
//	protected void getClouds() {
//		this.eventBus.addHandler(CacheManager.CloudListUpdate.TYPE, new CacheManager.CloudListUpdateEventHandler() {
//			@Override
//			public void onMessageReceived(CacheManager.CloudListUpdate event) {
//				updateClouds(cacheManager.getClouds());
//			}
//		});
//		updateClouds(cacheManager.getClouds());
//	}
//	
//	protected void getAccounts() {
//		
//		String url = accountListUrl;
//		// Send request to server and catch any errors.
//		RequestBuilder builder = AuthenticatedRequestFactory.newRequest(RequestBuilder.GET, url);
//		try {
//			builder.sendRequest(null, new RequestCallback() {
//				public void onError(Request request, Throwable exception) {
//					GWT.log("Couldn't retrieve JSON " + exception.getMessage());
//				}
//	
//				public void onResponseReceived(Request request, Response response) {
//					if (200 == response.getStatusCode()) {
//						GWT.log(response.getText());
//						Collection<Account> p = Account.asCollection(response.getText());
//						updateAccounts(p.getElements());
//					} else {
//						GWT.log("Couldn't retrieve JSON ("
//								+ response.getStatusText() + ")");
//					}
//				}
//			});
//		} catch (RequestException e) {
//			GWT.log("Couldn't retrieve JSON " + e.getMessage());
//		}
//	}

	public void exec(String action, String name, String arg, Context context) {
		List<String> params = new ArrayList<String>();
		if(action != null && action.trim().length()==0) {
			action = null;
		}
		if(action != null) {
			params.add("action="+URL.encodeQueryString(action));
		}
		if(name == null || name.trim().length() == 0) {
			name = null;
		}
		if(name != null)
			params.add("name="+URL.encodeQueryString(name.trim()));
		
		if(arg != null && arg.trim().length() == 0)
			arg = null;
		if(arg != null)
			params.add("arg="+URL.encodeQueryString(arg.trim()));
		
		String url = cacheManager.ServiceAddress+"process/exec";
		String seperator = "?";
		for(String param : params) {
			url = url+seperator+param;
			seperator = "&";
		}
		
		
		 // Send request to server and catch any errors.
	    RequestBuilder builder = AuthenticatedRequestFactory.newRequest(RequestBuilder.POST, url);
	    builder.setHeader("Content-type", "application/json");
	    
	    try {
	    	GWT.log("Context :"+context.toJSON().toString());
	      Request msg = builder.sendRequest(context==null?null:context.toJSON().toString(), new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					GWT.log("Couldn't retrieve JSON " + exception.getMessage());
					Window.alert("Couldn't retrieve JSON " + exception.getMessage());
				}
	
				public void onResponseReceived(Request request, Response response) {
					if (201 == response.getStatusCode()) {
						GWT.log(response.getText()+" "+response.getHeadersAsString());
						//cacheManager.refreshProgressList(); // deal with "eventual consistency" of queries
						goToPrevious();
					} else {
						Window.alert("Error code: "+response.getStatusCode()+" Status Text:"
								+response.getStatusText()+"\n"+response.getText());
						GWT.log("Couldn't submit command ("
								+ response.getStatusText() + " "+ response.getText()+")");
						
					}
				}
			});
	    } catch (RequestException e) {
	    	Window.alert("Request exception " + e.getMessage()+"\n" + e.toString());
	    }
	    
	}

	/**
	 * @param view
	 */
	public void fetchFiles(final FileNodeBrowser view, String repoURI, String prefix) {
		String url = repoURI+"/list";
		if(!isNullOrBlank(prefix)) {
			url += "?prefix="+URL.encodeQueryString(prefix);
		}
		 // Send request to server and catch any errors.
		RequestBuilder builder = AuthenticatedRequestFactory.newRequest(RequestBuilder.GET, url);
		try {
			builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					GWT.log("Couldn't retrieve JSON " + exception.getMessage());
				}
	
				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) {
						GWT.log(response.getText());
						RepoListResponse result = RepoListResponse.parseJSON(response.getText());
						List<FileNode> crumbs = result.getCrumbs();
						List<FileNode> namesWithPlaceholders = result.getFiles();
						if(crumbs != null && CommandActivity.this.placeholderMap != null) {
							String lastPath = "";
							if(crumbs.size() > 1) {
								FileNode lastCrumb = crumbs.get(crumbs.size()-1);
								lastPath = getCanonicalName(lastCrumb)+"/";
							}
							GWT.log("lastPath="+lastPath);
							if(CommandActivity.this.placeholderMap.containsKey(lastPath)) {
								java.util.Collection<FileNode> placeholders = CommandActivity.this.placeholderMap.get(lastPath).values();
								namesWithPlaceholders = new ArrayList<FileNode>(placeholders.size()+result.getFiles().size());
								GWT.log("adding placeholder");
								namesWithPlaceholders.addAll(placeholders);
								namesWithPlaceholders.addAll(result.getFiles());
							}
						}
						view.show(result.getCrumbs(), namesWithPlaceholders);
					} else {
						GWT.log("Couldn't retrieve JSON ("
								+ response.getStatusText() + ")");
					}
				}
			});
		} catch (RequestException e) {
			GWT.log("Couldn't retrieve JSON " + e.getMessage());
		}
		
	}

	/**
	 * @param view 
	 * @param folder
	 */
	public void addPlaceholder(FileNodeBrowser view, FileNode folder) {
		if(placeholderMap==null)
			this.placeholderMap = new HashMap<String,Map<String,FileNode>>();
		String folderPath = folder.getPath();
		if(!this.placeholderMap.containsKey(folderPath)) {
			this.placeholderMap.put(folderPath, new HashMap<String, FileNode>());
		}
		this.placeholderMap.get(folderPath).put(folder.getName(), folder);
		GWT.log("get "+folder.getRepository()+" "+folderPath);
		fetchFiles(view, folder.getRepository(), folder.getPath());
	}
	
	
	private String getCanonicalName(FileNode node) {
		String path = node.getPath();
		String result;
		if(isNullOrBlank(path)) {
			result = node.getName();
		} else {
			result = path + node.getName();
		}
		return result;
	}
	
	private boolean isNullOrBlank(String s) {
		return s==null || s.length()==0;
	}

	/**
	 * @param view
	 * @param repoURI
	 * @param filename
	 */
	public void checkExists(final FileNodeBrowser view, String repoURI,
			final String filename) {
		String url = repoURI+"/validate";
		if(filename != null) {
			url += "?filename="+URL.encodeQueryString(filename);
		}
		 // Send request to server and catch any errors.
		RequestBuilder builder = AuthenticatedRequestFactory.newRequest(RequestBuilder.GET, url);
		try {
			builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					GWT.log("Couldn't retrieve JSON " + exception.getMessage());
				}
	
				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) {
						GWT.log(response.getText());
						boolean result = ValidationResponse.parseJSON(response.getText()).getExists();
						if(result)
							view.enableRun(filename);
					} else {
						GWT.log("Couldn't retrieve JSON ("
								+ response.getStatusText() + ")");
					}
				}
			});
		} catch (RequestException e) {
			GWT.log("Couldn't retrieve JSON " + e.getMessage());
		}
		
		
	}
}
