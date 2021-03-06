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

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;

import java.text.SimpleDateFormat;

import n3phele.service.actions.CountDownAction;
import n3phele.service.core.NotFoundException;
import n3phele.service.core.Resource;
import n3phele.service.model.Account;
import n3phele.service.model.AccountCollection;
import n3phele.service.model.AccountData;
import n3phele.service.model.AccountDataCollection;
import n3phele.service.model.Action;
import n3phele.service.model.ActivityData;
import n3phele.service.model.ActivityDataCollection;
import n3phele.service.model.CachingAbstractManager;
import n3phele.service.model.Cloud;
import n3phele.service.model.CloudProcess;
import n3phele.service.model.CloudProcessCollection;
import n3phele.service.model.CommandCloudAccount;
import n3phele.service.model.CommandCloudAccountCollection;
import n3phele.service.model.CostsCollection;
import n3phele.service.model.ServiceModelDao;
import n3phele.service.model.core.Collection;
import n3phele.service.model.core.Credential;
import n3phele.service.model.core.GenericModelDao;
import n3phele.service.model.core.User;
import n3phele.time.MutableTimeFactory;

@Path("/account")
public class AccountResource {
	private static Logger log = Logger.getLogger(AccountResource.class.getName());
	
	private MutableTimeFactory timeFactory = new MutableTimeFactory();	

	@Context
	UriInfo uriInfo;
	@Context
	protected SecurityContext securityContext;

	@GET
	@Produces("application/json")
	@RolesAllowed("authenticated")
	public AccountCollection list(@DefaultValue("false") @QueryParam("summary") Boolean summary) {

		log.warning("list Accounts entered with summary " + summary);

		Collection<Account> result = dao.getAccountList(getUser(), summary);

		return new AccountCollection(result, 0, -1);
	}

	/*
	 * @return the list of accounts with using the CommandCloudAccount as JSon
	 */
	@GET
	@Produces("application/json")
	@RolesAllowed("authenticated")
	@Path("/listAccounts")
	public CommandCloudAccountCollection listAccounts(@DefaultValue("false") @QueryParam("summary") Boolean summary) {

		log.warning("list Accounts entered with summary " + summary);

		Collection<Account> result = dao.getAccountList(getUser(), summary);
		ArrayList<CommandCloudAccount> response = new ArrayList<CommandCloudAccount>();
		for(Account a: result.getElements()){
			response.add(new CommandCloudAccount(a.getCloudName(), a.getName(), a.getUri()));
		}
		
		return new CommandCloudAccountCollection( new Collection<CommandCloudAccount>(null, null, response));
	}
	@GET
	@Produces("application/json")
	@RolesAllowed("authenticated")
	@Path("/accountData")
	public AccountDataCollection listAccountOnlyData(@DefaultValue("false") @QueryParam("summary") Boolean summary) {
		Collection<Account> result = dao.getAccountList(getUser(), summary);
		List<Account> list = result.getElements();
		List<AccountData> data = new ArrayList<AccountData>();
		for (Account account : list) {
			List<ActivityData> dados = listRunningCloudProcessWithCostsActivityData(""+account.getId()).getElements();
			data.add(new AccountData(account.getName(),"US$" +totalCost24Hour(""+account.getId()).getElements().get(0), "" + dados.size(), account.getCloudName(),account.getUri().toString()));
		}
		return new AccountDataCollection(data);
	}

	public User getUser() {
		return UserResource.toUser(securityContext);
	}
	
	@POST
	@Produces("text/plain")
	@RolesAllowed("authenticated")
	public Response add(@FormParam("name") String name, @FormParam("description") String description, @FormParam("cloud") URI cloud, @FormParam("accountId") String accountId, @FormParam("secret") String secret) {

		Cloud myCloud = CloudResource.dao.load(cloud, getUser());
		if (name == null || name.trim().length() == 0) {
			throw new IllegalArgumentException("bad name");
		}
		Account account = new Account(name, description, cloud, myCloud.getName(), new Credential(accountId, secret).encrypt(), getUser().getUri(), false);

		dao.add(account);
		String result = CloudResource.testAccount(myCloud, getUser(), account, true);
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

		Cloud myCloud = CloudResource.dao.load(cloud, getUser());
		Account item = dao.load(id, getUser());
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
		String result = CloudResource.testAccount(myCloud, getUser(), item, true);

		log.warning("Updated " + item.getUri() + ((credential != null) ? " including credential " + result : ""));
		return item;
	}

