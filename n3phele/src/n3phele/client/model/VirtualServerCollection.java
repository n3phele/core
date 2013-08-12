/**
 * @author Gabriela Lavina
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

package n3phele.client.model;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.datepicker.client.CalendarUtil;

import n3phele.client.model.Entity;
import n3phele.client.model.VirtualServer;


public class VirtualServerCollection<T extends VirtualServer> extends Entity {

	protected VirtualServerCollection() {  }

	/**
	 * @return the total
	 */
	public final int getTotal() {
		int total = total();
		if(total < 0) {
			if(getElements() != null)
				return getElements().size();
		} else {
			return total;
		}
		return 0;
	}

	/**
	 * @return the elements
	 */

	public final List<VirtualServer> getElements() {
		JavaScriptObject jsa = elements();
		return JsList.asList(jsa);
	}
	private final native JavaScriptObject elements() /*-{
		var array = [];
		if(this.elements != undefined && this.elements !=null) {
			if(this.elements.length==undefined) {
				array[0] = this.elements;
			} else {
				array = this.elements;
			}
		}
		return array;
	}-*/;

	/**
	 * @return the total
	 */
	private final native int total() /*-{
		return this.total==null?-1:parseInt(this.total);
	}-*/;

	private final native boolean isUndefined(Object o) /*-{
		return o==undefined
	}-*/;

	public final ArrayList<Double> dayCost(){
		return dayCost(new Date());
	}

	@SuppressWarnings("deprecation")
	public final ArrayList<Double> dayCost(Date now){
		Date today = now;

		ArrayList<Double> totalCost = new ArrayList<Double>(24);
		for(int i=0; i<24; i++)
			totalCost.add(i, 0.0);

		Date yesterday = CalendarUtil.copyDate(today);
		CalendarUtil.addDaysToDate(yesterday, -1);


		List<VirtualServer> list = getElements();
		for(VirtualServer s:list){

			Date dateCreated = s.getCreated();
			Date dateEnd = null;
			if(s.getEndDate() != null)
				dateEnd = s.getEndDate();			

			if(!(dateCreated.before(yesterday)||dateCreated.after(today))){

				//VirtualServer was created today			
				if((today.getYear() == dateCreated.getYear()) &&
						(today.getMonth() == dateCreated.getMonth()) &&
						(today.getDate() == dateCreated.getDate())){

					if(dateCreated.getTime() <= today.getTime()){

						//VirtualServer is still running
						if(dateEnd == null || dateEnd.after(today)){
							int h = 23;
							
							// VirtualServer started with the same minutes as today or minutes is greater than today
							if (dateCreated.getMinutes() >= today.getMinutes()) {
								h--;
								for (int i = dateCreated.getHours(); i < today.getHours(); i++) {
									totalCost.set(h,totalCost.get(h)+ Double.parseDouble(s.getPrice()));
									h--;
								}
							} 
							
							// VirtualServer stated with minutes smaller than today
							if (dateCreated.getMinutes() < today.getMinutes()) {
								for (int i = dateCreated.getHours(); i <= today.getHours(); i++) {
									totalCost.set(h,totalCost.get(h)+ Double.parseDouble(s.getPrice()));
									h--;
								}
							}					
							
						}
						//Virtual Server was shutdown today
						else{
							int h = 23 - (today.getHours() - dateEnd.getHours());
							
							// VirtualServer started with the same minutes as end date or minutes is greater than end date
							if (dateCreated.getMinutes() >= dateEnd.getMinutes()) {
								h--;
								for (int i = dateCreated.getHours(); i < dateEnd.getHours(); i++) {
									totalCost.set(h,totalCost.get(h)+ Double.parseDouble(s.getPrice()));
									h--;
								}
							} 
							
							// VirtualServer stated with minutes smaller than end date
							if (dateCreated.getMinutes() < dateEnd.getMinutes()) {
								for (int i = dateCreated.getHours(); i <= dateEnd.getHours(); i++) {
									totalCost.set(h,totalCost.get(h)+ Double.parseDouble(s.getPrice()));
									h--;
								}
							}
							
						}
					}
				}
				//Virtual Server was created yesterday
				else{
					if(dateCreated.getHours() >= yesterday.getHours()){

						//Virtual Server is still running
						if(dateEnd == null || dateEnd.after(today)){
							int diff = dateCreated.getHours() - yesterday.getHours()-1;
							if(diff == -1) diff = 0;
							
							// VirtualServer started with the same minutes as today or minutes is greater than today
							if (dateCreated.getMinutes() >= today.getMinutes()) {
								//h--;
								for (int i = diff; i < 23; i++) {
									totalCost.set(i,totalCost.get(i)+ Double.parseDouble(s.getPrice()));
									//h--;
								}
							} 
							
							// VirtualServer stated with minutes smaller than today
							if (dateCreated.getMinutes() < today.getMinutes()) {
								for (int i = diff; i <= 23; i++) {
									totalCost.set(i,totalCost.get(i)+ Double.parseDouble(s.getPrice()));
									//h--;
								}
							}
						}
						else{
							//Virtual Server was shutdown yesterday
							if((yesterday.getYear()== dateEnd.getYear())&&
									(yesterday.getMonth() == dateEnd.getMonth()) &&
									(yesterday.getDate()== dateEnd.getDate())){

								int diff = dateCreated.getHours() - yesterday.getHours()-1;
								int limit = dateEnd.getHours() - today.getHours()-1;
								if(diff == -1) diff = 0;
								
								// VirtualServer started with the same minutes as limit or minutes is greater than limit
								if (dateCreated.getMinutes() >= dateEnd.getMinutes()) {
									for (int i = diff; i < limit; i++) {
										totalCost.set(i,totalCost.get(i)+ Double.parseDouble(s.getPrice()));
									}
								} 
								
								// VirtualServer stated with minutes smaller than limit
								if (dateCreated.getMinutes() < dateEnd.getMinutes()) {
									for (int i = diff; i <= limit; i++) {
										totalCost.set(i,totalCost.get(i)+ Double.parseDouble(s.getPrice()));
									}
								}
								
							}
							//Virtual Server was shutdown today
							else{
								int posToday = 0;
								int diff = 0;
								int limit = 0;
								if(dateCreated.getHours() > yesterday.getHours()){
									diff = dateCreated.getHours() -  yesterday.getHours()-1;
									if(diff == -1) diff = 0;
									limit = diff+ 24 -dateCreated.getHours();
								}
								else{
									diff = 0;
									limit = 24 -  yesterday.getHours();
								}

								posToday = limit;
								for(int i = diff; i < limit; i++){
									totalCost.set(i,totalCost.get(i)+Double.parseDouble(s.getPrice()));
								}
								
								limit = posToday + dateEnd.getHours();

								// VirtualServer started with the same minutes as limit or minutes is greater than limit
								if (dateCreated.getMinutes() >= dateEnd.getMinutes()) {
									for (int i = posToday; i < limit; i++) {
										totalCost.set(i,totalCost.get(i)+ Double.parseDouble(s.getPrice()));
									}
								} 
								
								// VirtualServer stated with minutes smaller than limit
								if (dateCreated.getMinutes() < dateEnd.getMinutes()) {
									for (int i = posToday; i <= limit; i++) {
										totalCost.set(i,totalCost.get(i)+ Double.parseDouble(s.getPrice()));
									}
								}
								
							}
						}

					}
				}

			}
			//VirtualServer was created before the 24 hour period
			else{

				//VirtualServer is still running
				if((dateEnd == null && dateCreated.before(today)) || (dateCreated.before(yesterday) && dateEnd.after(today))){
					
					// VirtualServer started with the same minutes as today or minutes is greater than today
					if (dateCreated.getMinutes() >= today.getMinutes()) {
						for (int i = 0; i < 23; i++) {
							totalCost.set(i,totalCost.get(i)+ Double.parseDouble(s.getPrice()));
						}
					} 
					
					// VirtualServer stated with minutes smaller than limit
					if (dateCreated.getMinutes() < today.getMinutes()) {
						for (int i = 0; i <= 23; i++) {
							totalCost.set(i,totalCost.get(i)+ Double.parseDouble(s.getPrice()));
						}
					}
				}

				else if(dateEnd != null){

					//Virtual Server was shutdown today
					if((today.getYear() == dateEnd.getYear()) &&
							(today.getMonth() == dateEnd.getMonth()) &&
							today.getDate() == dateEnd.getDate()){

						int limit = 23 - (today.getHours() - dateEnd.getHours());
						
						// VirtualServer started with the same minutes as end date or minutes is greater than end date
						if (dateCreated.getMinutes() >= dateEnd.getMinutes()) {
							for (int i = 0; i < limit; i++) {
								totalCost.set(i,totalCost.get(i)+ Double.parseDouble(s.getPrice()));
							}
						} 
						
						// VirtualServer stated with minutes smaller than end date
						if (dateCreated.getMinutes() < dateEnd.getMinutes()) {
							for (int i = 0; i <= limit; i++) {
								totalCost.set(i,totalCost.get(i)+ Double.parseDouble(s.getPrice()));
							}
						}
						
					}
					//Virtual Server was shutdown yesterday
					else if(dateEnd.getHours() >= yesterday.getHours() && dateEnd.getDate() == yesterday.getDate()){
						int limit = dateEnd.getHours() - today.getHours()-1;
						
						// VirtualServer started with the same minutes as end date or minutes is greater than end date
						if (dateCreated.getMinutes() >= dateEnd.getMinutes()) {
							for (int i = 0; i < limit; i++) {
								totalCost.set(i,totalCost.get(i)+ Double.parseDouble(s.getPrice()));
							}
						} 
						
						// VirtualServer stated with minutes smaller than end date
						if (dateCreated.getMinutes() < dateEnd.getMinutes()) {
							for (int i = 0; i <= limit; i++) {
								totalCost.set(i,totalCost.get(i)+ Double.parseDouble(s.getPrice()));
							}
						}
						
					}
				}		

			}
		}

		return totalCost;
	}

	@SuppressWarnings("deprecation")
	public final ArrayList<Double> weekCost() {

		ArrayList<Double> totalCost = new ArrayList<Double>(7);
		for(int i=0; i<7; i++)
			totalCost.add(i, 0.0);

		Date today = new Date();

		ArrayList<Double> dailyCost = new ArrayList<Double>(24);

		int diff = 23 - today.getHours();
		dailyCost = dayCost(today);
		for (int j = 0 ; j < 24; j++) {
			if(j >= diff)
				totalCost.set(6, totalCost.get(6) + dailyCost.get(j));
		}
		CalendarUtil.addDaysToDate(today, -1);
		today.setHours(23);
		for (int i = 5; i >= 0; i--) {
			dailyCost = dayCost(today);
			for (int j = 0 ; j < 24; j++) {
				totalCost.set(i, totalCost.get(i) + dailyCost.get(j));
			}
			CalendarUtil.addDaysToDate(today, -1);
			today.setHours(23);
		}

		return totalCost;
	}
	
	@SuppressWarnings("deprecation")
	public final ArrayList<Double> monthCost() {

		ArrayList<Double> totalCost = new ArrayList<Double>(30);
		for(int i=0; i<30; i++)
			totalCost.add(i, 0.0);

		Date today = new Date();

		ArrayList<Double> dailyCost = new ArrayList<Double>(24);

		int diff = 23 - today.getHours();
		dailyCost = dayCost(today);
		for (int j = 0 ; j < 24; j++) {
			if(j >= diff)
				totalCost.set(29, totalCost.get(29) + dailyCost.get(j));
		}
		CalendarUtil.addDaysToDate(today, -1);
		today.setHours(23);
		for (int i = 28; i >= 0; i--) {
			dailyCost = dayCost(today);
			for (int j = 0 ; j < 24; j++) {
				totalCost.set(i, totalCost.get(i) + dailyCost.get(j));
			}
			CalendarUtil.addDaysToDate(today, -1);
			today.setHours(23);
		}

		return totalCost;
	}
	
}