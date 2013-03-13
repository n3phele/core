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
package n3phele.client.view;

import java.util.ArrayList;
import java.util.List;

import n3phele.client.N3phele;
import n3phele.client.model.FileNode;
import n3phele.client.model.Repository;
import n3phele.client.presenter.RepoContentActivity;
import n3phele.client.presenter.RepoPlace;
import n3phele.client.presenter.helpers.StyledTextCellRenderer;
import n3phele.client.widgets.ActionDialogBox;
import n3phele.client.widgets.FileDetailsPanel;
import n3phele.client.widgets.MenuItem;
import n3phele.client.widgets.NewFolderPanel;
import n3phele.client.widgets.S3UploadPanel;
import n3phele.client.widgets.SectionPanel;
import n3phele.client.widgets.SwiftUploadPanel;
import n3phele.client.widgets.UploadPanel;
import n3phele.client.widgets.WorkspaceVerticalPanel;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.AbstractSafeHtmlCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

public class RepoContentView extends WorkspaceVerticalPanel {

	private static final int ROWLENGTH = 2;
	private HTML repositoryName;
	private String repositoryNameText="";
	private HTML repositoryDescription;
	private String repositoryDescriptionText="";
	private SectionPanel datasets;
	private FlowPanel crumbs;
	private CellTable<List<FileNode>> grid;
	private List<FileNode> crumbElements;
	private Repository repository;
	private RepoContentActivity presenter = null;
	private PopupPanel uploadPopup;
	private PopupPanel newFolderPopup;
	private String destination;
	private UploadPanel uploadPanel;
	private PopupPanel fileDetailsPopup;
	private List<List<FileNode>> rowList;

