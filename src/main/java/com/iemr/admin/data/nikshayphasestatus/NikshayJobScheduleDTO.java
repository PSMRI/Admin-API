package com.iemr.admin.data.nikshayphasestatus;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;

/** The one Ni-kshay cron job: on/off and when it runs, from nikshay_job_schedule. */
@Data
@AllArgsConstructor
public class NikshayJobScheduleDTO {

	private boolean enabled;
	// Spring cron: second minute hour day month weekday. Null when not set.
	private String cronExpression;
	private String updatedBy;
	private Timestamp updatedAt;
}
