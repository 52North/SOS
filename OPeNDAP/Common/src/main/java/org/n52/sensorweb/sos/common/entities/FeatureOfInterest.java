/*
 * Copyright (C) 2016 52north.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.n52.sensorweb.sos.common.entities;

import java.util.HashSet;
import java.util.Set;
import com.google.common.collect.Sets;
import org.n52.sos.util.StringHelper;
/**
 *
 * @author ankit
 */
//Spatial Entity Stores the Geometry Imformation
public class FeatureOfInterest extends SpatialEntity{

    private String descriptionXml;
    public static final String ID = "featureOfInterestId";
    private String url;
    //need to discuss the Example for this
    private Set<FeatureOfInterest> childs = Sets.newHashSet();
    private Set<FeatureOfInterest> parents = Sets.newHashSet();
    
    
    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    //Setting Up the DescriptionXML
    public String getDescriptionXml() {
        return descriptionXml;
    }

    public void setDescriptionXml(String descriptionXml) {
        this.descriptionXml = descriptionXml;
    }

    public boolean isSetDescriptionXml() {
        return StringHelper.isNotEmpty(descriptionXml);
    }

    public Set<FeatureOfInterest> getParents() {
        return parents;
    }
    
    
    public void setParents(final Set<FeatureOfInterest> parents) {
        this.parents = parents;
    }

    public Set<FeatureOfInterest> getChilds() {
        return childs;
    }

    public void setChilds(final Set<FeatureOfInterest> childs) {
        this.childs = childs;
    }

    public void addParent(FeatureOfInterest parent) {
        if (parent == null) {
            return;
        }
        if (this.parents == null) {
            this.parents = new HashSet<>();
        }
        this.parents.add(parent);
    }

    public void addChild(FeatureOfInterest child) {
        if (child == null) {
            return;
        }
        if (this.childs == null) {
            this.childs = new HashSet<>();
        }
        this.childs.add(child);
    }
    
}
