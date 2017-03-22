package org.n52.sos.ogc.gml;

import com.google.common.base.Strings;

/**
 * Internal representation of the OGC GML Definition.
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.4.0
 *
 */
public abstract class Definition extends DefinitionBase {

    private static final long serialVersionUID = -1766983518556023433L;
    private String remarks;

    public Definition(CodeWithAuthority identifier) {
        super(identifier);
    }

    /**
     * @return the remarks
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * @param remarks the remarks to set
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    
    public boolean hasRemarks() {
        return !Strings.isNullOrEmpty(getRemarks());
    }
}
