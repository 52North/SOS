package org.n52.sos.binding;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.exception.HTTPException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.swes.InvalidRequestException;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.ows.OWSConstants.RequestParams;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.AbstractProcess;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosObservationOffering;
import org.n52.sos.ogc.sos.SosOffering;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.ogc.swes.SwesExtensionImpl;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.request.InsertSensorRequest;
import org.n52.sos.response.AbstractServiceResponse;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.KvpHelper;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.util.http.MediaType;
import org.n52.sos.util.http.MediaTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

public class RegisterBinding extends SimpleBinding {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterBinding.class);

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.emptySet();
    }

    @Override
    protected MediaType getDefaultContentType() {
        return MediaTypes.APPLICATION_XML;
    }

    @Override
    public String getUrlPattern() {
        return "/register";
    }

    @Override
    public Set<MediaType> getSupportedEncodings() {
        return Collections.singleton(MediaTypes.APPLICATION_KVP);
    }
    
    @Override
    public void doPostOperation(HttpServletRequest req, HttpServletResponse res)
            throws HTTPException, IOException {
        AbstractServiceRequest<?> serviceRequest = null;
        try {
            serviceRequest = parseRequest(req);
            // add request context information
            serviceRequest.setRequestContext(getRequestContext(req));
            AbstractServiceResponse response = getServiceOperator(serviceRequest).receiveRequest(serviceRequest);
            writeResponse(req, res, response);
        } catch (OwsExceptionReport oer) {
            oer.setVersion(serviceRequest != null ? serviceRequest.getVersion() : null);
            writeOwsExceptionReport(req, res, oer);
        }
    }

    private AbstractServiceRequest<?> parseRequest(HttpServletRequest req) throws OwsExceptionReport {
        Map<String, String> parameterValueMap = KvpHelper.getKvpParameterValueMap(req);
        XmlObject doc = XmlHelper.parseXmlSosRequest(req);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("REGISTER-REQUEST: {}", doc.xmlText());
        }
        Object object = getDecoder(CodingHelper.getDecoderKey(doc)).decode(doc);
        if (object instanceof SosProcedureDescription) {
            
            SosProcedureDescription procDesc = (SosProcedureDescription)object;
            InsertSensorRequest request = new InsertSensorRequest();
            // isType extension
            String isType = KvpHelper.getParameterValue("isType", parameterValueMap);
            boolean isTypeRequest = false;
            if (!Strings.isNullOrEmpty(isType) && Boolean.parseBoolean(isType)) {
                SwesExtensionImpl<Boolean> extension = new SwesExtensionImpl<Boolean>();
                extension.setDefinition("isType").setValue(true);
                request.addExtension(extension);
                isTypeRequest = true;
            }
            // check for procedure and offering identifier
            // parameterValueMap
            checkForProcedureParameter(procDesc, parameterValueMap);
            checkForOfferingParameter(procDesc, parameterValueMap);
            
            // sensor description
            request.setProcedureDescription(procDesc);
            // service and version
            request.setService(getServiceParameterValue(parameterValueMap));
            request.setVersion(getVersionParameterValue(parameterValueMap));
            // format
            request.setProcedureDescriptionFormat(procDesc.getDescriptionFormat());
            // observable properties
            // get from output
            if (procDesc instanceof AbstractProcess && ((AbstractProcess)procDesc).isSetOutputs()) {
                
                //request.setObservableProperty();
            } else if (isTypeRequest) {
                // isType
            } else {
                throw new NoApplicableCodeException();
            }
            // metadata
            if (!isTypeRequest) {
             // default: all
                // request.setMetadata(parseMetadata(xbInsertSensor.getMetadataArray()));  
            }
           
            
            return request;
        } 
        // TODO
        throw new InvalidRequestException();
    }
    
    private String getServiceParameterValue(Map<String, String> map) {
        final String service = KvpHelper.getParameterValue(RequestParams.service, map);
        if (Strings.isNullOrEmpty(service)) {
            return Sos2Constants.SOS;
        }
        return service;
    }

    private String getVersionParameterValue(Map<String, String> map) {
        final String version = KvpHelper.getParameterValue(RequestParams.version, map);
        if (Strings.isNullOrEmpty(version)) {
            return Sos2Constants.SERVICEVERSION;
        }
        return version;
    }

    private void checkForProcedureParameter(SosProcedureDescription procDesc, Map<String, String> map) {
        final String procedure = KvpHelper.getParameterValue("procedure", map);
        if (!Strings.isNullOrEmpty(procedure)) {
            procDesc.setIdentifier(new CodeWithAuthority(procedure));
        }
    }

    private void checkForOfferingParameter(SosProcedureDescription procDesc, Map<String, String> map) {
        final String offering = KvpHelper.getParameterValue("offering", map);
        if (!Strings.isNullOrEmpty(offering)) {
            procDesc.addOffering(new SosOffering(offering, offering));
        }
    }
}
