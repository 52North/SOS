package org.n52.sos.ogc.gml;

import java.util.List;

public class VerticalDatum extends AbstractDatum {

    public VerticalDatum(CodeWithAuthority identifier, String scope) {
        super(identifier, scope);
    }
    
    public VerticalDatum(CodeWithAuthority identifier, List<String> scope) {
        super(identifier, scope);
    }

}