	/**
	 * @param account
	 * @param days
	 * @return All CloudProcess of the account passed who were completed in the
	 *         number of days given.
	 */
	@GET
	@Produces("application/json")
	@RolesAllowed("authenticated")
	@Path("/{account}/lastcompleted/{days:[0-9]+}")
	public CloudProcessCollection listCloudProcessWithCosts(@PathParam("account") String account, @PathParam("days") int days) {

		Collection<CloudProcess> result = dao.getCostsOfAccount(account, days);
		return new CloudProcessCollection(result);
	}

	/**
	 * @param account
	 * @param days
	 * @return All CloudProcess of the account passed who still running or were
	 *         completed in the number of days given.
	 */
	@GET
	@Produces("application/json")
	@RolesAllowed("authenticated")
	@Path("/{account}/lastcompleted/{days:[0-9]+}/all")
	public CloudProcessCollection listAllCloudProcessWithCosts(@PathParam("account") String account, @PathParam("days") int days) {

		Collection<CloudProcess> result = dao.getAllProcessByDays(account, days);
		return new CloudProcessCollection(result);
	}

	/**
	 * @param account
	 * @return All CloudProcess who still running.
	 */
	@GET
	@Produces("application/json")
	@RolesAllowed("authenticated")
	@Path("/{account}/runningprocess")
	public CloudProcessCollection listRunningCloudProcessWithCosts(@PathParam("account") String account, @PathParam("days") int days) {

		Collection<CloudProcess> result = dao.getRunningProcess(account);
		return new CloudProcessCollection(result);
	}
	
	@SuppressWarnings("deprecation")
	@GET
	@Produces("application/json")
	@RolesAllowed("authenticated")
	@Path("/{account}/runningprocess/get")
	public ActivityDataCollection listRunningCloudProcessWithCostsActivityData(@PathParam("account") String account) {
		Collection<CloudProcess> result = dao.getRunningProcess(account);
		List<ActivityData> list = new ArrayList<ActivityData>();
		for (CloudProcess c : result.getElements()) {
			String costs = "";
			double price = c.getCostPerHour();
			int index = c.getUri().toString().lastIndexOf("_");
			String  uri = c.getUri().toString();
			if(index > 0)
				uri = c.getUri().toString().substring(0, index);
			String age = calcAge(c);
			Date now = timeFactory.createMutableDateTime().toDate();
			if (now.before(c.getEpoch())) {
				costs += 0;
			} else if (c.getComplete() == null) {
				int hours = (int) (((now.getTime() - c.getEpoch().getTime()) / (1000 * 60 * 60 * 24)) * 24);
				if (now.getHours() >= c.getEpoch().getHours()) {
					hours += now.getHours() - c.getEpoch().getHours() + 1;
				} else {
					hours += 24 - (c.getEpoch().getHours() - now.getHours()) + 1;
				}
				if (now.getMinutes() - c.getEpoch().getMinutes() < 0) {
					hours--;
				}
				double total = price * hours;
				costs += "US$" + (double) Math.round(total * 1000) / 1000;
			}
			CloudProcess cTop = new CloudProcess();
			try {
				cTop = CloudProcessResource.dao.load(new URI(uri));
	
			} catch (Exception e) {
				e.printStackTrace();
			}

			list.add(new ActivityData(c.getUri().toString(),c.getName(),uri,costs,age,cTop.getName()));
		}
		return new ActivityDataCollection(list);
	}
	
