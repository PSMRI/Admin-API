package com.iemr.admin.service.assetmanagement;

import java.util.List;

import com.iemr.admin.data.assetmanagement.AssetCategory;

public interface AssetManagementService {

	AssetCategory save(AssetCategory assetCategory);

	List<AssetCategory> getAll(String assetType);
}
