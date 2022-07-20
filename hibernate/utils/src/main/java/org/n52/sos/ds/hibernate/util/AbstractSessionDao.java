/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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
package org.n52.sos.ds.hibernate.util;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import org.n52.janmayen.function.ThrowingConsumer;
import org.n52.janmayen.function.ThrowingFunction;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public abstract class AbstractSessionDao {

    private final SessionFactory sessionFactory;

    public AbstractSessionDao(SessionFactory sessionFactory) {
        this.sessionFactory = Objects.requireNonNull(sessionFactory);
    }

    protected <T, X extends Exception> Optional<T> throwingQueryOptional(ThrowingFunction<Session, T, X> query)
            throws X {
        T throwingQuery = throwingQuery(query);
        return Optional.ofNullable(throwingQuery);
    }

    protected <T, X extends Exception> T throwingQuery(ThrowingFunction<Session, T, X> query) throws X {
        Objects.requireNonNull(query);
        Session session = this.sessionFactory.openSession();
        Transaction tx;
        try {
            tx = getTransaction(session);
            T result = query.apply(session);
            tx.commit();
            return result;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    private Transaction getTransaction(Session session) {
        return session.getTransaction().isActive() ? session.getTransaction() : session.beginTransaction();
    }

    protected <X extends Exception> void throwingStatement(ThrowingConsumer<Session, X> statement) throws X {
        Objects.requireNonNull(statement);
        throwingQuery(s -> {
            statement.accept(s);
            return null;
        });
    }

    @SuppressWarnings(value = "unchecked")
    protected <T> T query(Function<Session, T> query) {
        return throwingQuery(s -> query.apply(s));
    }

    @SuppressWarnings(value = "unchecked")
    protected <T> Optional<T> queryOptional(Function<Session, ?> query) {
        return throwingQueryOptional(s -> (T) query.apply(s));
    }

    protected void statement(Consumer<Session> statement) {
        Objects.requireNonNull(statement);
        query(s -> {
            statement.accept(s);
            return null;
        });
    }
}
