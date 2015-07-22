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
package org.n52.sos.statistics.sos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import org.n52.iceland.event.ServiceEvent;
import org.n52.iceland.event.ServiceEventListener;
import org.n52.iceland.event.events.AbstractFlowEvent;
import org.n52.iceland.event.events.CountingOutputstreamEvent;
import org.n52.iceland.event.events.ExceptionEvent;
import org.n52.iceland.event.events.OutgoingResponseEvent;
import org.n52.iceland.event.events.RequestEvent;
import org.n52.iceland.event.events.ResponseEvent;
import org.n52.iceland.request.AbstractServiceRequest;
import org.n52.sos.statistics.api.interfaces.IStatisticsServiceEventResolver;
import org.n52.sos.statistics.api.interfaces.datahandler.IStatisticsDataHandler;
import org.n52.sos.statistics.impl.resolvers.CountingOutputstreamEventResolver;
import org.n52.sos.statistics.impl.resolvers.DefaultServiceEventResolver;
import org.n52.sos.statistics.impl.resolvers.OutgoingResponseEventResolver;
import org.n52.sos.statistics.sos.resolvers.SosExceptionEventResolver;
import org.n52.sos.statistics.sos.resolvers.SosRequestEventResolver;
import org.n52.sos.statistics.sos.resolvers.SosResponseEventResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.google.common.collect.ImmutableSet;

public class SosStatisticsServiceEventListener implements ServiceEventListener {

    private static final Logger logger = LoggerFactory.getLogger(SosStatisticsServiceEventListener.class);
    private static final int THREAD_POOL_SIZE = 2;
    private static final int EVENTS_ARR_SIZE = 4;
    private final ExecutorService executorService;
    private static final Set<Class<? extends ServiceEvent>> EVENT_TYPES = ImmutableSet.<Class<? extends ServiceEvent>> of(RequestEvent.class,
            ExceptionEvent.class, ResponseEvent.class, OutgoingResponseEvent.class, CountingOutputstreamEvent.class);

    private ConcurrentMap<Long, List<AbstractFlowEvent>> eventsCache = new ConcurrentHashMap<>();

    @Inject
    protected IStatisticsDataHandler dataHandler;

    // TODO spring-dependency is allowed here?
    @Inject
    private ApplicationContext ctx;

    public SosStatisticsServiceEventListener() {
        executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }

    @Override
    public Set<Class<? extends ServiceEvent>> getTypes() {
        return EVENT_TYPES;
    }

    @Override
    public void handle(ServiceEvent serviceEvent) {
        logger.debug("Event received: {}", serviceEvent);
        if (!dataHandler.isLoggingEnabled()) {
            return;
        }

        try {
            // collect events
            // since the key is the thread id. only one thread should write one
            // key-value. that is why no synchronization is needed.
            if (serviceEvent instanceof AbstractFlowEvent) {
                AbstractFlowEvent evt = (AbstractFlowEvent) serviceEvent;
                List<AbstractFlowEvent> eventList = eventsCache.get(evt.getMessageGroupId());
                if (eventList == null) {
                    eventList = new ArrayList<>(EVENTS_ARR_SIZE);
                    eventsCache.put(evt.getMessageGroupId(), eventList);
                }
                eventList.add(evt);

                // maybe here should use another implementation
                // which got some eviction policy if no last outgoing event
                // received
                // request - response - outgoingResponseEvent
                // request - exception - outgoingResponseEvent
                if (serviceEvent instanceof OutgoingResponseEvent) {
                    BatchResolver resolvers = new BatchResolver(dataHandler);
                    eventList.stream().forEach(l -> addToResolvers(resolvers, l));
                    executorService.execute(resolvers);
                    // resolvers.run();
                }

            } else {
                logger.trace("Unssupported type of event: {}", serviceEvent.getClass());
                DefaultServiceEventResolver defaultServiceEventResolver = getDefaultServiceEventResolver();
                defaultServiceEventResolver.setEvent(serviceEvent);

                BatchResolver singleOp = new BatchResolver(dataHandler);
                singleOp.events.add(defaultServiceEventResolver);
                executorService.execute(singleOp);
            }

        } catch (Throwable e) {
            logger.error("Can't handle event for statistics logging: {}", serviceEvent, e);
        } finally {

        }
    }

