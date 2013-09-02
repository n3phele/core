/**
 * @author Nigel Cook
 * @author Douglas Tondin
 * @author Leonardo Amado
 * (C) Copyright 2010-2013. Nigel Cook. All rights reserved.
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
import n3phele.client.model.Stack;
import n3phele.client.model.StackServiceAction;
import n3phele.client.presenter.AccountListActivity;
import n3phele.client.presenter.ServiceDetailsActivity;
import n3phele.client.presenter.helpers.PresentationIcon;
import n3phele.client.presenter.helpers.StyledTextCellRenderer;

import n3phele.client.widgets.MenuItem;
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
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.RangeChangeEvent;

public class ServiceDetailsView extends WorkspaceVerticalPanel {
	private CellTable<List<Stack>> grid;
	final private FlexTable table;
	private Button cancel;
	private StackServiceAction stackAction;
	private ServiceDetailsActivity presenter = null;
	
	final static List<Stack> nullList = new ArrayList<Stack>();
	private ServiceDetailsActivity commandActivity = null;
	private int total = 0;
	private final int ROWLENGTH = 2;
	private final int PAGESIZE = 16;
	private TextBox textBox;
	protected boolean suppressEvent = false;
	private SimplePager simplePager;
	private List<Stack> data = new ArrayList<Stack>();
	private Label lblNewLabel; 
	
	public StackServiceAction getStackAction() {
		return this.stackAction;
	}

	public void setStackAction(StackServiceAction stackAction) {
		data = stackAction.getStackList();
		setDisplayList(data, 0, 0);
		lblNewLabel.setText(stackAction.getName());
		lblNewLabel.setStyleName(N3phele.n3pheleResource.css().labelFontWeight());
		this.stackAction = stackAction;
	}

	public ServiceDetailsView() {
		super(new MenuItem(N3phele.n3pheleResource.serviceIcon(),
				"Service Details", null));

		// *******************************************

		table = new FlexTable();
		table.setCellPadding(10);
		

		// Selected service.
		lblNewLabel = new Label("");
		table.setWidget(0, 0, lblNewLabel);	
		


		table.setWidget(1, 2, cancel);
		table.getFlexCellFormatter().setHorizontalAlignment(1, 2,
				HasHorizontalAlignment.ALIGN_RIGHT);
		table.getFlexCellFormatter().setHorizontalAlignment(0, 0,
				HasHorizontalAlignment.ALIGN_RIGHT);
		table.getFlexCellFormatter().setHorizontalAlignment(1, 0,
				HasHorizontalAlignment.ALIGN_CENTER);

		table.getColumnFormatter().setWidth(0, "25%");
		table.getColumnFormatter().setWidth(1, "18px");
		table.getColumnFormatter().setWidth(4, "16px");
		table.setCellPadding(1);
		table.setCellSpacing(5);

		HorizontalPanel heading = new HorizontalPanel();
		heading.setWidth("500px");
		heading.setStyleName(N3phele.n3pheleResource.css().sectionPanelHeader());
		//add(heading);
		SimplePager.Resources pagerResources = GWT
				.create(SimplePager.Resources.class);
		simplePager = new SimplePager(TextLocation.CENTER, pagerResources,
				false, 0, true);
		heading.add(simplePager);

		textBox = new TextBox();
		textBox.setTitle("search for a command");
		heading.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		heading.add(textBox);
		heading.setCellHorizontalAlignment(textBox, HorizontalPanel.ALIGN_RIGHT);
		textBox.addKeyPressHandler(new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event) {
				if (KeyCodes.KEY_ENTER == event.getNativeEvent().getKeyCode()) {
					commandActivity.getProcess();
				}
			}
		});

		grid = new CellTable<List<Stack>>();
		grid.setWidth("100%", true);
		HasCell<Stack, ?> nameHasCell = new HasCell<Stack, Stack>() {

			@Override
			public Cell<Stack> getCell() {
				return new StackTextCell(StackNameRenderer.getInstance()); 
			}

			@Override
			public FieldUpdater<Stack, Stack> getFieldUpdater() {
				return new FieldUpdater<Stack, Stack>() {
					@Override
					public void update(int index, Stack object, Stack value) {
						presenter.onSelect(value);
					}
				};
			}

			@Override
			public Stack getValue(Stack object) {
				return object;
			}

		};
		HasCell<Stack, ?> versionHasCell = new HasCell<Stack, Stack>() {

			@Override
			public Cell<Stack> getCell() {
				return new StackTextCell(StackVersionRenderer.getInstance());
			}

			@Override
			public FieldUpdater<Stack, Stack> getFieldUpdater() {
				return new FieldUpdater<Stack, Stack>() {

					@Override
					public void update(int index, Stack object, Stack value) {
						presenter.onSelect(value);
						// TODO Stack Details View
						// if(value != null) {
						// GWT.log("got-166 "+index+" "+value.getName());
						// commandActivity.goTo(new
						// CommandPlace(value.getUri()));
						// }

					}
				};
			}

			@Override
			public Stack getValue(Stack object) {
				return object;
			}

		};

		List<HasCell<Stack, ?>> hasCells = new ArrayList<HasCell<Stack, ?>>(2);
		hasCells.add(nameHasCell);
		hasCells.add(versionHasCell);
		for (int i = 0; i < ROWLENGTH; i++) {
			Column<List<Stack>, Stack> c = new Column<List<Stack>, Stack>(new CommandIconTextCell(N3phele.n3pheleResource.stackIcon(),new CompositeCell<Stack>(hasCells), i)) {

				@Override
				public Stack getValue(List<Stack> object) {
					int index = ((CommandIconTextCell) this.getCell())
							.getIndex();
					if (index < object.size()) {
						return object.get(index);
					} else {
						return null;
					}
				}
			};
			c.setFieldUpdater(new FieldUpdater<List<Stack>, Stack>() {

				@Override
				public void update(int index, List<Stack> object,
						Stack value) {
					presenter.onSelect(value);

					if (value != null) {
						GWT.log("got-201 " + index + " " + value.getName());
					}

				}
			});
			grid.addColumn(c);
			grid.setColumnWidth(c, 100.0 / ROWLENGTH, Unit.PCT);
		}

		grid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
		simplePager.setDisplay(grid);
		simplePager.setPageSize(PAGESIZE);
		grid.addRangeChangeHandler(new RangeChangeEvent.Handler() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * com.google.gwt.view.client.RangeChangeEvent.Handler#onRangeChange
			 * (com.google.gwt.view.client.RangeChangeEvent)
			 */
			@Override
			public void onRangeChange(RangeChangeEvent event) {
				if (suppressEvent)
					return;
				Range range = grid.getVisibleRange();
				GWT.log("Table range is " + range.getStart() + " length "
						+ range.getLength());
				int start = range.getStart();
				if (start > total)
					start = total;
				commandActivity.getProcess();
			}

		});
		this.add(table);
		this.add(grid);
	}

	
	public void setDisplayList(List<Stack> commandList, int start, int max) {
		if(commandList == null)
			commandList = nullList;

		suppressEvent = true;
		this.total = max;
		this.grid.setRowCount(0, true);
		this.grid.setRowCount(max, true);
		simplePager.setRangeLimited(false);
		simplePager.setPageStart(start);
		suppressEvent = false;
		this.grid.setRowData(start, chunk(commandList.subList(0, Math.min(commandList.size(), PAGESIZE))));
		simplePager.setRangeLimited(true);
		
	}
	
	private List<List<Stack>> chunk(List<Stack> single) {
		if(single==null)
			return null;
		List<List<Stack>> result = new ArrayList<List<Stack>>((single.size()+ROWLENGTH-1)/ROWLENGTH);
		for(int i=0; i < single.size(); i = i+ROWLENGTH)
			result.add(single.subList(i, single.size()));
		return result;
		
	}
	
	private static class CommandIconTextCell extends IconCellDecorator<Stack> {

		final private int index;

		/**
		 * @param icon
		 * @param cell
		 */
		public CommandIconTextCell(ImageResource icon, Cell<Stack> cell,
				int index) {
			super(icon, cell);
			this.index = index;
		}

		public int getIndex() {
			return this.index;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.google.gwt.cell.client.Cell#render(com.google.gwt.cell.client
		 * .Cell.Context, java.lang.Object,
		 * com.google.gwt.safehtml.shared.SafeHtmlBuilder)
		 */
		@Override
		protected SafeHtml getIconHtml(Stack value) {
			SafeHtmlBuilder sb = new SafeHtmlBuilder();
			String iconHtml;
			if (value == null) {
				sb.appendHtmlConstant("<div></div>");
			} else {
				iconHtml = PresentationIcon.getIconImageHtml((N3phele.n3pheleResource.stackIcon().getName()));
				sb.appendHtmlConstant("<div style=\"position:absolute;left:0px;top:0px;line-height:0px;\" title=\""
						+ SafeHtmlUtils.htmlEscape(value.getName())
						+ "\">");
				sb.appendHtmlConstant(iconHtml);
				sb.appendHtmlConstant("</div>");
			}
			return sb.toSafeHtml();
		}

	}
	public static class StackNameRenderer extends StyledTextCellRenderer <Stack> {

		private static StackNameRenderer instance;

		public static StackNameRenderer getInstance() {
			if (instance == null) {
				instance = new StackNameRenderer(N3phele.n3pheleResource.css().commandBrowserIconText());
			}
			return instance;
		}
		public StackNameRenderer(String style) {
			super(style);
		}

		public String getValue(Stack object) {
			if(object != null)
				return object.getName();
			else
				return null;
		}
		 protected String getTooltip(Stack object) {
			 String tip = null;
			 if(object != null)
				 tip = object.getDescription();
			 if(tip == null || tip.length()==0)
				 tip = getValue(object);
			 return tip;
		  }
	}
	
	public static class StackTextCell extends AbstractSafeHtmlCell<Stack> {

		  /**
		   * Constructs a TextCell that uses a {@link SimpleSafeHtmlRenderer} to render
		   * its text.
		   */
		  public StackTextCell() {
		    super(StackNameRenderer.getInstance(), "click", "keydown");
		  }

		  /**
		   * Constructs a TextCell that uses the provided {@link SafeHtmlRenderer} to
		   * render its text.
		   * 
		   * @param renderer a {@link SafeHtmlRenderer SafeHtmlRenderer<String>} instance
		   */
		  public StackTextCell(SafeHtmlRenderer<Stack> renderer) {
		    super(renderer, "click", "keydown");
		  }
		  @Override
		  public void onBrowserEvent(Context context, Element parent, Stack value,
		      NativeEvent event, ValueUpdater<Stack> valueUpdater) {
		    super.onBrowserEvent(context, parent, value, event, valueUpdater);
		    if ("click".equals(event.getType())) {
		      onEnterKeyDown(context, parent, value, event, valueUpdater);
		    }
		  }

		  @Override
		  protected void onEnterKeyDown(Context context, Element parent, Stack value,
		      NativeEvent event, ValueUpdater<Stack> valueUpdater) {
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
	public static class StackVersionRenderer extends StyledTextCellRenderer <Stack> {

		private static StackVersionRenderer instance;

		public static StackVersionRenderer getInstance() {
			if (instance == null) {
				instance = new StackVersionRenderer(N3phele.n3pheleResource.css().commandBrowserVersionText());
			}
			return instance;
		}
		public StackVersionRenderer(String style) {
			super(style);
		}

		public String getValue(Stack object) {
			if(object != null)
				return "ID: " + object.getId().toString();
			else
				return null;
		}
		@Override
		 protected String getTooltip(Stack object) {
			if(object != null)
				return "ID: " + object.getId().toString();
			else
				return null;
		  }
	}
	
	
	public ServiceDetailsActivity getPresenter() {
		return this.presenter;
	}

	public void setPresenter(ServiceDetailsActivity presenter) {
		this.presenter = presenter;
	}
}
