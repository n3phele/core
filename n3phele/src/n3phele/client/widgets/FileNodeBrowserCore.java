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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.cell.client.AbstractSafeHtmlCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.IconCellDecorator;
import com.google.gwt.cell.client.ValueUpdater;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.CellWidget;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import n3phele.client.N3phele;
import n3phele.client.model.FileNode;
import n3phele.client.model.Repository;
import n3phele.client.presenter.helpers.StyledTextCellRenderer;

import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;

public class FileNodeBrowserCore extends VerticalPanel {
	final private HorizontalPanel header;
	final private FlexTable actions;
	private FlowPanel crumbs;
	private CellTable<List<FileNode>> grid;
	private Button cancelButton;
	private Button openButton;
	private TextBox filename;
	private ListBox repoList;
	private List<FileNode> crumbElements = new ArrayList<FileNode>();
	private BrowserPresenter presenter = null;
	final int ROWLENGTH;
	private Image errorIndicator;
	private List<Repository> repos = null;
	private final boolean isInput;
	private boolean isOptional;
	private DisclosurePanel newFolderPanel;
	private NewFolderView newFolderView;
	private boolean isZip;

	public FileNodeBrowserCore(BrowserPresenter presenter, boolean isInput, int rowLength, int width, MenuItem heading) {
		this.setWidth(width+"px");
		this.ROWLENGTH = rowLength;
		this.isInput = isInput;
		this.isOptional = !isInput;
		this.presenter = presenter;
		header = new HorizontalPanel();
		header.setWidth("100%");
		if(heading != null) {
			Cell<MenuItem> cell = new IconTextCell<MenuItem>(32,32);
			CellWidget<MenuItem> headerIcon = new CellWidget<MenuItem>(cell, heading);
			headerIcon.addStyleName(N3phele.n3pheleResource.css().workspacePanelHeader());
			header.add(headerIcon);
		}

		if(!isInput) {
			newFolderPanel = new DisclosurePanel("New Folder");
			newFolderView = NewFolderView.newInstance();
			newFolderPanel.add(newFolderView);
			newFolderPanel.addOpenHandler(new OpenHandler<DisclosurePanel>(){

				@Override
				public void onOpen(OpenEvent<DisclosurePanel> event) {
					FileNodeBrowserCore.this.newFolderView.clearName();
					
				}});
			newFolderPanel.addCloseHandler(new CloseHandler<DisclosurePanel>(){

				@Override
				public void onClose(CloseEvent<DisclosurePanel> event) {
					String name = FileNodeBrowserCore.this.newFolderView.getFolderName();
					if(name != null) {
						FileNodeBrowserCore.this.newFolder(name);
					}
					
				}
				
			});
			newFolderView.setDisclosurePanel(newFolderPanel);
			header.add(newFolderPanel);
		}
		this.add(header);
		actions = new FlexTable();
		this.add(actions);
		actions.setWidth("100%");
		
		crumbs = new FlowPanel();
		crumbs.setWidth("100%");
		actions.setWidget(0, 0, crumbs);

		ScrollPanel gridPanel = new ScrollPanel();
		gridPanel.setHeight("200px");
		grid = new CellTable<List<FileNode>>();
		grid.setWidth("100%");
		grid.setTableLayoutFixed(true);
		for(int i=0; i < ROWLENGTH; i++) {
			Column<List<FileNode>,FileNode> c = new Column<List<FileNode>, FileNode>(new FileNodeIconTextCell(N3phele.n3pheleResource.folderIcon(),new FileNodeTextCell(), i)) {
				
				@Override
				public FileNode getValue(List<FileNode> object) {
					int index = ((FileNodeIconTextCell)this.getCell()).getIndex();
					if(index < object.size()) {
						return object.get(index);
					} else {
						return null;
					}
				}};
				c.setFieldUpdater(new FieldUpdater<List<FileNode>, FileNode>() {

					@Override
					public void update(int index, List<FileNode> object,
							FileNode value) {
						if(value != null) {
							if(!value.getMime().endsWith("Folder") && !value.getMime().endsWith("Placeholder")) {
								FileNodeBrowserCore.this.filename.setText(value.getName());
								FileNodeBrowserCore.this.openButtonValidate(!isZip);
							} else {
								FileNodeBrowserCore.this.filename.setText(null);
								if(FileNodeBrowserCore.this.presenter != null) {
									FileNodeBrowserCore.this.openButtonValidate(isZip);
									FileNodeBrowserCore.this.presenter.selectFolder(value);
								}
							}
						}
						GWT.log("got "+index+" "+value.toFormattedString());
						
					}});
				grid.addColumn(c);
				grid.setColumnWidth(c, 100.0/ROWLENGTH, Unit.PCT);
		}
		grid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
		gridPanel.add(grid);
		actions.setWidget(1, 0, gridPanel);
		
		repoList = new ListBox(false);
		repoList.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				FileNodeBrowserCore.this.filename.setText(null);
				FileNodeBrowserCore.this.openButtonValidate(false);
				if(FileNodeBrowserCore.this.presenter != null) {
					int selected = FileNodeBrowserCore.this.repoList.getSelectedIndex()+(FileNodeBrowserCore.this.isOptional?-1:0);
					if(selected >= 0 && selected < FileNodeBrowserCore.this.repos.size()) {
						FileNodeBrowserCore.this.presenter.selectFolder(FileNodeBrowserCore.this.repos.get(selected));
					} else {
						FileNodeBrowserCore.this.presenter.selectFolder((Repository)null);
					}
				}
				
			}});
		actions.setWidget(2, 0, repoList);
		
		filename = new TextBox();
		filename.addKeyUpHandler(new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				FileNodeBrowserCore.this.enableOpenButton(false);
				String filename = FileNodeBrowserCore.this.filename.getText();
				String repoURI = FileNodeBrowserCore.this.repoList.getValue(FileNodeBrowserCore.this.repoList.getSelectedIndex());
				GWT.log("save "+filename+" on "+repoURI);
				if(repoURI.length()==0)
					repoURI = null;
				if(FileNodeBrowserCore.this.presenter != null) {
					validateAndEnableOpenButton(filename, repoURI);
				}
			}});
		actions.setWidget(2, 1, filename);
		filename.setWidth((width-50)+"px");

		
		this.openButton = getOpenButton(this.isInput);
		if(this.openButton != null)
			actions.setWidget(3, 2, this.openButton);
		

		this.cancelButton = getCancelButton();
		if(this.cancelButton != null)
			actions.setWidget(3, 3, cancelButton);
		errorIndicator = new Image(N3phele.n3pheleResource.inputErrorIcon());
		setErrorText();

		actions.setWidget(3, 1, errorIndicator);
		actions.getCellFormatter().setHorizontalAlignment(3, 1, HasHorizontalAlignment.ALIGN_RIGHT);
		actions.getFlexCellFormatter().setWidth(3, 3, "80px");
		actions.getFlexCellFormatter().setWidth(3, 2, "80px");
		actions.getFlexCellFormatter().setWidth(3, 1, (width-100)+"px");

		actions.getFlexCellFormatter().setColSpan(0, 0, 4);
		actions.getFlexCellFormatter().setColSpan(1, 0, 4);
		actions.getFlexCellFormatter().setColSpan(2, 1, 3);
	}
	
	private boolean isBlankOrNull(String s) {
		return s==null || s.trim().length()==0;
	}
	
	protected Button getOpenButton(boolean isInput) {
		Button openButton;
		openButton = new Button(isInput? "open":"save");
		openButton.addClickHandler(new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			if(FileNodeBrowserCore.this.presenter != null) {
				int index = FileNodeBrowserCore.this.repoList.getSelectedIndex();
				String repoURI = FileNodeBrowserCore.this.repoList.getValue(index);
				if(isBlankOrNull(repoURI))
					repoURI = null;
				String filename = getCanonicalFilename(FileNodeBrowserCore.this.filename.getText());
				if(isBlankOrNull(filename))
					filename = null;
				FileNodeBrowserCore.this.presenter.save(repoURI, filename);
			}	
		}});
		openButton.setWidth("80px");
		openButton.setEnabled(false);
		return openButton;
	}
	
	protected Button getCancelButton() {
		Button cancelButton = new Button("cancel");
		cancelButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if(FileNodeBrowserCore.this.presenter != null) {
					FileNodeBrowserCore.this.presenter.hide();
				}	
			}});
		cancelButton.setWidth("80px");
		return cancelButton;
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
		if(this.presenter != null) {
			this.presenter.addPlaceholder(folder);
		}
	}


	private static class NewFolderView extends FlexTable {
		private static NewFolderView instance = null;
		public static NewFolderView newInstance() {
			if(instance == null)
				instance = new NewFolderView();
			return instance;
		}

		public void clearName() {
			this.folderName.setText(null);
			name = null;
		}
		final private TextBox folderName;
		final private Button ok;
		final private Button cancel;
		private String name = null;
		private DisclosurePanel parent;
		protected NewFolderView() {
			super();
			this.setWidget(0, 0, new Label("name"));
			this.setWidget(0, 1, folderName = new TextBox());
			this.setWidget(1, 0, ok = new Button("add"));
			ok.setTitle("Adds folder name placeholder to browser view.\r\r" +
					"Actual folder will be created in the repository during command execution.\r" +
					"Unused placeholders are automatically delelted.");
			ok.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					NewFolderView.this.name = NewFolderView.this.folderName.getText();
					if(NewFolderView.this.parent != null)
						NewFolderView.this.parent.setOpen(false);
				}
			});
			this.setWidget(1, 1, cancel = new Button("cancel"));
			cancel.addClickHandler(new ClickHandler(){

				@Override
				public void onClick(ClickEvent event) {
					if(NewFolderView.this.parent != null)
						NewFolderView.this.parent.setOpen(false);
				}});
		}
		public String getFolderName() {
			return this.name;
		}
		public void setDisclosurePanel(DisclosurePanel panel) {
			this.parent = panel;
		}
	}

	/**
	 * @param b
	 */
	protected void openButtonValidate(boolean b) {
		if(b) {
			enableOpenButton(b && this.presenter != null);
		} else {
			if(this.presenter != null) {
				String repoURI = FileNodeBrowserCore.this.repoList.getValue(FileNodeBrowserCore.this.repoList.getSelectedIndex());
				String filename = FileNodeBrowserCore.this.filename.getText();
				GWT.log("Save "+filename+" from "+FileNodeBrowserCore.this.filename.getText()+" on "+repoURI);
				validateAndEnableOpenButton(filename, repoURI);
			}
		}
		
	}
	
	private void validateAndEnableOpenButton(String filename, String repoURI) {
		boolean enabled = false;

		if(this.isInput){
			if(isOptional) {
				boolean nullFile = isBlankOrNull(filename);
				boolean nullRepo = isBlankOrNull(repoURI);
				if(nullFile && nullRepo) {
					enabled = true;
				} else if(!nullFile && !nullRepo) {
					enabled = (!isBlankOrNull(filename)) && 
							FileNodeBrowserCore.this.presenter.validate(repoURI, getCanonicalFilename(filename));
				}
			} else {
				enabled = (!isBlankOrNull(filename)) && 
				FileNodeBrowserCore.this.presenter.validate(repoURI, getCanonicalFilename(filename));
			}
		} else {
			boolean nullFilename = filename == null || filename.trim().length()==0;
			if(FileNodeBrowserCore.this.isZip) {
				enabled = (nullFilename || filename.trim().endsWith(".zip"));
			} else {
				enabled = !nullFilename || this.repoList.getValue(this.repoList.getSelectedIndex()).length()==0;

			}
		}
		enableOpenButton(enabled);
	}
		

	private void enableOpenButton(boolean enable) {
		if(enable)
			GWT.log("enableOpenButton "+enable);
		else
			GWT.log("enableOpenButton "+enable);
		
		if(openButton != null)
			openButton.setEnabled(enable);
		this.errorIndicator.setVisible(!enable);
	}
	
	public void enableRun(String text) {
		enableOpenButton(text.equals(getCanonicalFilename(this.filename.getText())));
	}

	public void setPresenter(BrowserPresenter presenter) {
		this.presenter = presenter;
		enableOpenButton(false);
	}
	public void setRepos(List<Repository> repos, boolean isOptional) {
		this.repos = repos;
		this.repoList.clear();
		this.isOptional = isOptional;
		if(this.isOptional){
			this.repoList.addItem(isInput?"unspecified" : "no output", "");
		}
		for(Repository r : repos) {
			this.repoList.addItem(r.getName(), r.getUri());
		}
	}
	public void show(List<FileNode> crumbs, List<FileNode> files) {
		if(crumbs.size() > 0) {
			String firstCrumb = crumbs.get(0).getRepository();
			selectRepoByUri(firstCrumb);
			
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
		List<List<FileNode>> rowList = new ArrayList<List<FileNode>>((files.size()+ROWLENGTH-1)/ROWLENGTH);
		for(int i=0; i < files.size(); i = i+ROWLENGTH)
			rowList.add(files.subList(i, files.size()));
		grid.setRowData(rowList);
		openButtonValidate(false);
	}
	
	public static class CrumbsClickHandler implements ClickHandler {
		
		private final FileNode path;
		private final FileNodeBrowserCore scope;

		public CrumbsClickHandler(FileNode path, FileNodeBrowserCore scope) {
			this.path = path;
			this.scope = scope;
		}

		/* (non-Javadoc)
		 * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
		 */
		@Override
		public void onClick(ClickEvent event) {
			if(path.getName() != null) {
				boolean valid = scope.selectRepoByUri(path.getRepository());
				GWT.log(path.getName()+" "+valid);
				scope.filename.setText(null);
			} else {
				boolean valid = scope.selectRepoByUri(path.getRepository());
				GWT.log("Repo name "+valid);
				scope.filename.setText(null);
			}
			scope.openButtonValidate(false);
			if(scope.presenter != null) {
				scope.presenter.selectFolder(path);
			}
			GWT.log("Crumbs for "+path.toFormattedString());
		}
		
	}

//	private boolean selectRepoByName(String name) {
//		boolean result = false;
//		if(name != null && this.repos != null) {
//			int index = this.isOptional? 1: 0;
//			for(Repository r : this.repos){
//				if(r.getName().equals(name)) {
//					this.repoList.setItemSelected(index, true);
//					return true;
//				}
//				index++;
//			}
//		}
//		
//		return result;
//	}
	
	private boolean selectRepoByUri(String uri) {
		boolean result = false;
		if(uri != null && this.repos != null) {
			int index = this.isOptional? 1: 0;
			for(Repository r : this.repos){
				if(uri.equals(r.getUri())) {
					this.repoList.setItemSelected(index, true);
					return true;
				}
				index++;
			}
		}
		
		return result;
	}
	
//	private boolean selectRepoByURI(String uri) {
//		boolean result = false;
//		if(uri != null && this.repos != null) {
//			int index = this.isInput? 0: 1;
//			for(Repository r : this.repos){
//				if(r.getUri().equals(uri)) {
//					this.repoList.setItemSelected(index, true);
//					return true;
//				}
//				index++;
//			}
//		}
//		
//		return result;
//	}
	
	
	/** Provides a click handler for cell rendering FileNode
	 * @author Nigel Cook
	 *
	 * (C) Copyright 2010. All rights reserved.
	 * 
	 *
	 */
	public static class FileNodeTextCell extends AbstractSafeHtmlCell<FileNode> {

		  /**
		   * Constructs a TextCell that uses a {@link SimpleSafeHtmlRenderer} to render
		   * its text.
		   */
		  public FileNodeTextCell() {
		    super(MySafeHtmlRenderer.getInstance(), "click", "keydown");
		  }

		  /**
		   * Constructs a TextCell that uses the provided {@link SafeHtmlRenderer} to
		   * render its text.
		   * 
		   * @param renderer a {@link SafeHtmlRenderer SafeHtmlRenderer<String>} instance
		   */
		  public FileNodeTextCell(SafeHtmlRenderer<FileNode> renderer) {
		    super(renderer, "click", "keydown");
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
	
	public static class MySafeHtmlRenderer extends StyledTextCellRenderer <FileNode> {

		private static MySafeHtmlRenderer instance;

		public static MySafeHtmlRenderer getInstance() {
			if (instance == null) {
				instance = new MySafeHtmlRenderer(N3phele.n3pheleResource.css().fileBrowserIconText());
			}
			return instance;
		}
		public MySafeHtmlRenderer(String style) {
			super(style);
		}

		public String getValue(FileNode object) {
			if(object != null)
				return getFilename(object.getName());
			else
				return null;
		}
	}
	
	private static class FileNodeIconTextCell extends IconCellDecorator<FileNode> {

		final private int index;
		/**
		 * @param icon
		 * @param cell
		 */
		public FileNodeIconTextCell(ImageResource icon, Cell<FileNode> cell, int index) {
			super(icon, cell);
			this.index = index;
		}
		
		public int getIndex() {
			return this.index;
		}

		/* (non-Javadoc)
		 * @see com.google.gwt.cell.client.Cell#render(com.google.gwt.cell.client.Cell.Context, java.lang.Object, com.google.gwt.safehtml.shared.SafeHtmlBuilder)
		 */
		@Override
		 protected SafeHtml getIconHtml(FileNode value) {
			SafeHtmlBuilder sb = new SafeHtmlBuilder();
			ImageResource icon;
			if(value == null) {
				sb.appendHtmlConstant("<div></div>");
			} else {
				if(value.getMime().endsWith("Folder")) {
					if(value.getMime().endsWith("PublicFolder")) {
						icon = N3phele.n3pheleResource.publicFolder();
					} else {
						icon = N3phele.n3pheleResource.folderIcon();
					}
				} else if (value.getMime().endsWith("Placeholder")) {
					icon = N3phele.n3pheleResource.folderAddedIcon();
				} else {
					icon = N3phele.n3pheleResource.fileIcon();
				}
				AbstractImagePrototype proto = AbstractImagePrototype.create(icon);
			      sb.appendHtmlConstant("<div style=\"position:absolute;left:0px;top:0px;line-height:0px;\" title=\""+SafeHtmlUtils.htmlEscape(getFilename(value.getName()))+"\">");
			      sb.appendHtmlConstant(proto.getHTML());
			      sb.appendHtmlConstant("</div>");
			}
		    return sb.toSafeHtml();
		 }
	}
	
	/** Generates the index at which a is dissimilar to b
	 * @param a
	 * @param b
	 * @return
	 */
	public int match(List<FileNode> a, List<FileNode> b) {
		int len = Math.min(a.size(), b.size());
	
		for(int i=0; i < len; i++) {
			if(!(nullOrEquals(a.get(i).getRepositoryName(),b.get(i).getRepositoryName()) &&
			   nullOrEquals(a.get(i).getName(),b.get(i).getName())))
				return i;
		}
		return len;
	}
	
	private boolean nullOrEquals(String a, String b) {
		if(a == null) {
			return b==null;
		}
		return a.equals(b);
	}
	public boolean trimCrumbs(int size) {
		GWT.log("crumbElements has "+crumbElements.size());
		if(crumbElements != null && crumbElements.size() > size) {
//			if(size == 0) {
				crumbElements.clear();
				crumbs.clear();
//			} else {
//				for(int last = crumbElements.size()-1; last >= size; last--) {
//					crumbElements.remove(last); 
//					GWT.log("remove "+(crumbs.getWidgetCount()-1));
//					crumbs.remove(crumbs.getWidgetCount()-1);
//					GWT.log("remove "+(crumbs.getWidgetCount()-1));
//					crumbs.remove(crumbs.getWidgetCount()-1);
//				}
//			}
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
	
	public interface BrowserPresenter {
		public void selectFolder(FileNode value);
		public void selectFolder(Repository repo);
		
		public void hide();

		public void save(String repoURI, String filename);
		
		public boolean validate(String repoURI, String filename);

		void setView(FileNodeBrowser view);
		
		void addPlaceholder(FileNode folder);
	}

	/**
	 * @param isZip 
	 * @param fileSpecification
	 */
	public void setFilename(String filename, boolean isZip, boolean isOptional) {
		this.isZip = isZip;
		this.isOptional = isOptional;
		this.filename.setText(filename);
		openButtonValidate(filename != null && filename.length()!=0 || isOptional);
		setErrorText();
	}
	
	private void setErrorText() {
		if(isInput && !isOptional) {
			errorIndicator.setTitle("The specified file is unknown. Please select a known filename");
		} else if(isZip) {
			errorIndicator.setTitle("Select either a folder or specify a filename ending in .zip");
			
		} else {
			errorIndicator.setTitle("A valid repository and filename must be both specified together");
			
		}
	}

}
