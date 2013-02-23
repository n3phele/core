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

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;

public class SafeDate {
	public static Date parse(String str) {
		if(str == null)
			return null;
		if(str.endsWith("Z")) {
			str = str.replace("Z", "+00:00");
		}
		int i = str.lastIndexOf(".");
		if(i < 0) {
			str = str.substring(0, str.length()-6)+".000"+str.substring(str.length()-6);
		}
		return DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.ISO_8601).parse(str);
	}
}
