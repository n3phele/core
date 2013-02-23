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

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.text.shared.SafeHtmlRenderer;

public class HTMLTextCellRenderer implements SafeHtmlRenderer<String> {

	  private static HTMLTextCellRenderer instance;

	  public static HTMLTextCellRenderer getInstance() {
	    if (instance == null) {
	      instance = new HTMLTextCellRenderer();
	    }
	    return instance;
	  }

	  private HTMLTextCellRenderer() {
	  }

	  public SafeHtml render(String object) {
	    return SafeHtmlUtils.fromTrustedString(object);
	  }

	  public void render(String object, SafeHtmlBuilder appendable) {
	    appendable.append(SafeHtmlUtils.fromTrustedString(object));
	  }
}