	@SuppressWarnings("deprecation")
	private String calcAge(CloudProcess item){
		String result = "";
		
		if (item != null) {
			Date now = timeFactory.createMutableDateTime().toDate();
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
	
	/**
	 * 
	 * @param account
	 * @param days
	 * @return Collection of costs for the graphs
	 */

	@GET
	@Produces("application/json")
	@RolesAllowed("authenticated")
	@Path("/{account}/lastcompleted/{days:[0-9]+}/get")
	public CostsCollection listCostPerDays(@PathParam("account") String account, @PathParam("days") int days) {
		
		if (days == 1) {
			return listCost24hours(account);
		}

		List<CloudProcess> list = dao.getAllProcessByDays(account, days).getElements();
		List<Double> listfinal = new ArrayList<Double>();
		MutableDateTime date = timeFactory.createMutableDateTime();
		date.setMillisOfDay(0);

		DateTime cloudProcessEpoch, cloudProcessComplete;
		long today = date.getMillis();

		for (int i = 0; i < days; i++) {
			listfinal.add(0.0);
		}
		for (CloudProcess cloudProcess : list) {
			// just for the fake data
			
			if (cloudProcess.getEpoch() == null)
				cloudProcess.setEpoch(cloudProcess.getStart());
			
			if(cloudProcess.getComplete() == null){
				MutableDateTime fakecomplete = timeFactory.createMutableDateTime();
				cloudProcess.setComplete(fakecomplete.toDate());
			}				
			
			cloudProcessEpoch = new DateTime(cloudProcess.getEpoch());
			cloudProcessComplete = new DateTime(cloudProcess.getComplete());

			
			MutableDateTime dateStart = timeFactory.createMutableDateTime(cloudProcessEpoch);
			dateStart.setMillisOfDay(0);
			MutableDateTime dateComplete = timeFactory.createMutableDateTime(cloudProcessComplete);
			dateComplete.setMillisOfDay(0);

			if (dateStart.getMillis() == dateComplete.getMillis()) {
				dateStart.setTime(cloudProcessEpoch);
				long time = dateComplete.getMillis();
				dateComplete.setTime(cloudProcessComplete);
				long result = today - time;

				int pos = (int) (result / 1000 / 3600 / 24);
				int hoursCharged = (int) Math.floor((dateComplete.getMillis() - dateStart.getMillis()) / 3600000);
				double test = hoursCharged;
				if (test != (double) (dateComplete.getMillis() - dateStart.getMillis()) / 3600000)
					hoursCharged = hoursCharged + 1;
				pos = (days - 1) - pos;
				listfinal.set(pos, listfinal.get(pos) + cloudProcess.getCostPerHour() * (hoursCharged));
			} else {
				//long daysDif = cloudProcessComplete.getMillis() - cloudProcessEpoch.getMillis();
				long daysDif = dateComplete.getMillis() - dateStart.getMillis();
				int numDays = (int) (daysDif / 1000 / 3600 / 24);
				double test = numDays;
				if (test != ((double) daysDif / 1000 / 3600 / 24))
					numDays = numDays + 1;
				long result = today - dateComplete.getMillis();
				for (int i = 0; i < numDays - 1; i++) {
					int pos = (int) (result / 1000 / 3600 / 24);
					pos = (days - 1) - pos;
					pos = pos - (numDays - 1 - i);
					if (pos >= 0)
						listfinal.set(pos, listfinal.get(pos) + cloudProcess.getCostPerHour() * 24);
				}
				// after setting the 24hours(an entire day of the machine
				// running) cases, let's go to the days that were partially
				// executed

				int posFinal = (int) (result / 1000 / 3600 / 24);
				// setting the first day
				dateStart.setMillis(cloudProcess.getEpoch().getTime());
				dateComplete.setMillis(cloudProcess.getEpoch().getTime());
				dateComplete.setDayOfMonth(dateComplete.getDayOfMonth());
				dateComplete.addDays(1);
				dateComplete.setHourOfDay(0);
				int hoursCharged = (int) Math.floor((dateComplete.getMillis() - dateStart.getMillis()) / 3600000);
				test = hoursCharged;
				if (test != (double) (dateComplete.getMillis() - dateStart.getMillis()) / 3600000)
					hoursCharged = hoursCharged + 1;

				posFinal = (days - 1) - posFinal - numDays;
				if (posFinal >= 0)
					listfinal.set(posFinal, listfinal.get(posFinal) + cloudProcess.getCostPerHour() * (hoursCharged));

				// setting the last day
				posFinal = (int) (result / 1000 / 3600 / 24);
				dateStart.setMillis(cloudProcess.getComplete().getTime());
				dateComplete.setMillis(cloudProcess.getComplete().getTime());
				dateStart.setHourOfDay(0);
				dateStart.setMinuteOfHour(cloudProcessEpoch.getMinuteOfHour());
				dateStart.setSecondOfMinute(cloudProcessEpoch.getSecondOfMinute());
				hoursCharged = (int) Math.floor((dateComplete.getMillis() - dateStart.getMillis()) / 3600000);
				test = hoursCharged;
				if (test != (double) (dateComplete.getMillis() - dateStart.getMillis()) / 3600000)
					hoursCharged = hoursCharged + 1;
				posFinal = (days - 1) - posFinal;
				//if (posFinal >= 0)
					listfinal.set(posFinal, listfinal.get(posFinal) + cloudProcess.getCostPerHour() * (hoursCharged));

			}
		}
		
		// ?? format double - use ValueOf or Parse?
		/*for (int i = 0; i < listfinal.size(); i++) {
			listfinal.set(i, Double.valueOf(String.format("%.3f", listfinal.get(i)).replace(',', '.')));
		}*/
		formatDoubleList(listfinal);
		
		return new CostsCollection(listfinal);
	}

	private CostsCollection listCost24hours(String account) {
		List<CloudProcess> list = dao.getAllProcessByDays(account, 1).getElements();
		List<Double> listfinal = new ArrayList<Double>();
		MutableDateTime dateStart, dateEnd;
		MutableDateTime cpStart, cpComplete, now;
		dateStart = timeFactory.createMutableDateTime();
		dateEnd = timeFactory.createMutableDateTime();
		dateStart.setMinuteOfHour(0);
		dateStart.setSecondOfMinute(0);
		dateStart.setMillisOfSecond(0);
		dateStart.addHours(1);
		dateEnd = dateStart.copy();
		dateStart.addDays(-1);
		now = timeFactory.createMutableDateTime();
		
		for (int i = 0; i < 24; i++) {
			listfinal.add(0.0);
		}

		for (CloudProcess cloudProcess : list) {
			cpStart = timeFactory.createMutableDateTime(cloudProcess.getEpoch());
			if (cloudProcess.getComplete() != null)
				cpComplete = timeFactory.createMutableDateTime(cloudProcess.getComplete());
			else
				cpComplete = timeFactory.createMutableDateTime(Long.MAX_VALUE);

			int hourStart = 0;
			int hourEnd = 0;

			if (cpStart.isBefore(dateStart)) {
				// CloudProcess started before last 24h

				if (cpComplete.isAfter(dateEnd)) {
					// CloudProcess still running
					hourStart = 0;
					hourEnd = 23;

					hourEnd = processCloudProcessEndTime(cpStart, now, hourEnd);

				} else {
					// CloudProcess terminated in this day
					hourStart = 0;
					hourEnd = cpComplete.getHourOfDay() - dateStart.getHourOfDay();
					if (hourEnd < 0)
						hourEnd += 24;

					hourEnd = processCloudProcessEndTime(cpStart, cpComplete, hourEnd);
				}

			} else {
				// CloudProcess started today

				if (cpComplete.isAfter(dateEnd)) {
					// CloudProcess still running
					hourStart = cpStart.getHourOfDay() - dateStart.getHourOfDay();
					if (hourStart < 0)
						hourStart += 24;
					hourEnd = 23;

					hourEnd = processCloudProcessEndTime(cpStart, now, hourEnd);

				} else {
					// CloudProcess terminated today
					hourStart = cpStart.getHourOfDay() - dateStart.getHourOfDay();
					hourEnd = cpComplete.getHourOfDay() - dateStart.getHourOfDay();
					if (hourStart < 0) 
						hourStart += 24;
					if(hourEnd < 0)
						hourEnd += 24;
						

					hourEnd = processCloudProcessEndTime(cpStart, cpComplete, hourEnd);

				}
			}
			for (int j = hourStart; j < hourEnd; j++) {
				listfinal.set(j, listfinal.get(j) + cloudProcess.getCostPerHour());
			}
		}
		
		formatDoubleList(listfinal);

		return new CostsCollection(listfinal);
	}

	private int processCloudProcessEndTime(MutableDateTime start, MutableDateTime now, int hour)
	{
		if (now.getMinuteOfHour() > start.getMinuteOfHour()) {
			hour++;
		} else if (now.getMinuteOfHour() == start.getMinuteOfHour()) {
			if (now.getSecondOfMinute() > start.getSecondOfMinute()) {
				hour++;
			} else if (now.getSecondOfMinute() == start.getSecondOfMinute()) {
				if (now.getMillisOfSecond() > start.getMillisOfSecond()) {
					hour++;
				}
			}
		}
		return hour;
	}
	
	@GET
	@Produces("application/json")
	@RolesAllowed("authenticated")
	@Path("/{account}/totalCost24Hour")
	public CostsCollection totalCost24Hour(@PathParam("account") String account){
		List<Double> list = listCost24hours(account).getElements();
		double totalcost = 0.0;
		for (Double d : list) {
			totalcost = totalcost + d;
		}
		ArrayList<Double> list2 = new ArrayList<Double>();
		list2.add(Double.parseDouble(String.format("%.3f", totalcost).replace(',', '.')));
		list2.add(0.0);
		
		return new CostsCollection(list2);
	}
	protected void formatDoubleList(List<Double> listfinal) {
		for (int i = 0; i < listfinal.size(); i++) {
			listfinal.set(i, Double.parseDouble(String.format("%.3f", listfinal.get(i)).replace(',', '.')));
		}
		
	}


	@GET
	@Produces("application/json")
	@Path("{id}")
	@RolesAllowed("authenticated")
	public Account get(@PathParam("id") Long id) throws NotFoundException {

		Account item = dao.load(id, getUser());
		return item;
	}

	@GET
	@Produces("application/json")
	@Path("byName")
	@RolesAllowed("authenticated")
	public Account get(@QueryParam("id") String id) throws NotFoundException {

		Account item = dao.load(id, getUser());
		return item;
	}
	
	@DELETE
	@Path("{id}")
	@RolesAllowed("authenticated")
	public void delete(@PathParam("id") Long id) throws NotFoundException {
		Account item = dao.load(id, getUser());
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

			MutableDateTime date = new MutableDateTime();
			date.setMinuteOfHour(0);
			date.setSecondOfMinute(0);
			date.setMillisOfSecond(0);
			if (days == 1) {
				date.addHours(1);
				date.addDays(-1);
			} else {
				date.setHourOfDay(0);
				date.addDays(-days + 1);
			}
			List<CloudProcess> costs = ofy().load().type(CloudProcess.class).filter("account", super.path + "/" + account).filter("complete >", date.toDate()).list();
			Collection<CloudProcess> result = new Collection<CloudProcess>(itemDao.clazz.getSimpleName(), super.path, costs);
			result.setTotal(costs.size());
			return result;

		}

		public Collection<CloudProcess> getRunningProcess(String account) {
			List<CloudProcess> costs = ofy().load().type(CloudProcess.class).filter("account", super.path + "/" + account).filter("complete", null).list();
			Collection<CloudProcess> result = new Collection<CloudProcess>(itemDao.clazz.getSimpleName(), super.path, costs);
			result.setTotal(costs.size());
			return result;

		}

		public Collection<CloudProcess> getAllProcessByDays(String account, int days) {
			List<CloudProcess> list = dao.getCostsOfAccount(account, days).getElements();
			list.addAll(dao.getRunningProcess(account).getElements());
			Collection<CloudProcess> result = new Collection<CloudProcess>(itemDao.clazz.getSimpleName(), super.path, list);
			result.setTotal(list.size());
			return filterZombieProcess(result);
		}
		
		public Collection<CloudProcess> filterZombieProcess(Collection<CloudProcess> colProcess){
			List<CloudProcess> list = colProcess.getElements();
			ArrayList<CloudProcess> result = new ArrayList<CloudProcess>();
			
			boolean willAdd = true;
			int countNulls = 0;
			for(CloudProcess cp : list){
				if(cp.getEpoch() == null){
					countNulls += 1;
					continue;
				}
				for(int i = 0; i < result.size(); i++){
					CloudProcess cp2 = result.get(i);
					if(cp2.getEpoch().compareTo(cp.getEpoch()) == 0){
						if(cp2.getComplete() != null){
							if(cp.getComplete() == null){
								result.set(i, cp);
							}
							else if(cp2.getComplete().compareTo(cp.getComplete()) < 0){
								result.set(i, cp);
							}
						}
						willAdd = false;
						break;
					}
				}
				if(willAdd){
					result.add(cp);
				}
				willAdd = true;
				
			}
			return new Collection<CloudProcess>(itemDao.clazz.getSimpleName(), super.path, result);
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
		CloudProcess cloudProcess = new CloudProcess(UserResource.Root.getUri(), name, null, true, task, false);
		cloudProcess.setCostPerHour(cost);
		cloudProcess.setAccount(account);
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		if (dateComplete != null)
			cloudProcess.setComplete(df.parse(dateComplete));
		cloudProcess.setEpoch(df.parse(dateStart));
		CloudProcessResource.dao.add(cloudProcess);
		return cloudProcess.toString();
	}

	public MutableTimeFactory getTimeFactory() {
		return this.timeFactory;
	}

	public void setTimeFactory(MutableTimeFactory timeFactory) {
		this.timeFactory = timeFactory;
	}
}