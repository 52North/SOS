package org.n52.sos.ogc.gml;

/**
 * Internal representation of the OGC GML DefinitionBase.
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.4.0
 *
 */
public abstract class DefinitionBase extends AbstractGML {

    private static final long serialVersionUID = 1865619262896332937L;

    public DefinitionBase(CodeWithAuthority identifier) {
        setIdentifier(identifier);
    }
}
