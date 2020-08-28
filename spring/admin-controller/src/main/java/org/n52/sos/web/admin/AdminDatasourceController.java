/*
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * License version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 */
package org.n52.sos.web.admin;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.xmlbeans.XmlException;
import org.n52.iceland.ds.ConnectionProviderException;
import org.n52.iceland.request.operator.RequestOperatorRepository;
import org.n52.janmayen.Json;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.ows.service.OwsServiceRequestContext;
import org.n52.sos.ds.PredefinedInsertionHandler;
import org.n52.sos.ds.GeneralQueryDAO;
import org.n52.sos.exception.MissingServiceOperatorException;
import org.n52.sos.predefined.AbstractPredefined;
import org.n52.sos.predefined.PhenomenonPredefined;
import org.n52.sos.predefined.PredefinedType;
import org.n52.sos.predefined.UnitPredefined;
import org.n52.sos.web.common.ControllerConstants;
import org.n52.svalbard.decode.DecoderRepository;
import org.n52.svalbard.decode.exception.DecodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Maps;

/**
 * @since 4.0.0
 *
 */
@Controller
public class AdminDatasourceController extends AbstractDatasourceController {

    private static final Logger LOG = LoggerFactory.getLogger(AdminDatasourceController.class);

    private static final String ROWS = "rows";

    private static final String NAMES = "names";

    private static final String SUPPORTS_CLEAR = "supportsClear";

    private static final String SUPPORTS_DELETE_DELETED = "supportsDeleteDeleted";

    private static final String PREDEFINED_PHENOMENA = "predefinedPhenomena";

    private static final String PREDEFINED_UNITS = "predefinedUnits";

    private static final String NO_PREDEFINED_DATA = "No predefined data are available!";

    @Inject
    private Optional<GeneralQueryDAO> generalQueryDAO;

    @Inject
    private DecoderRepository decoderRepository;

    @Inject
    private RequestOperatorRepository requestOperatorRepository;

    @Inject
    private Optional<PredefinedInsertionHandler> handler;

    private Map<PredefinedType, List<AbstractPredefined<?>>> predefinedMap;

    private SampleDataInserter sampleDataInserter;

    @RequestMapping(value = ControllerConstants.Paths.ADMIN_DATABASE)
    public ModelAndView index() throws SQLException, OwsExceptionReport {
        Map<String, Object> model = Maps.newHashMap();
        model.put(SUPPORTS_CLEAR, getDatasource().supportsClear());
        model.put(SUPPORTS_DELETE_DELETED, generalQueryDAO != null);
        model.put(PREDEFINED_PHENOMENA, getPredefinedObservedProperties());
        model.put(PREDEFINED_UNITS, getPredefinedUnits());
        return new ModelAndView(ControllerConstants.Views.ADMIN_DATASOURCE, model);
    }

