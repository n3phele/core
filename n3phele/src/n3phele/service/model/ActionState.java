package n3phele.service.model;

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

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum(String.class)
public enum ActionState {
	NEWBORN,  /*
			   * Initial state after constructor. Next State: finalization of object setup -> INIT 
			   * 
			   */
	INIT, 	  /*
	           * Wait for dependencies to complete. Next State: Cancel or Dump request or Dependencies complete 
	           * 													with error -> CANCELLED
	           * 											    No dependencies or complete no error, finalization
	           * 												  of parameter,
	           * 												then set to execute one time initialization -> RUNABLE
	           */
	RUNABLE,  /*
			   * Task ready to run. Next State: Cancel or Dump request or Dependencies finish with error -> CANCELLED
			   * 					   	        Task put on RunQ -> RUNNING
			   * 								Dependencies added -> BLOCKED
			   */
	BLOCKED,  /*
			   * Wait for dependencies to complete. Next State: Cancel or Dump request or Dependencies complete
			   * 												   with error -> CANCELLED
			   * 											    Dependencies complete no error -> RUNABLE
			   */
	COMPLETE, /*
	  		   * Task processing complete. Next State: Task isFinalized is true
	  		   */
	CANCELLED, /*
	  		    * Task has been cancelled. Dump of state optionally taken. Next State: Task isFinalized is true
	  		    */ 
	FAILED,	   /*
	  		    * Task processing exception. Next State: Task isFinalized is true
	  		    */
	ONEXIT,	   /*
				* Wait for ONEXIT dependencies to complete. Next State: COMPLETE
				*/
	CLEANUP,	/*
				* Wait for ONEXIT dependencies to complete. Next State: FAILED
				*/
	CANCELLING,	/* Wait for ONEXIT dependencies to complete. Next State: CANCELLED */
	DUMPING		/* Wait for ONEXIT dependencies to complete. Next State: CANCELLED */
}
