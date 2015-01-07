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

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import org.n52.sos.service.AbstractLoggingConfigurator;
import org.n52.sos.web.AbstractController;
import org.n52.sos.web.ControllerConstants;

/**
 * @since 4.0.0
 *
 */
@Controller
@RequestMapping(value = ControllerConstants.Paths.ADMIN_LOGGING)
public class AdminLoggingController extends AbstractController {
    private static final Logger LOG = LoggerFactory.getLogger(AdminLoggingController.class);

    private static final int LOG_MESSAGES = 15;

    private static final String LOG_MESSAGES_MODEL_ATTRIBUTE = "logMessages";

    private static final String IS_CONSOLE_ENABLED_MODEL_ATTRIBUTE = "isConsoleEnabled";

    private static final String IS_FILE_ENABLED_MODEL_ATTRIBUTE = "isFileEnabled";

    private static final String ROOT_LOG_LEVEL_MODEL_ATTRIBUTE = "rootLogLevel";

    private static final String DAYS_TO_KEEP_MDOEL_ATTRIBUTE = "daysToKeep";

    private static final String LOGGER_LEVELS_MODEL_ATTRIBUTE = "loggerLevels";

    private static final String MAX_FILE_SIZE_MODEL_ATTRIBUTE = "maxFileSize";

    private static final String ERROR_MODEL_ATTRIBUTE = "error";

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView view() {
        AbstractLoggingConfigurator lc = AbstractLoggingConfigurator.getInstance();
        Map<String, Object> model = new HashMap<String, Object>(5);
        model.put(IS_FILE_ENABLED_MODEL_ATTRIBUTE, lc.isEnabled(AbstractLoggingConfigurator.Appender.FILE));
        model.put(IS_CONSOLE_ENABLED_MODEL_ATTRIBUTE, lc.isEnabled(AbstractLoggingConfigurator.Appender.CONSOLE));
        model.put(ROOT_LOG_LEVEL_MODEL_ATTRIBUTE, lc.getRootLogLevel());
        model.put(DAYS_TO_KEEP_MDOEL_ATTRIBUTE, lc.getMaxHistory());
        model.put(LOGGER_LEVELS_MODEL_ATTRIBUTE, lc.getLoggerLevels());
        model.put(LOG_MESSAGES_MODEL_ATTRIBUTE, lc.getLastLogEntries(LOG_MESSAGES));
        model.put(MAX_FILE_SIZE_MODEL_ATTRIBUTE, lc.getMaxFileSize());
        return new ModelAndView(ControllerConstants.Views.ADMIN_LOGGING, model);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView save(HttpServletRequest req) {

        @SuppressWarnings("unchecked")
        Set<String> parameters = new HashSet<String>(Collections.list((Enumeration<String>) req.getParameterNames()));

        int daysToKeep = Integer.parseInt(req.getParameter(DAYS_TO_KEEP_MDOEL_ATTRIBUTE));
        parameters.remove(DAYS_TO_KEEP_MDOEL_ATTRIBUTE);
        boolean fileEnabled = parseBoolean(req.getParameter(IS_FILE_ENABLED_MODEL_ATTRIBUTE));
        parameters.remove(IS_FILE_ENABLED_MODEL_ATTRIBUTE);
        boolean consoleEnabled = parseBoolean(req.getParameter(IS_CONSOLE_ENABLED_MODEL_ATTRIBUTE));
        parameters.remove(IS_CONSOLE_ENABLED_MODEL_ATTRIBUTE);
        AbstractLoggingConfigurator.Level rootLevel =
                AbstractLoggingConfigurator.Level.valueOf(req.getParameter(ROOT_LOG_LEVEL_MODEL_ATTRIBUTE));
        parameters.remove(ROOT_LOG_LEVEL_MODEL_ATTRIBUTE);
        String maxFileSize = req.getParameter(MAX_FILE_SIZE_MODEL_ATTRIBUTE);
        parameters.remove(MAX_FILE_SIZE_MODEL_ATTRIBUTE);

        Map<String, AbstractLoggingConfigurator.Level> levels =
                new HashMap<String, AbstractLoggingConfigurator.Level>(parameters.size());

        for (String logger : parameters) {
            levels.put(logger, AbstractLoggingConfigurator.Level.valueOf(req.getParameter(logger)));
        }

        AbstractLoggingConfigurator lc = AbstractLoggingConfigurator.getInstance();
        lc.setMaxHistory(daysToKeep);
        lc.enableAppender(AbstractLoggingConfigurator.Appender.FILE, fileEnabled);
        lc.enableAppender(AbstractLoggingConfigurator.Appender.CONSOLE, consoleEnabled);
        lc.setRootLogLevel(rootLevel);
        lc.setLoggerLevel(levels);
        lc.setMaxFileSize(maxFileSize);

        return new ModelAndView(new RedirectView(ControllerConstants.Paths.ADMIN_LOGGING, true));
    }

    @ExceptionHandler(Throwable.class)
    public ModelAndView error(Throwable t) {
        ModelAndView mav = view();
        mav.addObject(ERROR_MODEL_ATTRIBUTE, t.getMessage());
        LOG.error("Error updating the logging configuration.", t);
        return mav;
    }
}
