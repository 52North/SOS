package org.n52.sos.statistics;

import javax.inject.Inject;

import org.n52.sos.statistics.impl.resolvers.CountingOutputstreamEventResolver;
import org.n52.sos.statistics.impl.resolvers.DefaultServiceEventResolver;
import org.n52.sos.statistics.impl.resolvers.OutgoingResponseEventResolver;
import org.n52.sos.statistics.sos.resolvers.SosExceptionEventResolver;
import org.n52.sos.statistics.sos.resolvers.SosRequestEventResolver;
import org.n52.sos.statistics.sos.resolvers.SosResponseEventResolver;
import org.springframework.context.ApplicationContext;

public class SosStatisticsResolverFactory {

    @Inject
    private ApplicationContext ctx;

    // prototype instance dependencies
    public SosRequestEventResolver getSosRequestEventResolver() {
        return ctx.getBean(SosRequestEventResolver.class);
    }

    public SosResponseEventResolver getSosResponseEventResolver() {
        return ctx.getBean(SosResponseEventResolver.class);
    }

    public SosExceptionEventResolver getSosExceptionEventResolver() {
        return ctx.getBean(SosExceptionEventResolver.class);
    }

    public DefaultServiceEventResolver getDefaultServiceEventResolver() {
        return ctx.getBean(DefaultServiceEventResolver.class);
    }

    public OutgoingResponseEventResolver getOutgoingResponseEventResolver() {
        return ctx.getBean(OutgoingResponseEventResolver.class);
    }

    public CountingOutputstreamEventResolver getCountingOutputstreamEventResolver() {
        return ctx.getBean(CountingOutputstreamEventResolver.class);
    }

}
