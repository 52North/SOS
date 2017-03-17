package org.n52.sos.ogc.gml;

import java.util.List;

/**
 * Internal representation of the OGC GML VerticalDatum.
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.4.0
 *
 */
public class VerticalDatum extends AbstractDatum {

    public VerticalDatum(CodeWithAuthority identifier, String scope) {
        super(identifier, scope);
    }
    
    public VerticalDatum(CodeWithAuthority identifier, List<String> scope) {
        super(identifier, scope);
    }

}
