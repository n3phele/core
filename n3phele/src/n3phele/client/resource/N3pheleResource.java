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
package n3phele.client.resource;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;

public interface N3pheleResource extends ClientBundle {

	@Source("headshot8.png")
	ImageResource profile();
	@Source("n3phele.css")
	public
	N3pheleCssResource css();
	@Source("activity.png")
	ImageResource activityIcon();
	@Source("repository.png")
	ImageResource repositoryIcon();
	@Source("repository16x16.png")
	ImageResource repositorySmallIcon();
	@Source("repository-add.png")
	ImageResource repositoryAddIcon();
	@Source("accounts.png")
	ImageResource accountIcon();
	@Source("accounts-add.png")
	ImageResource accountAddIcon();
	@Source("profile.png")
	ImageResource profileIcon();
	@Source("export.png")
	ImageResource commandIcon();
	@Source("completed.png")
	ImageResource completedIcon();
	@Source("failed.png")
	ImageResource FailedIcon();
	@Source("cancelled.png")
	ImageResource cancelledIcon();
	@Source("service.png")
	ImageResource serviceIcon();
	@Source("init.png")
	ImageResource initIcon();
	@Source("blocked.png")
	ImageResource blockedIcon();
	@Source("transparent10x10.png")
	ImageResource transparent();
	
	@Source("progress_background.gif")
	ImageResource barBackground();
	
	@ImageOptions(repeatStyle = RepeatStyle.Horizontal)
	@Source("body_bg.jpg")
	ImageResource cloudBackground();	
	@ImageOptions(repeatStyle = RepeatStyle.Horizontal)
	@Source("clouds_left.jpg")
	ImageResource skyBackground();
	
	//@Source("fullLength5.png")
	@Source("cloud.png")
	ImageResource loginGraphic();
	@Source("cancelled.png")
	ImageResource deleteIcon();
	@Source("xx.png")
	ImageResource x();
	@Source("error.png")
	ImageResource inputErrorIcon();
	
	@Source("RedoRedo.png")
	ImageResource rerun();
	
	@Source("InspectInspect.png")
	ImageResource inspect();
	
	@Source("moreMore.png")
	ImageResource more();
	
	@Source("Import.png")
	ImageResource importIcon();
	
	

	@Source("narrative-error.png")
	ImageResource narrativeError();
	@Source("narrative-warning.png")
	ImageResource narrativeWarning();
	@Source("narrative-info.png")
	ImageResource narrativeInfo();
	
	@Source("dataSet.png")
	ImageResource dataSetIcon();
	@Source("error-32x32.png")
	ImageResource outOfSyncIcon();
	
	@Source("dialog_close.png")
	ImageResource dialog_close();
	
	@Source("RightArrowHead.png")
	ImageResource rightArrowHead();
	@Source("FolderIcon32x32.png")
	ImageResource folderIcon();
	@Source("FolderIcon-added32x32.png")
	ImageResource folderAddedIcon();
	@Source("FileIcon32x32.png")
	ImageResource fileIcon();
	
	@Source("upload.png")
	ImageResource uploadIcon();
	
	@Source("search.png")
	ImageResource searchIcon();
	@Source("Script.png")
	ImageResource scriptIcon();
	
	@Source("stack.png")
	ImageResource stackIcon();
	@Source("qiime.png") 
	ImageResource qiimeIcon();
	@Source("fileCopy.png")
	ImageResource fileCopyIcon();
	@Source("unzipIcon.png")
	ImageResource unzipIcon();
	@Source("tree.png")
	ImageResource treeIcon();
	@Source("juju.png")
	ImageResource jujuIcon();
	
	@Source("cloud-theme.png")
	ImageResource profile_alt();
	@Source("publicFolder32x32.png")
	ImageResource publicFolder();
	@Source("untarIcon.png")
	ImageResource untarIcon();
	@Source("concatenateIcon.png")
	ImageResource concatenateIcon();
}