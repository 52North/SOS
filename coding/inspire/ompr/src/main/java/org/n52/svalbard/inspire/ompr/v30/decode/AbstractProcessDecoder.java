package org.n52.svalbard.inspire.ompr.v30.decode;

import static java.util.Collections.singletonMap;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.n52.sos.decode.ProcedureDecoder;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.svalbard.inspire.ompr.InspireOMPRConstants;
import org.n52.svalbard.inspire.ompr.Process;

import com.google.common.collect.ImmutableSet;

import eu.europa.ec.inspire.schemas.base.x33.IdentifierType;
import eu.europa.ec.inspire.schemas.ompr.x30.ProcessType;

public abstract class AbstractProcessDecoder<S> implements ProcedureDecoder<Process, S> {

    private static final Map<SupportedTypeKey, Set<String>> SUPPORTED_TYPES =
            singletonMap(SupportedTypeKey.ProcedureDescriptionFormat,
                    (Set<String>) ImmutableSet.of(InspireOMPRConstants.OMPR_30_OUTPUT_FORMAT_URL,
                            InspireOMPRConstants.OMPR_30_OUTPUT_FORMAT_MIME_TYPE));
    
    @Override
    public Set<String> getConformanceClasses() {
        return Collections.emptySet();
    }

    @Override
    public Map<SupportedTypeKey, Set<String>> getSupportedTypes() {
        return Collections.unmodifiableMap(SUPPORTED_TYPES);
    }

    @Override
    public Set<String> getSupportedProcedureDescriptionFormats(final String service, final String version) {
        return Collections.emptySet();
    }
    
    protected Process parseProcessType(ProcessType pt) {
        Process process = new Process();
        parseInspireId(pt, process);
        return process;
    }

    private void parseInspireId(ProcessType pt, Process process) {
        IdentifierType identifier = pt.getInspireId().getIdentifier();
        String localId = identifier.getLocalId();
        String namespace = identifier.getNamespace();
        CodeWithAuthority codeWithAuthority;
        if (localId.contains(namespace)) {
            codeWithAuthority = new CodeWithAuthority(localId, namespace);
        } else {
            codeWithAuthority = new CodeWithAuthority(getIdentifier(localId, namespace), namespace);
        }
        process.setIdentifier(codeWithAuthority);
    }

    private String getIdentifier(String localId, String namespace) {
        if (namespace.startsWith("urn")) {
            return namespace + ":" + localId;
        } else if (namespace.startsWith("http")) {
            return namespace + "/" + localId;
        }
        return namespace + "-" + localId;
    }
}
