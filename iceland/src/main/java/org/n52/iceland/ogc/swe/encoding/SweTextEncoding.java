/**
 * Copyright 2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.iceland.ogc.swe.encoding;

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
