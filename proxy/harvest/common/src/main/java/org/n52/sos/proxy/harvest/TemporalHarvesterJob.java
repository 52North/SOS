package org.n52.sos.proxy.harvest;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.PersistJobDataAfterExecution;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public interface TemporalHarvesterJob extends Job {

}
