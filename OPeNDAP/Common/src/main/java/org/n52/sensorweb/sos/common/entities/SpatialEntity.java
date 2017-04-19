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

import com.vividsolutions.jts.geom.Geometry;

/**
 *
 * @author ankit
 */
//Class to Store the Geometry Properties of Feature of Interest

public class SpatialEntity {
    
    private Geometry geom;
    private Object longitude;
    private Object latitude;
    private Object altitude;
    
    public Object getLongitude() {
        return longitude;
    }
    //for call chaining
    public SpatialEntity setLongitude(final Object longitude) {
        this.longitude = longitude;
        return this;
    }

    public Object getLatitude() {
        return this.latitude;
    }

    public SpatialEntity setLatitude(final Object latitude) {
        this.latitude = latitude;
        return this;
    }

    public Object getAltitude() {
        return altitude;
    }

    public SpatialEntity setAltitude(final Object altitude) {
        this.altitude = altitude;
        return this;
    }
    
    public Geometry getGeom(){
        return geom;
    }
    
    public void setGeom(final Geometry geom){
        this.geom = geom;
    }
    
    public boolean isSetGeometry(){
        return getGeom() != null;
    }
    
    public boolean isSetAltitude(){
        return getAltitude() != null;
    }
    
    public boolean isSetLatitude(){
        return getLatitude() != null;
    }
    
    public boolean isSetLongitude(){
        return getLongitude() != null;
    }
    
}
