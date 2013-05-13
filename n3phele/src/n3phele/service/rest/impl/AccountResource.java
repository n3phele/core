/**
 * @author Nigel Cook
 *
 * (C) Copyright 2010-2011. All rights reserved.
 */
package n3phele.service.rest.impl;

import static com.googlecode.objectify.ObjectifyService.ofy;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import java.text.SimpleDateFormat;

import n3phele.service.actions.CountDownAction;
import n3phele.service.core.NotFoundException;
import n3phele.service.core.Resource;
import n3phele.service.model.Account;
import n3phele.service.model.AccountCollection;
import n3phele.service.model.Action;
import n3phele.service.model.CachingAbstractManager;
import n3phele.service.model.Cloud;
import n3phele.service.model.CloudProcess;
import n3phele.service.model.CloudProcessCollection;
import n3phele.service.model.CostsCollection;
import n3phele.service.model.ServiceModelDao;
import n3phele.service.model.core.Collection;
import n3phele.service.model.core.Credential;
import n3phele.service.model.core.GenericModelDao;
import n3phele.service.model.core.User;

@Path("/account")
public class AccountResource {
	private static Logger log = Logger.getLogger(AccountResource.class.getName());

	@Context
	UriInfo uriInfo;
	@Context
	protected SecurityContext securityContext;

	@GET
	@Produces("application/json")
	@RolesAllowed("authenticated")
	public AccountCollection list(@DefaultValue("false") @QueryParam("summary") Boolean summary) {

		log.warning("list Accounts entered with summary " + summary);

		Collection<Account> result = dao.getAccountList(UserResource.toUser(securityContext), summary);

		return new AccountCollection(result, 0, -1);
	}

	@POST
	@Produces("text/plain")
	@RolesAllowed("authenticated")
	public Response add(@FormParam("name") String name, @FormParam("description") String description, @FormParam("cloud") URI cloud, @FormParam("accountId") String accountId, @FormParam("secret") String secret) {

		Cloud myCloud = CloudResource.dao.load(cloud, UserResource.toUser(securityContext));
		if (name == null || name.trim().length() == 0) {
			throw new IllegalArgumentException("bad name");
		}
		Account account = new Account(name, description, cloud, myCloud.getName(), new Credential(accountId, secret).encrypt(), UserResource.toUser(securityContext).getUri(), false);

		dao.add(account);
		String result = CloudResource.testAccount(myCloud, UserResource.toUser(securityContext), account, true);
		if (result == null || result.trim().length() == 0) {
			log.warning("Created " + account.getUri());
			return Response.created(account.getUri()).build();
		} else {
			log.warning("Created " + account.getUri() + " with warnings " + result);
			return Response.ok(result, MediaType.TEXT_PLAIN_TYPE).location(account.getUri()).build();
		}
	}

	@POST
	@Produces("application/json")
	@Path("{id}")
	@RolesAllowed("authenticated")
	public Account update(@PathParam("id") Long id, @FormParam("name") String name, @FormParam("description") String description, @FormParam("cloud") URI cloud, @FormParam("accountId") String accountId, @FormParam("secret") String secret) {

		Cloud myCloud = CloudResource.dao.load(cloud, UserResource.toUser(securityContext));
		Account item = dao.load(id, UserResource.toUser(securityContext));
		if (name == null || name.trim().length() == 0) {
			throw new IllegalArgumentException("bad name");
		}
		Credential credential = null;
		if (secret != null && secret.trim().length() != 0) {
			credential = new Credential(accountId, secret).encrypt();
		}

		item.setName(name);
		item.setDescription(description == null ? null : description.trim());
		item.setCloud(cloud);
		if (credential != null)
			item.setCredential(credential);
		dao.update(item);
		String result = CloudResource.testAccount(myCloud, UserResource.toUser(securityContext), item, true);

		log.warning("Updated " + item.getUri() + ((credential != null) ? " including credential " + result : ""));
		return item;
	}

