/**
 * @author Nigel Cook
 * @author Leonardo Amado
 * @author Douglas Tondin
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import n3phele.client.N3phele;
import n3phele.client.model.Account;
import n3phele.client.model.Cloud;
import n3phele.client.model.CommandCloudAccount;
import n3phele.client.model.User;
import n3phele.client.model.Variable;
import n3phele.client.presenter.AccountActivity;
import n3phele.client.presenter.CreateServiceActivity;
import n3phele.client.widgets.MenuItem;
import n3phele.client.widgets.SectionPanel;
import n3phele.client.widgets.SensitiveCheckBoxCell;
import n3phele.client.widgets.ValidInputIndicatorCell;
import n3phele.client.widgets.ValidInputIndicatorWidget;
import n3phele.client.widgets.WorkspaceVerticalPanel;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.CellWidget;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.view.client.ProvidesKey;

public class CreateServiceView extends WorkspaceVerticalPanel {
	final private FlexTable table;
	private Account account;
	private TextBox name;
	private TextBox description;
	private TextBox cloudId;
	private TextBox secret;
	//private PasswordTextBox confirmPassword;
	private Button cancel;
	private Button run;
	private CreateServiceActivity presenter;
	private final ListBox cloud = new ListBox(false);
	private final Map<String,Integer> cloudMap = new HashMap<String,Integer>();
	private final List<String> uriMap = new ArrayList<String>();
	private final ValidInputIndicatorWidget nameValid;
	private final ValidInputIndicatorWidget cloudSelected;
	private final ValidInputIndicatorWidget gotCloudId;
	private final ValidInputIndicatorWidget secretTextSupplied;
	//private final ValidInputIndicatorWidget passwordConfirmSupplied;
	private final ValidInputIndicatorWidget errorsOnPage;
	final private SectionPanel clouds;
	private String selectedImplementation = null;
	private CellWidget<String> gotExecutionSelection;
	private String selectedAccountURI = null;
	final private CellTable<CommandCloudAccount> accountTable;
	final private List<CommandCloudAccount> accounts = new ArrayList<CommandCloudAccount>();
	
	public CreateServiceView() {
		super(new MenuItem(N3phele.n3pheleResource.serviceIcon(), "Create Service", null));
		table = new FlexTable();
		table.setCellPadding(10);
		
		ChangeHandler update = new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				validateAccount(true);
				
			}};
			
		KeyUpHandler keyup = new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				validateAccount(true);
			}

			};
		
		this.account = null;
		Label lblNewLabel = new Label("Service Name");
		table.setWidget(0, 0, lblNewLabel);
		nameValid = new ValidInputIndicatorWidget("Text value required", false);
		table.setWidget(0, 1, nameValid);
		
		name = new TextBox();
		name.setVisibleLength(40);
		name.addChangeHandler(update);
		name.addKeyUpHandler(keyup);
		table.setWidget(0, 2, name);
		
		
		//accounts
		clouds = new SectionPanel("Execute On");
		HorizontalPanel divider = new HorizontalPanel();
		divider.setWidth("100%");
		clouds.add(divider);
		gotExecutionSelection = new CellWidget<String>(new ValidInputIndicatorCell(N3phele.n3pheleResource.inputErrorIcon()),"+Execution target selection required");
		gotExecutionSelection.setPixelSize(20, 20);
		divider.add(gotExecutionSelection);
		divider.setCellVerticalAlignment(gotExecutionSelection, HorizontalPanel.ALIGN_MIDDLE);
		divider.setCellWidth(gotExecutionSelection, "20px");
		
		
		accountTable = createAccountTable();
		divider.add(accountTable);
		this.add(clouds);
		
		
		
		secretTextSupplied = new ValidInputIndicatorWidget("Secret text required", false);

		
		gotCloudId = new ValidInputIndicatorWidget("Cloud id required", false);
		cloudSelected = new ValidInputIndicatorWidget("Cloud selection required", false);

		
		cancel = new Button("cancel",  new ClickHandler() {
          public void onClick(ClickEvent event) {
            do_cancel();
          }
        });

		table.setWidget(1, 2, cancel);
			
		run = new Button("create",  new ClickHandler() {
	          public void onClick(ClickEvent event) {
	              do_run();
	            }
	          });
		table.setWidget(1, 0, run);
		
		
		errorsOnPage = new ValidInputIndicatorWidget("check for missing or invalid parameters marked with this icon", false);
		table.setWidget(0, 3, errorsOnPage);
		table.getFlexCellFormatter().setHorizontalAlignment(1, 2, HasHorizontalAlignment.ALIGN_RIGHT);
		table.getFlexCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_RIGHT);
		table.getFlexCellFormatter().setHorizontalAlignment(1, 0, HasHorizontalAlignment.ALIGN_CENTER);
		

		table.getColumnFormatter().setWidth(0, "25%");
		table.getColumnFormatter().setWidth(1, "18px");
		table.getColumnFormatter().setWidth(4, "16px");
		table.setCellPadding(1);
		table.setCellSpacing(5);
		this.add(table);
		validateAccount(true);
		
	}
	
	public void do_run() {
		n3phele.client.model.Context context = new n3phele.client.model.Context();
		context.put("account", Variable.newInstance("account", "Object", getSelectedAccount()));
        presenter.exec(name.getText(), context);
	}
	public void do_cancel() {
		setData(null);
		this.presenter.goToPrevious();
	}
	
	public void setData(Account account) {
		this.account = account;
		if(account != null) {
			name.setText(account.getName());
			if(cloudMap.containsKey(account.getCloud())) {
				cloud.setSelectedIndex(cloudMap.get(account.getCloud()));
			} else {
				if(cloudMap.isEmpty() || (account.getCloud()!=null && account.getCloud().length()!=0))
					cloud.setSelectedIndex(-1);
				else
					cloud.setSelectedIndex(0);
			}
			validateAccount(true);
		} else {
			name.setText("");
			validateAccount(true);
		}
	}

	public void setPresenter(CreateServiceActivity presenter) {
		this.presenter = presenter;
	}

	
	public void setClouds(List<Cloud> list) {
		uriMap.clear();
		cloudMap.clear();
		cloud.clear();
		if(list != null) {
			int i=0;
			for(Cloud c : list) {
				uriMap.add(c.getUri());
				cloudMap.put(c.getUri(),i++);
				cloud.addItem(c.getName());
			}
			if(account != null && cloudMap.containsKey(account.getCloud())) {
				cloud.setSelectedIndex(cloudMap.get(account.getCloud()));
			} else {
				cloud.setSelectedIndex(-1);
			}
		}
		validateAccount(true);
	}

	private boolean validateAccount(boolean isValid) {
		boolean nameValid = (name.getText()!= null && name.getText().length()!=0);
		this.nameValid.setVisible(!nameValid);
		isValid = isValid && nameValid;

		if(selectedImplementation == null){
			isValid = false;
		}
		if(!nameValid) {
			this.nameValid.setVisible(true);
			isValid = false; 
		}
		this.errorsOnPage.setVisible(!isValid);
		this.run.setEnabled(isValid);
		return isValid;
	}
	// Account table
	
	public CellTable<CommandCloudAccount> createAccountTable() {
		final SensitiveCheckBoxCell checkbox = new SensitiveCheckBoxCell(true, true);
		final ProvidesKey<CommandCloudAccount> KEY_PROVIDER = new ProvidesKey<CommandCloudAccount>() {

		    public Object getKey(CommandCloudAccount item) {
		      return item.getAccountUri();
		    }
		  };
		final CellTable<CommandCloudAccount> table = new CellTable<CommandCloudAccount>(KEY_PROVIDER);
		table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
		Column<CommandCloudAccount, Boolean> checkColumn = new Column<CommandCloudAccount, Boolean>(checkbox)
		{
			@Override
		      public Boolean getValue(CommandCloudAccount profile) {
				return (profile.getAccountUri().equals(CreateServiceView.this.selectedAccountURI));
		      }

		};
		checkColumn.setFieldUpdater(new FieldUpdater<CommandCloudAccount, Boolean>() {
			
			@Override
			public void update(int index, CommandCloudAccount profile,
					Boolean value) {
				if(profile != null) {
					if(value) {
						CreateServiceView.this.selectedImplementation = profile.getImplementation();
						CreateServiceView.this.selectedAccountURI = profile.getAccountUri().toString();
					} else {
						if(profile.getImplementation().equals(CreateServiceView.this.selectedImplementation)) {
							
							CreateServiceView.this.selectedImplementation = null;
							CreateServiceView.this.selectedAccountURI = null;
						}
					}
					table.redraw();
					String visible = value?"-":"+";
					CreateServiceView.this.gotExecutionSelection.setValue(visible+CreateServiceView.this.gotExecutionSelection.getValue().substring(1));
					updateRunButton(true);
					validateAccount(true);
				} else {
					checkbox.clearViewData(KEY_PROVIDER.getKey(profile));
					table.redraw();
					updateRunButton(false);
					validateAccount(false);
					GWT.log("update account");
					CreateServiceView.this.gotExecutionSelection.setValue("+"+CreateServiceView.this.gotExecutionSelection.getValue().substring(1));
				}
				
			}
			
		});
		table.addColumn(checkColumn);
		table.setColumnWidth(checkColumn, "40px");
		TextColumn<CommandCloudAccount> accountColumn = new TextColumn<CommandCloudAccount>() {
			@Override
			public String getValue(CommandCloudAccount profile) {
				String result = "";
				if(profile != null)
					return profile.getAccountName();
				return result;
			}
		};
		table.addColumn(accountColumn);
		TextColumn<CommandCloudAccount> nameColumn = new TextColumn<CommandCloudAccount>() {
			@Override
			public String getValue(CommandCloudAccount profile) {
				String result = "";
				if(profile != null) {
					return profile.getImplementation();
				}
				return result;
			}
		};
		table.addColumn(nameColumn);
		table.setWidth("400px");
		table.addStyleName(N3phele.n3pheleResource.css().lineBreakStyle());
		table.setTableLayoutFixed(true);
		return table;
	}
	public void updateRunButton(boolean allValid) {
		GWT.log("update run "+allValid);
		if(allValid) {
			allValid = getSelectedAccount() != null;
			GWT.log("update run4 "+allValid);
		
		} else {
			allValid = false;
		}
		GWT.log("update run final "+allValid);
		this.run.setEnabled(allValid);
	}
	
	private String getSelectedAccount() {
		return CreateServiceView.this.selectedAccountURI;
	}
	
	public void setCloudAccounts(List<CommandCloudAccount> accounts) {
		this.accounts.clear();
		this.accounts.addAll(accounts);
		accountTable.setRowCount(this.accounts.size());
		accountTable.setRowData(this.accounts);
		accountTable.setVisible(this.accounts.size() > 0);
		
		if(CreateServiceView.this.selectedImplementation!=null) {
			String accountURI = CreateServiceView.this.selectedAccountURI;
			setSelectedImplementation(accountURI);
		} else {
			String visible;
			if(accounts != null && accounts.size()==1) {
				setSelectedImplementation(accounts.get(0).getAccountUri());
			} else {
				visible = "+";
				CreateServiceView.this.gotExecutionSelection.setValue(visible+CreateServiceView.this.gotExecutionSelection.getValue().substring(1));
			}
		}
	}
	
	/**
	 * @param implementationId
	 */
	public void setSelectedImplementation(String accountURI) {
		CreateServiceView.this.selectedAccountURI = accountURI;
		if(this.accounts != null && this.accounts.size() > 0) {
			for(CommandCloudAccount ep : this.accounts) {
				if(ep.getAccountUri().equals(accountURI)) {
					CreateServiceView.this.selectedImplementation = ep.getImplementation();
					this.accountTable.redraw();
					String visible = "-";
					CreateServiceView.this.gotExecutionSelection.setValue(visible+CreateServiceView.this.gotExecutionSelection.getValue().substring(1));
					CreateServiceView.this.validateAccount(true);
					return;
				}
			}
		}
		CreateServiceView.this.gotExecutionSelection.setValue("+"+CreateServiceView.this.gotExecutionSelection.getValue().substring(1));
	}
}
