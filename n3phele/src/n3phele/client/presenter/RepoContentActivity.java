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

import n3phele.client.AppPlaceHistoryMapper;
import n3phele.client.CacheManager;
import n3phele.client.ClientFactory;
import n3phele.client.model.FileNode;
import n3phele.client.model.Origin;
import n3phele.client.model.RepoListResponse;
import n3phele.client.model.Repository;
import n3phele.client.model.UploadSignature;
import n3phele.client.presenter.helpers.AuthenticatedRequestFactory;
import n3phele.client.view.RepoContentView;
import n3phele.client.widgets.FileDetailsPanel;
import n3phele.client.widgets.UploadPanel;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
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
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class RepoContentActivity extends AbstractActivity {
	private final String repositoryUri;
	private EventBus eventBus;
	private final PlaceController placeController;
	private HandlerRegistration handlerRegistration;
	private final RepoContentView display;
	private Repository repo = null;
	private final CacheManager cacheManager;
	private Map<String, Map<String, FileNode>> placeholderMap;
	private String lastRepoURI;
	private String lastPrefix;
	private String prefix;
	private AppPlaceHistoryMapper historyMapper;
	public RepoContentActivity(String repoURI, String prefix, ClientFactory factory) {
		this.cacheManager = factory.getCacheManager();
		this.historyMapper = factory.getHistoryMapper();
		this.repositoryUri = repoURI;
		this.prefix = isBlankOrNull(prefix)?null:prefix;
		this.placeController = factory.getPlaceController();
		this.display = factory.getRepoContentView();	
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		this.eventBus = eventBus;

		display.setPresenter(this);
		panel.setWidget(display);
		this.eventBus = eventBus;
		handlerRegistration(eventBus);
		if(repositoryUri == null || repositoryUri.length()==0 || repositoryUri.equals("null")) {
			this.repo =  JavaScriptObject.createObject().<Repository> cast();
			display.setData(this.repo);
		} else {
			display.setData(this.repo);
			getRepo(this.repositoryUri);
			fetchFiles(repositoryUri, this.prefix);
			CacheManager.EventConstructor change = new CacheManager.EventConstructor() {
				@Override
				public RepoUpdate newInstance(String key) {
					return new RepoUpdate();
				}
			};
			cacheManager.register(repositoryUri, "repoContent", change);
		}
	} 
	
	public void handlerRegistration(EventBus eventBus) {
		this.handlerRegistration = this.eventBus.addHandler(RepoUpdate.TYPE, new RepoUpdateEventHandler() {
			@Override
			public void onMessageReceived(RepoUpdate event) {
				fetchFiles(lastRepoURI, lastPrefix);
			}
		});
		
	}

	
	@Override
	public String mayStop() {
	    return null;
	}
	@Override
	public void onCancel() {
		
	}
	@Override
	public void onStop() {
		this.display.setData(null);
		cacheManager.unregister(repositoryUri, "repoContent");
	}

	protected void updateRepo(Repository item) {
		this.repo = item;
		display.setData(this.repo);
	}
	
	protected void updateRepoContent(List<FileNode>crumbs, List<FileNode>files) {
		display.show(crumbs, files);
	}
	
	
	public void goToPrevious() {
		History.back();
	}
	
	private void provideOrigin(FileNode node, Origin origin, FileDetailsPanel fileDetailsPanel) {
		if(fileDetailsPanel != null) {
			fileDetailsPanel.setOrigin(node, origin);
		}
	}
	
	private void provideSignature(UploadSignature signature) {
		UploadPanel uploadPanel = this.display.getUploadPanel();
		if(uploadPanel != null) {
			GWT.log("upload to "+signature.getUrl());
			uploadPanel.setSignature(signature.getUrl(), signature.getFilename(), signature.getAwsId(), signature.getBase64Policy(),
					signature.getSignature(), signature.getContentType());
		}
	}

	/**
	 * @param node
	 */
	public void makePublic(final FileNode node, boolean isPublic) {
		String url = node.getRepository()+"/permissions";
		RequestBuilder builder = AuthenticatedRequestFactory.newRequest(RequestBuilder.POST, url);
		builder.setHeader("Content-type", "application/x-www-form-urlencoded");
		StringBuilder args = new StringBuilder();
		args.append("isPublic=");
		args.append(URL.encodeQueryString(Boolean.toString(isPublic)));
		args.append("&filename=");
		args.append(URL.encodeQueryString(getCanonicalName(node)));
		try {
			Request request = builder.sendRequest(args.toString(), new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					// displayError("Couldn't retrieve JSON "+exception.getMessage());
				}

				public void onResponseReceived(Request request, Response response) {
					GWT.log("Got reply");
					if (200 == response.getStatusCode()) {
						GWT.log("Reply is"+response.getText());
						GWT.log("Headers is "+response.getHeadersAsString());
						GWT.log("Status Text "+response.getStatusText());
						fetchFiles(repositoryUri, node.getPath());
					} else {
						Window.alert("Update failure: "+response.getStatusText()+" "+response.getText());
					}
				}

			});
		} catch (RequestException e) {
			Window.alert("Update exception: "+e.toString());
		}
	}
	/**
	 * @param node
	 * @param fileDetailsPanel 
	 */
	public void getOrigin(final FileNode node, final FileDetailsPanel fileDetailsPanel) {

		String url = node.getRepository()+"/origin"+"?name="+URL.encodeQueryString(node.getName());
		if(!isNullOrBlank(node.getPath())) {
			url += "&path="+URL.encodeQueryString(node.getPath());
		}

		RequestBuilder builder = AuthenticatedRequestFactory.newRequest(RequestBuilder.GET, url);
		try {
			Request request = builder.sendRequest(null, new RequestCallback() {
			public void onError(Request request, Throwable exception) {
				// displayError("Couldn't retrieve JSON "+exception.getMessage());
			}

			public void onResponseReceived(Request request, Response response) {
				GWT.log(response.getStatusCode()+" "+response.getText());
				if (200 == response.getStatusCode()) {
					Origin origin = Origin.asOrigin(response.getText());
					provideOrigin(node, origin, fileDetailsPanel);		
				} else {
					Window.alert("Origin fetch failure: "+response.getStatusText()+" "+response.getText());
				}
			}


		});
		} catch (RequestException e) {
			Window.alert("Origin fetch exception: "+e.toString());
		}
		
	}
	
	/**
	 */
	public void getSignature(final String file) {

		String url = this.repo.getUri()+"/sign?name="+URL.encodeQueryString(file);
		final String uri = this.repositoryUri;

		RequestBuilder builder = AuthenticatedRequestFactory.newRequest(RequestBuilder.GET, url);
		try {
			Request request = builder.sendRequest(null, new RequestCallback() {
			public void onError(Request request, Throwable exception) {
				// displayError("Couldn't retrieve JSON "+exception.getMessage());
			}

			public void onResponseReceived(Request request, Response response) {
				GWT.log(response.getStatusCode()+" "+response.getText());
				if (200 == response.getStatusCode()) {
					if(uri.equals(RepoContentActivity.this.repositoryUri)) {
						UploadSignature signature = UploadSignature.asUploadSignature(response.getText());
						provideSignature(signature);
					}
				} else {
					Window.alert("Signature fetch failure: "+response.getStatusText()+" "+response.getText());
				}
			}


		});
		} catch (RequestException e) {
			Window.alert("Signature fetch exception: "+e.toString());
		}
		
	}
	
	public void uploadComplete(final String file) {

		String url = this.repo.getUri()+"/uploadComplete?name="+URL.encodeQueryString(file);

		RequestBuilder builder = AuthenticatedRequestFactory.newRequest(RequestBuilder.GET, url);
		try {
			Request request = builder.sendRequest(null, new RequestCallback() {
			public void onError(Request request, Throwable exception) {
				Window.alert("Update complete request exception: "+exception.getMessage());
			}

			public void onResponseReceived(Request request, Response response) {
			}


		});
		} catch (RequestException e) {
			Window.alert("Update complete exception: "+e.toString());
		}
		
	}

	/*
	 * Data Handling
	 * -------------
	 */
	

	public void getRepo(String repoUri) {
		// Send request to server and catch any errors.
		this.lastRepoURI = repoUri;
		// this.lastPrefix = null;
		RequestBuilder builder = AuthenticatedRequestFactory.newRequest(RequestBuilder.GET, repoUri);
		try {
			Request request = builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					// displayError("Couldn't retrieve JSON "+exception.getMessage());
				}

				public void onResponseReceived(Request request, Response response) {
					GWT.log(response.getStatusCode()+" "+response.getText());
					if (200 == response.getStatusCode()) {
						Repository repo = Repository.asRepository(response.getText());
						updateRepo(repo);		
					} else {
						Window.alert("Update failure: "+response.getStatusText()+" "+response.getText());
					}
				}

			});
		} catch (RequestException e) {
			Window.alert("Update exception: "+e.toString());
		}
	}
	
	/**
	 * @param view
	 */
	public void fetchFiles(String repoURI, final String prefix) {
		String url = repoURI+"/list";
		if(prefix != null) {
			url += "?prefix="+URL.encodeQueryString(prefix);
		}
		this.lastRepoURI = repoURI;
		this.lastPrefix = prefix;
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
						if(crumbs != null && placeholderMap != null) {
							String lastPath = "";
							if(crumbs.size() > 1) {
								FileNode lastCrumb = crumbs.get(crumbs.size()-1);
								lastPath = getCanonicalName(lastCrumb)+"/";
							}
							GWT.log("lastPath="+lastPath);
							if(placeholderMap.containsKey(lastPath)) {
								java.util.Collection<FileNode> placeholders = placeholderMap.get(lastPath).values();
								namesWithPlaceholders = new ArrayList<FileNode>(placeholders.size()+result.getFiles().size());
								GWT.log("adding placeholder");
								namesWithPlaceholders.addAll(placeholders);
								namesWithPlaceholders.addAll(result.getFiles());
							}
						}
						updateRepoContent(result.getCrumbs(), namesWithPlaceholders);
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




	public void onSelect(Repository selected) {
		// TODO Auto-generated method stub
		
	}

	
	/*
	 * Event Definition
	 * ----------------
	 */
	
	/**
	 * @param value
	 */
	public void selectFolder(FileNode value) {
		if(!value.getMime().equals("application/vnd.com.n3phele.Repository+json")) {
			String path = value.getPath()==null?value.getName()+"/":(value.getPath()+value.getName()+"/");
			if(path.startsWith("/"))
				GWT.log("FileNode "+value+" produces query with leading /");
			fetchFiles(value.getRepository(), path);

			History.newItem(historyMapper.getToken(new RepoContentPlace(value.getRepository(), path)));
		} else {
			fetchFiles(value.getRepository(), null);
			History.newItem(historyMapper.getToken(new RepoContentPlace(value.getRepository(), null)));
		}
		
	}

	private boolean isNullOrBlank(String s) {
		return s==null || s.isEmpty();
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

	/**
	 * @param folder
	 */
	public void addPlaceholder(FileNode folder) {
		if(placeholderMap==null)
			this.placeholderMap = new HashMap<String,Map<String,FileNode>>();
		String folderPath = folder.getPath();
		if(!this.placeholderMap.containsKey(folderPath)) {
			this.placeholderMap.put(folderPath, new HashMap<String, FileNode>());
		}
		this.placeholderMap.get(folderPath).put(folder.getName(), folder);
		GWT.log("get "+folder.getRepository()+" "+folderPath);
		fetchFiles(folder.getRepository(), 
				isNullOrBlank(folder.getPath())?null:folder.getPath());
	}

	public void clearPlaceholders() {
		placeholderMap = null;
	}

	public void deleteFile(final FileNode object) {
		String filename = getCanonicalName(object);
		String url = this.repo.getUri()+"/file?filename="+URL.encodeQueryString(filename);
		// Send request to server and catch any errors.
		RequestBuilder builder = AuthenticatedRequestFactory.newRequest(RequestBuilder.DELETE, url);
		try {
			Request request = builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					// displayError("Couldn't retrieve JSON "+exception.getMessage());
				}

				public void onResponseReceived(Request request, Response response) {
					GWT.log(response.getStatusCode()+" "+response.getText());
					if (200 == response.getStatusCode()) {
						fetchFiles(object.getRepository(), object.getPath());
					} else {
						Window.alert("Delete failure: "+response.getStatusText()+" "+response.getText());
					}
				}

			});
		} catch (RequestException e) {
			Window.alert("Delete exception: "+e.toString());
		}
	}

		public void deleteFolder(final FileNode object) {
		String filename = getCanonicalName(object);

		String url = this.repo.getUri()+"/folder?filename="+URL.encodeQueryString(filename);
		// Send request to server and catch any errors.
		RequestBuilder builder = AuthenticatedRequestFactory.newRequest(RequestBuilder.DELETE, url);
		try {
			Request request = builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					// displayError("Couldn't retrieve JSON "+exception.getMessage());
				}

				public void onResponseReceived(Request request, Response response) {
					GWT.log(response.getStatusCode()+" "+response.getText());
					if (200 != response.getStatusCode()) {
						Window.alert("Delete failure: "+response.getStatusText()+" "+response.getText());
					}
				}

			});
		} catch (RequestException e) {
			Window.alert("Delete exception: "+e.toString());
		}
	}
		
	public interface RepoUpdateEventHandler extends EventHandler {
		void onMessageReceived(RepoUpdate repoUpdate);
	}
		
	public static class RepoUpdate extends GwtEvent<RepoUpdateEventHandler> {
		public static Type<RepoUpdateEventHandler> TYPE = new Type<RepoUpdateEventHandler>();
		public RepoUpdate() {}
		@Override
		public com.google.gwt.event.shared.GwtEvent.Type<RepoUpdateEventHandler> getAssociatedType() {
			return TYPE;
		}
		@Override
		protected void dispatch(RepoUpdateEventHandler handler) {
			handler.onMessageReceived(this);
		}
	}
	
	private boolean isBlankOrNull(String s) {
		return s==null || s.isEmpty();
	}

}
