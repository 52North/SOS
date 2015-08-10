package org.n52.sos.statistics.sos;

import javax.inject.Inject;

import org.n52.iceland.event.ServiceEvent;
import org.n52.iceland.event.events.RequestEvent;
import org.n52.iceland.event.events.ResponseEvent;
import org.n52.sos.statistics.api.interfaces.StatisticsServiceEventResolver;
import org.n52.sos.statistics.impl.AbstractStatisticsServiceEventListener;
import org.n52.sos.statistics.impl.StatisticsResolverFactory;
import org.n52.sos.statistics.sos.resolvers.SosRequestEventResolver;
import org.n52.sos.statistics.sos.resolvers.SosResponseEventResolver;

import com.google.common.collect.ImmutableSet;

public class SosStatisticsServiceEventListener extends AbstractStatisticsServiceEventListener {

    @Inject
    private StatisticsResolverFactory resolverFactory;

    public SosStatisticsServiceEventListener() {
        registerEventType(ImmutableSet.<Class<? extends ServiceEvent>> of(RequestEvent.class, ResponseEvent.class));
    }

    @Override
    protected StatisticsServiceEventResolver<?> findResolver(ServiceEvent serviceEvent) {
        StatisticsServiceEventResolver<?> evtResolver = null;
        if (serviceEvent instanceof RequestEvent) {
            SosRequestEventResolver sosRequestEventResolver = resolverFactory.getPrototypeBean(SosRequestEventResolver.class);
            sosRequestEventResolver.setEvent((RequestEvent) serviceEvent);
            evtResolver = sosRequestEventResolver;
        } else if (serviceEvent instanceof ResponseEvent) {
            SosResponseEventResolver responseEventResolver = resolverFactory.getPrototypeBean(SosResponseEventResolver.class);
            responseEventResolver.setEvent((ResponseEvent) serviceEvent);
            evtResolver = responseEventResolver;
        }
        return evtResolver;
    }

}
