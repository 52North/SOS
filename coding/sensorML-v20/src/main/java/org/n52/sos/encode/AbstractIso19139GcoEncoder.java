package org.n52.sos.encode;

import org.isotc211.x2005.gco.AbstractObjectType;
import org.n52.sos.iso.gco.AbstractObject;

public abstract class AbstractIso19139GcoEncoder extends AbstractXmlEncoder<Object> {
    
    protected AbstractObjectType encodeAbstractObject(AbstractObjectType aot, AbstractObject abstractObject) {
        if (abstractObject.isSetId()) {
            aot.setId(abstractObject.getId());
        }
        if (abstractObject.isSetUuid()) {
            aot.setUuid(abstractObject.getUuid());
        }
        return aot;
    }

}
