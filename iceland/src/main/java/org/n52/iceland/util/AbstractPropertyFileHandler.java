/**
 * Copyright 2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.iceland.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.n52.iceland.exception.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 4.0.0
 * 
 */
public class AbstractPropertyFileHandler {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractPropertyFileHandler.class);

    private final File propertiesFile;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private Properties cache;

    public File getFile(boolean create) throws IOException {
        if (propertiesFile.exists() || (create && propertiesFile.createNewFile())) {
            return propertiesFile;
        }
        return null;
    }

    protected AbstractPropertyFileHandler(String name) {
        this.propertiesFile = new File(name);
    }

    private Properties load() throws IOException {
        if (this.cache == null) {
            File f = getFile(false);
            if (f == null) {
                return new Properties();
            }
            InputStream is = null;
            try {
                is = new FileInputStream(getFile(true));
                Properties p = new Properties();
                p.load(is);
                cache = p;
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        LOG.error("Error closing input stream", e);
                    }
                }
            }
        }
        return cache;
    }

    private void save(Properties p) throws IOException {
        OutputStream os = null;
        try {
            File f = getFile(true);
            os = new FileOutputStream(f);
            p.store(os, null);
            this.cache = p;
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    LOG.error("Error closing output stream", e);
                }
            }
        }
    }

    public String get(String m) throws ConfigurationException {
        lock.readLock().lock();
        try {
            return load().getProperty(m);
        } catch (IOException e) {
            throw new ConfigurationException("Error reading properties", e);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void delete(String m) throws ConfigurationException {
        lock.writeLock().lock();
        try {
            Properties p = load();
            p.remove(m);
            save(p);
        } catch (IOException e) {
            throw new ConfigurationException("Error writing properties", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void save(String m, String value) throws ConfigurationException {
        lock.writeLock().lock();
        try {
            Properties p = load();
            p.setProperty(m, value);
            save(p);
        } catch (IOException e) {
            throw new ConfigurationException("Error writing properties", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void saveAll(Properties properties) throws ConfigurationException {
        lock.writeLock().lock();
        try {
            Properties p = load();
            for (String key : properties.stringPropertyNames()) {
                p.setProperty(key, properties.getProperty(key));
            }
            save(p);
        } catch (IOException e) {
            throw new ConfigurationException("Error writing properties", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Properties getAll() throws ConfigurationException {
        lock.readLock().lock();
        try {
            return copyOf(load());
        } catch (IOException e) {
            throw new ConfigurationException("Error reading properties", e);
        } finally {
            lock.readLock().unlock();
        }
    }

    private static Properties copyOf(Properties p) {
        Properties np = new Properties();
        for (String s : p.stringPropertyNames()) {
            np.put(s, p.get(s));
        }
        return np;
    }

    public String getPath() {
        return this.propertiesFile.getAbsolutePath();
    }

    public boolean exists() {
        try {
            return getFile(false) != null;
        } catch (IOException ex) {
            /* won't be thrown */
            throw new RuntimeException(ex);
        }
    }

    public boolean delete() {
        try {
            cache = null;
            LOG.debug("Removing properties file: {}.", getFile(false));
            return exists() ? getFile(false).delete() : true;
        } catch (IOException ex) {
            return false;
        }
    }
}
