package org.n52.sos.ogc.gml;

import java.util.List;

import org.n52.sos.w3c.Nillable;
import org.n52.sos.w3c.xlink.Referenceable;

import com.google.common.collect.Lists;

public class VerticalCRS extends AbstractCRS {

    private static final long serialVersionUID = -3827860576029113036L;
    
    private Referenceable<VerticalCS> verticalCS = Referenceable.of(Nillable.<VerticalCS>missing());
    
    private Referenceable<VerticalDatum> verticalDatum = Referenceable.of(Nillable.<VerticalDatum>missing());

    public VerticalCRS(CodeWithAuthority identifier, String scope, Referenceable<VerticalCS> verticalCS, Referenceable<VerticalDatum> verticalDatum) {
        this(identifier, Lists.newArrayList(scope), verticalCS, verticalDatum);
    }
    
    public VerticalCRS(CodeWithAuthority identifier, List<String> scope, Referenceable<VerticalCS> verticalCS, Referenceable<VerticalDatum> verticalDatum) {
        super(identifier, scope);
        setVerticalCS(verticalCS);
        setVerticalDatum(verticalDatum);
    }

    /**
     * @return the verticalCS
     */
    public Referenceable<VerticalCS> getVerticalCS() {
        return verticalCS;
    }

    /**
     * @param verticalCS the verticalCS to set
     */
    public void setVerticalCS(Referenceable<VerticalCS> verticalCS) {
        this.verticalCS = verticalCS;
    }

    /**
     * @return the verticalDatum
     */
    public Referenceable<VerticalDatum> getVerticalDatum() {
        return verticalDatum;
    }

    /**
     * @param verticalDatum the verticalDatum to set
     */
    public void setVerticalDatum(Referenceable<VerticalDatum> verticalDatum) {
        this.verticalDatum = verticalDatum;
    }
    
}
