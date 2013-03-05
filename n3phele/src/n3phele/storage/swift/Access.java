/*
 * (C) Copyright 2010-2013. Nigel Cook. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * 
 * Adapted from Luis Gervaso woorea project.
 * 
 * Licensed under the terms described in LICENSE file that accompanied this code, (the "License"); you may not use this file
 * except in compliance with the License. 
 * 
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on 
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the 
 *  specific language governing permissions and limitations under the License.
 *
 */

package n3phele.storage.swift;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

//@XmlRootElement(name="access")
public class Access  {
	public Access(){}

	public static final class Token {
		
		public static final class Tenant {
			public Tenant(){}
			@XmlElement
			private String id;
			@XmlElement
			private String name;
			@XmlElement
			private String description;
			@XmlElement
			private Boolean enabled;

			/**
			 * @return the id
			 */
			public String getId() {
				return id;
			}

			/**
			 * @return the name
			 */
			public String getName() {
				return name;
			}

			/**
			 * @return the description
			 */
			public String getDescription() {
				return description;
			}

			/**
			 * @return the enabled
			 */
			public Boolean getEnabled() {
				return enabled;
			}

			/* (non-Javadoc)
			 * @see java.lang.Object#toString()
			 */
			@Override
			public String toString() {
				return "Tenant [id=" + id + ", name=" + name + ", description="
						+ description + ", enabled=" + enabled + "]";
			}
			
			
		}
		@XmlElement
		private String id;
		@XmlElement
		private Date issued_at;
		@XmlElement
		private Date expires;
		@XmlElement
		private Tenant tenant;

		/**
		 * @return the id
		 */
		public String getId() {
			return id;
		}

    /**
     * @return the issued_at
     */
    public Date getIssued_at() {
      return issued_at;
    }

		/**
		 * @return the expires
		 */
		public Date getExpires() {
			return expires;
		}

		/**
		 * @return the tenant
		 */
		public Tenant getTenant() {
			return tenant;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Token [id=" + id + ", Issued_at=" + issued_at + ", expires=" + expires + ", tenant="
              + tenant + "]";
		}
		
	}
	@XmlType(name="service")
	public static final class Service {
		public Service(){}
		@XmlType(name="endpoint")
		public static final class Endpoint {
			public Endpoint(){}
			@XmlElement
			private String region;
			@XmlElement
			private String publicURL;
			@XmlElement
			private String internalURL;
			@XmlElement
			private String adminURL;

			/**
			 * @return the region
			 */
			public String getRegion() {
				return region;
			}

			/**
			 * @return the publicURL
			 */
			public String getPublicURL() {
				return publicURL;
			}

			/**
			 * @return the internalURL
			 */
			public String getInternalURL() {
				return internalURL;
			}

			/**
			 * @return the adminURL
			 */
			public String getAdminURL() {
				return adminURL;
			}

			/* (non-Javadoc)
			 * @see java.lang.Object#toString()
			 */
			@Override
			public String toString() {
				return "Endpoint [region=" + region + ", publicURL="
						+ publicURL + ", internalURL=" + internalURL
						+ ", adminURL=" + adminURL + "]";
			}
			
		}
		@XmlElement
		private String type;
		@XmlElement
		private String name;
		
		@XmlElement(name="endpoints")
		private List<Endpoint> endpoints;
		@XmlElement
		private List<Link> endpoints_links;

		/**
		 * @return the type
		 */
		public String getType() {
			return type;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @return the endpoints
		 */
		public List<Endpoint> getEndpoints() {
			return endpoints;
		}

		/**
		 * @return the endpointsLinks
		 */
		public List<Link> getEndpoints_links() {
			return endpoints_links;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Service [type=" + type + ", name=" + name + ", endpoints="
					+ endpoints + ", endpoints_links=" + endpoints_links + "]";
		}
		
	}
	
	public static final class Link {
		public Link(){}
		@XmlElement
		private String rel;
		@XmlElement
		private String href;
		@XmlElement
		private String type;

		/**
		 * @return the rel
		 */
		public String getRel() {
			return rel;
		}

		/**
		 * @return the href
		 */
		public String getHref() {
			return href;
		}

		/**
		 * @return the type
		 */
		public String getType() {
			return type;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Link [rel=" + rel + ", href=" + href + ", type=" + type + "]";
		}
		
	}
	
	public static final class User {
		public User(){}
		public static final class Role {
			public Role() {}
			@XmlElement
			private String id;
			@XmlElement
			private String name;

			/**
			 * @return the id
			 */
			public String getId() {
				return id;
			}

			/**
			 * @return the name
			 */
			public String getName() {
				return name;
			}

			/* (non-Javadoc)
			 * @see java.lang.Object#toString()
			 */
			@Override
			public String toString() {
				return "Role [id=" + id + ", name=" + name + "]";
			}
			
		}
		@XmlElement
		private String id;
		@XmlElement
		private String name;
		@XmlElement
		private String username;
		@XmlElement
		private List<Role> roles;
		@XmlElement
		private List<Link> roles_links;

		/**
		 * @return the id
		 */
		public String getId() {
			return id;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @return the username
		 */
		public String getUsername() {
			return username;
		}

		/**
		 * @return the roles
		 */
		public List<Role> getRoles() {
			return roles;
		}

		/**
		 * @return the rolesLinks
		 */
		public List<Link> getRoles_links() {
			return roles_links;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "User [id=" + id + ", name=" + name + ", username="
					+ username + ", roles=" + roles + ", roles_links="
					+ roles_links + "]";
		}
		
	}
	@XmlElement
	private Token token;
	@XmlElement
	private List<Service> serviceCatalog;
	@XmlElement
	private User user;
	@XmlElement
	private HashMap<String, Object> metadata;

	/**
	 * @return the token
	 */
	public Token getToken() {
		return token;
	}

	/**
	 * @return the serviceCatalog
	 */
	public List<Service> getServiceCatalog() {
		return serviceCatalog;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @return the metadata
	 */
	public Map<String, Object> getMetadata() {
		return metadata;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Access [token=" + token + ", serviceCatalog=" + serviceCatalog
				+ ", user=" + user + ", metadata=" + metadata + "]";
	}
	
}
