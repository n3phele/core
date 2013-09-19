package n3phele.service.model;

import n3phele.service.core.NotFoundException;
import n3phele.service.model.core.AbstractManager;

public abstract class ProcessCachingAbstractManager extends AbstractManager<CloudProcess> {
	/** Add a new item to the persistent data store. The item will be updated with a unique key, as well
	 * the item URI will be updated to include that defined unique team.
	 * @param item to be added
	 * @throws IllegalArgumentException for a null argument
	 */
	@Override
	protected void add(CloudProcess item) throws IllegalArgumentException {
			super.add(item);
			
			log.info("entered add");
			if(item != null) 
			if(item.isTopLevel())
			{
				log.info("added change for collection");
				ChangeManager.factory().addChange(super.path);
			}
			else
			{
				log.info("added change for parent");
				ChangeManager.factory().addChange(item.getParent());				
			}
	}
		
	/** Update a particular object in the persistent data store
	 * @param item the item to update
	 */
	@Override
	protected void update(CloudProcess item)throws NotFoundException {
		try {
			super.update(item);
		} catch (NotFoundException e) {
			ChangeManager.factory().addChange(super.path);
			throw e;
		}
		
		if(item != null) 
		if(item.isTopLevel())
		{
			ChangeManager.factory().addChange(item);
		}
		else
		{
			//add change to parent (?)
			ChangeManager.factory().addChange(item.getParent());
		}
	}
	
	/**
	 * Delete item from the persistent store
	 * @param item to be deleted
	 */
	@Override
	protected void delete(CloudProcess item) {
		if(item != null) {
			super.delete(item);
			if(item.isTopLevel())
			{
				ChangeManager.factory().addChange(super.path);				
			}
			else
			{
				ChangeManager.factory().addChange(item.getParent());				
			}
			ChangeManager.factory().addDelete(item);			
		}
	}
}
