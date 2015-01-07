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
package org.n52.sos.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.service.AbstractLoggingConfigurator;
import org.n52.sos.util.FileIOHelper;
import org.slf4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @since 4.0.0
 * 
 */
public class LogBackLoggingConfigurator extends AbstractLoggingConfigurator {

    private static final String CONFIGURATION_FILE_NAME = "/logback.xml";

    private static final String AN_LEVEL = "level";

    private static final String AN_NAME = "name";

    private static final String AN_REF = "ref";

    private static final String AN_VALUE = "value";

    private static final String EN_ROLLING_POLICY = "rollingPolicy";

    private static final String EN_MAX_HISTORY = "maxHistory";

    private static final String EN_APPENDER = "appender";

    private static final String EN_APPENDER_REF = "appender-ref";

    private static final String EN_ROOT = "root";

    private static final String EN_LOGGER = "logger";

    private static final String EN_FILE = "file";

    private static final String EN_PROPERTY = "property";

    private static final String EN_MAX_FILE_SIZE = "maxFileSize";

    private static final String EN_TIME_BASED_FILE_NAME_AND_TRIGGERING_POLICY =
            "timeBasedFileNamingAndTriggeringPolicy";

    private static final String NOT_FOUND_ERROR_MESSAGE = "Can't find Logback configuration file.";

    private static final String UNPARSABLE_ERROR_MESSAGE = "Can't parse configuration file.";

    private static final String UNWRITABLE_ERROR_MESSAGE = "Can't write configuration file.";

    private static final String LOG_FILE_NOT_FOUND_ERROR_MESSAGE = "Log file could not be found";

    private static final int WRITE_DELAY = 4000;

    private static final Pattern PROPERTY_MATCHER = Pattern.compile("\\$\\{([^}]+)\\}");

    private static final ReadWriteLock LOCK = new ReentrantReadWriteLock();

    private Document cache = null;

    private File configuration = null;

    private DelayedWriteThread delayedWriteThread = null;

    private class DelayedWriteThread extends Thread {

        private Document doc;

        private boolean canceled = false;

        DelayedWriteThread(Document doc) {
            this.doc = doc;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(WRITE_DELAY);
                synchronized (this) {
                    if (!canceled) {
                        write();
                    }
                }
            } catch (InterruptedException e) {
            }
        }

        void cancel() {
            synchronized (this) {
                canceled = true;
            }
        }

