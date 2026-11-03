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

import java.time.Instant;
import java.time.ZonedDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronExpression;

import com.iemr.admin.service.nikshayphasestatus.NikshayPhaseJob;

/**
 * One cron task for the whole Ni-kshay flow (NikshayPhaseJob). It wakes about once a
 * minute and reads nikshay_job_schedule again, so turning it off or changing the cron
 * applies without a restart. The phases run only when the row is on and the cron matches
 * the current minute. There is no separate cron per phase.
 */
@Configuration
@EnableScheduling
public class NikshayJobSchedulingConfig implements SchedulingConfigurer {
	private static final Logger logger = LoggerFactory.getLogger(NikshayJobSchedulingConfig.class);

	// How often the row is read again. On, off, and a new cron are noticed within this time.
	private static final long RECHECK_SECONDS = 60;

	@Autowired
	private NikshayPhaseJob nikshayPhaseJob;

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.addTriggerTask(nikshayPhaseJob::runAutomaticPhases, this::nextRun);
		logger.info("Ni-kshay job uses nikshay_job_schedule for on/off and cron");
	}

	private Instant nextRun(TriggerContext triggerContext) {
		if (triggerContext.lastCompletion() == null) {
			return Instant.now();
		}
		return Instant.now().plusSeconds(RECHECK_SECONDS);
	}

	/** True when this minute is one the cron expression names. The second field is not waited for. */
	public static boolean isDue(String cron, ZonedDateTime now) {
		if (cron == null || !CronExpression.isValidExpression(cron.trim())) {
			return false;
		}
		ZonedDateTime minute = now.withSecond(0).withNano(0);
		ZonedDateTime next = CronExpression.parse(cron.trim()).next(minute.minusSeconds(1));
		return next != null && next.withSecond(0).withNano(0).equals(minute);
	}
}
