/**
 * Copyright 2017 BOCO Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openo.nfvo.emsdriver.taskscheduler;


import java.text.ParseException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openo.nfvo.emsdriver.commons.model.CollectVo;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

public class QuartzManager {


	private static Log log = LogFactory.getFactory().getInstance(QuartzManager.class);
	private static SchedulerFactory gSchedulerFactory = new StdSchedulerFactory();
	private static String JOB_GROUP_NAME = "EXTJWEB_JOBGROUP_NAME";
	private static String TRIGGER_GROUP_NAME = "EXTJWEB_TRIGGERGROUP_NAME";

	/**
	 * @param jobName
	 * @param job
	 * @param time
	 * @throws SchedulerException
	 * @throws ParseException
	 */
	public static boolean addJob(String jobName, String jobClass, String time,CollectVo collectVo) {
		boolean sucess = false;
		try {
			Scheduler sched = gSchedulerFactory.getScheduler();
			JobDetail jobDetail = new JobDetail(jobName, JOB_GROUP_NAME, Class.forName(jobClass));
			
			CronTrigger trigger = new CronTrigger(jobName, TRIGGER_GROUP_NAME);
			trigger.setCronExpression(time);
			
			jobDetail.getJobDataMap().put("collectVo", collectVo);
			sched.scheduleJob(jobDetail, trigger);
			if (!sched.isShutdown()){
				sched.start();
				
			}
			sucess = true;
		} catch (Exception e) {
			log.error("add job fail cronExpression="+time,e);
			sucess = false;
		}
		return sucess;
	}



	/**
	 * @param jobName
	 * @param time
	 */
	@SuppressWarnings("unchecked")
	public static boolean modifyJobTime(String jobName, String time,CollectVo collectVo) {
		boolean sucess = false;
		try {
			Scheduler sched = gSchedulerFactory.getScheduler();
			CronTrigger trigger = (CronTrigger) sched.getTrigger(jobName, TRIGGER_GROUP_NAME);
			if(trigger == null) {
				return false;
			}
			String oldTime = trigger.getCronExpression();
			if (!oldTime.equalsIgnoreCase(time)) {
				JobDetail jobDetail = sched.getJobDetail(jobName, JOB_GROUP_NAME);
				
				Class<Job> objJobClass = jobDetail.getJobClass();
				String jobClass = objJobClass.getName();
				removeJob(jobName);

				addJob(jobName, jobClass, time,collectVo);
			}
			sucess = true;
		} catch (Exception e) {
			log.error("modifyJobTime fail cronExpression="+time,e);
			sucess = false;
		}
		return sucess ;
	}

	/**


	/**
	 * @param jobName
	 */
	public static boolean removeJob(String jobName) {
		boolean sucess = false;
		try {
			Scheduler sched = gSchedulerFactory.getScheduler();
			sched.pauseTrigger(jobName, TRIGGER_GROUP_NAME);
			sched.unscheduleJob(jobName, TRIGGER_GROUP_NAME);
			sched.deleteJob(jobName, JOB_GROUP_NAME);
			sucess = true;
		} catch (Exception e) {
			sucess = false;
			log.error("remove job fail jobName="+jobName,e);
		}
		return sucess;
	}

	

	/**
	 * 
	 * @return
	 */
	public static boolean startJobs() {
		boolean sucess = false;
		try {
			Scheduler sched = gSchedulerFactory.getScheduler();
			sched.start();
			sucess = true;
		} catch (Exception e) {
			sucess = false;
			log.error("start jobs fail",e);
		}
		return sucess;
	}

	/**
	 * 
	 * @return
	 */
	public static boolean shutdownJobs() {
		boolean sucess = false;
		try {
			Scheduler sched = gSchedulerFactory.getScheduler();
			if(!sched.isShutdown()) {
				sched.shutdown();
			}
			sucess = true;
		} catch (Exception e) {
			sucess = false;
			log.error("shutdown jobs fail ",e);
		}
		
		return sucess;
	}
}
