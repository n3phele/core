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

import java.net.URI;
import java.util.List;

import n3phele.service.core.ForbiddenException;
import n3phele.service.model.repository.FileNode;
import n3phele.service.model.repository.Repository;

public abstract class CloudStorage implements CloudStorageInterface {

	public static CloudStorageInterface factory(String type) {
		try {
			return Class.forName("n3phele.storage."+type.toLowerCase()+".CloudStorageImpl").asSubclass(CloudStorageInterface.class).newInstance();
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("Unknown Cloud storage type: "+type);
		} catch (InstantiationException e) {
			throw new IllegalArgumentException("Instantiation Exception "+e.getMessage()+" Cloud storage type: "+type);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Illegal Access Exception "+e.getMessage()+" Cloud storage type: "+type);
		}
	}
	public static CloudStorageInterface factory(Repository repo) {
		return factory(repo.getKind());
	}
	
	private final static CloudStorageInterface provider = new CloudStorageInterface() {

		@Override
		public boolean createBucket(Repository repo) throws ForbiddenException {
			return factory(repo.getKind()).createBucket(repo);
		}

		@Override
		public FileNode getMetadata(Repository repo, String filename) {
			return factory(repo.getKind()).getMetadata(repo, filename);
		}

		@Override
		public boolean deleteFile(Repository repo, String filename) {
			return factory(repo.getKind()).deleteFile(repo, filename);
		}

		@Override
		public boolean deleteFolder(Repository repo, String filename) {
			return factory(repo.getKind()).deleteFolder(repo, filename);
		}

		@Override
		public boolean setPermissions(Repository repo, String filename,
				boolean isPublic) {
			return factory(repo.getKind()).setPermissions(repo, filename, isPublic);
		}

		@Override
		public boolean checkExists(Repository repo, String filename) {
			return factory(repo.getKind()).checkExists(repo, filename);
		}

		@Override
		public List<FileNode> getFileList(Repository repo, String prefix,
				int max) {
			return factory(repo.getKind()).getFileList(repo, prefix, max);
		}


		@Override
		public URI getRedirectURL(Repository repo, String path, String filename) {
			return factory(repo.getKind()).getRedirectURL(repo, path, filename);
		}


//		@Override
//		public UploadSignature getUploadSignature(Repository repo, String name) {
//			return factory(repo.getKind()).getUploadSignature(repo, name);
//		}
		
		
	};
	public static CloudStorageInterface factory() {
		return provider;
	}
}
