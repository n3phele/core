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
package n3phele.client.model;

import java.util.AbstractList;
import java.util.List;
import java.util.RandomAccess;

import com.google.gwt.core.client.JavaScriptObject;

public class JsList {
	public static <T extends JavaScriptObject> List<T> asList(JavaScriptObject a) {
		return new ArrayList<T>(a);
	}
    private static class ArrayList<E  extends JavaScriptObject> extends AbstractList<E>
	implements RandomAccess
    {
    private static final long serialVersionUID = 1L;
	private final JavaScriptObject a;
	


	ArrayList(JavaScriptObject array) {
            if (array==null)
                throw new NullPointerException();
	    a = array;
	}

	public int size() {
	    return length();
	}

	private final native int length() /*-{ return this.@n3phele.client.model.JsList$ArrayList::a.length; }-*/;
	private final native E getter(int i) /*-{ return this.@n3phele.client.model.JsList$ArrayList::a[i]; }-*/;
	private final native void setter(int i, E e) /*-{ this.@n3phele.client.model.JsList$ArrayList::a[i] = e; }-*/;
	private final native void removeByIndex(int i) /*-{ this.@n3phele.client.model.JsList$ArrayList::a.splice(i,1); }-*/;
	private final native void clearAll() /*-{ this.@n3phele.client.model.JsList$ArrayList::a = []; }-*/;

	public E get(int index) {
	    return getter(index);
	}

	public E set(int index, E element) {
	    E oldValue = getter(index);
	    setter(index, element);
	    return oldValue;
	}

        public int indexOf(Object o) {
            if (o==null) {
                for (int i=0; i<length(); i++)
                    if (getter(i)==null)
                        return i;
            } else {
                for (int i=0; i<length(); i++)
                    if (o.equals(getter(i)))
                        return i;
            }
            return -1;
        }

        public boolean contains(Object o) {
            return indexOf(o) != -1;
        }
        
        @Override
        public E remove(int index) {
			E old = getter(index);
			removeByIndex(index);
			return old;
        	
        }
        
        @Override
        public void clear() {
			clearAll();
        }
    }

}
