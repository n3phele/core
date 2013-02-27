/**
 * @author Nigel Cook
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
package n3phele.client.presenter.helpers;

import n3phele.client.model.CloudProcessSummary;

public class ProcessUpdateHelper {

	public static int updateProcess(CloudProcessSummary process) {
//		int complete = process.getPercentx10Complete();
//		if(complete < 0) return 0;
//		if(complete >= 1000) return complete;
//		
//		Date started = process.getStart();
//		long duration = process.getDuration()*1000L;
//		Date lastUpdate = process.getLastUpdate();
//		if(lastUpdate == null || started == null || duration <= 0 )
//			return 0;
//		long now = new Date().getTime();
//		double interval = (lastUpdate.getTime()-started.getTime());
//		if(interval > 0) {
//			if(complete > 0) {
//				int complete1 = (int) ((now - started.getTime()) * complete / interval);
//				int complete2 = (int) ((now - started.getTime())*1000/duration);
//				complete = Math.min(complete1, complete2);
//			} else
//				complete = (int) ((now - started.getTime())*1000/duration);
//		}
//		if(complete >= 1000) complete = 975;
//		return complete;
		return 0; // FIXME
	}

}
