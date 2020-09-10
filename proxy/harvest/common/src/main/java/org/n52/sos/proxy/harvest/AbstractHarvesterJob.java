package org.n52.sos.proxy.harvest;

import javax.inject.Inject;

import org.n52.io.task.ScheduledJob;
import org.n52.janmayen.event.EventBus;
import org.n52.sos.proxy.da.InsertionRepository;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;

public abstract class AbstractHarvesterJob extends ScheduledJob implements Job {

    @Inject
    private InsertionRepository insertionRepository;
    @Inject
    private EventBus eventBus;

    @Override
    public JobDetail createJobDetails() {
        return JobBuilder.newJob(this.getClass()).withIdentity(getJobName()).build();
    }

    protected InsertionRepository getInsertionRepository() {
        return insertionRepository;
    }

    protected EventBus getEventBus() {
        return eventBus;
    }
}