    @Deprecated
    @ResponseBody
    @RequestMapping(value = ControllerConstants.Paths.ADMIN_DATABASE_EXECUTE, method = RequestMethod.POST)
    public String processQuery(@RequestBody String querySQL) {
        return "Not supported!";
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(UnsupportedOperationException.class)
    public String onError(UnsupportedOperationException e) {
        return "The operation is not supported.";
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = ControllerConstants.Paths.ADMIN_DATABASE_CLEAR, method = RequestMethod.POST)
    public void clearDatasource() throws OwsExceptionReport, ConnectionProviderException {
        if (getDatasource().supportsClear()) {
            LOG.info("Clearing database contents by calling clear method.");
            getDatasource().clear(getSettings());
        } else {
            LOG.info("Clearing database contents by deleting and recreating the SOS database schema.");
            Map<String, Object> settings = getDatasource().parseDatasourceProperties(getSettings());
            getDatasource().dropSchema(settings);
            getDatasource().createSchema(settings);
        }
        updateCache();
    }

    @ResponseBody
    @ExceptionHandler(MissingServiceOperatorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String onConnectionMissingServiceOperatorException(MissingServiceOperatorException e) {
        return e.getMessage();
    }

    @ResponseBody
    @RequestMapping(value = ControllerConstants.Paths.ADMIN_DATABASE_ADD_SAMPLEDATA, method = RequestMethod.POST)
    public String addSampledata(HttpServletRequest request) throws OwsExceptionReport, ConnectionProviderException,
            IOException, URISyntaxException, XmlException, MissingServiceOperatorException, DecodingException {
        if (sampleDataInserter == null) {
            try {
                sampleDataInserter = new SampleDataInserter(OwsServiceRequestContext.fromRequest(request),
                        decoderRepository, requestOperatorRepository);
                boolean sampledataAdded = sampleDataInserter.insertSampleData();
                if (sampledataAdded) {
                    updateCache();
                }
                return "OK";
            } finally {
                sampleDataInserter = null;
            }
        }
        return "Insert is still in progress!";
    }

    @ResponseBody
    @RequestMapping(value = ControllerConstants.Paths.ADMIN_DATABASE_LOAD_PREDEFINED_PHENOMENA,
                    method = RequestMethod.GET)
    public void loadPredefinedObservedProperties(@RequestParam("name") String name) throws OwsExceptionReport {
        if (handler.isPresent() && name != null && !name.isEmpty() && !name.equalsIgnoreCase(NO_PREDEFINED_DATA)) {
            Optional<AbstractPredefined<?>> phenomena = predefinedMap.get(PredefinedType.PHENOMENA).stream()
                    .filter(u -> u.getName().equals(name)).findFirst();
            if (phenomena.isPresent() && phenomena.get() instanceof PhenomenonPredefined) {
                handler.get()
                        .insertPredefinedObservableProperties(((PhenomenonPredefined) phenomena.get()).getValues());
            }
        }
    }

    @ResponseBody
    @RequestMapping(value = ControllerConstants.Paths.ADMIN_DATABASE_LOAD_PREDEFINED_UNITS, method = RequestMethod.GET)
    public void loadPredefinedUnits(@RequestParam("name") String name) throws OwsExceptionReport {
        if (handler.isPresent() && name != null && !name.isEmpty() && !name.equalsIgnoreCase(NO_PREDEFINED_DATA)) {
            Optional<AbstractPredefined<?>> units =
                    predefinedMap.get(PredefinedType.UNIT).stream().filter(u -> u.getName().equals(name)).findFirst();
            if (units.isPresent() && units.get() instanceof UnitPredefined) {
                handler.get().insertPredefinedUnits(((UnitPredefined) units.get()).getValues());
            }
        }
    }

    private Set<String> getPredefinedObservedProperties() throws OwsExceptionReport {
        return getPredefined(PredefinedType.PHENOMENA);
    }

    private Set<String> getPredefinedUnits() throws OwsExceptionReport {
        return getPredefined(PredefinedType.UNIT);
    }

    private Set<String> getPredefined(PredefinedType type) throws OwsExceptionReport {
        if (predefinedMap == null || predefinedMap.isEmpty()) {
            loadPredefinedFiles();
        }
        if (predefinedMap.containsKey(type)) {
            return predefinedMap.get(type).stream().map(u -> u.getName()).collect(Collectors.toSet());
        }
        Set<String> predefined = new LinkedHashSet<>();
        predefined.add("NO_PREDEFINED_DATA");
        return predefined;
    }

    private void loadPredefinedFiles() throws OwsExceptionReport {
        ObjectMapper om = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        if (predefinedMap == null) {
            predefinedMap = new LinkedHashMap<>();
        }
        for (File file : loadFiles()) {
            try {
                JsonNode predefined = Json.loadFile(file);
                switch (PredefinedType.valueOf(predefined.get("type").asText())) {
                    case PHENOMENA:
                        List<AbstractPredefined<?>> pl;
                        if (predefinedMap.containsKey(PredefinedType.PHENOMENA)) {
                            pl = predefinedMap.get(PredefinedType.PHENOMENA);
                        } else {
                            pl = new LinkedList<AbstractPredefined<?>>();
                        }
                        pl.add(om.treeToValue(predefined, PhenomenonPredefined.class));
                        predefinedMap.put(PredefinedType.PHENOMENA, pl);
                        break;
                    case UNIT:
                        List<AbstractPredefined<?>> ul;
                        if (predefinedMap.containsKey(PredefinedType.UNIT)) {
                            ul = predefinedMap.get(PredefinedType.UNIT);
                        } else {
                            ul = new LinkedList<AbstractPredefined<?>>();
                        }
                        ul.add(om.treeToValue(predefined, UnitPredefined.class));
                        predefinedMap.put(PredefinedType.UNIT, ul);
                        break;
                    default:
                        break;
                }
            } catch (IOException ioe) {
                throw new NoApplicableCodeException().causedBy(ioe).withMessage("Error while loading profies file.");
            }
        }
    }

    private Collection<File> loadFiles() {
        File folder = FileUtils.toFile(AdminDatasourceController.class.getResource("/predefined"));
        return FileUtils.listFiles(folder, FileFilterUtils.trueFileFilter(), null);
    }
}