	/**
	 * @param content
	 * @param extras
	 */
	public RepoContentView() {
		super(new MenuItem(N3phele.n3pheleResource.repositoryIcon(), "File Repository", null),
				new MenuItem(N3phele.n3pheleResource.repositoryIcon(), "edit", "repo"));
		
		HorizontalPanel title = new HorizontalPanel();
		title.setWidth("95%");
		repositoryName = new HTML(repositoryNameText);
		title.add(repositoryName);
		repositoryName.addStyleName(N3phele.n3pheleResource.css().commandDetailHeader());
		repositoryName.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		repositoryDescription = new HTML(repositoryDescriptionText);
		repositoryDescription.addStyleName(N3phele.n3pheleResource.css().commandDetailHeader());
		title.add(repositoryDescription);
		this.add(title);
		
		/*
		 * Setup dataset section of the view
		 */
		
		datasets = new SectionPanel("Contents");
		
		Button addDataSet = new Button("<u>import</u>", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				createUploadPopup();	
			}});
		this.datasets.addButton(addDataSet);
		Button newFolder = new Button("<u>new folder</u>", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				createNewFolderPopup(null);	
			}});
		this.datasets.addButton(newFolder);
		
		
		
		
		this.datasets.add(createCrumbsPanel());
		this.datasets.add(createDatasetTable());
		this.add(datasets);
	}
	public void setPresenter(RepoContentActivity presenter) {
		this.presenter  = presenter;
	}

	/**
	 * @param repo
	 */
	public void setData(Repository repository) {
		this.repository = repository;
		if(repository != null) {
			this.repositoryName.setText(this.repository.getName());
			this.repositoryDescription.setText(this.repository.getDescription());
			this.headerExtras.get(0).setTargetHistoryToken( 
							RepoPlace.getPlaceToken(repository.getUri()));
		} else {
			this.show(nullList, null);
			this.grid.setRowCount(0, true);
			this.grid.setEmptyTableWidget(this.grid.getLoadingIndicator());
			this.grid.setVisibleRangeAndClearData(this.grid.getVisibleRange(), true);


		}
	}
	final static List<FileNode> nullList = new ArrayList<FileNode>(0);
	private static Widget emptyTable=null;
	
	/*
	 * View Object creation
	 */
	
	protected FlowPanel createCrumbsPanel() {
		crumbs = new FlowPanel();
		crumbs.setWidth("100%");
		return crumbs;

	}
	
	protected Widget createDatasetTable() {
		grid = new CellTable<List<FileNode>>();
		grid.setWidth("100%");
		grid.setTableLayoutFixed(true);

		for(int i=0; i < ROWLENGTH; i++) {
			
			Column<List<FileNode>,FileNode> icon = new Column<List<FileNode>, FileNode>(new FileNodeIconCell(i)) {				
				@Override
				public FileNode getValue(List<FileNode> object) {
					int index = ((FileNodeIconCell)this.getCell()).getIndex();
					if(index < object.size()) {
						FileNode value = object.get(index);
						return value;
					} else {
						return null;
					}
				}};
				icon.setFieldUpdater(new FieldUpdater<List<FileNode>, FileNode>() {
	
					@Override
					public void update(int index, List<FileNode> object, FileNode value) {
						if(value != null) {
							String type = value.getMime();
							PopupPanel popup;
							if(!type.endsWith("Folder") && !type.endsWith("Placeholder")) {
								GWT.log("got filename on row "+index+" "+value.toFormattedString());
								popup = createFileDetailsPopup(value);
							} else {
								GWT.log("got folder on row "+index+" "+value.toFormattedString());
								popup = createFileDetailsPopup(value);
							}
							if(popup != null) {
								int column = 0;
								for(int i=0; i < ROWLENGTH && i < object.size(); i++) {
									FileNode n = object.get(i);
									if(n.getName() != null && n.getName().equals(value.getName())) {
										column = i;
										GWT.log("Found at "+column);
										break;
									}
								}
								
								popup.showRelativeTo(new ElementWrapper(grid.getRowElement(index).getChild(column*2).getFirstChild().getParentElement().getFirstChildElement()));
								popup.addAutoHidePartner(grid.getRowElement(index).getChild(column*2).getFirstChild().getParentElement());
							}
						}
					}});
	
				Column<List<FileNode>,FileNode> text = new Column<List<FileNode>, FileNode>(new FileNodeTextCell(i)) {				
					@Override
					public FileNode getValue(List<FileNode> object) {
						int index = ((FileNodeTextCell)this.getCell()).getIndex();
						if(index < object.size()) {
							FileNode value = object.get(index);
							return value;
						} else {
							return null;
						}
					}};
					text.setFieldUpdater(new FieldUpdater<List<FileNode>, FileNode>() {
	
						@Override
						public void update(int index, List<FileNode> object, FileNode value) {
							if(value != null) {
								if(isFile(value)) {
									GWT.log("got filename on row "+index+" "+value.toFormattedString());
								} else {
									GWT.log("got folder on row "+index+" "+value.toFormattedString());
									RepoContentView.this.presenter.selectFolder(value);
								}
							}
						}});


			grid.addColumn(icon);
			grid.setColumnWidth(icon, 50, Unit.PX);
			grid.addColumn(text);
			text.setCellStyleNames(N3phele.n3pheleResource.css().repoTextCell());
			grid.setColumnWidth(text, (100.0/ROWLENGTH), Unit.PCT);
			
				
		}
		grid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
		emptyTable = grid.getEmptyTableWidget(); 
		return grid;
	}
	private class ElementWrapper extends UIObject {
	public ElementWrapper(Element e) {
		setElement(e);
	}
}
	
	public MenuBar getActionsPopup(final FileNode node, final PopupPanel popup) {
		MenuBar b = new MenuBar(true);
		b.setStyleName(N3phele.n3pheleResource.css().repoContentMenuBar(), true);
		if(node.getMime().endsWith("Folder")) {
			// Folder menu
			b.addItem(new com.google.gwt.user.client.ui.MenuItem("make public", new Command(){

				@Override
				public void execute() {
					popup.hide();
					presenter.makePublic(node, true);
					
				}}));
			b.addItem(new com.google.gwt.user.client.ui.MenuItem("make private", new Command(){

				@Override
				public void execute() {
					popup.hide();
					presenter.makePublic(node, false);
					
				}}));
			b.addSeparator();
			b.addItem(new com.google.gwt.user.client.ui.MenuItem("delete", new Command(){

				@Override
				public void execute() {
					createDeleteFolderPopup(node);
					popup.hide();
				}}));

		} else {
			// Regular file menu

			b.addItem(new com.google.gwt.user.client.ui.MenuItem("origin", new Command(){

				@Override
				public void execute() {
					// createOriginPopup(node);
					popup.hide();
				}}));

			b.addSeparator();
			
			b.addItem(new com.google.gwt.user.client.ui.MenuItem("delete", new Command(){

				@Override
				public void execute() {
					createDeletePopup(node);
					popup.hide();
				}}));
			
		}

		return b;
	}
	
	public UploadPanel getUploadPanel() {
		if(this.uploadPopup != null) {
			return this.uploadPanel;
		}
		return null;
	}
	
	private void createUploadPopup() {
		if(this.uploadPopup != null) {
			if(this.uploadPopup.isShowing()) {
				this.uploadPopup.hide();
				this.uploadPopup = null;
			}
		}
		this.uploadPopup = new PopupPanel(true);
		this.uploadPopup.addCloseHandler(new CloseHandler<PopupPanel>(){

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if(crumbElements != null && crumbElements.size()>0) {
					FileNode lastNode = crumbElements.get(crumbElements.size()-1);
					if(!uploadPanel.isCancelled()) {
						presenter.clearPlaceholders();
					}
					presenter.selectFolder(lastNode);
				}
			}});
		this.uploadPopup.setStyleName(N3phele.n3pheleResource.css().repoContentMenuPanel(), true);
		destination = "";
		if(this.crumbElements != null && this.crumbElements.size()>1) {
			FileNode lastNode = crumbElements.get(this.crumbElements.size()-1);
			String path = lastNode.getPath();
			if(path==null)
				path = "";
			destination = path+lastNode.getName()+"/";
		}
		GWT.log("Repo type is "+repository.getKind());
		if("Swift".equals(repository.getKind())) {
			GWT.log("got swift repo");
			uploadPanel = SwiftUploadPanel.getInstance(uploadPopup, destination, presenter);
		} else {
			GWT.log("got S3 repo");
			uploadPanel = S3UploadPanel.getInstance(uploadPopup, destination, presenter);
		}
		uploadPopup.add(uploadPanel);
		uploadPopup.center();
	}
	
	
	protected void createDeletePopup(FileNode node) {
		ActionDialogBox<FileNode> deletePopup = new ActionDialogBox<FileNode>("File Delete Confirmation",
				"No", "Yes", new Delegate<FileNode>(){

					@Override
					public void execute(FileNode object) {
						presenter.deleteFile(object);
						
					}});
		deletePopup.setGlassEnabled(true);
		deletePopup.setAnimationEnabled(true);

		deletePopup.setValue("Remove file \""+node.getName()+"\" from repository "+node.getRepositoryName()+"?<p>", node);
		deletePopup.center();
	}
	
	protected void createDeleteFolderPopup(FileNode node) {
		ActionDialogBox<FileNode> deleteFolderPopup = new ActionDialogBox<FileNode>("Folder Delete Confirmation",
				"No", "Yes", new Delegate<FileNode>(){

					@Override
					public void execute(FileNode object) {
						presenter.deleteFolder(object);
						
					}});
		deleteFolderPopup.setGlassEnabled(true);
		deleteFolderPopup.setAnimationEnabled(true);

		deleteFolderPopup.setValue("Remove folder \""+node.getName()+"\" from repository "+node.getRepositoryName()+"?<p>" +
				"For large filesets, this operation may take up to 10 minutes to complete.<p> The folder will refresh when the operation completes", node);
		deleteFolderPopup.center();
	}
	
	private void createNewFolderPopup(FileNode node) {
		if(this.newFolderPopup != null) {
			if(this.newFolderPopup.isShowing()) {
				this.newFolderPopup.hide();
				this.newFolderPopup = null;
			}
		}
		this.newFolderPopup = new PopupPanel(true);

		this.newFolderPopup.setStyleName(N3phele.n3pheleResource.css().repoContentMenuPanel(), true);
		final NewFolderPanel newFilePanel = NewFolderPanel.getInstance(newFolderPopup, repository);
		newFolderPopup.add(newFilePanel);
		this.newFolderPopup.addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				String name = newFilePanel.getFolderName();
				if(name !=null && (name=name.trim()).length() > 0) {
					newFolder(name);
				} 
			}});
		newFolderPopup.center();
	}
	
	private String getCanonicalFilename(String filename) {
		String result="";
		if(this.crumbElements != null && this.crumbElements.size() > 1) {
			FileNode tail = this.crumbElements.get(this.crumbElements.size()-1);
			String path = tail.getPath();
			String name = tail.getName();
			if(path == null) {
				result = name + "/";
			} else {
				result = path + name + "/";
			}
		}
		
		return result + filename;
	}
	/**
	 * @param name
	 */
	protected void newFolder(String name) {
		GWT.log("new folder "+name);
		if(name.endsWith("/"))
			name = name.substring(0, name.length()-1);
		String path = getCanonicalFilename("");
		FileNode folder = FileNode.newInstance(name, path, "Placeholder", 
				this.crumbElements.get(0).getRepository(), this.crumbElements.get(0).getRepositoryName());
		GWT.log(folder.toFormattedString());
		if(this.presenter != null) {
			this.presenter.addPlaceholder(folder);
		}
	}
	
	public void show(List<FileNode> crumbs, List<FileNode> files) {
		if(crumbs.size() > 0) {
			
			int delta = match(crumbElements, crumbs);
			GWT.log("delta = "+delta);
			if(trimCrumbs(delta))
				delta = 0;
	
			for(int i=delta; i < crumbs.size(); i++) {
				Button button;
				if(i == 0) {
					button = new Button(getFilename(crumbs.get(i).getRepositoryName()));
				} else {
					this.crumbs.add(new Image(N3phele.n3pheleResource.rightArrowHead()));
					button = new Button(getFilename(crumbs.get(i).getName()));
				}
				
				button.setStyleName(N3phele.n3pheleResource.css().crumbsButton());
				button.addClickHandler(new CrumbsClickHandler(crumbs.get(i), this));
				this.crumbs.add(button);
			}
		} else {
			trimCrumbs(0);
		}
		crumbElements = crumbs;
		if(files != null) {
			rowList = new ArrayList<List<FileNode>>((files.size()+ROWLENGTH-1)/ROWLENGTH);
			for(int i=0; i < files.size(); i = i+ROWLENGTH)
				rowList.add(files.subList(i, files.size()));
			if(rowList.isEmpty() && (this.grid.getEmptyTableWidget() != emptyTable))
				this.grid.setEmptyTableWidget(emptyTable);
		} else {
			rowList = emptyRowList;
		}
		grid.setRowData(rowList);

	}
	private final static ArrayList<List<FileNode>> emptyRowList = new ArrayList<List<FileNode>>(0);
	public static class CrumbsClickHandler implements ClickHandler {
		
		private final FileNode path;
		private final RepoContentView scope;

		public CrumbsClickHandler(FileNode path, RepoContentView scope) {
			this.path = path;
			this.scope = scope;
		}

		/* (non-Javadoc)
		 * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
		 */
		@Override
		public void onClick(ClickEvent event) {
			if(scope.presenter != null) {
				scope.presenter.selectFolder(path);
			}
			GWT.log("Crumbs for "+path.toFormattedString());
		}
		
	}

	private PopupPanel createFileDetailsPopup(FileNode node) {
		if(this.fileDetailsPopup != null) {
			FileDetailsPanel panel = (FileDetailsPanel) fileDetailsPopup.getWidget();
			if(this.fileDetailsPopup.isShowing()) {
				this.fileDetailsPopup.hide();
				if(panel.isShowing(node))
					return null;	// if user has clicked twice dismiss popup
			}
			panel.setFileNode(node);
			if(isFile(node)) {
				presenter.getOrigin(node, panel);
			}
		} else {
			this.fileDetailsPopup = new PopupPanel(true);
	
			this.fileDetailsPopup.setStyleName(N3phele.n3pheleResource.css().repoContentMenuPanel(), true);
			this.fileDetailsPopup.setGlassEnabled(false);
			this.fileDetailsPopup.setAnimationEnabled(true);
			final FileDetailsPanel fileDetailsPanel = FileDetailsPanel.getInstance(fileDetailsPopup, node,
					new Delegate<FileNode>(){		// delete

						@Override
						public void execute(FileNode object) {
							if(isFile(object)) {
								if(fileDetailsPopup.isShowing())
									fileDetailsPopup.hide();
								RepoContentView.this.createDeletePopup(object);
								GWT.log("Delete File "+object);
							} else {
								if(fileDetailsPopup.isShowing())
									fileDetailsPopup.hide();
								RepoContentView.this.createDeleteFolderPopup(object);
								GWT.log("Delete Folder "+object);
							}
						}},
					new Delegate<FileNode>(){	// make public

						@Override
						public void execute(FileNode object) {
							if(fileDetailsPopup.isShowing())
								fileDetailsPopup.hide();
							presenter.makePublic(object, true);
							GWT.log("Make public "+object);
						}},
					new Delegate<FileNode>(){	// make Private
	
						@Override
						public void execute(FileNode object) {
							if(fileDetailsPopup.isShowing())
								fileDetailsPopup.hide();
							presenter.makePublic(object, false);
							GWT.log("Make private "+object);
						}});
			if(isFile(node)) {
				presenter.getOrigin(node, fileDetailsPanel);
			}
			fileDetailsPopup.add(fileDetailsPanel);
		}
		return this.fileDetailsPopup;
	}
	
	private static boolean isFile(FileNode node) {
		return node.getMime() != null && !(node.getMime().endsWith("Folder") || node.getMime().endsWith("Placeholder"));
	}
	/** Provides a click handler for cell rendering FileNode
	 * @author Nigel Cook
	 *
	 * (C) Copyright 2010. All rights reserved.
	 * 
	 *
	 */
	public static class FileNodeTextCell extends AbstractSafeHtmlCell<FileNode> {
		  final private int index;
		  public int getIndex() {
			  return this.index;
		  }
		  /**
		   * Constructs a TextCell that uses a {@link SimpleSafeHtmlRenderer} to render
		   * its text.
		   */
		  public FileNodeTextCell(int index) {
		    super(RepoItemNameSafeHtmlRenderer.getInstance(), "click", "keydown");
		    this.index = index;
		  }

		  /**
		   * Constructs a TextCell that uses the provided {@link SafeHtmlRenderer} to
		   * render its text.
		   * 
		   * @param renderer a {@link SafeHtmlRenderer SafeHtmlRenderer<String>} instance
		   */
		  public FileNodeTextCell(SafeHtmlRenderer<FileNode> renderer, int index) {
		    super(renderer, "click", "keydown");
		    this.index = index;
		  }
		  @Override
		  public void onBrowserEvent(Context context, Element parent, FileNode value,
		      NativeEvent event, ValueUpdater<FileNode> valueUpdater) {
		    super.onBrowserEvent(context, parent, value, event, valueUpdater);
		    if ("click".equals(event.getType())) {
		      onEnterKeyDown(context, parent, value, event, valueUpdater);
		    }
		  }

		  @Override
		  protected void onEnterKeyDown(Context context, Element parent, FileNode value,
		      NativeEvent event, ValueUpdater<FileNode> valueUpdater) {
		    if (valueUpdater != null) {
		      valueUpdater.update(value);
		    }
		  }

		  @Override
		  public void render(Context context, SafeHtml value, SafeHtmlBuilder sb) {
		    if (value != null) {
		      sb.append(value);
		    }
		  }
	}
	
	public static class RepoItemNameSafeHtmlRenderer extends StyledTextCellRenderer <FileNode> {

		private static RepoItemNameSafeHtmlRenderer instance;

		public static RepoItemNameSafeHtmlRenderer getInstance() {
			if (instance == null) {
				instance = new RepoItemNameSafeHtmlRenderer(N3phele.n3pheleResource.css().repoContentIconText());
			}
			return instance;
		}
		public RepoItemNameSafeHtmlRenderer(String style) {
			super(style);
		}

		public String getValue(FileNode object) {
			if(object != null)
				return getFilename(object.getName());
			else
				return null;
		}
		@Override
		public String getTooltip(FileNode object) {
			return getFileNodeTooltip(object);
		}
	
		@Override
	    public SafeHtml render(FileNode object) {

		String name = this.getValue(object);  
		if(!isFile(object)) {
		    return template.text(SafeHtmlUtils.fromString(name), getTooltip(object), style);
		}
	    return template.text(SafeHtmlUtils.fromSafeConstant("<a href='"+redirectAddr(object)+"' target='_blank'>"+SafeHtmlUtils.htmlEscape(name)+"</a>"), name, style);

	  }
		
		@Override
		public void render(FileNode object, SafeHtmlBuilder appendable) {
			String name = this.getValue(object);  
			if(!isFile(object)) {
			    appendable.append(template.text(SafeHtmlUtils.fromString(name), getTooltip(object), style));		
			} else {
		        appendable.append(template.text(SafeHtmlUtils.fromSafeConstant("<a href='"+redirectAddr(object)+"' target='_blank'>"+SafeHtmlUtils.htmlEscape(name)+"</a>"), name, style));
			}
		}
		
		private String redirectAddr(FileNode object) {
			String x = object.getRepository()+"/redirect"+"?name="+object.getName();
			if(object.getPath()!=null) {
				x += "&path="+object.getPath();
			}
			return x;
		}
	}
	
	private static class FileNodeIconCell extends AbstractCell<FileNode> {

		  public FileNodeIconCell(int index) {
			  super("click", "keydown");
		    this.index = index;
		  }
		  @Override
		  public void onBrowserEvent(Context context, Element parent, FileNode value,
		      NativeEvent event, ValueUpdater<FileNode> valueUpdater) {
		    super.onBrowserEvent(context, parent, value, event, valueUpdater);
		    if ("click".equals(event.getType())) {
		      onEnterKeyDown(context, parent, value, event, valueUpdater);
		    }
		  }

		  @Override
		  protected void onEnterKeyDown(Context context, Element parent, FileNode value,
		      NativeEvent event, ValueUpdater<FileNode> valueUpdater) {
		    if (valueUpdater != null) {
		      valueUpdater.update(value);
		    }
		  }

		  @Override
		  public void render(Context context, FileNode value, SafeHtmlBuilder sb) {
		    if (value != null) {
		    	String icon;
		    	if(value.getMime().endsWith("Folder")) {
					if(value.getMime().endsWith("PublicFolder")) {
						icon = N3phele.n3pheleResource.publicFolder().getSafeUri().asString();
					} else {
						icon = N3phele.n3pheleResource.folderIcon().getSafeUri().asString();
					}
				} else if (value.getMime().endsWith("Placeholder")) {
					icon = N3phele.n3pheleResource.folderAddedIcon().getSafeUri().asString();
				} else {
					icon = N3phele.n3pheleResource.fileIcon().getSafeUri().asString();
				}
		      sb.append(SafeHtmlUtils.fromSafeConstant("<img src=\""+icon+"\"/>"));
		    }
		  }
		final private int index;
		
		public int getIndex() {
			return this.index;
		}
	}
	
	
	
	
	public static String getFileNodeTooltip(FileNode value) {
		if(value.getMime().endsWith("Folder")) {
			return (value.getMime().endsWith("PublicFolder")?"Public Folder ":"Folder ")+value.getName();
		} else if(value.getMime().endsWith("Placeholder")) {
			return "Placeholder Folder "+value.getName();
		}	else {
			return value.getName();
		}
	}
	
	/** Generates the index at which a is dissimilar to b
	 * @param a
	 * @param b
	 * @return
	 */
	public int match(List<FileNode> a, List<FileNode> b) {
		int len = Math.min(a==null?0:a.size(), b==null?0:b.size());
	
		for(int i=0; i < len; i++) {
			if(!(isNullOrEquals(a.get(i).getRepositoryName(),b.get(i).getRepositoryName()) &&
			   isNullOrEquals(a.get(i).getName(),b.get(i).getName())))
				return i;
		}
		return len;
	}
	
	private boolean isNullOrEquals(String a, String b) {
		if(a == null) {
			return b==null;
		}
		return a.equals(b);
	}
	public boolean trimCrumbs(int size) {
		if(crumbElements != null && crumbElements.size() > size) {
			crumbElements.clear();
			crumbs.clear();
			return true;
		}
		return false;
	}
	
	public static String getFilename(String pathName) {
		String result = pathName;
		if(pathName.endsWith("/"))
			result = pathName.substring(0, pathName.length()-1);
		return result;
	}
}
