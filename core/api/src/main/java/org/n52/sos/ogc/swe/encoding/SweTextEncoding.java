/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * License version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 */
package org.n52.sos.ogc.swe.encoding;

/**
 * @since 4.0.0
 * 
 */
public class SweTextEncoding extends SweAbstractEncoding {

    private String blockSeparator;

    private String tokenSeparator;

    private String decimalSeparator;

    private Boolean collapseWhiteSpaces;

    public String getBlockSeparator() {
        return blockSeparator;
    }

    public String getTokenSeparator() {
        return tokenSeparator;
    }

    public void setBlockSeparator(String blockSeparator) {
        this.blockSeparator = blockSeparator;
    }

    public void setTokenSeparator(String tokenSeparator) {
        this.tokenSeparator = tokenSeparator;
    }

    public void setDecimalSeparator(String decimalSeparator) {
        this.decimalSeparator = decimalSeparator;
    }

    public void setCollapseWhiteSpaces(boolean collapseWhiteSpaces) {
        this.collapseWhiteSpaces = collapseWhiteSpaces ? Boolean.TRUE : Boolean.FALSE;
    }

    public String getDecimalSeparator() {
        return decimalSeparator;
    }

    public boolean isCollapseWhiteSpaces() {
        return collapseWhiteSpaces.booleanValue();
    }

    public boolean isSetCollapseWhiteSpaces() {
        return collapseWhiteSpaces != null;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int hash = 5;
        hash = prime * hash + (this.getBlockSeparator() != null ? this.getBlockSeparator().hashCode() : 0);
        hash = prime * hash + (this.getTokenSeparator() != null ? this.getTokenSeparator().hashCode() : 0);
        hash = prime * hash + (this.getDecimalSeparator() != null ? this.getDecimalSeparator().hashCode() : 0);
        hash = prime * hash + (this.collapseWhiteSpaces != null ? this.collapseWhiteSpaces.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SweTextEncoding other = (SweTextEncoding) obj;
        if ((this.getBlockSeparator() == null) ? (other.getBlockSeparator() != null) : !this.getBlockSeparator()
                .equals(other.getBlockSeparator())) {
            return false;
        }
        if ((this.getTokenSeparator() == null) ? (other.getTokenSeparator() != null) : !this.getTokenSeparator()
                .equals(other.getTokenSeparator())) {
            return false;
        }
        if ((this.getDecimalSeparator() == null) ? (other.getDecimalSeparator() != null) : !this.getDecimalSeparator()
                .equals(other.getDecimalSeparator())) {
            return false;
        }
        if (this.collapseWhiteSpaces != other.collapseWhiteSpaces
                && (this.collapseWhiteSpaces == null || !this.collapseWhiteSpaces.equals(other.collapseWhiteSpaces))) {
            return false;
        }
        return true;
    }

}