	@GET
	@Produces("application/json")
	@RolesAllowed("authenticated")
	@Path("/{account}/lastcompleted/{days:[0-9]+}")
	public CloudProcessCollection listCloudProcessWithCosts(@PathParam("account") String account, @PathParam("days") int days) {

		Collection<CloudProcess> result = dao.getCostsOfAccount(account, days);
		return new CloudProcessCollection(result);
	}

	@GET
	@Produces("application/json")
	// @RolesAllowed("authenticated")
	@Path("/{account}/lastcompleted/{days:[0-9]+}/get")
	public CostsCollection listCostPerDays(@PathParam("account") String account, @PathParam("days") int days) {

		List<CloudProcess> list = dao.getCostsOfAccount(account, days).getElements();
		List<Double> listfinal = new ArrayList<Double>();
		Calendar date = Calendar.getInstance();
		date.set(Calendar.HOUR_OF_DAY, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);
		long today = date.getTimeInMillis();
		
		if (days == 1) {
			for (int i = 0; i < 24; i++) {
				listfinal.add(0.0);
			}
			// only for testing, remove this later
			List<CloudProcess> list2 = list;
//			List<CloudProcess> list2 = new ArrayList<CloudProcess>();
//			for (CloudProcess cloudProcess : list) {
//				if (cloudProcess.getCostPerHour() == 0.5) {
//					list2.add(cloudProcess);
//				}
//			}
			date = Calendar.getInstance();
			date.set(Calendar.MINUTE, 0);
			date.set(Calendar.SECOND, 0);
			date.set(Calendar.MILLISECOND, 0);
			today = date.getTimeInMillis();
			for (CloudProcess cloudProcess : list2) {
				Calendar dateComplete = Calendar.getInstance();
				dateComplete.setTimeInMillis(cloudProcess.getComplete().getTime());
				dateComplete.set(Calendar.MINUTE, 0);
				dateComplete.set(Calendar.SECOND, 0);
				dateComplete.set(Calendar.MILLISECOND, 0);

				Calendar dateStart = Calendar.getInstance();
				dateStart.setTimeInMillis(cloudProcess.getStart().getTime());
				long time = dateComplete.getTimeInMillis();
				dateComplete.setTimeInMillis(cloudProcess.getComplete().getTime());
				long result = today - time;
				String s = today + " - " + time;
				System.out.println("String s:" + s + " = " + result);
				int pos = (int) (result / 1000 / 3600);
				System.out.println("TESTING : " + pos);
				int hoursCharged = (int) Math.floor((dateComplete.getTimeInMillis() - dateStart.getTimeInMillis()) / 3600000);
				double test = hoursCharged;
				if (test != (double) (dateComplete.getTimeInMillis() - dateStart.getTimeInMillis()) / 3600000)
					hoursCharged = hoursCharged + 1;
				System.out.println("!HOURS CHARGED: " + hoursCharged);
				System.out.println(dateStart.getTime() + " " + dateComplete.getTime());
				for (int i = 0; i < hoursCharged ; i++) {
					pos = (int) (result / 1000 / 3600);
					pos = (24 - 1) - pos;
					pos = pos - i;
					System.out.println("I: " + i + "POS :" + pos);
					if (pos >= 0)
						listfinal.set(pos, listfinal.get(pos) + cloudProcess.getCostPerHour());
				}
				
			}
			return new CostsCollection(listfinal);
		}
		for (int i = 0; i < days; i++) {
			listfinal.add(0.0);
		}
		// only for testing, remove this later
		List<CloudProcess> list2 = list;
//		List<CloudProcess> list2 = new ArrayList<CloudProcess>();
//		for (CloudProcess cloudProcess : list) {
//			if (cloudProcess.getCostPerHour() == 2.0) {
//				list2.add(cloudProcess);
//			}
//		}

		for (CloudProcess cloudProcess : list2) {

			Calendar dateComplete = Calendar.getInstance();
			dateComplete.setTimeInMillis(cloudProcess.getComplete().getTime());
			dateComplete.set(Calendar.HOUR_OF_DAY, 0);
			dateComplete.set(Calendar.MINUTE, 0);
			dateComplete.set(Calendar.SECOND, 0);
			dateComplete.set(Calendar.MILLISECOND, 0);

			Calendar dateStart = Calendar.getInstance();
			dateStart.setTimeInMillis(cloudProcess.getStart().getTime());
			//should be deleted since it's round to the earlier day
			int timeNotPerfect = 0;
			dateStart.setTimeInMillis(cloudProcess.getStart().getTime());
//			if(dateStart.get(Calendar.SECOND) != 0 || dateStart.get(Calendar.MILLISECOND) != 0 
//					||dateStart.get(Calendar.MINUTE) != 0) //timeNotPerfect++;
			System.out.println("TIME NOT PERFECT " + timeNotPerfect);
			dateStart.set(Calendar.HOUR_OF_DAY, 0);
			dateStart.set(Calendar.MINUTE, 0);
			dateStart.set(Calendar.SECOND, 0);
			dateStart.set(Calendar.MILLISECOND, 0);

			if (dateStart.getTimeInMillis() == dateComplete.getTimeInMillis()) {
				dateStart.setTimeInMillis(cloudProcess.getStart().getTime());
				long time = dateComplete.getTimeInMillis();
				dateComplete.setTimeInMillis(cloudProcess.getComplete().getTime());
				long result = today - time;
				String s = today + " - " + time;
				System.out.println("String s:" + s + " = " + result);
				int pos = (int) (result / 1000 / 3600 / 24);
				System.out.println("TESTING : " + pos);
				int hoursCharged = (int) Math.floor((dateComplete.getTimeInMillis() - dateStart.getTimeInMillis()) / 3600000);
				double test = hoursCharged;
				if (test != (double) (dateComplete.getTimeInMillis() - dateStart.getTimeInMillis()) / 3600000)
					hoursCharged = hoursCharged + 1;
				System.out.println("!HOURS CHARGED: " + hoursCharged);
				System.out.println(dateStart.getTime() + " " + dateComplete.getTime() );
				pos = (days - 1) - pos;
				listfinal.set(pos, listfinal.get(pos) + cloudProcess.getCostPerHour() * (hoursCharged));
			} else {
				long daysDif = cloudProcess.getComplete().getTime() - cloudProcess.getStart().getTime();
				int numDays = (int) (daysDif / 1000 / 3600 / 24);
				
				
				double test = numDays;
				System.out.println("VALUES " + ( ((double)daysDif / 1000 / 3600 / 24)) + " - " +test);
				if (test != ((double)daysDif / 1000 / 3600 / 24))
					numDays = numDays + 1;
				
				System.out.println("DAYS DIF " + numDays);
				System.out.println("TIMES " + dateStart.getTime() + " " + dateComplete.getTime());
				long result = today - dateComplete.getTimeInMillis();
				for (int i = 0; i < numDays - 1; i++) {
					int pos = (int) (result / 1000 / 3600 / 24);
					pos = (days - 1) - pos;
					pos = pos - (numDays - 1 - i);
					System.out.println("I: " + i + "POS :" + pos);
					if (pos >= 0)
						listfinal.set(pos, listfinal.get(pos) + cloudProcess.getCostPerHour() * 24);
				}
				// after setting the 24hours(an entire day of the machine
				// running) cases, let's go to the days that were partially
				// executed

				int posFinal = (int) (result / 1000 / 3600 / 24);
				// setting the first day
				dateStart.setTimeInMillis(cloudProcess.getStart().getTime());
				dateComplete.setTimeInMillis(cloudProcess.getStart().getTime());
				dateComplete.set(Calendar.DAY_OF_MONTH, dateComplete.get(Calendar.DAY_OF_MONTH) + 1);
				dateComplete.set(Calendar.HOUR_OF_DAY, 0);
				// dateComplete.set(Calendar.MINUTE, 0);
				// dateComplete.set(Calendar.SECOND, 0);
				// dateComplete.set(Calendar.MILLISECOND, 0);
				int hoursCharged = (int) Math.floor((dateComplete.getTimeInMillis() - dateStart.getTimeInMillis()) / 3600000);
				 test = hoursCharged;
				if (test != (double) (dateComplete.getTimeInMillis() - dateStart.getTimeInMillis()) / 3600000)
					hoursCharged = hoursCharged + 1;
				
				// if(dateComplete.getTimeInMillis() -
				// dateStart.getTimeInMillis() == 0) hoursCharged = 0;
				System.out.println("!HOURS CHARGED: " + hoursCharged);
				System.out.println(dateStart.getTime() + " " + dateComplete.getTime());
				posFinal = (days - 1) - posFinal - numDays;
				if (posFinal >= 0)
					listfinal.set(posFinal, listfinal.get(posFinal) + cloudProcess.getCostPerHour() * (hoursCharged -timeNotPerfect));
				
				// setting the last day
				posFinal = (int) (result / 1000 / 3600 / 24);
				dateStart.setTimeInMillis(cloudProcess.getComplete().getTime());
				dateComplete.setTimeInMillis(cloudProcess.getComplete().getTime());
				dateStart.set(Calendar.HOUR_OF_DAY, 0);
				dateStart.set(Calendar.MINUTE, cloudProcess.getStart().getMinutes());
				dateStart.set(Calendar.SECOND, cloudProcess.getStart().getSeconds());
				// dateStart.set(Calendar.MILLISECOND,
				// cloudProcess.getStart().get);

				hoursCharged = (int) Math.floor((dateComplete.getTimeInMillis() - dateStart.getTimeInMillis()) / 3600000);
				System.out.println(dateComplete.getTimeInMillis() + " " + dateStart.getTimeInMillis());
				test = hoursCharged;
				if (test != (double) (dateComplete.getTimeInMillis() - dateStart.getTimeInMillis()) / 3600000)
					hoursCharged = hoursCharged + 1;
				// if(dateComplete.getTimeInMillis() -
				// dateStart.getTimeInMillis() == 0) hoursCharged = 0;
				System.out.println("!HOURS CHARGED: " + hoursCharged);
				System.out.println(dateStart.getTime() + " " + dateComplete.getTime());
				posFinal = (days - 1) - posFinal;
				listfinal.set(posFinal, listfinal.get(posFinal) + cloudProcess.getCostPerHour() * (hoursCharged +timeNotPerfect));

			}
		}

		return new CostsCollection(listfinal);
	}

