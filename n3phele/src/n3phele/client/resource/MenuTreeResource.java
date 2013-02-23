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

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.google.gwt.user.cellview.client.CellTree.Resources;
import com.google.gwt.user.cellview.client.CellTree.Style;

public interface MenuTreeResource extends Resources {

    /**
     * An image indicating a closed branch.
     */
    @ImageOptions(flipRtl = true)
    @Source("transparent10x10.png")
    ImageResource cellTreeClosedItem();

    /**
     * An image indicating that a node is loading.
     */
    @ImageOptions(flipRtl = true)
    ImageResource cellTreeLoading();

    /**
     * An image indicating an open branch.
     */
    @ImageOptions(flipRtl = true)
    @Source("transparent10x10.png")
    ImageResource cellTreeOpenItem();

    /**
     * The background used for selected items.
     */
    @ImageOptions(repeatStyle = RepeatStyle.Horizontal, flipRtl = true)
    ImageResource cellTreeSelectedBackground();

    /**
     * The styles used in this widget.
     */
    @Source("menuCellTree.css")
    Style cellTreeStyle();
  }