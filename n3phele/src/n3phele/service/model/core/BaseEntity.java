package n3phele.service.model.core;

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

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlRootElement(name="BaseEntity")
@XmlType(name="BaseEntity")
public class BaseEntity extends Entity {
	
	public BaseEntity() {}
	
	public BaseEntity(Entity entity) {
		this.name = entity.name;
		this.uri = entity.uri;
		this.owner = entity.owner;
		this.isPublic = entity.isPublic;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String
				.format("BaseEntity [name=%s, uri=%s, owner=%s, isPublic=%s]",
						name, uri, owner, isPublic);
	}

}