	@GET
	@Produces("application/json")
	@Path("{id}")
	@RolesAllowed("authenticated")
	public Account get(@PathParam("id") Long id) throws NotFoundException {

		Account item = dao.load(id, UserResource.toUser(securityContext));
		return item;
	}

	@GET
	@Produces("application/json")
	@Path("byName")
	@RolesAllowed("authenticated")
	public Account get(@QueryParam("id") String id) throws NotFoundException {

		Account item = dao.load(id, UserResource.toUser(securityContext));
		return item;
	}

	// @GET
	// @Path("{id}/init")
	// @Produces("text/plain")
	// @RolesAllowed("authenticated")
	// public Response init(@PathParam ("id") Long id) throws NotFoundException
	// {
	// Account item = dao.load(id, UserResource.toUser(securityContext));
	// Cloud myCloud = CloudResource.dao.load(item.getCloud(),
	// UserResource.toUser(securityContext));
	// String result = CloudResource.testAccount(myCloud,
	// UserResource.toUser(securityContext), item, true);
	// if(result == null || result.trim().length()==0) {
	// return
	// Response.ok("ok",MediaType.TEXT_PLAIN_TYPE).location(item.getUri()).build();
	// } else {
	// log.warning("Init "+item.getUri()+" with warnings "+result);
	// return
	// Response.ok(result,MediaType.TEXT_PLAIN_TYPE).location(item.getUri()).build();
	// }
	// }

