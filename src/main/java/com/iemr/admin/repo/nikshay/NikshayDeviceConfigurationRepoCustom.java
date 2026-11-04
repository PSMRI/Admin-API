package com.iemr.admin.repo.nikshay;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.iemr.admin.data.nikshay.NikshayDeviceConfiguration;

public interface NikshayDeviceConfigurationRepoCustom {

	@Transactional(rollbackFor = Exception.class)
	void batchInsert(List<NikshayDeviceConfiguration> devices);

	@Transactional(rollbackFor = Exception.class)
	void insert(NikshayDeviceConfiguration device);
}
