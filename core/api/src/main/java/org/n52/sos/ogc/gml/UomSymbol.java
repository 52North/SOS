package org.n52.sos.ogc.gml;

public class UomSymbol implements UomIdentifier<String> {
    
    private final String value;
    
    public UomSymbol(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

}
