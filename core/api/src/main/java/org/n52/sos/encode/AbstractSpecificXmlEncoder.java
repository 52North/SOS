package org.n52.sos.encode;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.util.http.MediaType;
import org.n52.sos.util.http.MediaTypes;
import org.n52.sos.w3c.SchemaLocation;

import com.google.common.collect.Sets;

public abstract class AbstractSpecificXmlEncoder<T, S> implements Encoder<T, S> {

    @Override
    public Map<SupportedTypeKey, Set<String>> getSupportedTypes() {
        return Collections.emptyMap();
    }

    @Override
    public void addNamespacePrefixToMap(Map<String, String> nameSpacePrefixMap) {
    }
    
    @Override
    public MediaType getContentType() {
        return MediaTypes.TEXT_XML;
    }

    @Override
    public Set<SchemaLocation> getSchemaLocations() {
        return Sets.newHashSet();
    }

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.emptySet();
    }
    
    protected XmlObject substitute(XmlObject elementToSubstitute, XmlObject substitutionElement) {
        XmlObject substituteElement =
                XmlHelper.substituteElement(elementToSubstitute, substitutionElement);
        substituteElement.set(substitutionElement);
        return substituteElement;
    }
    
    protected Encoder<?, ?> getEncoder(EncoderKey key) {
        return CodingRepository.getInstance().getEncoder(key);
    }
    
    

    
    
}
