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

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.text.shared.SafeHtmlRenderer;

public abstract class StyledTextCellRenderer<C> implements SafeHtmlRenderer<C>{
	public interface Template extends SafeHtmlTemplates {	    
	    @Template("<div class='{2}' title='{1}' >{0}</div>")
	    SafeHtml text(SafeHtml text, String title, String style);
	}
  
  protected static Template template;

  protected final String style;

  public StyledTextCellRenderer(String style) {
	  if(template == null)
		  template = GWT.create(Template.class);
	  this.style = style;
  }

  public SafeHtml render(C object) {
	  String name = this.getValue(object); 
    return template.text(SafeHtmlUtils.fromString(name), getTooltip(object), style);
  }

  public void render(C object, SafeHtmlBuilder appendable) {
	String name = this.getValue(object);  
    appendable.append(template.text(SafeHtmlUtils.fromString(name), name, style));
  }
	
  public abstract String getValue(C object) ;
  
  protected String getTooltip(C object) {
	  return getValue(object);
  }

}
