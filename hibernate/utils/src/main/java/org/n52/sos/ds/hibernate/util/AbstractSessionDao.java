/*
 * Copyright 2016 52°North GmbH
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

    protected <T, X extends Exception> Optional<T> throwingQueryOptional(ThrowingFunction<Session, T, X> query) throws X {
        T throwingQuery = throwingQuery(query);
        return Optional.ofNullable(throwingQuery);
    }

    protected <T, X extends Exception> T throwingQuery(ThrowingFunction<Session, T, X> query) throws X {
        Objects.requireNonNull(query);
        Session session = this.sessionFactory.openSession();
        Transaction tx;
        try {
            tx = session.beginTransaction();
            T result = query.apply(session);
            tx.commit();
            return result;
        } finally {
            if (session != null) {
                session.close();
            }
        }
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
