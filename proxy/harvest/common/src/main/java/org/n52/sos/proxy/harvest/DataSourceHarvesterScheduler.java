package org.n52.sos.proxy.harvest;

import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Date;

import org.joda.time.DateTime;
import org.n52.io.task.ScheduledJob;
import org.n52.janmayen.lifecycle.Destroyable;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataSourceHarvesterScheduler implements Destroyable {

    private final static Logger LOGGER = LoggerFactory.getLogger(DataSourceHarvesterScheduler.class);

    private int startupDelayInSeconds = 5;

    private Scheduler scheduler;

    private boolean enabled = true;

    public void init() {
        if (!enabled) {
            LOGGER.info(
                    "Job schedular disabled. No jobs will be triggered. This is also true for particularly enabled jobs.");
            return;
        }
        try {
            scheduler.startDelayed(startupDelayInSeconds);
            LOGGER.info("Scheduler will start jobs in {}s ...", startupDelayInSeconds);
        } catch (SchedulerException e) {
            LOGGER.error("Could not start scheduler.", e);
        }
    }

    public void updateJob(ScheduledJob taskToSchedule) throws SchedulerException {
        JobDetail details = taskToSchedule.createJobDetails();
        Trigger trigger = taskToSchedule.createTrigger(details.getKey());
        Date nextExecution = scheduler.rescheduleJob(trigger.getKey(), trigger);
        LOGGER.debug("Rescheduled job '{}' will be executed at '{}'!", details.getKey(), new DateTime(nextExecution));
    }

    public void scheduleJob(ScheduledJob taskToSchedule) {
        try {
            JobDetail details = taskToSchedule.createJobDetails();
            Trigger trigger = taskToSchedule.createTrigger(details.getKey());
            Date nextExecution = scheduler.scheduleJob(details, trigger);
            LOGGER.debug("Schedule job '{}' will be executed at '{}'!", details.getKey(), new DateTime(nextExecution));
            if (taskToSchedule.isTriggerAtStartup()) {
                LOGGER.debug("Schedule job '{}' to run once at startup.", details.getKey());
                Trigger onceAtStartup = newTrigger().withIdentity(details.getKey() + "_onceAtStartup")
                        .forJob(details.getKey()).build();
                Date startupExecution = scheduler.scheduleJob(onceAtStartup);
                LOGGER.debug("Schedule job '{}' will be executed on startup at '{}'!", details.getKey(),
                        new DateTime(startupExecution));
            }
        } catch (SchedulerException e) {
            LOGGER.warn("Could not schdule Job '{}'.", taskToSchedule.getJobName(), e);
        }
    }

    /**
     * Shuts down the task scheduler without waiting tasks to be finished.
     */
    public void shutdown() {
        try {
            scheduler.shutdown(false);
            LOGGER.info("Shutdown scheduler");
        } catch (SchedulerException e) {
            LOGGER.error("Could not scheduler.", e);
        }
    }

    @Override
    public void destroy() {
        shutdown();
    }

    public int getStartupDelayInSeconds() {
        return startupDelayInSeconds;
    }

    public void setStartupDelayInSeconds(int startupDelayInSeconds) {
        this.startupDelayInSeconds = startupDelayInSeconds;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}