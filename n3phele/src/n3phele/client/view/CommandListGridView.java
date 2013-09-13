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
import java.util.HashSet;
import java.util.List;

import n3phele.client.N3phele;
import n3phele.client.model.Command;
import n3phele.client.presenter.CommandListActivity;
import n3phele.client.presenter.CommandPlace;
import n3phele.client.presenter.helpers.PresentationIcon;
import n3phele.client.presenter.helpers.StyledTextCellRenderer;
import n3phele.client.widgets.MenuItem;
import n3phele.client.widgets.UploadCommandPanel;
import n3phele.client.widgets.WorkspaceVerticalPanel;

import com.google.gwt.cell.client.AbstractSafeHtmlCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.IconCellDecorator;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
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
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.SimpleCheckBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.RangeChangeEvent;

public class CommandListGridView extends WorkspaceVerticalPanel implements CommandListViewInterface {
	private CellTable<List<Command>> grid;
	final static List<Command> nullList = new ArrayList<Command>();
//	private List<Command> data = nullList;
	private CommandListActivity commandActivity = null;
	private int total=0;
	private final int ROWLENGTH=2;
	private final int PAGESIZE=16;
	private TextBox textBox;
	private String searchText=null;
	private SimpleCheckBox allVersions;
	private SimpleCheckBox allTags;
	private SimpleCheckBox untagged;
	private PopupPanel uploadPopup;
	private UploadCommandPanel uploadPanel;
	protected boolean suppressEvent = false;
	private SimplePager simplePager;
	private List<SimpleCheckBox> checkBoxList = new ArrayList<SimpleCheckBox>();
	private HorizontalPanel disclosed;
	private final HorizontalPanel tagPanel = new HorizontalPanel();
	private String cellWidth = "100px";
	@SuppressWarnings("deprecation")
	public CommandListGridView() {
		super(new MenuItem(N3phele.n3pheleResource.commandIcon(), "Commands", null));
		String html = "<img style='border:none; vertical-align:bottom; margin:-2px; padding-right:2px;' width=20 height=20 src='"+N3phele.n3pheleResource.commandIcon().getURL()+"'/>create a new command";
		Button addDataSet = new Button(html, new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				createUploadPopup();	
			}});
		addDataSet.setStyleName(N3phele.n3pheleResource.css().newCommandButton());
		this.strip.add(addDataSet);
		HorizontalPanel heading = new HorizontalPanel();
		heading.setWidth("500px");
		heading.setStyleName(N3phele.n3pheleResource.css().sectionPanelHeader());
		add(heading);	
		tagPanel.setVisible(false);
		tagPanel.setStyleName(N3phele.n3pheleResource.css().sectionPanelHeader());
		add(tagPanel);
		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
	    simplePager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		heading.add(simplePager);
		tagPanel.setBorderWidth(100);
		
		textBox = new TextBox();
		textBox.setTitle("search for a command");
		heading.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		heading.add(textBox);
		heading.setCellHorizontalAlignment(textBox, HorizontalPanel.ALIGN_RIGHT);
		textBox.addKeyPressHandler(new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event) {
				if (KeyCodes.KEY_ENTER == event.getNativeEvent().getKeyCode()) {
					ArrayList<String> tags = new ArrayList<String>();
					if(untagged.getValue()){
						tags.add("untagged");
					}
					if(allTags.getValue()){
						tags.add("alltags");
					}
					for(SimpleCheckBox checkBox : checkBoxList){
						if(checkBox.getValue()){
							tags.add(checkBox.getName());
						}
					}
					commandActivity.fetch(0, searchText = textBox.getText(), !allVersions.getValue(),tags);
				}
			}
		});

		Image searchIcon = new Image(N3phele.n3pheleResource.searchIcon().getURL());
		searchIcon.setPixelSize(20, 20);
		PushButton search = new PushButton(searchIcon);
		search.setTitle("search for a command");
		search.setStyleName(N3phele.n3pheleResource.css().commandSearchButton());
		heading.add(search);
		search.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				ArrayList<String> tags = new ArrayList<String>();
				if(untagged.getValue()){
					tags.add("untagged");
				}
				if(allTags.getValue()){
					tags.add("alltags");
				}
				for(SimpleCheckBox checkBox : checkBoxList){
					if(checkBox.getValue()){
						tags.add(checkBox.getName());
					}
				}
				commandActivity.fetch(0, searchText = textBox.getText(), !allVersions.getValue(),tags);
				
			}});
		

		heading.setCellHorizontalAlignment(simplePager, HorizontalPanel.ALIGN_CENTER);
		DisclosurePanel more = new DisclosurePanel("advanced");
		
		more.setStyleName(N3phele.n3pheleResource.css().sectionPanelHeader());
		heading.add(more);
		heading.setCellHorizontalAlignment(more, HorizontalPanel.ALIGN_LEFT);
		disclosed = new HorizontalPanel();
		//more.add(disclosed);
		more.addOpenHandler(new OpenHandler<DisclosurePanel>() {
			@Override
			public void onOpen(OpenEvent<DisclosurePanel> event) {
				tagPanel.setVisible(true);
			}
		});
		more.addCloseHandler(new CloseHandler<DisclosurePanel>() {		
			@Override
			public void onClose(CloseEvent<DisclosurePanel> event) {
				tagPanel.setVisible(false);       
			}
		});
		disclosed.add(new InlineLabel("Search all versions"));
		FlowPanel fl = new FlowPanel();
		allVersions = new SimpleCheckBox();
		allVersions.setName("allVersions");
		allVersions.setFormValue("Search all versions");
		allVersions.setStyleName(N3phele.n3pheleResource.css().tagSpacing());
		fl.add(allVersions);
		fl.setStyleName(N3phele.n3pheleResource.css().tagSpacing());
		disclosed.add(fl);
		tagPanel.add(disclosed);
		grid = new CellTable<List<Command>>();
		grid.setWidth("100%", true);
		HasCell<Command,?> nameHasCell = new HasCell<Command,Command>() {

			@Override
			public Cell<Command> getCell() {
				return new CommandTextCell(CommandNameRenderer.getInstance());
			}

			@Override
			public FieldUpdater<Command, Command> getFieldUpdater() {
				return new FieldUpdater<Command, Command>() {

					@Override
					public void update(int index, Command object,
							Command value) {
//						if(value != null) {
//							GWT.log("got-139 "+index+" "+value.getName());
//							commandActivity.goTo(new CommandPlace(value.getUri()));
//						}

					}};
			}

			@Override
			public Command getValue(Command object) {
				return object;
			}
			
		};
		HasCell<Command,?> versionHasCell = new HasCell<Command,Command>() {

			@Override
			public Cell<Command> getCell() {
				return new CommandTextCell(CommandVersionRenderer.getInstance());
			}

			@Override
			public FieldUpdater<Command, Command> getFieldUpdater() {
				return new FieldUpdater<Command, Command>() {

					@Override
					public void update(int index, Command object,
							Command value) {
//						if(value != null) {
//							GWT.log("got-166 "+index+" "+value.getName());
//							commandActivity.goTo(new CommandPlace(value.getUri()));
//						}

					}};
			}

			@Override
			public Command getValue(Command object) {
				return object;
			}
			
		};

		List<HasCell<Command, ?>> hasCells = new ArrayList<HasCell<Command, ?>>(2);
		hasCells.add(nameHasCell);
		hasCells.add(versionHasCell);
		for(int i=0; i < ROWLENGTH; i++) {
			Column<List<Command>,Command> c = new Column<List<Command>, Command>(new CommandIconTextCell(N3phele.n3pheleResource.scriptIcon(),
					new CompositeCell<Command>(hasCells), i)) {
				
				@Override
				public Command getValue(List<Command> object) {
					int index = ((CommandIconTextCell)this.getCell()).getIndex();
					if(index < object.size()) {
						return object.get(index);
					} else {
						return null;
					}
				}};
				c.setFieldUpdater(new FieldUpdater<List<Command>, Command>() {

					@Override
					public void update(int index, List<Command> object,
							Command value) {
						if(value != null) {
							GWT.log("got-201 "+index+" "+value.getName());
							commandActivity.goTo(new CommandPlace(value.getUri()));
						}
	
						
					}});
				grid.addColumn(c);
				grid.setColumnWidth(c, 100.0/ROWLENGTH, Unit.PCT);
		}

	    grid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
	    simplePager.setDisplay(grid);
		simplePager.setPageSize(PAGESIZE);
	    grid.addRangeChangeHandler(new RangeChangeEvent.Handler(){

			/* (non-Javadoc)
			 * @see com.google.gwt.view.client.RangeChangeEvent.Handler#onRangeChange(com.google.gwt.view.client.RangeChangeEvent)
			 */
			@Override
			public void onRangeChange(RangeChangeEvent event) {
				if(suppressEvent )
					return;
				Range range = grid.getVisibleRange();
				GWT.log("Table range is "+range.getStart()+" length "+range.getLength());
				int start = range.getStart();
				if(start > total)
					start = total;
//				if(data == null || (data.size() < start) ){
					
				ArrayList<String> tags = new ArrayList<String>();
				if(untagged.getValue()){
					tags.add("untagged");
				}
				if(allTags.getValue()){
					tags.add("alltags");
				}
				for(SimpleCheckBox checkBox : checkBoxList){
					if(checkBox.getValue()){
						tags.add(checkBox.getName());
					}
				}
				commandActivity.fetch(0, searchText = textBox.getText(), !allVersions.getValue(),tags);
//				} else {
//					if(length+start > data.size())
//						length = data.size()-start;
//					GWT.log("data available start="+start);
//					grid.setRowData(start, chunk(data.subList(start, start+length)));
//				}
			}
	    	
	    });
		this.add(grid);	
	}
	
	/* (non-Javadoc)
	 * @see n3phele.client.view.CommandListViewInterface#setDisplayList(java.util.List, int, int)
	 */
	@Override
	public void setDisplayList(List<Command> commandList, int start, int max) {
		if(commandList == null)
			commandList = nullList;

		suppressEvent = true;
		this.total = max;
		//this.grid.setRowCount((max + ROWLENGTH - 1)/ROWLENGTH, true);
		this.grid.setRowCount(0, true);
		this.grid.setRowCount(max, true);
		simplePager.setRangeLimited(false);
		simplePager.setPageStart(start);
		suppressEvent = false;
		this.grid.setRowData(start, chunk(commandList.subList(0, Math.min(commandList.size(), PAGESIZE))));
		simplePager.setRangeLimited(true);
		
	}


	/* (non-Javadoc)
	 * @see n3phele.client.view.CommandListViewInterface#setPresenter(n3phele.client.presenter.CommandListActivity)
	 */
	@Override
	public void setPresenter(CommandListActivity presenter) {
		this.commandActivity = presenter;
		searchText = null;
		textBox.setText(searchText);
		allVersions.setValue(false);
		setDisplayList(null, 0, 0);
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
				
			}});
		this.uploadPopup.setStyleName(N3phele.n3pheleResource.css().repoContentMenuPanel(), true);


		uploadPanel = UploadCommandPanel.getInstance(uploadPopup, commandActivity);
		uploadPopup.add(uploadPanel);
		uploadPopup.center();
	}
	
	/** Provides a click handler for cell rendering FileNode
	 * @author Nigel Cook
	 *
	 * (C) Copyright 2010. All rights reserved.
	 * 
	 *
	 */
	public static class CommandTextCell extends AbstractSafeHtmlCell<Command> {

		  /**
		   * Constructs a TextCell that uses a {@link SimpleSafeHtmlRenderer} to render
		   * its text.
		   */
		  public CommandTextCell() {
		    super(CommandNameRenderer.getInstance(), "click", "keydown");
		  }

		  /**
		   * Constructs a TextCell that uses the provided {@link SafeHtmlRenderer} to
		   * render its text.
		   * 
		   * @param renderer a {@link SafeHtmlRenderer SafeHtmlRenderer<String>} instance
		   */
		  public CommandTextCell(SafeHtmlRenderer<Command> renderer) {
		    super(renderer, "click", "keydown");
		  }
		  @Override
		  public void onBrowserEvent(Context context, Element parent, Command value,
		      NativeEvent event, ValueUpdater<Command> valueUpdater) {
		    super.onBrowserEvent(context, parent, value, event, valueUpdater);
		    if ("click".equals(event.getType())) {
		      onEnterKeyDown(context, parent, value, event, valueUpdater);
		    }
		  }

		  @Override
		  protected void onEnterKeyDown(Context context, Element parent, Command value,
		      NativeEvent event, ValueUpdater<Command> valueUpdater) {
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
	
	public static class CommandNameRenderer extends StyledTextCellRenderer <Command> {

		private static CommandNameRenderer instance;

		public static CommandNameRenderer getInstance() {
			if (instance == null) {
				instance = new CommandNameRenderer(N3phele.n3pheleResource.css().commandBrowserIconText());
			}
			return instance;
		}
		public CommandNameRenderer(String style) {
			super(style);
		}

		public String getValue(Command object) {
			if(object != null)
				return object.getName();
			else
				return null;
		}
		 protected String getTooltip(Command object) {
			 String tip = null;
			 if(object != null)
				 tip = object.getDescription();
			 if(tip == null || tip.length()==0)
				 tip = getValue(object);
			 return tip;
		  }
	}
	
	public static class CommandVersionRenderer extends StyledTextCellRenderer <Command> {

		private static CommandVersionRenderer instance;

		public static CommandVersionRenderer getInstance() {
			if (instance == null) {
				instance = new CommandVersionRenderer(N3phele.n3pheleResource.css().commandBrowserVersionText());
			}
			return instance;
		}
		public CommandVersionRenderer(String style) {
			super(style);
		}

		public String getValue(Command object) {
			if(object != null)
				return object.getVersion();
			else
				return null;
		}
		@Override
		 protected String getTooltip(Command object) {
			if(object != null)
				return "Version: "+object.getVersion()+"\nOwner: "+object.getOwnerName();
			else
				return null;
		  }
	}
	
	private static class CommandIconTextCell extends IconCellDecorator<Command> {

		final private int index;
		/**
		 * @param icon
		 * @param cell
		 */
		public CommandIconTextCell(ImageResource icon, Cell<Command> cell, int index) {
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
		 protected SafeHtml getIconHtml(Command value) {
			SafeHtmlBuilder sb = new SafeHtmlBuilder();
			String iconHtml;
			if(value == null) {
				sb.appendHtmlConstant("<div></div>");
			} else {
				iconHtml = PresentationIcon.getIconImageHtml(value.getIcon());
			      sb.appendHtmlConstant("<div style=\"position:absolute;left:0px;top:0px;line-height:0px;\" title=\""+SafeHtmlUtils.htmlEscape(value.getDescription())+"\">");
			      sb.appendHtmlConstant(iconHtml);
			      sb.appendHtmlConstant("</div>");
			}
		    return sb.toSafeHtml();
		 }
		
	}
	

	
	private List<List<Command>> chunk(List<Command> single) {
		if(single==null)
			return null;
		List<List<Command>> result = new ArrayList<List<Command>>((single.size()+ROWLENGTH-1)/ROWLENGTH);
		for(int i=0; i < single.size(); i = i+ROWLENGTH)
			result.add(single.subList(i, single.size()));
		return result;
		
	}


	@Override
	public int getPageSize() {
		return PAGESIZE;
	}
	
	public void updateTags(HashSet<String> tags){
		if(this.checkBoxList.size() > 0) return;
		FlowPanel fl = new FlowPanel();
		fl.setStyleName(N3phele.n3pheleResource.css().tagSpacing());
		disclosed.add(new InlineLabel("All tags"));
		allTags = new SimpleCheckBox();
		allTags.setName("alltags");
		allTags.setFormValue(" All tags");
		fl.add(allTags);
		disclosed.add(fl);
		disclosed.add(new InlineLabel(" Without tag"));
		fl = new FlowPanel();
		fl.setStyleName(N3phele.n3pheleResource.css().tagSpacing());
		untagged= new SimpleCheckBox();
		untagged.setName("Untagged");
		untagged.setFormValue("Without tag");
		fl.add(untagged);
		disclosed.add(fl);
		for(String s : tags){
			if(s.trim().length() == 0) continue;
			SimpleCheckBox newTag = new SimpleCheckBox();
			disclosed.add(new InlineLabel(" " +s));
			newTag = new SimpleCheckBox();
			newTag.setName(s);
			newTag.setFormValue(s);
			this.checkBoxList.add(newTag);
			fl = new FlowPanel();
			fl.setStyleName(N3phele.n3pheleResource.css().tagSpacing());
			fl.add(newTag);
			disclosed.add(fl);
		}
		
	}
}
