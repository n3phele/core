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

import com.google.gwt.text.shared.AbstractRenderer;

public class FileSizeRenderer extends AbstractRenderer<Long> {

	  public FileSizeRenderer() {
	    
	  }


	  public String render(Long size) {
	    if (size == null) {
	      return "";
	    }
	    double result = (double)size;
	    String formatted;
	    if(size < 1024) {
	    	formatted =  size+" B";
	    } else if(size < 1048576) {
	    	result = Math.round(result/102.4);
	    	formatted = Double.toString(result/10)+" KB";
	    } else if(size < 1073741824) {
	    	result = Math.round(result/104857.6);
	    	formatted = Double.toString(result/10)+" MB";
	    } else {
	    	result = Math.round(result/107374182.4);
	    	formatted = Double.toString(result/10)+" GB";
	    }
	    return formatted;
	  }
	}
