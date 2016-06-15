package org.n52.sos.ds.hibernate;

import java.util.Set;

import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.swe.simpleType.SweText;
import org.n52.sos.ogc.swes.OfferingExtensionKey;
import org.n52.sos.ogc.swes.OfferingExtensionProvider;
import org.n52.sos.ogc.swes.SwesExtensionImpl;
import org.n52.sos.ogc.swes.SwesExtensions;

import com.google.common.collect.Sets;

public class CloudJsOfferingExtensionProvider implements OfferingExtensionProvider {
    
    Set<OfferingExtensionKey> providerKeys = Sets.newHashSet(new OfferingExtensionKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION, "CloudJS"));

    public CloudJsOfferingExtensionProvider() {
    }

    @Override
    public Set<OfferingExtensionKey> getOfferingExtensionKeyTypes() {
        return providerKeys;
    }

    @Override
    public SwesExtensions getOfferingExtensions(String identifier) {
        SwesExtensions extensions = new SwesExtensions();
        extensions.addSwesExtension(new SwesExtensionImpl<SweText>().setValue(
                new SweText().setValue(getCloudJsForOffering(identifier))));
        return extensions;
    }

    private String getCloudJsForOffering(String identifier) {
        // TODO get cloud.js for offering 
        return "";
    }

    @Override
    public boolean hasExtendedOfferingFor(String identifier) {
        return true;
    }
}