	@DELETE
	@Path("{id}")
	@RolesAllowed("authenticated")
	public void delete(@PathParam("id") Long id) throws NotFoundException {
		Account item = dao.load(id, UserResource.toUser(securityContext));
		dao.delete(item);
	}

	/*
	 * Helpers
	 */

	public String createAccountForUser(User user, String accountId, String secret) throws NotFoundException {
		Cloud ec2 = CloudResource.dao.load("EC2", user);
		Credential credential = null;
		if (secret != null && secret.trim().length() != 0) {
			credential = new Credential(accountId, secret).encrypt();
		}
		Account defaultEC2 = new Account("EC2", "Amazon EC2 account", ec2.getUri(), ec2.getName(), credential, user.getUri(), false);
		dao.add(defaultEC2);
		String result = CloudResource.testAccount(ec2, user, defaultEC2, true);
		return result;
	}

	static public class AccountManager extends CachingAbstractManager<Account> {
		public AccountManager() {
		}

		@Override
		protected URI myPath() {
			return UriBuilder.fromUri(Resource.get("baseURI", "http://127.0.0.1:8888/resources")).path(AccountResource.class).build();
		}

		@Override
		public GenericModelDao<Account> itemDaoFactory() {
			return new ServiceModelDao<Account>(Account.class);
		}

