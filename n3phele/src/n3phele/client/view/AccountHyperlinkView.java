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

import n3phele.client.ClientFactory;
import n3phele.client.N3phele;
import n3phele.client.model.Account;
import n3phele.client.model.Activity;
import n3phele.client.model.Collection;
import n3phele.client.model.FileNode;
import n3phele.client.model.RepoListResponse;
//import n3phele.service.model.CloudProcessSummary;
import n3phele.service.model.CloudProcessCollection;
//import n3phele.service.model.CloudProcessSummary;
//import n3phele.client.model.CloudProcessSummary;
import n3phele.client.model.CloudProcessSummary;
import n3phele.client.presenter.AccountHyperlinkActivity;
import n3phele.client.presenter.CommandActivity;
import n3phele.client.presenter.helpers.AuthenticatedRequestFactory;
import n3phele.client.resource.DataGridResource;
import n3phele.client.widgets.ActionDialogBox;
import n3phele.client.widgets.CancelButtonCell;
import n3phele.client.widgets.MenuItem;
import n3phele.client.widgets.SectionPanel;
import n3phele.client.widgets.ValidInputIndicatorWidget;
import n3phele.client.widgets.WorkspaceVerticalPanel;
import n3phele.service.rest.impl.AccountResource;
import n3phele.service.rest.impl.CloudProcessResource;

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
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Timer;
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
import com.google.gwt.visualization.client.Color3D;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.LegendPosition;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.visualizations.AnnotatedTimeLine;
import com.google.gwt.visualization.client.visualizations.LineChart;
import com.google.gwt.visualization.client.visualizations.LineChart.Options;
import com.google.gwt.user.datepicker.client.CalendarUtil;
//import com.sun.jersey.api.client.Client;
//import com.sun.jersey.api.client.ClientResponse;
//import com.sun.jersey.api.client.WebResource;

@SuppressWarnings("deprecation")
public class AccountHyperlinkView extends WorkspaceVerticalPanel implements EntryPoint {
	private DataGrid<CloudProcessSummary> dataGrid;
	private List<CloudProcessSummary> vsData = null;
	private final FlexTable table;
	private Account account = null;
	private AccountHyperlinkActivity presenter = null;
	private ActionDialogBox<CloudProcessSummary> dialog;
	private final ValidInputIndicatorWidget errorsOnPage;
	private static DataGridResource resource = null;
	private List<Double> chartValues;
	private LineChart chart = null;
	private HashMap<CloudProcessSummary, Activity> activityPerVS = null;
	private Panel chartPanel = null;
	final private FlexTable historyTable = new FlexTable();
	final private FlexTable vsTable = new FlexTable();
	private ListBox options = new ListBox(false);
	private String costOption = "normal";
	private String chartTitle = "24 Hours Costs Chart";
	private Button hours, days, month;
	private boolean semaph;
	private List<CloudProcessSummary> pricesQuerry; 

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
		dataGrid = new DataGrid<CloudProcessSummary>(15, resource);
		dataGrid.setSize("495px", "100px");

		TextColumn<CloudProcessSummary> nameColumn = new TextColumn<CloudProcessSummary>() {
			@Override
			public String getValue(CloudProcessSummary item) {
				String result = "";
				if (item != null) {
					result += item.getName();
				}
				return result;
			}
		};
		dataGrid.addColumn(nameColumn, "Name");
		dataGrid.setColumnWidth(nameColumn, "130px");
		
		//TODO: Working on Activity column
		
