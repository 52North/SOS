package org.n52.sos.proxy.harvest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.n52.faroe.Validation;
import org.n52.faroe.annotation.Configurable;
import org.n52.faroe.annotation.Setting;
import org.n52.io.task.ScheduledJob;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configurable
public class DataSourceHarvesterJobFactory {

    public final static String PROXY_FULL_HARVEST_UPDATE = "proxy.harvest.full";
    public final static String PROXY_TEMPORAL_HARVEST_UPDATE = "proxy.harvest.temporal";
    private final static Logger LOGGER = LoggerFactory.getLogger(DataSourceHarvesterJobFactory.class);
    private String cronFullExpression;
    private String cronTemporalExpression;
    private DataSourceHarvesterScheduler scheduler;
    private Set<String> jobs = new HashSet<>();
    private List<AbstractHarvesterJob> scheduledJobs = new ArrayList<>();

    @Inject
    public void setDataSourceHarvesterScheduler(DataSourceHarvesterScheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Inject
    public void setScheduledJobs(Optional<List<AbstractHarvesterJob>> scheduledJobs) {
        this.scheduledJobs.clear();
        if (scheduledJobs.isPresent()) {
            this.scheduledJobs.addAll(scheduledJobs.get());
        }
    }

    public List<AbstractHarvesterJob> getScheduledJobs() {
        return scheduledJobs;
    }

    /**
     * @return the updateDefinition
     */
    public String getFullCronExpression() {
        return cronFullExpression;
    }

    /**
     * @param updateDefinition
     *            the updateDefinition to set
     */
    @Setting(PROXY_FULL_HARVEST_UPDATE)
    public void setFullCronExpression(String cronExpression) {
        Validation.notNullOrEmpty("Cron expression for full update!", cronExpression);
        if (this.cronFullExpression == null) {
            this.cronFullExpression = cronExpression;
            reschedule();
        } else if (!this.cronFullExpression.equalsIgnoreCase(cronExpression)) {
            this.cronFullExpression = cronExpression;
            reschedule();
        }
    }

    /**
     * @return the updateDefinition
     */
    public String getTemporalCronExpression() {
        return cronFullExpression;
    }

    /**
     * @param updateDefinition
     *            the updateDefinition to set
     */
    @Setting(PROXY_TEMPORAL_HARVEST_UPDATE)
    public void setTemporalCronExpression(String cronExpression) {
        Validation.notNullOrEmpty("Cron expression for temporal update!", cronExpression);
        if (this.cronTemporalExpression == null) {
            this.cronTemporalExpression = cronExpression;
            reschedule();
        } else if (!this.cronTemporalExpression.equalsIgnoreCase(cronExpression)) {
            this.cronTemporalExpression = cronExpression;
            reschedule();
        }
    }

    private void reschedule() {
        for (ScheduledJob job : getScheduledJobs()) {
            if (jobs.contains(job.getJobName())) {
                try {
                    if (job instanceof FullHarvesterJob) {
                        job.setCronExpression(getFullCronExpression());
                        scheduler.updateJob(job);
                    } else if (job instanceof TemporalHarvesterJob) {
                        job.setCronExpression(getTemporalCronExpression());
                        scheduler.updateJob(job);
                    }
                } catch (SchedulerException e) {
                    LOGGER.error("Error while updating a job!", e);
                }
            } else {
                scheduler.scheduleJob(job);
            }
            jobs.add(job.getJobName());
        }
    }
}
