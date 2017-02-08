package org.n52.sos.ogc.gml;

import com.google.common.base.Strings;

public class Definition extends AbstractGML {

    private static final long serialVersionUID = -1766983518556023433L;
    private String remarks;

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
    
    public boolean hasRemorks() {
        return !Strings.isNullOrEmpty(getRemarks());
    }
}