		Column<CloudProcessSummary, String> activityColumn = new Column<CloudProcessSummary, String>(new ClickableTextCell()) {
			@Override
			public String getValue(CloudProcessSummary item) {
				String result = "";
				System.out.println(item.getName() + " " + item.getUri());
//				if (item != null) {
//					if (item.getActivity() == null || !(activityPerVS.containsKey(item)) || activityPerVS.get(item) == null) {
//						result += "none";
//					} else {
//						result += activityPerVS.get(item).getName();
//					}
//				}
				System.out.println("CLICKABLE ACTIVITY");

				return "TESTE";
			}

		};
		activityColumn.setFieldUpdater(new FieldUpdater<CloudProcessSummary, String>() {
			@Override
			public void update(int index, CloudProcessSummary obj, String value) {
//				if (presenter != null) {
//					if (obj.getActivity() == null || !(activityPerVS.containsKey(obj)) || activityPerVS.get(obj) == null) {
//						Window.alert(obj.getName() + " has no activities running on n3phele.");
//					} else
//						presenter.onSelect(activityPerVS.get(obj));
//				}
				System.out.println("UPDATE ACTIVITY");
			}
		});
		activityColumn.setCellStyleNames(N3phele.n3pheleResource.css().clickableTextCellEffect());
		dataGrid.addColumn(activityColumn, "Activity");
		dataGrid.setColumnWidth(activityColumn, "100px");

		TextColumn<CloudProcessSummary> ageColumn = new TextColumn<CloudProcessSummary>() {
			@Override
			public String getValue(CloudProcessSummary item) {
				String result = "";
				
				if (item != null) {
					Date now = new Date();
					if (now.before(item.getEpoch())) {
						result += 0;
					} else {
						int minutes = 0;
						int hours = 0;
						int days = 0;

						// MINUTES
						if (now.getMinutes() < item.getEpoch().getMinutes())
							minutes = 60 + now.getMinutes() - item.getEpoch().getMinutes();
						else
							minutes = now.getMinutes() - item.getEpoch().getMinutes();

						// HOURS
						if (now.getHours() > item.getEpoch().getHours()) {
							hours += now.getHours() - item.getEpoch().getHours();
							if (now.getMinutes() - item.getEpoch().getMinutes() < 0)
								hours--;
						} else if (now.getHours() < item.getEpoch().getHours())
							hours += 24 - (item.getEpoch().getHours() - now.getHours());

						// DAYS
						days = (int) ((now.getTime() - item.getEpoch().getTime()) / (1000 * 60 * 60 * 24));

						if (days == 0) {
							int hoursDifference = 0;
							if (now.getHours() >= item.getEpoch().getHours())
								hoursDifference = now.getHours() - item.getEpoch().getHours();
							else
								hoursDifference = 24 - (item.getEpoch().getHours() - now.getHours());
							int minutesDifference = now.getMinutes() - item.getEpoch().getMinutes();
							if (hoursDifference == 0 || (hoursDifference == 1 && minutesDifference < 0)) {
								result += minutes + "min";
							} else {
								result += hours + "h " + minutes + "min";
							}
						} else {
							if (hours == 0 && minutes == 0)
								result += days + "d";
							else if (hours == 0 && minutes > 0)
								result += days + "d " + minutes + "min";
							else if (hours > 0 && minutes == 0)
								result += days + "d " + hours + "h";
							else
								result += days + "d " + hours + "h " + minutes + "min";
						}
					}
				}
				return result;
			}
		};
		dataGrid.addColumn(ageColumn, "Age");
		dataGrid.setColumnWidth(ageColumn, "80px");

		TextColumn<CloudProcessSummary> priceColumn = new TextColumn<CloudProcessSummary>() {
			@Override
			public String getValue(CloudProcessSummary item) {
				String result = "";
				if (item != null) {
					double price = item.getCost();
					Date now = new Date();
					if (now.before(item.getEpoch())) {
						result += 0;
					} else if (item.getComplete() == null) {
						Date test = new Date();
						int hours = (int) (((now.getTime() - item.getEpoch().getTime()) / (1000 * 60 * 60 * 24)) * 24);
						if (now.getHours() >= item.getEpoch().getHours()) {
							hours += now.getHours() - item.getEpoch().getHours() + 1;
						} else {
							hours += 24 - (item.getEpoch().getHours() - now.getHours()) + 1;
						}
						if (now.getMinutes() - item.getEpoch().getMinutes() < 0) {
							hours--;
						}
						double total = price * hours;
						result += "US$" + (double) Math.round(total * 1000) / 1000;
					}
				}
				return result;
			}
		};
		dataGrid.addColumn(priceColumn, "Total Cost");
		dataGrid.setColumnWidth(priceColumn, "75px");

