package n3phele.process;
/**
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

import static org.junit.Assert.*;

import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import n3phele.service.actions.tasks.CountDownAction;
import n3phele.service.actions.tasks.JobAction;
import n3phele.service.model.ActionState;
import n3phele.service.model.CloudProcess;
import n3phele.service.model.Context;
import n3phele.service.model.core.User;
import n3phele.service.rest.impl.ActionResource;
import n3phele.service.rest.impl.CloudProcessResource;
import n3phele.service.rest.impl.UserResource;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.googlecode.objectify.Key;

public class CloudProcessTest extends CloudProcessResource {
	
	private final LocalServiceTestHelper helper =   new LocalServiceTestHelper(
			new LocalDatastoreServiceTestConfig()
				.setApplyAllHighRepJobPolicy(),
			new LocalTaskQueueTestConfig()
								.setDisableAutoTaskExecution(false)             
								.setCallbackClass(LocalTaskQueueTestConfig.DeferredTaskCallback.class)) ;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		helper.setUp();
	}
	
	 @After     
	 public void tearDown() {         helper.tearDown();     } 

	
	/** Creates and runs a simple test process verifying preservation of running task state
	 * @throws InterruptedException
	 */
	@Test
	public void oneProcessTest() throws InterruptedException, ClassNotFoundException {
		addSecurityContext(null);
		User root = UserResource.Root;
		assertNotNull(root);
		Response result;

		result = super.exec("CountDown", "now is the time");

		assertEquals(201, result.getStatus());
		URI processId = (URI) result.getMetadata().getFirst("Location");
		Thread.sleep(3000);
		dao.clear();
		CloudProcess process = dao.load(processId);
		assertEquals(ActionState.RUNABLE, process.getState());
		dao.clear();
		result = super.refresh();
		assertEquals(200,result.getStatus());
		assertEquals("{\"RUNABLE\": 1}", result.getEntity());
		Thread.sleep(1000);
		dao.clear();
		CountDownAction action = (CountDownAction) ActionResource.dao.load(process.getTask());
		assertEquals("Count value", 3, action.getCount());
	}

	/** Creates and runs a simple test process verifying preservation of running task state
	 * @throws InterruptedException
	 */
	@Test
	public void oneProcessJobOkExitTest() throws InterruptedException, ClassNotFoundException {
		addSecurityContext(null);
		dao.clear();
		Response result;
		result = super.exec("Job", "CountDown foo");
		
		assertEquals(201, result.getStatus());
		URI processId = (URI) result.getMetadata().getFirst("Location");
		Thread.sleep(2000);
		dao.clear();
		CloudProcess job = dao.load(processId);
		assertEquals(ActionState.RUNABLE, job.getState());
		dao.clear();
		result = super.refresh();
		assertEquals(200,result.getStatus());
		assertEquals("{\"RUNABLE\": 1, \"RUNABLE_Wait\": 1}", result.getEntity());
		Thread.sleep(1000);
		dao.clear();
		JobAction jobAction = (JobAction) ActionResource.dao.load(job.getTask());
		URI childProcess = jobAction.getChildProcess();
		CloudProcess countDownProcess = CloudProcessResource.dao.load(childProcess);
		
		CountDownAction countDownAction = (CountDownAction) ActionResource.dao.load(countDownProcess.getTask());
		
		assertEquals("Count value", 3, countDownAction.getCount());
		countDownAction.setCount(1);
		ActionResource.dao.update(countDownAction);
		dao.clear();
		result = refresh();
		assertEquals("{\"RUNABLE\": 1, \"RUNABLE_Wait\": 1}", result.getEntity());
		Thread.sleep(1000);
		dao.clear();
		result = refresh();
		assertEquals("{}", result.getEntity());
		dao.clear();
		
		
		
	}
	
	/** Demonstrates cancellation of a task
	 * @throws InterruptedException
	 * @throws ClassNotFoundException 
	 */
	@Test
	public void oneProcessCancellationTest() throws InterruptedException, ClassNotFoundException {
		addSecurityContext(null);
		Response result = super.exec("CountDown", "doomed to die");
		assertEquals(201, result.getStatus());
		URI processId = (URI) result.getMetadata().getFirst("Location");
		CloudProcess process = dao.load(processId);
		Thread.sleep(3000);
		assertEquals(ActionState.RUNABLE, process.getState());

		cancel(process);
		dao.clear();
		Thread.sleep(1000);
		dao.clear();
		process = dao.load(processId);
		assertEquals(ActionState.CANCELLED, process.getState());
		assertTrue(process.isFinalized());
		CountDownAction action = (CountDownAction) ActionResource.dao.load(process.getTask());
		assertEquals("Count value", 1000, action.getCount());
	}
	
	/** Demonstrates cancellation of a child job task
	 * @throws InterruptedException
	 * @throws ClassNotFoundException 
	 */
	@Test
	public void oneProcessJobCancellationTest() throws InterruptedException, ClassNotFoundException {
		addSecurityContext(null);
		Response result;
		result = super.exec("Job", "CountDown doomed2die2");
		
		assertEquals(201, result.getStatus());
		URI processId = (URI) result.getMetadata().getFirst("Location");
		
		Thread.sleep(2000);
		dao.clear();
		CloudProcess job = dao.load(processId);
		JobAction jobAction = (JobAction) ActionResource.dao.load(job.getTask());
		CloudProcess process = dao.load(jobAction.getChildProcess());
		
		assertEquals(ActionState.RUNABLE, process.getState());

		cancel(process);
		dao.clear();
		Thread.sleep(1000);
		dao.clear();
		process = dao.load(process.getId());
		assertEquals(ActionState.CANCELLED, process.getState());
		assertTrue(process.isFinalized());
		CountDownAction action = (CountDownAction) ActionResource.dao.load(process.getTask());
		assertEquals("Count value", 1000, action.getCount());
		job = dao.load(job.getId());
		jobAction = (JobAction) ActionResource.dao.load(jobAction.getId());
		assertEquals(ActionState.COMPLETE, job.getState());
		assertEquals(ActionState.CANCELLED, jobAction.getChildEndState());
	}
	
	
	/** Demonstrates cancellation of the parent job task showing child cleanup
	 * @throws InterruptedException
	 * @throws ClassNotFoundException 
	 */
	@Test
	public void oneProcessJobParentCancellationTest() throws InterruptedException, ClassNotFoundException {
		addSecurityContext(null);
		Response result;
		result = super.exec("Job", "CountDown doomed2die2");
		
		assertEquals(201, result.getStatus());
		URI processId = (URI) result.getMetadata().getFirst("Location");
		
		Thread.sleep(2000);
		dao.clear();
		CloudProcess job = dao.load(processId);
		JobAction jobAction = (JobAction) ActionResource.dao.load(job.getTask());
		CloudProcess process = dao.load(jobAction.getChildProcess());
		
		assertEquals(ActionState.RUNABLE, process.getState());

		cancel(job);
		dao.clear();
		Thread.sleep(1000);
		dao.clear();
		process = dao.load(process.getId());
		assertEquals(ActionState.CANCELLED, process.getState());
		assertTrue(process.isFinalized());
		CountDownAction action = (CountDownAction) ActionResource.dao.load(process.getTask());
		assertEquals("Count value", 2000, action.getCount());
		job = dao.load(job.getId());
		jobAction = (JobAction) ActionResource.dao.load(jobAction.getId());
		assertEquals(ActionState.CANCELLED, job.getState());
		assertEquals(ActionState.CANCELLED, jobAction.getChildEndState());
	}
	
	/** Demonstrates exception throw in Init phase
	 * @throws InterruptedException
	 * @throws ClassNotFoundException 
	 */
	@Test
	public void oneProcessInitExceptionTest() throws InterruptedException, ClassNotFoundException {
		addSecurityContext(null);
		Response result = super.exec("CountDown", "throwInit");
		assertEquals(201, result.getStatus());
		URI processId = (URI) result.getMetadata().getFirst("Location");
		Thread.sleep(3000);
		dao.clear();
		CloudProcess process = dao.load(processId);

		assertEquals(ActionState.FAILED, process.getState());
		assertTrue(process.isFinalized());
		CountDownAction action = (CountDownAction) ActionResource.dao.load(process.getTask());
		assertEquals("Count value", 5, action.getCount());
	}
	
	/** Demonstrates exception throw in Init phase of a child job
	 * @throws InterruptedException
	 * @throws ClassNotFoundException 
	 */
	@Test
	public void oneProcessJobInitExceptionTest() throws InterruptedException, ClassNotFoundException {
		addSecurityContext(null);
		Response result;
		result = super.exec("Job", "CountDown throwInit");
		assertEquals(201, result.getStatus());
		URI processId = (URI) result.getMetadata().getFirst("Location");
		Thread.sleep(3000);
		dao.clear();
		CloudProcess job = dao.load(processId);
		JobAction jobAction = (JobAction) ActionResource.dao.load(job.getTask());
		CloudProcess process = dao.load(jobAction.getChildProcess());

		assertEquals(ActionState.FAILED, process.getState());
		assertTrue(process.isFinalized());
		CountDownAction action = (CountDownAction) ActionResource.dao.load(process.getTask());
		assertEquals("Count value", 5, action.getCount());
		dao.clear();
		result = super.refresh();
		assertEquals(200,result.getStatus());
		Thread.sleep(1000);
		dao.clear();
		job = dao.load(job.getId());
		jobAction = (JobAction) ActionResource.dao.load(jobAction.getId());
		assertEquals(ActionState.COMPLETE, job.getState());
		assertEquals(ActionState.FAILED, jobAction.getChildEndState());
	}
	
	/** Demonstrates exception throw in Run phase
	 * @throws InterruptedException
	 * @throws ClassNotFoundException 
	 */
	@Test
	public void oneProcessRunExceptionTest() throws InterruptedException, ClassNotFoundException {
		addSecurityContext(null);
		Response result = super.exec("CountDown", "throw5");
		assertEquals(201, result.getStatus());
		URI processId = (URI) result.getMetadata().getFirst("Location");
		Thread.sleep(3000);
		dao.clear();
		CloudProcess process = dao.load(processId);
		assertEquals(ActionState.FAILED, process.getState());
	
		assertTrue(process.isFinalized());
		CountDownAction action = (CountDownAction) ActionResource.dao.load(process.getTask());
		assertEquals("Count value", 4, action.getCount());
	}
	
	/** Demonstrates exception throw in Run phase of a Child 
	 * @throws InterruptedException
	 * @throws ClassNotFoundException 
	 */
	@Test
	public void oneProcessJobRunExceptionTest() throws InterruptedException, ClassNotFoundException {
		addSecurityContext(null);
		Response result = super.exec("Job", "CountDown throw5");
		assertEquals(201, result.getStatus());
		URI processId = (URI) result.getMetadata().getFirst("Location");
		Thread.sleep(3000);
		dao.clear();
		CloudProcess job = dao.load(processId);
		JobAction jobAction = (JobAction) ActionResource.dao.load(job.getTask());
		CloudProcess process = dao.load(jobAction.getChildProcess());;
		assertEquals(ActionState.FAILED, process.getState());
		assertTrue(process.isFinalized());
		CountDownAction action = (CountDownAction) ActionResource.dao.load(process.getTask());
		assertEquals("Count value", 4, action.getCount());
		dao.clear();
		result = super.refresh();
		assertEquals(200,result.getStatus());
		assertEquals("{}", result.getEntity());
		Thread.sleep(1000);
		dao.clear();
		
		
		job = dao.load(job.getId());
		jobAction = (JobAction) ActionResource.dao.load(jobAction.getId());
		assertEquals(ActionState.COMPLETE, job.getState());
		assertEquals(ActionState.FAILED, jobAction.getChildEndState());
	}
	
	/** Demonstrates two processes and activation based on dependency completion
	 * @throws InterruptedException
	 */
	@Test
	public void twoProcessWithPreestablishedDependency() throws InterruptedException {
		addSecurityContext(null);
		Context tom_env = new Context();
		tom_env.putValue("arg", "tom rocks!");
		
		final Context jerry_env = new Context();
		jerry_env.putValue("arg", "jelly rolls");
	
		CloudProcess tom = dao.create(UserResource.Root, "tom", tom_env, null, null, CountDownAction.class);
		List<URI> jerryDependency = new ArrayList<URI>();
		jerryDependency.add(tom.getUri());
		CloudProcess jerry = dao.create(UserResource.Root, "jerry", jerry_env, jerryDependency, null, CountDownAction.class);
		init(tom);
		init(jerry);

		Thread.sleep(3000);
		dao.clear();
		
		
		tom  = dao.load(tom.getUri());
		jerry  = dao.load(jerry.getUri());
		assertEquals(ActionState.RUNABLE, tom.getState());
		assertEquals(ActionState.INIT, jerry.getState());
		dao.clear();
		CountDownAction tomAction = (CountDownAction) ActionResource.dao.load(tom.getTask());
		tomAction.setCount(1);
		ActionResource.dao.update(tomAction);
		
		dao.clear();
		Response result = super.refresh();
		assertEquals(200,result.getStatus());
		assertEquals("{\"RUNABLE\": 1, \"INIT\": 1}", result.getEntity());
		
		Thread.sleep(2000);
		dao.clear();
		tom  = dao.load(tom.getUri());
		jerry  = dao.load(jerry.getUri());
		assertEquals(ActionState.COMPLETE, tom.getState());
		assertEquals(ActionState.RUNABLE, jerry.getState());

	}
	
	/** Demonstrates two processes and activation based on dependency completion
	 * @throws InterruptedException
	 */
	@Test
	public void twoProcessWithDependencyAddedAfterDependentCompleted() throws InterruptedException {
		addSecurityContext(null);
		Context tom_env = new Context();
		tom_env.putValue("arg", "tom rocks!");
		
		final Context jerry_env = new Context();
		jerry_env.putValue("arg", "jelly rolls");
	
		CloudProcess tom = dao.create(UserResource.Root, "tom", tom_env, null, null, CountDownAction.class);
		init(tom);
		Thread.sleep(3000);
		dao.clear();
		tom  = dao.load(tom.getUri());
		assertEquals(ActionState.RUNABLE, tom.getState());
		dao.clear();
		CountDownAction tomAction = (CountDownAction) ActionResource.dao.load(tom.getTask());
		tomAction.setCount(1);
		ActionResource.dao.update(tomAction);
		dao.clear();
		
		List<URI> jerryDependency = new ArrayList<URI>();
		jerryDependency.add(tom.getUri());
		
		refresh();
		
		CloudProcess jerry = dao.create(UserResource.Root, "jerry", jerry_env, jerryDependency, null, CountDownAction.class);
		init(jerry);

		Thread.sleep(3500);
		dao.clear();
		
		
		tom  = dao.load(tom.getUri());
		jerry  = dao.load(jerry.getUri());
		assertEquals(ActionState.COMPLETE, tom.getState());
		assertEquals(ActionState.RUNABLE, jerry.getState());

	}
	
	/** Demonstrates two processes and clean up processing associated with task cancellation
	 * @throws InterruptedException
	 */
	@Test
	public void twoProcessWithPreestablishedDependencyCancelTest() throws InterruptedException {
		addSecurityContext(null);
		Context tom_env = new Context();
		tom_env.putValue("arg", "tom rocks!");
		
		final Context jerry_env = new Context();
		jerry_env.putValue("arg", "jelly rolls");
	
		CloudProcess tom = dao.create(UserResource.Root, "tom", tom_env, null, null, CountDownAction.class);
		List<URI> jerryDependency = new ArrayList<URI>();
		jerryDependency.add(tom.getUri());
		CloudProcess jerry = dao.create(UserResource.Root, "jerry", jerry_env, jerryDependency, null, CountDownAction.class);
		init(tom);
		init(jerry);

		Thread.sleep(3000);
		dao.clear();

		
		tom  = dao.load(tom.getUri());
		jerry  = dao.load(jerry.getUri());
		assertEquals(ActionState.RUNABLE, tom.getState());
		assertEquals(ActionState.INIT, jerry.getState());
		dao.clear();
		cancel(tom);

		Thread.sleep(1000);
		dao.clear();
		tom = dao.load(tom.getId());
		assertEquals(ActionState.CANCELLED, tom.getState());
		assertTrue(tom.isFinalized());
		dao.clear();

		jerry = dao.load(jerry.getId());
		assertEquals(ActionState.CANCELLED, jerry.getState());
		assertTrue(jerry.isFinalized());

	}
	
	/** Demonstrates two processes and activation based on dependency completion
	 * @throws InterruptedException
	 */
	@Test
	public void twoProcessWithDependencyAddedAfterDependentCancelled() throws InterruptedException {
		addSecurityContext(null);
		Context tom_env = new Context();
		tom_env.putValue("arg", "tom rocks!");
		
		final Context jerry_env = new Context();
		jerry_env.putValue("arg", "jelly rolls");
	
		CloudProcess tom = dao.create(UserResource.Root, "tom", tom_env, null, null, CountDownAction.class);
		init(tom);
		Thread.sleep(3000);
		dao.clear();
		tom  = dao.load(tom.getUri());
		assertEquals(ActionState.RUNABLE, tom.getState());
		dao.clear();
		cancel(tom);
		Thread.sleep(1000);
		dao.clear();
		
		List<URI> jerryDependency = new ArrayList<URI>();
		jerryDependency.add(tom.getUri());
		
		CloudProcess jerry;
		try {
			jerry = dao.create(UserResource.Root, "jerry", jerry_env, jerryDependency, null, CountDownAction.class);
			fail("Exception expected");
		} catch (IllegalArgumentException e) {
			dao.clear();
			tom  = dao.load(tom.getUri());
			assertEquals(ActionState.CANCELLED, tom.getState());
		}

	}
	
	/** Demonstrates two processes and clean up processing associated with task init failure
	 * @throws InterruptedException
	 */
	@Test
	public void twoProcessWithPreestablishedDependencyInitFailTest() throws InterruptedException {
		addSecurityContext(null);
		
		Context tom_env = new Context();
		tom_env.putValue("arg", "throwInit");
		
		final Context jerry_env = new Context();
		jerry_env.putValue("arg", "jelly rolls");
	
		CloudProcess tom = dao.create(UserResource.Root, "tom", tom_env, null, null, CountDownAction.class);
		List<URI> jerryDependency = new ArrayList<URI>();
		jerryDependency.add(tom.getUri());
		CloudProcess jerry = dao.create(UserResource.Root, "jerry", jerry_env, jerryDependency, null, CountDownAction.class);
		init(tom);
		init(jerry);

		Thread.sleep(2000);
		dao.clear();

		
		tom  = dao.load(tom.getUri());
		jerry  = dao.load(jerry.getUri());
		assertEquals(ActionState.FAILED, tom.getState());
		assertEquals(ActionState.CANCELLED, jerry.getState());
		assertTrue(tom.isFinalized());
		assertTrue(jerry.isFinalized());

	}
	
	/** Demonstrates two processes and clean up processing associated with task runtime failure
	 * @throws InterruptedException
	 */
	@Test
	public void twoProcessWithPreestablishedDependencyFailTest() throws InterruptedException {
		addSecurityContext(null);

		Context tom_env = new Context();
		tom_env.putValue("arg", "throw5");
		
		final Context jerry_env = new Context();
		jerry_env.putValue("arg", "jelly rolls");
	
		CloudProcess tom = dao.create(UserResource.Root, "tom", tom_env, null, null, CountDownAction.class);
		List<URI> jerryDependency = new ArrayList<URI>();
		jerryDependency.add(tom.getUri());
		CloudProcess jerry = dao.create(UserResource.Root, "jerry", jerry_env, jerryDependency, null, CountDownAction.class);
		init(tom);
		init(jerry);

		Thread.sleep(2000);
		dao.clear();

		
		tom  = dao.load(tom.getUri());
		jerry  = dao.load(jerry.getUri());
		assertEquals(ActionState.FAILED, tom.getState());
		assertEquals(ActionState.CANCELLED, jerry.getState());
		assertTrue(jerry.isFinalized());
		assertTrue(tom.isFinalized());

	}
	
	/** Demonstrates dump of first of two processes and activation based on dependency completion
	 * @throws InterruptedException
	 */
	@Test
	public void twoProcessWithPreestablishedDependencyGetsDump() throws InterruptedException {
		addSecurityContext(null);
		Context tom_env = new Context();
		tom_env.putValue("arg", "tom rocks!");
		
		final Context jerry_env = new Context();
		jerry_env.putValue("arg", "jelly rolls");
	
		CloudProcess tom = dao.create(UserResource.Root, "tom", tom_env, null, null, CountDownAction.class);
		List<URI> jerryDependency = new ArrayList<URI>();
		jerryDependency.add(tom.getUri());
		CloudProcess jerry = dao.create(UserResource.Root, "jerry", jerry_env, jerryDependency, null, CountDownAction.class);
		init(tom);
		init(jerry);

		Thread.sleep(3000);
		dao.clear();
		
		
		tom  = dao.load(tom.getUri());
		jerry  = dao.load(jerry.getUri());
		assertEquals(ActionState.RUNABLE, tom.getState());
		assertEquals(ActionState.INIT, jerry.getState());
		dao.clear();
		dump(tom);
		
		dao.clear();
		
		Thread.sleep(2000);
		dao.clear();
		tom  = dao.load(tom.getUri());
		jerry  = dao.load(jerry.getUri());
		assertEquals(ActionState.CANCELLED, tom.getState());
		assertTrue(tom.isFinalized());
		assertEquals(ActionState.CANCELLED, jerry.getState());
		assertTrue(jerry.isFinalized());
		
		CountDownAction cda = (CountDownAction) ActionResource.dao.load(tom.getTask());
		assertEquals("dump entry point activated", 2000, cda.getCount());
		CountDownAction jcda = (CountDownAction) ActionResource.dao.load(jerry.getTask());
		assertEquals("cancel entry point not activated", 99, jcda.getCount());

	}
	
	/** Demonstrates a running process that gets blocked by an added dependency
	 * @throws InterruptedException
	 */
	@Test
	public void runningProcessGetsDependencyAdded() throws InterruptedException {
		addSecurityContext(null);
		Context tom_env = new Context();
		tom_env.putValue("arg", "tom rocks!");
		
		final Context jerry_env = new Context();
		jerry_env.putValue("arg", "jelly rolls");
	
		CloudProcess tom = dao.create(UserResource.Root, "tom", tom_env, null, null, CountDownAction.class);
		List<URI> jerryDependency = new ArrayList<URI>();
		init(tom);
		Thread.sleep(2000);
		dao.clear();
		refresh();
		Thread.sleep(2000);
		dao.clear();
		tom  = dao.load(tom.getUri());
		assertEquals(ActionState.RUNABLE, tom.getState());
		//
		CloudProcess jerry = dao.create(UserResource.Root, "jerry", jerry_env, null, null, CountDownAction.class);
		dao.clear();
		refresh();
		addDependentOn(tom, jerry);
		init(jerry);

		Thread.sleep(3000);
		dao.clear();
		
		
		tom  = dao.load(tom.getUri());
		jerry  = dao.load(jerry.getUri());
		assertEquals(ActionState.BLOCKED, tom.getState());
		assertEquals(ActionState.RUNABLE, jerry.getState());
	}
	
	/**  Demonstrates a running process that blocked by an added dependency resuming after the dependency completes
	 * @throws InterruptedException
	 */
	@Test
	public void runningProcessGetsDependencyAddedAndResumesOnCompletion() throws InterruptedException {
		addSecurityContext(null);
		Context tom_env = new Context();
		tom_env.putValue("arg", "tom rocks!");
		
		final Context jerry_env = new Context();
		jerry_env.putValue("arg", "jelly rolls");
	
		CloudProcess tom = dao.create(UserResource.Root, "tom", tom_env, null, null, CountDownAction.class);
		List<URI> jerryDependency = new ArrayList<URI>();
		init(tom);
		Thread.sleep(2000);
		dao.clear();
		refresh();
		Thread.sleep(2000);
		dao.clear();
		tom  = dao.load(tom.getUri());
		assertEquals(ActionState.RUNABLE, tom.getState());
		//
		CloudProcess jerry = dao.create(UserResource.Root, "jerry", jerry_env, null, null, CountDownAction.class);
		dao.clear();
		refresh();
		addDependentOn(tom, jerry);
		init(jerry);

		Thread.sleep(3000);
		dao.clear();
		
		
		tom  = dao.load(tom.getUri());
		jerry  = dao.load(jerry.getUri());
		assertEquals(ActionState.BLOCKED, tom.getState());
		assertEquals(ActionState.RUNABLE, jerry.getState());
		
		CountDownAction jerryAction = (CountDownAction) ActionResource.dao.load(jerry.getTask());
		jerryAction.setCount(1);
		ActionResource.dao.update(jerryAction);
		dao.clear();
		refresh();
		Thread.sleep(1000);
		dao.clear();
		tom  = dao.load(tom.getUri());
		jerry  = dao.load(jerry.getUri());
		assertEquals(ActionState.RUNABLE, tom.getState());
		assertEquals(ActionState.COMPLETE, jerry.getState());
		
	}
	
	
	/** Demonstrates a running process that gets blocked by an added dependency and cancelled
	 * @throws InterruptedException
	 */
	@Test
	public void runningProcessGetsDependencyAddedAndCancelled() throws InterruptedException {
		addSecurityContext(null);
		Context tom_env = new Context();
		tom_env.putValue("arg", "tom rocks!");
		
		final Context jerry_env = new Context();
		jerry_env.putValue("arg", "jelly rolls");
	
		CloudProcess tom = dao.create(UserResource.Root, "tom", tom_env, null, null, CountDownAction.class);
		List<URI> jerryDependency = new ArrayList<URI>();
		init(tom);
		Thread.sleep(2000);
		dao.clear();
		refresh();
		Thread.sleep(2000);
		dao.clear();
		tom  = dao.load(tom.getUri());
		assertEquals(ActionState.RUNABLE, tom.getState());
		//
		CloudProcess jerry = dao.create(UserResource.Root, "jerry", jerry_env, null, null, CountDownAction.class);
		dao.clear();
		refresh();
		addDependentOn(tom, jerry);
		init(jerry);

		Thread.sleep(3000);
		dao.clear();
		
		
		tom  = dao.load(tom.getUri());
		jerry  = dao.load(jerry.getUri());
		assertEquals(ActionState.BLOCKED, tom.getState());
		assertEquals(ActionState.RUNABLE, jerry.getState());
		
		CountDownAction jerryAction = (CountDownAction) ActionResource.dao.load(jerry.getTask());
		jerryAction.setCount(1);
		ActionResource.dao.update(jerryAction);
		cancel(tom);
		dao.clear();
		refresh();
		Thread.sleep(1000);
		dao.clear();
		tom  = dao.load(tom.getUri());
		jerry  = dao.load(jerry.getUri());
		assertEquals(ActionState.CANCELLED, tom.getState());
		assertEquals(ActionState.COMPLETE, jerry.getState());
	}
	
	/** Demonstrates a running process that gets blocked by an added dependency and cancelled
	 * @throws InterruptedException
	 */
	@Test
	public void runningProcessGetsDependencyAddedAndRunningProcessCancelled() throws InterruptedException {
		addSecurityContext(null);
		Context tom_env = new Context();
		tom_env.putValue("arg", "tom rocks!");
		
		final Context jerry_env = new Context();
		jerry_env.putValue("arg", "jelly rolls");
	
		CloudProcess tom = dao.create(UserResource.Root, "tom", tom_env, null, null, CountDownAction.class);
		List<URI> jerryDependency = new ArrayList<URI>();
		init(tom);
		Thread.sleep(2000);
		dao.clear();
		refresh();
		Thread.sleep(2000);
		dao.clear();
		tom  = dao.load(tom.getUri());
		assertEquals(ActionState.RUNABLE, tom.getState());
		//
		CloudProcess jerry = dao.create(UserResource.Root, "jerry", jerry_env, null, null, CountDownAction.class);
		dao.clear();
		refresh();
		addDependentOn(tom, jerry);
		init(jerry);

		Thread.sleep(3000);
		dao.clear();
		
		
		tom  = dao.load(tom.getUri());
		jerry  = dao.load(jerry.getUri());
		assertEquals(ActionState.BLOCKED, tom.getState());
		assertEquals(ActionState.RUNABLE, jerry.getState());
		
		CountDownAction jerryAction = (CountDownAction) ActionResource.dao.load(jerry.getTask());
		jerryAction.setCount(1);
		ActionResource.dao.update(jerryAction);
		cancel(jerry);
		dao.clear();
		refresh();
		Thread.sleep(1000);
		dao.clear();
		tom  = dao.load(tom.getUri());
		jerry  = dao.load(jerry.getUri());
		assertEquals(ActionState.CANCELLED, tom.getState());
		assertEquals(ActionState.CANCELLED, jerry.getState());
		CountDownAction ta = (CountDownAction) ActionResource.dao.load(tom.getTask());
		assertEquals("cancel has been called", 1000, ta.getCount());
		CountDownAction ja = (CountDownAction) ActionResource.dao.load(jerry.getTask());
		assertEquals("cancel has been called", 1000, ja.getCount());
	}
	
	/** Demonstrates a running process that gets blocked by an added dependency and cancelled
	 * @throws InterruptedException
	 */
	@Test
	public void runningProcessGetsDependencyAddedAndDumped() throws InterruptedException {
		addSecurityContext(null);
		Context tom_env = new Context();
		tom_env.putValue("arg", "tom rocks!");
		
		final Context jerry_env = new Context();
		jerry_env.putValue("arg", "jelly rolls");
	
		CloudProcess tom = dao.create(UserResource.Root, "tom", tom_env, null, null, CountDownAction.class);
		List<URI> jerryDependency = new ArrayList<URI>();
		init(tom);
		Thread.sleep(2000);
		dao.clear();
		refresh();
		Thread.sleep(2000);
		dao.clear();
		tom  = dao.load(tom.getUri());
		assertEquals(ActionState.RUNABLE, tom.getState());
		//
		CloudProcess jerry = dao.create(UserResource.Root, "jerry", jerry_env, null, null, CountDownAction.class);
		dao.clear();
		refresh();
		addDependentOn(tom, jerry);
		init(jerry);

		Thread.sleep(3000);
		dao.clear();
		
		
		tom  = dao.load(tom.getUri());
		jerry  = dao.load(jerry.getUri());
		assertEquals(ActionState.BLOCKED, tom.getState());
		assertEquals(ActionState.RUNABLE, jerry.getState());
		
		CountDownAction jerryAction = (CountDownAction) ActionResource.dao.load(jerry.getTask());
		jerryAction.setCount(1);
		ActionResource.dao.update(jerryAction);
		dump(tom);
		dao.clear();
		refresh();
		Thread.sleep(1000);
		dao.clear();
		tom  = dao.load(tom.getUri());
		jerry  = dao.load(jerry.getUri());
		assertEquals(ActionState.CANCELLED, tom.getState());
		assertEquals(ActionState.COMPLETE, jerry.getState());
		CountDownAction ta = (CountDownAction) ActionResource.dao.load(tom.getTask());
		assertEquals("dump has been called", 2000, ta.getCount());
	}
	
	/** Demonstrates a running process that gets blocked by an added dependency and cancelled
	 * @throws InterruptedException
	 */
	@Test
	public void runningProcessGetsDependencyAddedAndRunningProcessDumped() throws InterruptedException {
		addSecurityContext(null);
		Context tom_env = new Context();
		tom_env.putValue("arg", "tom rocks!");
		
		final Context jerry_env = new Context();
		jerry_env.putValue("arg", "jelly rolls");
	
		CloudProcess tom = dao.create(UserResource.Root, "tom", tom_env, null, null, CountDownAction.class);
		List<URI> jerryDependency = new ArrayList<URI>();
		init(tom);
		Thread.sleep(2000);
		dao.clear();
		refresh();
		Thread.sleep(2000);
		dao.clear();
		tom  = dao.load(tom.getUri());
		assertEquals(ActionState.RUNABLE, tom.getState());
		//
		CloudProcess jerry = dao.create(UserResource.Root, "jerry", jerry_env, null, null, CountDownAction.class);
		dao.clear();
		refresh();
		addDependentOn(tom, jerry);
		init(jerry);

		Thread.sleep(3000);
		dao.clear();
		
		
		tom  = dao.load(tom.getUri());
		jerry  = dao.load(jerry.getUri());
		assertEquals(ActionState.BLOCKED, tom.getState());
		assertEquals(ActionState.RUNABLE, jerry.getState());
		
		CountDownAction jerryAction = (CountDownAction) ActionResource.dao.load(jerry.getTask());
		jerryAction.setCount(1);
		ActionResource.dao.update(jerryAction);
		dump(jerry);
		dao.clear();
		refresh();
		Thread.sleep(1000);
		dao.clear();
		tom  = dao.load(tom.getUri());
		jerry  = dao.load(jerry.getUri());
		assertEquals(ActionState.CANCELLED, tom.getState());
		assertEquals(ActionState.CANCELLED, jerry.getState());
		CountDownAction ta = (CountDownAction) ActionResource.dao.load(tom.getTask());
		assertEquals("cancel has been called", 1000, ta.getCount());
		CountDownAction ja = (CountDownAction) ActionResource.dao.load(jerry.getTask());
		assertEquals("dump has been called", 2000, ja.getCount());
	}


	
	protected void addSecurityContext(User user) {
		final User u;
		if(user == null) {
			try {
				User temp = com.googlecode.objectify.ObjectifyService.ofy().load().type(User.class).id(UserResource.Root.getId()).safeGet();
			} catch (com.googlecode.objectify.NotFoundException e) {
				User temp = UserResource.Root;
				URI initial = temp.getUri();
				temp.setId(null);
				Key<User>key =  com.googlecode.objectify.ObjectifyService.ofy().save().entity(temp).now();
				temp = com.googlecode.objectify.ObjectifyService.ofy().load().type(User.class).id(UserResource.Root.getId()).get();
				UserResource.Root.setId(temp.getId());
				UserResource.Root.setUri(temp.getUri());
				System.out.println("============================>addSecurity notfoundexception initial="+initial.toString()+" final "+temp.toString());
			}
			u = UserResource.Root;
			System.out.println("============================>Root is "+u.getUri());

		} else {
			u = user;
		}
		SecurityContext context = new SecurityContext() {

			@Override
			public String getAuthenticationScheme() {
				return "Basic";
			}

			@Override
			public Principal getUserPrincipal() {
				return new Principal() {

					@Override
					public String getName() {
						return u.getName();
					}};
			}

			@Override
			public boolean isSecure() {
				return true;
			}

			@Override
			public boolean isUserInRole(String arg0) {
				return true;
			}};
			
			super.securityContext = context;
	}

}
