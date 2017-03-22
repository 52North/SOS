package org.n52.sos.ogc.gml;

public abstract class IdentifiedObject extends Definition {

    private static final long serialVersionUID = -4358620975046498129L;

    public IdentifiedObject(CodeWithAuthority identifier) {
        super(identifier);
    }

}
