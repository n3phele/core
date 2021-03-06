/**
 * @author Gabriela Lavina
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
 * 
 */

package n3phele.client.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import n3phele.client.N3phele;
import n3phele.client.model.Account;
import n3phele.client.model.Activity;
import n3phele.client.model.ActivityData;

import n3phele.client.presenter.AccountHyperlinkActivity;
import n3phele.client.presenter.helpers.AuthenticatedRequestFactory;
import n3phele.client.resource.DataGridResource;
import n3phele.client.widgets.ActionDialogBox;
import n3phele.client.widgets.CancelButtonCell;
import n3phele.client.widgets.MenuItem;
import n3phele.client.widgets.SectionPanel;
import n3phele.client.widgets.ValidInputIndicatorWidget;
import n3phele.client.widgets.WorkspaceVerticalPanel;

import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.LegendPosition;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.visualizations.LineChart;
import com.google.gwt.visualization.client.visualizations.LineChart.Options;
import com.google.gwt.user.datepicker.client.CalendarUtil;


@SuppressWarnings("deprecation")
public class AccountHyperlinkView extends WorkspaceVerticalPanel implements EntryPoint {
	private DataGrid<ActivityData> dataGrid;
	private List<ActivityData> vsData = null;
	private final FlexTable table;
	private Account account = null;
	private AccountHyperlinkActivity presenter = null;
	private ActionDialogBox<ActivityData> dialog;
	private final ValidInputIndicatorWidget errorsOnPage;
	private static DataGridResource resource = null;
	private List<Double> chartValues;
	private LineChart chart = null;
	private Panel chartPanel = null;
	final private FlexTable historyTable = new FlexTable();
	final private FlexTable vsTable = new FlexTable();
	private ListBox options = new ListBox(false);
	private String costOption = "normal";
	private String chartTitle = "24 Hours Costs Chart";
	private Button hours, days, month;

	public List<ActivityData> getVsData() {
		return this.vsData;
	}

