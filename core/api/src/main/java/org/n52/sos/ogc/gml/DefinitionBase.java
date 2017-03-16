package org.n52.sos.ogc.gml;

public abstract class DefinitionBase extends AbstractGML {

    private static final long serialVersionUID = 1865619262896332937L;

    public DefinitionBase(CodeWithAuthority identifier) {
        setIdentifier(identifier);
    }
}