    private void addToResolvers(BatchResolver resolver,
            ServiceEvent event) {
        IStatisticsServiceEventResolver evtResolver = null;
        if (event instanceof RequestEvent) {
            AbstractServiceRequest<?> request = ((RequestEvent) event).getRequest();
            SosRequestEventResolver sosRequestEventResolver = getSosRequestEventResolver();
            sosRequestEventResolver.setRequest(request);
            evtResolver = sosRequestEventResolver;
        } else if (event instanceof ExceptionEvent) {
            SosExceptionEventResolver sosExceptionEventResolver = getSosExceptionEventResolver();
            sosExceptionEventResolver.setException(((ExceptionEvent) event).getException());
            evtResolver = sosExceptionEventResolver;
        } else if (event instanceof ResponseEvent) {
            SosResponseEventResolver responseEventResolver = getSosResponseEventResolver();
            responseEventResolver.setResponse(((ResponseEvent) event).getResponse());
            evtResolver = responseEventResolver;
        } else if (event instanceof OutgoingResponseEvent) {
            OutgoingResponseEventResolver outgoingResponseEventResolver = getOutgoingResponseEventResolver();
            outgoingResponseEventResolver.setResponse((OutgoingResponseEvent) event);
            evtResolver = outgoingResponseEventResolver;
        } else if (event instanceof CountingOutputstreamEvent) {
            CountingOutputstreamEventResolver countingOutputstreamEventResolver = getCountingOutputstreamEventResolver();
            countingOutputstreamEventResolver.setEvent((CountingOutputstreamEvent) event);
            evtResolver = countingOutputstreamEventResolver;
        } else {
            throw new IllegalArgumentException("Not a valid class " + event.getClass());
        }

        resolver.events.add(evtResolver);
    }

    @Override
    protected void finalize() throws Throwable {
        this.executorService.shutdown();
    }

    // prototype instance dependencies
    public SosRequestEventResolver getSosRequestEventResolver() {
        return ctx.getBean(SosRequestEventResolver.class);
    }

    private SosResponseEventResolver getSosResponseEventResolver() {
        return ctx.getBean(SosResponseEventResolver.class);
    }

    public SosExceptionEventResolver getSosExceptionEventResolver() {
        return ctx.getBean(SosExceptionEventResolver.class);
    }

    public DefaultServiceEventResolver getDefaultServiceEventResolver() {
        return ctx.getBean(DefaultServiceEventResolver.class);
    }

    private OutgoingResponseEventResolver getOutgoingResponseEventResolver() {
        return ctx.getBean(OutgoingResponseEventResolver.class);
    }

    private CountingOutputstreamEventResolver getCountingOutputstreamEventResolver() {
        return ctx.getBean(CountingOutputstreamEventResolver.class);
    }

    /**
     * Custom class for persisting the resolved {@link ServiceEvent}s
     */
    private static class BatchResolver implements Runnable {
        private final List<IStatisticsServiceEventResolver> events;
        private final IStatisticsDataHandler dataHandler;

        public BatchResolver(IStatisticsDataHandler dataHandler) {
            events = new ArrayList<>(EVENTS_ARR_SIZE);
            this.dataHandler = dataHandler;
        }

        @Override
        public void run() {
            Map<String, Object> data = new HashMap<>();
            try {
                events.stream().forEach(l -> data.putAll(l.resolve()));
                dataHandler.persist(data);
            } catch (Throwable e) {
                logger.error("Cannot persist event", e);
            }
        }
    }

}