        void write() {
            LOCK.writeLock().lock();
            LOG.debug("Writing LogBack configuration file!");
            try {
                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(configuration);
                    Transformer trans = TransformerFactory.newInstance().newTransformer();
                    trans.setOutputProperty(OutputKeys.INDENT, "yes");
                    OutputStreamWriter writer = new OutputStreamWriter(out);
                    trans.transform(new DOMSource(doc), new StreamResult(writer));
                } catch (TransformerException ex) {
                    LOG.error(UNWRITABLE_ERROR_MESSAGE, ex);
                } catch (IOException ex) {
                    LOG.error(UNWRITABLE_ERROR_MESSAGE, ex);
                } finally {
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e) {
                            LOG.error(UNWRITABLE_ERROR_MESSAGE, e);
                        }
                    }
                }
            } finally {
                LOCK.writeLock().unlock();
            }
        }
    }

    public LogBackLoggingConfigurator() throws ConfigurationException {
        this(CONFIGURATION_FILE_NAME);
    }

    public LogBackLoggingConfigurator(String filename) throws ConfigurationException {
        this(getFile(filename));
    }

    private static File getFile(String name) throws ConfigurationException {
        File f = new File(name);
        if (f.exists()) {
            return f;
        }
        URL url = LogBackLoggingConfigurator.class.getResource(name);
        try {
            return new File(url.toURI());
        } catch (Exception ex) {
            LOG.error(NOT_FOUND_ERROR_MESSAGE, ex);
            throw new ConfigurationException(NOT_FOUND_ERROR_MESSAGE, ex);
        }
    }

    public LogBackLoggingConfigurator(File file) throws ConfigurationException {
        configuration = file;
        if (configuration == null || !configuration.exists()) {
            LOG.error(NOT_FOUND_ERROR_MESSAGE);
            throw new ConfigurationException(NOT_FOUND_ERROR_MESSAGE);
        }
        LOG.info("Using Logback Config File: {}", configuration.getAbsolutePath());
    }

    private Document read() throws ConfigurationException {
        LOCK.readLock().lock();
        try {
            try {
                if (cache == null) {
                    cache = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(configuration);
                }
                return cache;
            } catch (ParserConfigurationException ex) {
                LOG.error(UNPARSABLE_ERROR_MESSAGE, ex);
                throw new ConfigurationException(UNPARSABLE_ERROR_MESSAGE, ex);
            } catch (SAXException ex) {
                LOG.error(UNPARSABLE_ERROR_MESSAGE, ex);
                throw new ConfigurationException(UNPARSABLE_ERROR_MESSAGE, ex);
            } catch (IOException ex) {
                LOG.error(UNPARSABLE_ERROR_MESSAGE, ex);
                throw new ConfigurationException(UNPARSABLE_ERROR_MESSAGE, ex);
            }
        } finally {
            LOCK.readLock().unlock();
        }
    }

    private void write() {
        LOCK.writeLock().lock();
        try {
            /* delay the actual writing to aggregate changes to one IO task */
            if (this.delayedWriteThread != null) {
                this.delayedWriteThread.cancel();
                this.delayedWriteThread.interrupt();
            }
            this.delayedWriteThread = new DelayedWriteThread(this.cache);
            this.delayedWriteThread.start();
        } finally {
            LOCK.writeLock().unlock();
        }

    }

    @Override
    public boolean setMaxHistory(int days) {
        LOCK.writeLock().lock();
        try {
            Document doc = read();
            List<Element> appender = getChildren(doc.getDocumentElement(), EN_APPENDER);
            for (Element a : appender) {
                if (getAttribute(a, AN_NAME).getValue().equals(Appender.FILE.getName())) {
                    Element rollingPolicy = getSingleChildren(a, EN_ROLLING_POLICY);
                    Element maxHistory = getSingleChildren(rollingPolicy, EN_MAX_HISTORY);
                    int before = -1;
                    try {
                        before = Integer.parseInt(maxHistory.getTextContent());
                    } catch (NumberFormatException e) {
                    }
                    if (before != days) {
                        LOG.debug("Setting max logging history to {} days.", days);
                        maxHistory.setTextContent(String.valueOf(days));
                    }
                }
            }
            write();
        } catch (ConfigurationException e) {
            LOG.error(UNPARSABLE_ERROR_MESSAGE, e);
            return false;
        } finally {
            LOCK.writeLock().unlock();
        }
        return true;
    }

    @Override
    public Set<Appender> getEnabledAppender() {
        LOCK.readLock().lock();
        Set<Appender> appender = new HashSet<Appender>(Appender.values().length);
        try {
            List<Element> refs = getChildren(getRoot(read().getDocumentElement()), EN_APPENDER_REF);
            for (Element ref : refs) {
                appender.add(Appender.byName(getAttribute(ref, AN_REF).getValue()));
            }
        } catch (ConfigurationException e) {
            LOG.error(UNPARSABLE_ERROR_MESSAGE, e);
            return Collections.emptySet();
        } finally {
            LOCK.readLock().unlock();
        }
        return appender;
    }

    @Override
    public boolean isEnabled(Appender a) {
        LOCK.readLock().lock();
        try {
            return getEnabledAppender().contains(a);
        } finally {
            LOCK.readLock().unlock();
        }
    }

    @Override
    public boolean enableAppender(Appender a, boolean enable) {
        LOCK.writeLock().lock();
        try {
            Document doc = read();
            Element root = getRoot(doc.getDocumentElement());
            Element refNode = null;
            List<Element> refs = getChildren(root, EN_APPENDER_REF);
            for (Element ref : refs) {
                if (getAttribute(ref, AN_REF).getValue().equals(a.getName())) {
                    refNode = ref;
                    break;
                }
            }
            if (enable && refNode == null) {
                LOG.debug("Enabling {} logging appender", a.getName());
                refNode = doc.createElement(EN_APPENDER_REF);
                refNode.setAttribute(AN_REF, a.getName());
                root.appendChild(refNode);
                write();
            } else if (!enable && refNode != null) {
                LOG.debug("Disabling {} logging appender", a.getName());
                root.removeChild(refNode);
                write();
            }
            return true;
        } catch (ConfigurationException e) {
            LOG.error(UNPARSABLE_ERROR_MESSAGE, e);
            return false;
        } finally {
            LOCK.writeLock().unlock();
        }
    }

    private Element getSingleChildren(Node parent, String name) throws ConfigurationException {
        NodeList nl = parent.getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            Node n = nl.item(i);
            if (name.equals(n.getNodeName())) {
                return (Element) n;
            }

        }
        throw new ConfigurationException("<" + name + "> not found!");
    }

    private Element getRoot(Node configuration) throws ConfigurationException {
        return getSingleChildren(configuration, EN_ROOT);
    }

    private Attr getAttribute(Node x, String name) throws ConfigurationException {
        NamedNodeMap attributes = x.getAttributes();
        Attr a = (Attr) attributes.getNamedItem(name);
        if (a != null) {
            return a;
        }
        throw new ConfigurationException("Missing attribute: " + name);
    }

    @Override
    public boolean setRootLogLevel(Level level) {
        LOCK.writeLock().lock();
        try {
            try {
                Document doc = read();
                Element root = getRoot(doc.getDocumentElement());
                String currentLevel = getAttribute(root, AN_LEVEL).getValue();
                if (Level.valueOf(currentLevel) == level) {
                    return true;
                }
                LOG.debug("Setting root logging level to {}", level);
                root.setAttribute(AN_LEVEL, level.toString());
                write();
            } catch (ConfigurationException e) {
                LOG.error(UNPARSABLE_ERROR_MESSAGE, e);
                return false;
            }
            return true;
        } finally {
            LOCK.writeLock().unlock();
        }
    }

    private List<Element> getChildren(Element parent, String name) {
        NodeList nl = parent.getChildNodes();
        ArrayList<Element> childs = new ArrayList<Element>(nl.getLength());
        for (int i = 0; i < nl.getLength(); ++i) {
            if (nl.item(i).getNodeType() == Node.ELEMENT_NODE && nl.item(i).getNodeName().equals(name)) {
                childs.add((Element) nl.item(i));
            }
        }
        return childs;
    }

    @Override
    public boolean setLoggerLevel(String id, Level level) {
        LOCK.writeLock().lock();
        try {
            if (id.equals(Logger.ROOT_LOGGER_NAME)) {
                return setRootLogLevel(level);
            }
            Document doc = read();
            Element conf = doc.getDocumentElement();
            Element l = null;
            List<Element> loggers = getChildren(conf, EN_LOGGER);
            for (Element logger : loggers) {
                if (getAttribute(logger, AN_NAME).getValue().equals(id)) {
                    l = logger;
                }
            }
            if (l == null) {
                LOG.debug("Setting logging level of {} to {}.", id, level);
                l = doc.createElement(EN_LOGGER);
                l.setAttribute(AN_NAME, id);
                l.setAttribute(AN_LEVEL, level.name());
                conf.appendChild(l);
                write();
            } else {
                String oldLevel = l.getAttribute(AN_LEVEL);
                if (!oldLevel.equals(level.name())) {
                    LOG.debug("Setting logging level of {} to {}.", id, level);
                    l.setAttribute(AN_LEVEL, level.name());
                    write();
                }
            }
            return true;
        } catch (ConfigurationException e) {
            LOG.error(UNPARSABLE_ERROR_MESSAGE, e);
            return false;
        } finally {
            LOCK.writeLock().unlock();
        }
    }

    @Override
    public boolean setLoggerLevel(Map<String, Level> levels) {
        LOCK.writeLock().lock();
        try {
            Document doc = read();
            Element conf = doc.getDocumentElement();

            List<Element> loggers = getChildren(conf, EN_LOGGER);
            Map<String, Element> currentLoggers = new HashMap<String, Element>(loggers.size());
            for (Element logger : loggers) {
                currentLoggers.put(getAttribute(logger, AN_NAME).getValue(), logger);
            }
            boolean write = false;
            /* remove obsolete loggers */
            for (String logger : currentLoggers.keySet()) {
                if (levels.get(logger) == null) {
                    LOG.debug("Removing logger {}", logger);
                    conf.removeChild(currentLoggers.get(logger));
                    write = true;
                }
            }

            for (String logger : levels.keySet()) {
                if (logger.equals(Logger.ROOT_LOGGER_NAME)) {
                    setRootLogLevel(levels.get(logger));
                } else {
                    Element l = currentLoggers.get(logger);
                    if (l == null) {
                        LOG.debug("Setting logging level of {} to {}.", logger, levels.get(logger));
                        l = doc.createElement(EN_LOGGER);
                        l.setAttribute(AN_NAME, logger);
                        l.setAttribute(AN_LEVEL, levels.get(logger).name());
                        conf.appendChild(l);
                        write = true;
                    } else {
                        String oldLevel = l.getAttribute(AN_LEVEL);
                        if (!oldLevel.equals(levels.get(logger).name())) {
                            LOG.debug("Setting logging level of {} to {}.", logger, levels.get(logger));
                            l.setAttribute(AN_LEVEL, levels.get(logger).name());
                            write = true;
                        }
                    }
                }
            }
            if (write) {
                write();
            }
            return true;
        } catch (ConfigurationException e) {
            LOG.error(UNPARSABLE_ERROR_MESSAGE, e);
            return false;
        } finally {
            LOCK.writeLock().unlock();
        }
    }

    @Override
    public Level getRootLogLevel() {
        LOCK.readLock().lock();
        try {
            Level level = null;
            try {
                Document doc = read();
                Element root = getRoot(doc.getDocumentElement());
                String currentLevel = getAttribute(root, AN_LEVEL).getValue();
                level = Level.valueOf(currentLevel);
            } catch (ConfigurationException e) {
                LOG.error(UNPARSABLE_ERROR_MESSAGE, e);
            }
            return level;
        } finally {
            LOCK.readLock().unlock();
        }
    }

    @Override
    public Map<String, Level> getLoggerLevels() {
        LOCK.readLock().lock();
        try {
            Map<String, Level> levels = new HashMap<String, Level>();
            try {
                List<Element> loggers = getChildren(read().getDocumentElement(), EN_LOGGER);
                for (Element logger : loggers) {
                    levels.put(getAttribute(logger, AN_NAME).getValue(),
                            Level.valueOf(getAttribute(logger, AN_LEVEL).getValue()));
                }
            } catch (ConfigurationException e) {
                LOG.error(UNPARSABLE_ERROR_MESSAGE, e);
            }
            return levels;
        } finally {
            LOCK.readLock().unlock();
        }
    }

    @Override
    public Level getLoggerLevel(String id) {
        LOCK.readLock().lock();
        try {
            if (id.equals(Logger.ROOT_LOGGER_NAME)) {
                return getRootLogLevel();
            }
            return getLoggerLevels().get(id);
        } finally {
            LOCK.readLock().unlock();
        }
    }

    @Override
    public int getMaxHistory() {
        LOCK.readLock().lock();
        try {
            int max = -1;
            try {
                Document doc = read();
                List<Element> appender = getChildren(doc.getDocumentElement(), EN_APPENDER);
                for (Element a : appender) {
                    if (getAttribute(a, AN_NAME).getValue().equals(Appender.FILE.getName())) {
                        try {
                            max =
                                    Integer.parseInt(getSingleChildren(getSingleChildren(a, EN_ROLLING_POLICY),
                                            EN_MAX_HISTORY).getTextContent());
                        } catch (NumberFormatException e) {
                        }
                    }
                }
            } catch (ConfigurationException e) {
                LOG.error(UNPARSABLE_ERROR_MESSAGE, e);
            }
            return max;
        } finally {
            LOCK.readLock().unlock();
        }
    }

    @Override
    public String getMaxFileSize() {
        LOCK.readLock().lock();
        try {
            String maxFileSize = null;
            try {
                Document doc = read();
                List<Element> appender = getChildren(doc.getDocumentElement(), EN_APPENDER);
                for (Element a : appender) {
                    if (getAttribute(a, AN_NAME).getValue().equals(Appender.FILE.getName())) {
                        maxFileSize =
                                getSingleChildren(
                                        getSingleChildren(getSingleChildren(a, EN_ROLLING_POLICY),
                                                EN_TIME_BASED_FILE_NAME_AND_TRIGGERING_POLICY), EN_MAX_FILE_SIZE)
                                        .getTextContent();
                    }
                }
            } catch (ConfigurationException e) {
                LOG.error(UNPARSABLE_ERROR_MESSAGE, e);
            }
            return maxFileSize;
        } finally {
            LOCK.readLock().unlock();
        }
    }

    @Override
    public boolean setMaxFileSize(String size) {
        if (size == null) {
            return false;
        }
        LOCK.writeLock().lock();
        try {
            Document doc = read();
            List<Element> appender = getChildren(doc.getDocumentElement(), EN_APPENDER);
            for (Element a : appender) {
                if (getAttribute(a, AN_NAME).getValue().equals(Appender.FILE.getName())) {
                    Element maxFileSize =
                            getSingleChildren(
                                    getSingleChildren(getSingleChildren(a, EN_ROLLING_POLICY),
                                            EN_TIME_BASED_FILE_NAME_AND_TRIGGERING_POLICY), EN_MAX_FILE_SIZE);
                    String before = maxFileSize.getTextContent().trim();
                    if (!before.equals(size.trim())) {
                        LOG.debug("Setting max logging file size to {}.", size);
                        maxFileSize.setTextContent(size.trim());
                    }
                }
            }
            write();
        } catch (ConfigurationException e) {
            LOG.error(UNPARSABLE_ERROR_MESSAGE, e);
            return false;
        } finally {
            LOCK.writeLock().unlock();
        }
        return true;
    }

    @Override
    public List<String> getLastLogEntries(int maxSize) {
        File f = getLogFile1();
        if (f != null) {
            try {
                return FileIOHelper.tail(f, maxSize);
            } catch (IOException ex) {
                LOG.error("Could not read log file", ex);
            }
        }
        return Collections.emptyList();
    }

    private File getLogFile1() {
        String file = null;
        LOCK.readLock().lock();
        try {
            Element doc = read().getDocumentElement();
            for (Element a : getChildren(doc, EN_APPENDER)) {
                if (getAttribute(a, AN_NAME).getValue().equals(Appender.FILE.getName())) {
                    file = getSingleChildren(a, EN_FILE).getTextContent();
                }
            }

            Map<String, String> properties = new HashMap<String, String>();
            for (Element p : getChildren(doc, EN_PROPERTY)) {
                properties.put(getAttribute(p, AN_NAME).getValue(), getAttribute(p, AN_VALUE).getValue());
            }

            if (file == null) {
                LOG.error(LOG_FILE_NOT_FOUND_ERROR_MESSAGE);
                return null;
            }

            Matcher matcher = PROPERTY_MATCHER.matcher(file);
            while (matcher.find()) {
                String key = matcher.group(1);
                String value = properties.get(key);
                if (value == null) {
                    value = System.getProperty(key, null);
                }

                if (value == null) {
                    LOG.error("Could not replace property {} in file name string {}", key, file);
                    return null;
                }
                file = file.replace(matcher.group(), value);
                matcher = PROPERTY_MATCHER.matcher(file);
            }
            LOG.debug("Logfile: {}", file);
            File f = new File(file);
            if (!f.exists()) {
                LOG.error("Can not find log file {}", f.getAbsolutePath());
                return null;
            }
            return f;
        } catch (ConfigurationException ex) {
            LOG.error(UNPARSABLE_ERROR_MESSAGE, ex);
            return null;
        } finally {
            LOCK.readLock().unlock();
        }
    }

    @Override
    public InputStream getLogFile() {
        File f = getLogFile1();
        if (f != null) {
            try {
                return FileIOHelper.loadInputStreamFromFile(f);
            } catch (OwsExceptionReport ex) {
                LOG.error("Could not read log file", ex);
            }
        }
        return null;
    }
}
