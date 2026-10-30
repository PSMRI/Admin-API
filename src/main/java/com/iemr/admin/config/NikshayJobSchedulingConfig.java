/*
* AMRIT – Accessible Medical Records via Integrated Technology
* Integrated EHR (Electronic Health Records) Solution
*
* Copyright (C) "Piramal Swasthya Management and Research Institute"
*
* This file is part of AMRIT.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see https://www.gnu.org/licenses/.
*/
package com.iemr.admin.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronExpression;

import com.iemr.admin.service.nikshayphasestatus.NikshayPhaseJob;

/**
 * Schedules the automatic Ni-kshay job (NikshayPhaseJob) with the cron expression in
 * nikshay.job.cron (Spring format: second minute hour day month weekday). The job is not
 * scheduled when the property is missing, empty or not a valid cron expression. Runs on
 * one thread, so a run never overlaps the previous one.
 */
@Configuration
@EnableScheduling
public class NikshayJobSchedulingConfig implements SchedulingConfigurer {
	private static final Logger logger = LoggerFactory.getLogger(NikshayJobSchedulingConfig.class);

	static final String CRON_PROPERTY = "nikshay.job.cron";

	@Autowired
	private Environment environment;

	@Autowired
	private NikshayPhaseJob nikshayPhaseJob;

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		String cron = environment.getProperty(CRON_PROPERTY);
		if (cron == null || cron.isBlank()) {
			logger.info("{} is not set: the automatic Ni-kshay job does not run", CRON_PROPERTY);
			return;
		}
		cron = cron.trim();
		if (!CronExpression.isValidExpression(cron)) {
			logger.error("{} '{}' is not a valid cron expression: the automatic Ni-kshay job does not run",
					CRON_PROPERTY, cron);
			return;
		}
		taskRegistrar.addCronTask(nikshayPhaseJob::runAutomaticPhases, cron);
		logger.info("Automatic Ni-kshay job scheduled with {} '{}'", CRON_PROPERTY, cron);
	}
}
