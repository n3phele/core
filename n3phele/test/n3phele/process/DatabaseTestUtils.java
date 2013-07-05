package n3phele.process;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import n3phele.service.actions.JobAction;
import n3phele.service.actions.StackServiceAction;
import n3phele.service.model.Action;
import n3phele.service.model.CloudProcess;
import n3phele.service.model.Context;
import n3phele.service.rest.impl.ActionResource.ActionManager;
import n3phele.service.rest.impl.CloudProcessResource.CloudProcessManager;

public class DatabaseTestUtils {

	public DatabaseTestUtils() {
		super();
	}

	public List<Action> createValidServiceStackActions(int count)
			throws URISyntaxException {
				List<Action> actions = new ArrayList<Action>();
				for(int i=0;i<count;i++)
				{
					actions.add(new StackServiceAction().create(new URI("http://localhost/account/1"), "service", new Context()));
				}
				return actions;
			}

	public List<Action> createValidJobActions(int count) throws URISyntaxException {
		List<Action> actions = new ArrayList<Action>();
		for(int i=0;i<count;i++)
		{
			actions.add(new JobAction().create(new URI("http://localhost/account/1"), "service", new Context()));
		}
		return actions;
	}

	public List<CloudProcess> populateDatabaseWithRandomProcessAndTheseActions(CloudProcessManager manager, ActionManager actionManager, List<Action> actions) 
			throws URISyntaxException {
		
		List<CloudProcess> processes = new ArrayList<CloudProcess>();
		
		for(int i=1; i <= actions.size(); i++)
		{
			CloudProcess process = buildValidCloudProcess("http://127.0.0.1/account/1");
			process.setTopLevel(true);
			process.setAction(actions.get(i-1).getUri());
			manager.add(process);
			actions.get(i-1).setProcess(process.getUri());
			actionManager.update(actions.get(i-1));
			
			processes.add(process);
		}
		
		return processes;
	}
	
	public List<CloudProcess> populateDatabaseWithRandomProcessAndTheseActionsTwoAccounts(CloudProcessManager manager, ActionManager actionManager, List<Action> actions) 
			throws URISyntaxException {
		
		List<CloudProcess> processes = new ArrayList<CloudProcess>();
		
		for(int i=1; i <= actions.size(); i++)
		{
			CloudProcess process = buildValidCloudProcess("http://127.0.0.1/account/1");
			process.setTopLevel(true);
			process.setAction(actions.get(i-1).getUri());
			manager.add(process);
			actions.get(i-1).setProcess(process.getUri());
			actionManager.update(actions.get(i-1));
			
			processes.add(process);
		}
		
		for(int i=1; i <= actions.size(); i++)
		{
			CloudProcess process = buildValidCloudProcess("http://127.0.0.1/account/2");
			process.setTopLevel(true);
			process.setAction(actions.get(i-1).getUri());
			manager.add(process);
			actions.get(i-1).setProcess(process.getUri());
			actionManager.update(actions.get(i-1));
			
			processes.add(process);
		}
		
		return processes;
	}

	public CloudProcess buildValidCloudProcess(String ownerUri)
			throws URISyntaxException {
				CloudProcess process = new CloudProcess();		
				process.setAccount("http://127.0.0.1/account/1");
				process.setCostPerHour(1.0d);
				process.setOwner(new URI(ownerUri));
				return process;	
			}

	public void populateDatabaseWithRandomProcessesNoAction(CloudProcessManager manager,
			int count, String owner) throws URISyntaxException {
				for(int i=1; i <= count; i++)
				{
					CloudProcess process = buildValidCloudProcess(owner);
					process.setTopLevel(true);
					manager.add(process);
				}
			}

	public void populateDatabaseWithRandomProcessesNoAction(CloudProcessManager manager,
			int count) throws URISyntaxException {
				populateDatabaseWithRandomProcessesNoAction(manager, count, "http://127.0.0.1/account/1");
			}

}