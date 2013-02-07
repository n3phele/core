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

package n3phele.storage;

import javax.ws.rs.core.StreamingOutput;

public class ObjectStream {
	private StreamingOutput outputStream;
	private String contextType;
	public ObjectStream(StreamingOutput stream, String contentType) {
		this.outputStream = stream;
		this.contextType = contentType;
	}
	
	
	/**
	 * @return the outputStream
	 */
	public StreamingOutput getOutputStream() {
		return outputStream;
	}


	/**
	 * @return the contextType
	 */
	public String getContextType() {
		return contextType;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("ObjectStream [outputStream=%s, contextType=%s]",
				outputStream, contextType);
	}
	
}
