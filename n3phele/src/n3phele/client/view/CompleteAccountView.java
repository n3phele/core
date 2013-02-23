/**
 * 
 * 
 */

package n3phele.client.view;

import n3phele.client.N3phele;
import n3phele.client.model.Account;
import n3phele.client.presenter.AccountActivity;
import n3phele.client.widgets.MenuItem;
import n3phele.client.widgets.WorkspaceVerticalPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

public class CompleteAccountView extends WorkspaceVerticalPanel {
	
	final private FlexTable table;
	private Account account;
	private AccountActivity presenter;
	
	public CompleteAccountView() {
		super(new MenuItem(N3phele.n3pheleResource.accountIcon(), "Cloud Account", null));
		
		table = new FlexTable();
		table.setCellPadding(10);
		
		Label name = new Label(account.getName());
		table.setWidget(0, 0, name);
		
		Label cloudName = new Label("Cloud Name");
		table.setWidget(0, 1, cloudName);
		
		Label cloudDescription = new Label("Cloud Description bla bla bla bla bla bla");
		table.setWidget(1, 0, cloudDescription);
		
		this.add(table);
		this.add(new Label("Teste"));
		this.add(new Label("Teste2"));
		
	}
	
	public void setData(Account account) {
		this.account = account;
	}
	
	public void setPresenter(AccountActivity presenter) {
		this.presenter = presenter;
	}
}