	public AccountHyperlinkView(String uri) {
		super(new MenuItem(N3phele.n3pheleResource.accountIcon(), "Account", null), new MenuItem(N3phele.n3pheleResource.accountAddIcon(), "Account Edit", "account:" + uri));

		if (resource == null)
			resource = GWT.create(DataGridResource.class);

		// TABLE
		table = new FlexTable();
		table.setCellPadding(10);
		errorsOnPage = new ValidInputIndicatorWidget("check for missing or invalid parameters marked with this icon", false);
		setTableData();
		table.getFlexCellFormatter().setRowSpan(0, 1, 2);
		table.getFlexCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_MIDDLE);
		table.getFlexCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_MIDDLE);
		table.getFlexCellFormatter().setVerticalAlignment(1, 0, HasVerticalAlignment.ALIGN_MIDDLE);
		table.getColumnFormatter().setWidth(0, "220px");
		table.getColumnFormatter().setWidth(1, "290px");
		table.setCellPadding(1);
		table.setCellSpacing(1);

		// DATAGRID
		dataGrid = new DataGrid<ActivityData>(15, resource);
		dataGrid.setSize("495px", "100px");

		TextColumn<ActivityData> nameColumn = new TextColumn<ActivityData>() {
			@Override
			public String getValue(ActivityData item) {
				String result = "";
				if (item != null) {
					result += item.getName();
				}
				return result;
			}
		};
		dataGrid.addColumn(nameColumn, "Name");
		dataGrid.setColumnWidth(nameColumn, "130px");

		// TODO: Working on Activity column

		Column<ActivityData, String> activityColumn = new Column<ActivityData, String>(new ClickableTextCell()) {
			@Override
			public String getValue(ActivityData item) {

				return item.getNameTop();
			}

		};
		activityColumn.setFieldUpdater(new FieldUpdater<ActivityData, String>() {
			@Override
			public void update(int index, ActivityData obj, String value) {
				presenter.onSelect(obj);

			}
		});

		activityColumn.setCellStyleNames(N3phele.n3pheleResource.css().clickableTextCellEffect());
		dataGrid.addColumn(activityColumn, "Activity");
		dataGrid.setColumnWidth(activityColumn, "100px");

		TextColumn<ActivityData> ageColumn = new TextColumn<ActivityData>() {
			@Override
			public String getValue(ActivityData item) {
				return item.getAge();
			}
		};
		dataGrid.addColumn(ageColumn, "Age");
		dataGrid.setColumnWidth(ageColumn, "80px");

		TextColumn<ActivityData> priceColumn = new TextColumn<ActivityData>() {
			@Override
			public String getValue(ActivityData item) {
				return item.getCost();
			}
		};
		dataGrid.addColumn(priceColumn, "Total Cost");
		dataGrid.setColumnWidth(priceColumn, "75px");

		// Add a selection model to handle user selection.
		final SingleSelectionModel<ActivityData> selectionModel = new SingleSelectionModel<ActivityData>();
		dataGrid.setSelectionModel(selectionModel);
		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			public void onSelectionChange(SelectionChangeEvent event) {
				selectionModel.getSelectedObject();
			}
		});

		Column<ActivityData, ActivityData> cancelColumn = new Column<ActivityData, ActivityData>(new CancelButtonCell<ActivityData>(new Delegate<ActivityData>() {
			@Override
			public void execute(ActivityData value) {
				if (value != null) {
					dataGrid.getSelectionModel().setSelected(value, false);
					getDialog(value).show();
				}
			}
		}, "delete virtual machine")) {
			@Override
			public ActivityData getValue(ActivityData object) {
				return object;
			}
		};
		cancelColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		dataGrid.addColumn(cancelColumn);
		dataGrid.setColumnWidth(cancelColumn, "50px");

		// CALL onModuleLoad()
		chartPanel = get();
		chartPanel.add(table);
		chartPanel.add(new SectionPanel("History"));
		chartPanel.add(historyTable);
		chartPanel.add(new SectionPanel("Active Machines"));
		chartPanel.add(vsTable);
		chartPanel = get();
		setChartTableData();
		onModuleLoad();
	}

	public void onModuleLoad() {
		Runnable onLoadCallback = new Runnable() {
			public void run() {
				chartPanel = get();
				setChartTableData();
				if (historyTable.isCellPresent(2, 0))
					historyTable.clearCell(2, 0);
				historyTable.setWidget(2, 0, chart);
				historyTable.setWidget(2, 0, new LineChart(createTable(), createOptions(chartTitle)));
				historyTable.getCellFormatter().setHorizontalAlignment(2, 0, HasHorizontalAlignment.ALIGN_CENTER);

				vsTable.setWidget(1, 0, dataGrid);
				vsTable.getCellFormatter().setHorizontalAlignment(1, 0, HasHorizontalAlignment.ALIGN_CENTER);
			}
		};
		VisualizationUtils.loadVisualizationApi(onLoadCallback, LineChart.PACKAGE);
	}
	public void setTableData() {
		table.setCellSpacing(8);
		table.setTitle("HP Cloud account information");
		if (account != null) {
			HTML name = new InlineHTML(account.getName());
			name.addStyleName(N3phele.n3pheleResource.css().commandDetailHeader());
			table.setHTML(0, 0, "" + name);
			HTML description = new InlineHTML(account.getDescription());
			description.addStyleName(N3phele.n3pheleResource.css().commandDetailText());
			table.setHTML(0, 1, "" + description);
			HTML cloudName = new InlineHTML("" + account.getCloudName());
			cloudName.addStyleName(N3phele.n3pheleResource.css().commandDetailText());
			table.setHTML(1, 0, "" + cloudName);
		} else {
			HTML name = new InlineHTML("");
			name.addStyleName(N3phele.n3pheleResource.css().commandDetailHeader());
			table.setHTML(0, 0, "" + name);
			HTML description = new InlineHTML("");
			description.addStyleName(N3phele.n3pheleResource.css().commandDetailText());
			table.setHTML(0, 1, "" + description);
			HTML cloudName = new InlineHTML("");
			cloudName.addStyleName(N3phele.n3pheleResource.css().commandDetailText());
			table.setHTML(1, 0, "" + cloudName);
		}
		table.setWidget(2, 0, errorsOnPage);
	}

	public void setDisplayList(List<ActivityData> list) {
		if (list == null)
			list = new ArrayList<ActivityData>();
		setTableData();
		vsData = list;
		this.dataGrid.setRowCount(list.size(), true);
		this.dataGrid.setRowData(vsData = list);
	}

	public void setPresenter(AccountHyperlinkActivity accountHyperlinkActivity) {
		this.presenter = accountHyperlinkActivity;

	}

	public void updateActivity(Activity activity) {
		// this.activity = activity;
	}

	public void refresh(List<ActivityData> newList, HashMap<ActivityData, Activity> activityPerVS) {
		setDisplayList(newList);
		refreshChart();
	}

	public void refresh(int i, String update) {
		this.dataGrid.setRowData(i, vsData.subList(i, i + 1));
	}

	public void refreshAccount(Account update) {
		setData(update);
	}

	protected ActionDialogBox<ActivityData> getDialog(ActivityData item) {
		if (dialog == null) {
			dialog = new ActionDialogBox<ActivityData>("VirtualMachine Removal Confirmation", "No", "Yes", new Delegate<ActivityData>() {

				@Override
				public void execute(ActivityData object) {
					kill(object.getUriTopLevel());

				}
			});
			dialog.setGlassEnabled(true);
			dialog.setAnimationEnabled(false);

		}
		dialog.setValue("Remove virtual machine \"" + item.getName() + "\"?<p>", item);
		dialog.center();
		return dialog;
	}

	private void kill(String uri) {
		String url = uri;
		// Send request to server and catch any errors.
		RequestBuilder builder = AuthenticatedRequestFactory.newRequest(RequestBuilder.DELETE, url);
		builder.setHeader("account", account.getUri().substring(account.getUri().lastIndexOf('/') + 1, account.getUri().length()));
		try {
			@SuppressWarnings("unused")
			Request request = builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					Window.alert("Couldn't delete " + exception.getMessage());
				}

				public void onResponseReceived(Request request, Response response) {
					if (204 == response.getStatusCode()) {
						if (AccountHyperlinkView.this.presenter != null){
							presenter.initTimerDelete();
						}
							//presenter.callGetTopLevel();
							//AccountHyperlinkView.this.presenter.getVSList();
					} else {
						Window.alert("Couldn't delete (" + response.getStatusText() + ")");
					}
				}
			});
		} catch (RequestException e) {
			Window.alert("Couldn't delete " + e.getMessage());

		}
	}

	public void requestChartData(String time) {
		this.presenter.getChartData(time);
	}

	public void setChartData(List<Double> valuesList) {
		if (valuesList == null || valuesList.size() == 0)
			chartValues = null;
		else
			chartValues = valuesList;
	}

	public void setData(Account account) {
		this.account = account;
		setTableData();
	}

	private AccountHyperlinkView get() {
		return this;
	}

	private void setChartTableData() {

		ChangeHandler dropBoxEvent = new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				if (options.isItemSelected(0)) {
					costOption = "normal";
					if (historyTable.isCellPresent(2, 0))
						historyTable.clearCell(2, 0);
					chart = new LineChart(createTable(), createOptions(chartTitle));
					historyTable.setWidget(2, 0, chart);
				} else {
					costOption = "cumulative";
					if (historyTable.isCellPresent(2, 0))
						historyTable.clearCell(2, 0);
					chart = new LineChart(createTable(), createOptions(chartTitle));
					historyTable.setWidget(2, 0, chart);
				}
			}
		};

		vsTable.getColumnFormatter().setWidth(0, chartPanel.getOffsetWidth() + "px");

		historyTable.getColumnFormatter().setWidth(0, chartPanel.getOffsetWidth() + "px");
		HorizontalPanel chartOptionsTable = new HorizontalPanel();
		if (options.getItemCount() < 2) {
			options.insertItem("Cost", 0);
			options.insertItem("Cumulative Cost", 1);
		}
		options.setWidth("126px");
		options.addChangeHandler(dropBoxEvent);
		chartOptionsTable.add(options);
		chartOptionsTable.setCellWidth(options, "160px");
		hours = new Button("24 hours", new ClickHandler() {
			public void onClick(ClickEvent event) {
				requestChartData("24hours");
				chartTitle = "24 Hours Costs Chart";
				if (historyTable.isCellPresent(2, 0))
					historyTable.clearCell(2, 0);
				chart = new LineChart(createTable(), createOptions(chartTitle));
				historyTable.setWidget(2, 0, chart);
			}
		});
		hours.setWidth("70px");
		chartOptionsTable.add(hours);
		days = new Button("7 days", new ClickHandler() {
			public void onClick(ClickEvent event) {
				// getProcessByDay(7);
				requestChartData("7days");
				chartTitle = "7 Days Costs Chart";
				if (historyTable.isCellPresent(2, 0))
					historyTable.clearCell(2, 0);
				chart = new LineChart(createTable(), createOptions(chartTitle));
				historyTable.setWidget(2, 0, chart);

			}
		});
		days.setWidth("70px");
		chartOptionsTable.add(days);
		month = new Button("30 days", new ClickHandler() {
			public void onClick(ClickEvent event) {
				// getProcessByDay(30);
				requestChartData("30days");
				chartTitle = "30 Days Costs Chart";
				if (historyTable.isCellPresent(2, 0))
					historyTable.clearCell(2, 0);
				chart = new LineChart(createTable(), createOptions(chartTitle));
				historyTable.setWidget(2, 0, chart);

			}
		});
		month.setWidth("70px");
		chartOptionsTable.add(month);
		historyTable.setWidget(1, 0, chartOptionsTable);
		historyTable.getCellFormatter().setHorizontalAlignment(1, 0, HasHorizontalAlignment.ALIGN_CENTER);
		if (historyTable.isCellPresent(2, 0))
			historyTable.clearCell(2, 0);
		historyTable.setWidget(2, 0, chart);
		historyTable.getCellFormatter().setHorizontalAlignment(2, 0, HasHorizontalAlignment.ALIGN_CENTER);

		vsTable.setWidget(1, 0, dataGrid);
		vsTable.getCellFormatter().setHorizontalAlignment(1, 0, HasHorizontalAlignment.ALIGN_CENTER);
	}

	public void updateChartTable() {
		Options options = null;
		setChartTableData();
		if(chartValues == null) return;
		switch (chartValues.size()) {
		case 24:
			chartTitle = "24 Hours Costs Chart";
			options = createOptions("24 Hours Costs Chart");
			break;
		case 30:
			chartTitle = "30 Days Costs Chart";
			options = createOptions("30 Days Costs Chart");
			break;
		case 7:
			chartTitle = "7 Days Costs Chart";
			options = createOptions("7 Days Costs Chart");
			break;
		}
		if (historyTable.isCellPresent(2, 0))
			historyTable.clearCell(2, 0);
		chart = new LineChart(createTable(), createOptions(chartTitle));
		historyTable.setWidget(2, 0, chart);
		AbstractDataTable data = createTable();
		chart.draw(data, options);
	}

	private LineChart.Options createOptions(String time) {
		Options options = Options.create();
		options.setWidth(460);
		options.setHeight(180);
		options.setTitle(time);
		options.setLegend(LegendPosition.NONE);
		options.setTitleFontSize(13.0);
		double max = maxValue();
		double min = minValue(maxValue());
		options.setMax((int) max + 0.1);
		options.setMin((int) min);
		options.setPointSize(2);
		options.setAxisFontSize(12.0);
		return options;
	}

	private AbstractDataTable createTable() {
		DataTable data = DataTable.create();
		data.addColumn(ColumnType.STRING, "Time");
		data.addColumn(ColumnType.NUMBER, "Cost");
		Date date = new Date();
		if (chartValues != null) {
			if (chartValues.size() == 24) {
				if (chartValues != null) {
					int time = 0;
					if ((date.getHours() + 1) - 23 < 0)
						time = date.getHours() + 1;
					else
						time = date.getHours() - 23;
					if (costOption.equals("cumulative")) {
						double value = 0.0;

						for (int i = 0; i < chartValues.size(); i++) {
							value += chartValues.get(i);
							data.addRow();
							data.setValue(i, 0, time + "h");
							data.setValue(i, 1, value);
							time++;
							if (time == 24)
								time = 0;
						}
					} else {
						for (int i = 0; i < chartValues.size(); i++) {
							data.addRow();
							data.setValue(i, 0, time + "h");
							data.setValue(i, 1, chartValues.get(i));
							time++;
							if (time == 24)
								time = 0;
						}
					}
				}
			} else if (chartValues.size() == 7) {
				String[] month = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
				if (chartValues != null) {
					CalendarUtil.addDaysToDate(date, -6);
					if (costOption.equals("cumulative")) {
						double value = 0.0;
						for (int i = 0; i < chartValues.size(); i++) {

							value += chartValues.get(i);
							data.addRow();
							data.setValue(i, 0, "" + month[date.getMonth()] +" "+ date.getDate());
							data.setValue(i, 1, value);
							CalendarUtil.addDaysToDate(date, 1);
						}
					} else {
						for (int i = 0; i < chartValues.size(); i++) {
							data.addRow();
							data.setValue(i, 0, "" + month[date.getMonth()] +" "+ date.getDate());
							data.setValue(i, 1, chartValues.get(i));
							CalendarUtil.addDaysToDate(date, 1);
						}
					}
				}
			} else {
				String[] month = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
				if (chartValues != null) {
					CalendarUtil.addDaysToDate(date, -29);
					if (costOption.equals("cumulative")) {
						double value = 0.0;
						for (int i = 0; i < chartValues.size(); i++) {
							value += chartValues.get(i);
							data.addRow();
							data.setValue(i, 0, "" + month[date.getMonth()] +" "+ date.getDate());
							data.setValue(i, 1, value);
							CalendarUtil.addDaysToDate(date, 1);
						}
					} else {
						for (int i = 0; i < chartValues.size(); i++) {
							data.addRow();
							data.setValue(i, 0, "" + month[date.getMonth()] +" "+ date.getDate());
							data.setValue(i, 1, chartValues.get(i));
							CalendarUtil.addDaysToDate(date, 1);
						}
					}
				}
			}
		}
		return data;
	}

	public double maxValue() {
		if (chartValues == null)
			return 0.0;
		double max = 0.0;
		for (int i = 0; i < chartValues.size(); i++) {
			double j = chartValues.get(i);
			if (chartValues.get(i) > max)
				max = j;
		}
		return max;
	}

	public double minValue(double maxValue) {
		if (chartValues == null)
			return 0.0;
		double min = maxValue;
		for (int i = 0; i < chartValues.size(); i++) {
			if (chartValues.get(i) < min)
				min = chartValues.get(i);
		}
		return min;
	}

	public void refreshChart() {
		if (chart == null || historyTable == null)
			return;
		requestChartData("24hours");
		if (chartValues == null)
			return;
		if (historyTable.isCellPresent(2, 0))
			historyTable.clearCell(2, 0);
		chart = new LineChart(createTable(), createOptions(chartTitle));
		historyTable.setWidget(2, 0, chart);
	}
}