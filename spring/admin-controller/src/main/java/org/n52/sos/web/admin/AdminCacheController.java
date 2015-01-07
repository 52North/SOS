/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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


import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.sos.SosEnvelope;
import org.n52.sos.service.Configurator;
import org.n52.sos.util.JSONUtils;
import org.n52.sos.web.AbstractController;
import org.n52.sos.web.ControllerConstants;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.google.common.base.Function;

/**
 * @since 4.0.0
 *
 */
@Controller
public class AdminCacheController extends AbstractController {
    private static final ObjectMapper objectMapper = buildObjectMapper();

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(AdminCacheController.class);

    @RequestMapping(value = ControllerConstants.Paths.ADMIN_CACHE, method = RequestMethod.GET)
    public String view() {
        return ControllerConstants.Views.ADMIN_CACHE;
    }

    private static ObjectMapper buildObjectMapper() {
        ObjectMapper om = new ObjectMapper();

        //enable pretty printing
        om.enable(SerializationFeature.INDENT_OUTPUT);

        //specify which types should be serialized using toString
        SimpleModule module = new SimpleModule("CacheSerializerModule", new Version(1, 0, 0, null, null, null));
        module.addSerializer(DateTime.class, new ToStringSerializer());
        module.addSerializer(TimePeriod.class, new ToStringSerializer());
        module.addSerializer(SosEnvelope.class, new ToStringSerializer());
        om.registerModule(module);

        //set property visibility
        om.setVisibility(PropertyAccessor.GETTER, Visibility.NON_PRIVATE);
        om.setVisibility(PropertyAccessor.IS_GETTER, Visibility.NON_PRIVATE);
        return om;
    }

    @ResponseBody
    @RequestMapping(value = ControllerConstants.Paths.ADMIN_CACHE_SUMMARY, method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    public String getCacheSummary() {

        Function<String, JsonNode> textToJson
                = new Function<String, JsonNode>() {
                    @Override
                    public JsonNode apply(String t) {
                        return JSONUtils.nodeFactory().textNode(t);
                    }
                };
        return JSONUtils.print(JSONUtils.toJSON(CacheSummaryHandler.getCacheValues()));
    }

    @ResponseBody
    @RequestMapping(value = ControllerConstants.Paths.ADMIN_CACHE_DUMP, method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    public String getCacheDump() throws JsonProcessingException {
        return objectMapper.writeValueAsString(Configurator.getInstance().getCache());
    }
}
