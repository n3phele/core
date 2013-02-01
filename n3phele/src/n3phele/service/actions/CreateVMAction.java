package n3phele.service.actions;

import java.util.HashMap;
import java.util.Map;

import com.googlecode.objectify.annotation.Embed;

import n3phele.service.core.NotFoundException;
import n3phele.service.model.Action;
import n3phele.service.model.FileTracker;
import n3phele.service.model.SignalKind;

public class CreateVMAction extends Action {
	
	@Embed private HashMap<String,FileTracker> fileTable = new HashMap<String,FileTracker>();

	@Override
	public void init() throws Exception {
	
	}
	
	
	@Override
	public boolean call() throws n3phele.service.lifecycle.ProcessLifecycle.WaitForSignalRequest, Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dump() {
		// TODO Auto-generated method stub

	}

	@Override
	public void signal(SignalKind kind, String assertion) {
		// TODO Auto-generated method stub

	}


	public void killVM() throws NotFoundException {
		throw new NotFoundException();
		
	}


	/**
	 * @return the fileTable
	 */
	public Map<String, FileTracker> getFileTable() {
		return fileTable;
	}


	/**
	 * @param fileTable the fileTable to set
	 */
	public void setFileTable(Map<String, FileTracker> fileTable) {
		this.fileTable.clear();
		this.fileTable.putAll(fileTable);
	}

}
