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
package org.n52.sos.web.common.auth;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.n52.faroe.annotation.Configurable;
import org.n52.faroe.annotation.Setting;
import org.n52.janmayen.lifecycle.Constructable;
import org.n52.janmayen.net.IPAddress;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@Configurable
public class LimitLoginAttemptService implements Constructable {
    public static final String MAX_ATTEMPT_KEY = "service.security.login.attempt";

    public static final String LOGIN_LOCK_DURATION_KEY = "service.security.login.lock.duration";

    public static final String LOGIN_LOCK_TIME_UNIT_KEY = "service.security.login.lock.timeunit";

    private static final int MAX_ATTEMPT = 5;

    private LoadingCache<String, Integer> cache;

    private int maxAttempt = MAX_ATTEMPT;

    private int duration = 1;

    private TimeUnit timeUnit = TimeUnit.DAYS;

    public LimitLoginAttemptService() {
        super();
    }

    @Override
    public void init() {
        cache = CacheBuilder.newBuilder()
                .expireAfterWrite(duration, timeUnit)
                .build(new CacheLoader<String, Integer>() {
                    public Integer load(String key) {
                        return 0;
                    }
                });
    }

    public void loginSucceeded(String key) {
        cache.invalidate(key);
    }

    public void loginFailed(String key) {
        if (!checkLocalhost(key)) {
            int attempts = 0;
            try {
                attempts = cache.get(key);
            } catch (ExecutionException e) {
                attempts = 0;
            }
            attempts++;
            cache.put(key, attempts);
        }
    }

    public boolean isBlocked(String key) {
        try {
            return cache.get(key) >= maxAttempt;
        } catch (ExecutionException e) {
            return false;
        }
    }

    private boolean checkLocalhost(String key) {
        IPAddress ipAddress = new IPAddress(key);
        if (ipAddress.isIPv4()) {
            return new IPAddress("127.0.0.1").equals(ipAddress);
        } else if (ipAddress.isIPv6()) {
            return new IPAddress("::1").equals(ipAddress);
        }
        return false;
    }

    @Setting(value = MAX_ATTEMPT_KEY, required = false)
    public void setMaxAttempt(Integer attempts) {
        this.maxAttempt = attempts != null && attempts > 0 ? attempts : MAX_ATTEMPT;
    }

    @Setting(value = LOGIN_LOCK_DURATION_KEY, required = false)
    public void setDuration(Integer duration) {
        this.duration = duration != null && duration > 0 ? duration : 1;
    }

    @Setting(value = LOGIN_LOCK_TIME_UNIT_KEY, required = false)
    public void setTimeUnit(String timeUnit) {
        this.timeUnit = timeUnit != null && !timeUnit.isEmpty() ? TimeUnit.valueOf(timeUnit) : TimeUnit.DAYS;
    }
}
