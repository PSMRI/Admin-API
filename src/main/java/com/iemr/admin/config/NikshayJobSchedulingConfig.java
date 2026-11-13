/*
* AMRIT – Accessible Medical Records via Integrated Technologies
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronExpression;

import com.iemr.admin.service.nikshayphasestatus.NikshayPhaseJob;

/**
 * Schedules the one Ni-kshay job (NikshayPhaseJob) with the cron expression in
 * nikshay.job.cron (env NIKSHAY_JOB_CRON; Spring format: second minute hour day month
 * weekday, e.g. 0 0 2 * * * for 02:00 every day). "-" turns the job off, and is the default
 * in the environment property files when NIKSHAY_JOB_CRON is not set. The application does
 * not start when the value is blank or not a valid cron expression. A change
 * needs a restart. Runs on one thread, so a run never overlaps the previous one.
 */
@Configuration
@EnableScheduling
public class NikshayJobSchedulingConfig implements SchedulingConfigurer {
	private static final Logger logger = LoggerFactory.getLogger(NikshayJobSchedulingConfig.class);

	static final String CRON_PROPERTY = "nikshay.job.cron";

	// Spring's value for "no schedule"
	private static final String CRON_DISABLED = "-";

	// The property files default a missing NIKSHAY_JOB_CRON to "-" (job off)
	@Value("${" + CRON_PROPERTY + "}")
	private String cron;

	@Autowired
	private NikshayPhaseJob nikshayPhaseJob;

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		String expression = cron == null ? "" : cron.trim();
		if (expression.isEmpty()) {
			throw new IllegalStateException(CRON_PROPERTY + " (env NIKSHAY_JOB_CRON) is not set. Set a cron expression, "
					+ "e.g. 0 0 2 * * *, or - to turn the Ni-kshay job off");
		}
		if (CRON_DISABLED.equals(expression)) {
			logger.info("{} is '-': the automatic Ni-kshay job is off", CRON_PROPERTY);
			return;
		}
		if (!CronExpression.isValidExpression(expression)) {
			throw new IllegalStateException(CRON_PROPERTY + " (env NIKSHAY_JOB_CRON) '" + expression
					+ "' is not a valid cron expression (second minute hour day month weekday)");
		}
		taskRegistrar.addCronTask(nikshayPhaseJob::runAutomaticPhases, expression);
		logger.info("Automatic Ni-kshay job scheduled with {} '{}'", CRON_PROPERTY, expression);
	}
}