		// Add a selection model to handle user selection.
		final SingleSelectionModel<CloudProcessSummary> selectionModel = new SingleSelectionModel<CloudProcessSummary>();
		dataGrid.setSelectionModel(selectionModel);
		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			public void onSelectionChange(SelectionChangeEvent event) {
				selectionModel.getSelectedObject();
			}
		});

		Column<CloudProcessSummary, CloudProcessSummary> cancelColumn = new Column<CloudProcessSummary, CloudProcessSummary>(new CancelButtonCell<CloudProcessSummary>(new Delegate<CloudProcessSummary>() {
			@Override
			public void execute(CloudProcessSummary value) {
				if (value != null) {
					dataGrid.getSelectionModel().setSelected(value, false);
					getDialog(value).show();
				}
			}
		}, "delete virtual machine")) {
			@Override
			public CloudProcessSummary getValue(CloudProcessSummary object) {
				return object;
			}
		};
		cancelColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		dataGrid.addColumn(cancelColumn);
		dataGrid.setColumnWidth(cancelColumn, "50px");

		// CALL onModuleLoad()
		onModuleLoad();
	}

	public void onModuleLoad() {
		Runnable onLoadCallback = new Runnable() {
			public void run() {
				chartPanel = get();

				createOptions(chartTitle);

				setChartTableData();

				chartPanel.add(table);
				chartPanel.add(new SectionPanel("History"));
				chartPanel.add(historyTable);
				chartPanel.add(new SectionPanel("Active Machines"));
				chartPanel.add(vsTable);
				requestChartData("24hours");

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

	public void setDisplayList(List<CloudProcessSummary> list) {
		if (list == null)
			list = new ArrayList<CloudProcessSummary>();
		setTableData();
		CloudProcessSummary vs;
		System.out.println("CALLED!");
		vsData = list;
		this.dataGrid.setRowCount(list.size(), true);
		this.dataGrid.setRowData(vsData = list);
	}

	public void setPresenter(AccountHyperlinkActivity accountHyperlinkActivity) {
		this.presenter = accountHyperlinkActivity;

	}

	public void updateActivity(Activity activity) {
		//this.activity = activity;
	}

	public void refresh(List<CloudProcessSummary> newList, HashMap<CloudProcessSummary, Activity> activityPerVS) {
		this.activityPerVS = activityPerVS;
		setDisplayList(newList);
		refreshChart();
	}

	public void refresh(int i, String update) {
		this.dataGrid.setRowData(i, vsData.subList(i, i + 1));
	}

	public void refreshAccount(Account update) {
		setData(update);
	}

	protected ActionDialogBox<CloudProcessSummary> getDialog(CloudProcessSummary item) {
		if (dialog == null) {
			dialog = new ActionDialogBox<CloudProcessSummary>("VirtualMachine Removal Confirmation", "No", "Yes", new Delegate<CloudProcessSummary>() {

				@Override
				public void execute(CloudProcessSummary object) {
					kill(object.getUri().toString());

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
						if (AccountHyperlinkView.this.presenter != null)
							AccountHyperlinkView.this.presenter.getVSList();
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
		options.insertItem("Cost", 0);
		options.insertItem("Cumulative Cost", 1);
		options.setWidth("126px");
		options.addChangeHandler(dropBoxEvent);
		chartOptionsTable.add(options);
		chartOptionsTable.setCellWidth(options, "160px");
		hours = new Button("24 hours", new ClickHandler() {
			public void onClick(ClickEvent event) {
				//getProcessByDay(1);
				requestChartData("24hours");
				chartTitle = "24 Hours Costs Chart";
				if (historyTable.isCellPresent(2, 0))
					historyTable.clearCell(2, 0);
				chart = new LineChart(createTable(), createOptions(chartTitle));
				historyTable.setWidget(2, 0, chart);
				//setChartData(getCost24h());
//				AbstractDataTable data = createTable();
//				Options options = createOptions("24 hours");
//				chart.draw(data, options);
			}
		});
		hours.setWidth("70px");
		chartOptionsTable.add(hours);
		days = new Button("7 days", new ClickHandler() {
			public void onClick(ClickEvent event) {
				//getProcessByDay(7);
				semaph = false;
				requestChartData("7days");
				//while(!semaph){}
				
				Timer togglebuttonTimer = new Timer() { 
				    public void run() { 
				        
				    } 
				}; 
				togglebuttonTimer.schedule(2000); 
				System.out.println("ESCAPOU DO SEMAFORO!");
				chartTitle = "7 Days Costs Chart";
				if (historyTable.isCellPresent(2, 0))
					historyTable.clearCell(2, 0);
				chart = new LineChart(createTable(), createOptions(chartTitle));
				historyTable.setWidget(2, 0, chart);
				//setChartData(getCost7());
//				AbstractDataTable data = createTable();
//				Options options = createOptions("7 days");
//				
//				chart.draw(data, options);
				
			}
		});
		days.setWidth("70px");
		chartOptionsTable.add(days);
		month = new Button("30 days", new ClickHandler() {
			public void onClick(ClickEvent event) {
				//getProcessByDay(30);
				requestChartData("30days");
				chartTitle = "30 Days Costs Chart";
				if (historyTable.isCellPresent(2, 0))
					historyTable.clearCell(2, 0);
				chart = new LineChart(createTable(), createOptions(chartTitle));
				historyTable.setWidget(2, 0, chart);
				//setChartData(getCost30());
//				AbstractDataTable data = createTable();
//				Options options = createOptions("30 days");
//				chart.draw(data, options);
				
			}
		});
		month.setWidth("70px");
		chartOptionsTable.add(month);
		historyTable.setWidget(1, 0, chartOptionsTable);
		historyTable.getCellFormatter().setHorizontalAlignment(1, 0, HasHorizontalAlignment.ALIGN_CENTER);

		//requestChartData("24hours");
		if (historyTable.isCellPresent(2, 0))
			historyTable.clearCell(2, 0);
		//chart = new LineChart(createTable(), createOptions(chartTitle));
		historyTable.setWidget(2, 0, chart);
		historyTable.setWidget(2, 0, new LineChart(createTable(), createOptions(chartTitle)));
		historyTable.getCellFormatter().setHorizontalAlignment(2, 0, HasHorizontalAlignment.ALIGN_CENTER);

		vsTable.setWidget(1, 0, dataGrid);
		vsTable.getCellFormatter().setHorizontalAlignment(1, 0, HasHorizontalAlignment.ALIGN_CENTER);

	}
	public void updateChartTable(){
		Options options = null;
		/*
		 * FIXME 
		 * chartValues might not have been initialized
		 */
		switch(chartValues.size()){
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
		options.setMax((int) max + 1);
		options.setMin((int) min);
		options.setPointSize(2);
		options.setAxisFontSize(11.0);
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
							data.setValue(i, 0, "" + month[date.getMonth()] + date.getDate());
							data.setValue(i, 1, value);
							CalendarUtil.addDaysToDate(date, 1);
						}
					} else {
						for (int i = 0; i < chartValues.size(); i++) {
							data.addRow();
							data.setValue(i, 0, "" + month[date.getMonth()] + date.getDate());
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
							data.setValue(i, 0, "" + month[date.getMonth()] + date.getDate());
							data.setValue(i, 1, value);
							CalendarUtil.addDaysToDate(date, 1);
						}
					} else {
						for (int i = 0; i < chartValues.size(); i++) {
							data.addRow();
							data.setValue(i, 0, "" + month[date.getMonth()] + date.getDate());
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