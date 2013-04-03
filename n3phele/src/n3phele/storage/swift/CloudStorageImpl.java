/*
 * (C) Copyright 2010-2013. Nigel Cook. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * 
 * Portions dapted from Luis Gervaso woorea project.
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

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.core.UriBuilder;

import n3phele.service.core.ForbiddenException;
import n3phele.service.core.NotFoundException;
import n3phele.service.model.core.Credential;
import n3phele.service.model.core.Helpers;
import n3phele.service.model.repository.FileNode;
import n3phele.service.model.repository.Repository;
import n3phele.service.model.repository.UploadSignature;
import n3phele.storage.CloudStorageInterface;

import org.apache.commons.codec.binary.Hex;

import com.amazonaws.services.s3.internal.Mimetypes;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;


public class CloudStorageImpl implements CloudStorageInterface {
	private static Logger log = Logger.getLogger(CloudStorageImpl.class.getName());
	public CloudStorageImpl() {  }

	/* (non-Javadoc)
	 * @see n3phele.storage.CloudStorageInterface#createBucket(n3phele.service.model.repository.Repository)
	 */
	@Override
	public boolean createBucket(Repository repo) throws ForbiddenException {
		SwiftClient swiftClient = null;
		try {
			Credential credential = repo.getCredential().decrypt();
			Access access;
			try {
				access = getAccess(repo.getTarget(), credential.getAccount(), credential.getSecret());
			} catch (UniformInterfaceException e) {
				int status = e.getResponse().getStatus();
				if(status == 401)
					throw new ForbiddenException("Invalid credentials entered");
				throw e;
			}
			
			swiftClient = new SwiftClient(access, getRegion(repo));
			Map<String, String> params = new HashMap<String,String>();
			params.put("delimiter", "/");
			params.put("limit", "1");
			List<SwiftObject> content = swiftClient.listObjects(getContainer(repo), params);
			// it exists and the current account owns it
            return false;
		} catch (UniformInterfaceException e) {
			int status = e.getResponse().getStatus();
			if(status == 401)
				throw new ForbiddenException("Bucket "+repo.getRoot()+" has already been created by another user.");
			if(status == 404) {
				boolean result = swiftClient.createContainer(getContainer(repo));
	        	return result;
			}
			log.log(Level.WARNING, "Swift getObject exception", e);
			throw e;
		}
	}
	/* (non-Javadoc)
	 * @see n3phele.storage.CloudStorageInterface#getMetadata(n3phele.service.model.repository.Repository, java.lang.String)
	 */
	@Override
	public FileNode getMetadata(Repository repo, String filename) {
		SwiftClient swiftClient = null;
		try {
			Credential credential = repo.getCredential().decrypt();
			Access access = getAccess(repo.getTarget(), credential.getAccount(), credential.getSecret());	
			
			swiftClient = new SwiftClient(access, getRegion(repo));
			
			ClientResponse headOfObj = swiftClient.getObjectMetadata(getContainer(repo), filename);
			FileNode file;
			if("application/directory".equals(headOfObj.getHeaders().getFirst("Context_Type"))) {
				String fullName = filename;
				if(fullName.endsWith("/")) {
					fullName = fullName.substring(0, fullName.length()-1);
				}
				int end = fullName.lastIndexOf("/");
				String name = fullName.substring(end+1);
				String prefix = end < 0 ? "" : fullName.substring(0, end);
				ClientResponse metadata = swiftClient.getContainerMetadata(getContainer(repo));
				String readAcl = metadata.getHeaders().getFirst("X-Container-Read");
				boolean isPublic = readAcl != null && readAcl.startsWith(".r:*");
				file = FileNode.newFolder(name, prefix, repo, isPublic);
				file.setModified(headOfObj.getLastModified());
			} else {
				int end = filename.lastIndexOf("/");
				String name =filename.substring(end+1);
				String prefix = end < 0 ? "" : filename.substring(0, end+1);
				UriBuilder build = UriBuilder.fromUri(swiftClient.getTarget());
				String canonical = build.path(getContainer(repo)).path(filename).build().toString();
				file = FileNode.newFile(name, prefix, repo, headOfObj.getLastModified(), headOfObj.getLength(), canonical);
				file.setMime(headOfObj.getHeaders().getFirst("Context_Type"));
			}
			log.info("File:"+file);
			return file;
		} catch (UniformInterfaceException e) {
			int status = e.getResponse().getStatus();
			if(status == 401)
				throw new ForbiddenException("Unauthorized to access container "+repo.getRoot());
		}
		return null;
	}
	/* (non-Javadoc)
	 * @see n3phele.storage.CloudStorageInterface#deleteFile(n3phele.service.model.repository.Repository, java.lang.String)
	 */
	@Override
	public boolean deleteFile(Repository repo, String filename) {
		boolean result = false;
		try {
			SwiftClient swiftClient = null;
			Credential credential = repo.getCredential().decrypt();
			Access access = getAccess(repo.getTarget(), credential.getAccount(), credential.getSecret());	
			
			swiftClient = new SwiftClient(access, getRegion(repo));
			return swiftClient.removeObject(getContainer(repo), filename);
		} catch (UniformInterfaceException e) {
			log.warning("Unauthorized to access Container "+repo.getRoot());
		}
		return result;
	}
	/* (non-Javadoc)
	 * @see n3phele.storage.CloudStorageInterface#deleteFolder(n3phele.service.model.repository.Repository, java.lang.String)
	 */
	@Override
	public boolean deleteFolder(Repository repo, String filename) {
		boolean result = false;
		SwiftClient swiftClient = null;
		Credential credential = repo.getCredential().decrypt();
		Access access = getAccess(repo.getTarget(), credential.getAccount(), credential.getSecret());	
		
		swiftClient = new SwiftClient(access, getRegion(repo));
		int retry = 3;
		
		// setPermissions(repo, filename, false); // not needed since swift pattern is global
		if(!filename.endsWith("/")) {
			filename += "/";
		}
		Map<String, String> params = new HashMap<String,String>();
		params.put("delimiter", "/");
		params.put("prefix", filename);
		params.put("limit", "99999");

		while(retry-- > 0) {
			try {

				List<SwiftObject> list = swiftClient.listObjects(getContainer(repo), params);
				log.info("Delete "+repo.getRoot()+" gets "+list.size());
				if(list.isEmpty()) {
					result = true;
					break;
				} else {
					retry++;
				}
			    for (SwiftObject objectSummary : list) {
			    	log.info("Delete "+repo.getRoot()+":"+objectSummary.getName());
			        swiftClient.removeObject(getContainer(repo), objectSummary.getName());
			    }	
			    if(list.size()!=99999) {
			    	log.info("Doing next portion");
			    	continue;
			    }

			} catch (UniformInterfaceException e) {
				throw new ForbiddenException("Unauthorized to access Container "+repo.getRoot());
			}
		}
		swiftClient.removeObject(getContainer(repo), filename.substring(0, filename.length()-1));

		return result;
	}
	/* (non-Javadoc)
	 * @see n3phele.storage.CloudStorageInterface#setPermissions(n3phele.service.model.repository.Repository, java.lang.String, boolean)
	 */
	@Override
	public boolean setPermissions(Repository repo, String filename,
			boolean isPublic) {
		SwiftClient swiftClient = null;
		try {
			Credential credential = repo.getCredential().decrypt();
			Access access = getAccess(repo.getTarget(), credential.getAccount(), credential.getSecret());	
			
			swiftClient = new SwiftClient(access, getRegion(repo));
			
			ClientResponse metadata = swiftClient.getContainerMetadata(getContainer(repo));
			String readAcl = metadata.getHeaders().getFirst("X-Container-Read");
			boolean containerPublic = readAcl != null && readAcl.startsWith(".r:*");
			if(containerPublic != isPublic) {
				if(isPublic) {
					swiftClient.createContainer(getContainer(repo),"X-Container-Read",".r:*");
				} else {
					swiftClient.createContainer(getContainer(repo),"X-Container-Read","");
				}
			}
			
			return true;
		} catch (Exception e) {
			log.log(Level.WARNING, "Permissions set", e);
			return false;
		}
	}
	/* (non-Javadoc)
	 * @see n3phele.storage.CloudStorageInterface#checkExists(n3phele.service.model.repository.Repository, java.lang.String)
	 */
	@Override
	public boolean checkExists(Repository repo, String filename) {
		try {
			Credential credential = repo.getCredential().decrypt();
			Access access = getAccess(repo.getTarget(), credential.getAccount(), credential.getSecret());	
			
			SwiftClient swiftClient = new SwiftClient(access, getRegion(repo));
			
			ClientResponse headOfObj = swiftClient.getObjectMetadata(getContainer(repo), filename);
			return true;
		} catch (UniformInterfaceException e) {
			int status = e.getResponse().getStatus();
			if(status == 401)
				throw new ForbiddenException("Unauthorized to access container "+repo.getRoot());
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see n3phele.storage.CloudStorageInterface#getFileList(n3phele.service.model.repository.Repository, java.lang.String, int)
	 */
	@Override
	public List<FileNode> getFileList(Repository repo, String prefix, int max) throws ForbiddenException, NotFoundException {

		try {
			Credential credential = repo.getCredential().decrypt();
			Access access = getAccess(repo.getTarget(), credential.getAccount(), credential.getSecret());	
			
			SwiftClient swiftClient = new SwiftClient(access, getRegion(repo));
			Map<String, String> params = new HashMap<String,String>();
			if(prefix != null && !prefix.isEmpty())
				params.put("prefix", prefix);
			params.put("delimiter", "/");
			if(max > 0)
				params.put("limit", Integer.toString(max));
	
			List<FileNode> result = new ArrayList<FileNode>();

			List<SwiftObject> content = swiftClient.listObjects(getContainer(repo), params);
			ClientResponse metadata = swiftClient.getContainerMetadata(getContainer(repo));
			String readAcl = metadata.getHeaders().getFirst("X-Container-Read");
			boolean isPublic = readAcl != null && readAcl.startsWith(".r:*");
			int i = 0;
			for(SwiftObject o : Helpers.safeIterator(content)) {
				i++;
				if(o.getName() != null && o.getName().endsWith("/")) {
					if(o.getName() != null && o.getName().equals(prefix) && "application/directory".equals(o.getContentType()))
						continue; // found self psuedo-directory
					
					String name = o.getName().substring(0,o.getName().length()-1);
					name = name.substring(name.lastIndexOf("/")+1);
					FileNode folder = FileNode.newFolder(name, prefix, repo, isPublic);
					result.add(folder);
					log.info("Folder:"+folder.toString());
				} else if(o.getName() == null && o.getSubdir() != null) {
					String name = o.getSubdir();
					if(o.getSubdir().endsWith("/")) {						
						name = o.getSubdir().substring(0,o.getSubdir().length()-1);
					}
					name = name.substring(name.lastIndexOf("/")+1);
					FileNode folder = FileNode.newFolder(name, prefix, repo, isPublic);
					result.add(folder);
					log.info("Subdir Folder:"+folder.toString());
				}	else  {
					String key = o.getName();
					log.info("Found "+key);
					if(key != null && !key.equals(prefix)) {
						String name = key.substring(key.lastIndexOf("/")+1);
						UriBuilder build = UriBuilder.fromUri(swiftClient.getTarget()).path(getContainer(repo));
						if(prefix != null && !prefix.isEmpty())
							build = build.path(prefix);
						String canonical = build.path(name).build().toString();
						FileNode file = FileNode.newFile(name, prefix, repo, o.getLastModified(), o.getBytes(), canonical);
						file.setMime(o.getContentType());
						log.info("File:"+file);
						result.add(file);
					}
	
				}
			}
			return result;
		} catch (UniformInterfaceException e) {
			int status = e.getResponse().getStatus();
			if(status == 401)
				throw new ForbiddenException("Unauthorized to access Container "+repo.getRoot());
			if(status == 404)
				throw new NotFoundException("Container "+repo.getRoot()+" unknown");
			log.log(Level.WARNING, "Swift getObject exception", e);
			throw e;
		}
	}
	
	/* (non-Javadoc)
	 * @see n3phele.storage.CloudStorageInterface#getRedirectURL(n3phele.service.model.repository.Repository, java.lang.String, java.lang.String)
	 */
	@Override
	public URI getRedirectURL(Repository repo, String path, String filename) {
		Credential credential = repo.getCredential().decrypt();
		Access access = getAccess(repo.getTarget(), credential.getAccount(), credential.getSecret());
		UriBuilder result = null;
		String endpoint = SwiftClient.findEndpointURL(access.getServiceCatalog(), "object-store", getRegion(repo), "public");
		result = UriBuilder.fromUri(endpoint);
		result.path(repo.getRoot()).path(path).path(filename);
		
		String expires = Long.toString((Calendar.getInstance().getTimeInMillis()/1000) + 60*60);

		String stringToSign = "GET\n"+expires+"\n"+result.build().getPath().replace(" ", "%20");
		String signature = access.getToken().getTenant().getId()+":"+credential.getAccount().split(":")[1]+":"+signSwiftQueryString(stringToSign, repo.getCredential());

		result.queryParam("temp_url_expires", expires);
		result.queryParam("temp_url_sig", signature);
		log.warning("Access "+result.build().getPath()+ " "+result.build());
		return result.build();
	}
	
	@Override
	public UploadSignature getUploadSignature(Repository repo, String name) {
		Credential credential = repo.getCredential().decrypt();
		Access access = getAccess(repo.getTarget(), credential.getAccount(), credential.getSecret());

		String endpoint = SwiftClient.findEndpointURL(access.getServiceCatalog(), "object-store", getRegion(repo), "public");
		URI canonicalName = UriBuilder.fromUri(endpoint).path(repo.getRoot()).path(name).build();
		int last = canonicalName.getPath().lastIndexOf('/');
		String path = canonicalName.getPath().substring(0, last+1);
		int discard = canonicalName.getPath().length()-path.length();
		String canonicalNameWithFile = canonicalName.toString();
		URI target = URI.create(canonicalNameWithFile.substring(0, canonicalNameWithFile.length()-discard));

		log.info("Target=<"+target+"> path=<"+path+">");
	
		String expires = Long.toString((Calendar.getInstance().getTimeInMillis()/1000) + 60*60);
		// hmac_body = '%s\n%s\n%s\n%s\n%s' % (path, redirect, max_file_size, max_file_count, expires) 
		String stringToSign = path.replace(" ", "%20")+"\n"+"\n"+"1073741824"+"\n"+"1"+"\n"+expires;
		String signature = access.getToken().getTenant().getId()+":"+credential.getAccount().split(":")[1]+":"+signSwiftQueryString(stringToSign, repo.getCredential());
		
		String acl = "swift";

		String contentType = Mimetypes.getInstance().getMimetype(name);
		
		UploadSignature uploadSignature = new UploadSignature(name,acl,target, repo.getRoot(), expires, signature, "none" ,contentType);
		return uploadSignature;

	}
	
	private final String signSwiftQueryString(String stringToSign, Credential credential ) {
		try {
			byte[] keyBytes = credential.decrypt().getSecret().getBytes();
			SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA1");
			Mac mac = Mac.getInstance("HmacSHA1");
			mac.init(signingKey);
			
			byte[] rawHmac = mac.doFinal(stringToSign.getBytes());
			byte[] hexBytes = new Hex().encode(rawHmac);
			return new String(hexBytes, "UTF-8");
		} catch (IllegalStateException e) {
			log.log(Level.SEVERE, "Signing error", e);
			throw new IllegalArgumentException(e.getMessage());
		} catch (InvalidKeyException e) {
			log.log(Level.SEVERE, "Signing error", e);
			throw new IllegalArgumentException(e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			log.log(Level.SEVERE, "Signing error", e);
			throw new IllegalArgumentException(e.getMessage());
		} catch (UnsupportedEncodingException e) {
			log.log(Level.SEVERE, "Signing error", e);
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	
	private static Map<String,Access> cache = new HashMap<String,Access>();
	public Access getAccess(URI target, String accessKey, String secretKey) {
		String key = target.toString()+"|"+accessKey+"|"+secretKey;
		if(cache.containsKey(key)) {
			Access existing = cache.get(key);
			if(existing.getToken().getExpires().getTime() > (new Date().getTime()+(5*60*1000))) {
				return existing;
			}
		}
		Authenticate authenticate;
		String[] pieces = accessKey.split(":");
		if(pieces.length == 2) {
			authenticate = Authenticate.withApiAccessKeyCredentials(pieces[1], secretKey).withTenantName(pieces[0]);
		} else {
			authenticate = Authenticate.withApiAccessKeyCredentials(accessKey, secretKey);
		}
		WebResource resource = client.resource(target);
		Access access = authenticate.getAccess(resource);
		log.info(access.toString());
		cache.put(key, access);
		return access;
	}
	
	private final static Client client = new Client();
	static {
		//client.addFilter(new LoggingFilter(Logger.getLogger("swift")));
	}

	private String getRegion(Repository repo) {
		String root = repo.getRoot();
		int index;
		if(root != null && (index = root.indexOf("/"))>0) {
			return root.substring(0,index);
		}
		return null;
	}
	
	private String getContainer(Repository repo) {
		String root = repo.getRoot();
		int index;
		if(root != null && (index = root.indexOf("/"))>0) {
			return root.substring(index+1);
		}
		return root;
	}
}
