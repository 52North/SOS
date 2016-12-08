package org.n52.sos.ds.hibernate.cache.proxy;

import java.util.Set;

import javax.inject.Inject;

import org.n52.iceland.cache.ctrl.ScheduledContentCacheControllerSettings;
import org.n52.iceland.config.annotation.Setting;
import org.n52.iceland.ds.ConnectionProvider;
import org.n52.iceland.util.Validation;
import org.n52.io.task.ScheduledJob;
import org.quartz.SchedulerException;

public class HibernateDataSourceHarvestJobFactory {

    private String cronExpression;
    private ConnectionProvider connectionProvider;
    private HibernateDataSourceHarvesterScheduler scheduler;
    private Set<ScheduledJob> jobs;
    
    @Inject
    public void setConnectionProvider(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }
    
    @Inject
    public void setHibernateDataSourceHarvesterScheduler(HibernateDataSourceHarvesterScheduler scheduler) {
        this.scheduler = scheduler;
    }

    /**
     * @return the updateDefinition
     */
    public String getCronExpression() {
        return cronExpression;
    }

    /**
     * @param updateDefinition the updateDefinition to set
     */
    @Setting(ScheduledContentCacheControllerSettings.CAPABILITIES_CACHE_UPDATE)
    public void setCronExpression(String cronExpression) {
        Validation.notNullOrEmpty("Cron expression for cache update", cronExpression);
        if (!this.cronExpression.equalsIgnoreCase(cronExpression)) {
            this.cronExpression = cronExpression;
            reschedule();
        }
    }

    private void reschedule() {
        HibernateDataSourceHarvesterJob job = new HibernateDataSourceHarvesterJob();
        job.setEnabled(true);
        job.setCronExpression(getCronExpression());
        job.setTriggerAtStartup(true);
        job.setConnectionProvider(connectionProvider);
        if (jobs.contains(job)) {
            try {
                scheduler.updateJob(job);
            } catch (SchedulerException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            scheduler.scheduleJob(job);
        }
    }
}