		public Account load(Long id, User requestor) throws NotFoundException {
			return super.get(id, requestor);
		}

		/**
		 * Locate a item from the persistent store based on the item name.
		 * 
		 * @param name
		 * @param requestor
		 *            requesting user
		 * @return the item
		 * @throws NotFoundException
		 *             is the object does not exist
		 */
		public Account load(String name, User requestor) throws NotFoundException {
			return super.get(name, requestor);
		}

		/**
		 * Locate a item from the persistent store based on the item URI.
		 * 
		 * @param uri
		 * @param requestor
		 *            requesting user
		 * @return the item
		 * @throws NotFoundException
		 *             is the object does not exist
		 */
		public Account load(URI uri, User requestor) throws NotFoundException {
			return super.get(uri, requestor);
		}

		public Account load(URI uri, URI requestor) throws NotFoundException {
			return super.get(uri, requestor);
		}

		public void add(Account account) {
			super.add(account);
		}

		public void update(Account account) {
			super.update(account);
		}

		public void delete(Account account) {
			super.delete(account);
		}

		public Collection<Account> getCollection(User user) {
			return super.getCollection(user);
		}

		public Collection<Account> getAccountList(User user, boolean summary) {

			log.warning("list Accounts entered with summary " + summary);

			Collection<Account> result = getCollection(user);

			if (result.getElements() != null) {
				for (int i = 0; i < result.getElements().size(); i++) {
					Account account = result.getElements().get(i);
					if (summary)
						result.getElements().set(i, Account.summary(account));
				}
			}
			return result;
		}

		public List<Account> getAccountsForCloud(User user, String cloudName) {
			return super.itemDao().listByPropertyForUser(user.getUri(), "cloudName", cloudName);
		}

		public Collection<CloudProcess> getCostsOfAccount(String account, int days) {

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			if (days == 1) {
				calendar.add(Calendar.HOUR_OF_DAY, 1);
				calendar.add(Calendar.DAY_OF_YEAR, 0 - 1);
			} else {
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				calendar.add(Calendar.DAY_OF_YEAR, 0 - (days - 1));
			}
			List<CloudProcess> costs = ofy().load().type(CloudProcess.class).filter("account", super.path + "/" + account).filter("complete >", calendar.getTime()).list();
			Collection<CloudProcess> result = new Collection<CloudProcess>(itemDao.clazz.getSimpleName(), super.path, costs);
			result.setTotal(costs.size());
			return result;

		}

	}

	final public static AccountManager dao = new AccountManager();

	@GET
	@Produces("text/plain")
	// @RolesAllowed("authenticated")
	@Path("/addcloudprocess")
	public String addCloudProcess(@QueryParam("account") String account, @QueryParam("name") String name, @QueryParam("dateStart") String dateStart, @QueryParam("dateComplete") String dateComplete, @QueryParam("cost") double cost) throws URISyntaxException, ParseException {

		Action task = new CountDownAction();
		task.setUri(new URI("http://www.google.com.br"));
		CloudProcess cloudProcess = new CloudProcess(UserResource.Root.getUri(), name, null, true, task);
		cloudProcess.setCostPerHour(cost);
		cloudProcess.setAccount(account);
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		cloudProcess.setComplete(df.parse(dateComplete));
		cloudProcess.setStart(df.parse(dateStart));
		CloudProcessResource.dao.add(cloudProcess);
		return cloudProcess.toString();
	}

}
