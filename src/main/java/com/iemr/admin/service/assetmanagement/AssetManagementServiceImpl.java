package com.iemr.admin.service.assetmanagement;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iemr.admin.data.assetmanagement.AssetCategory;
import com.iemr.admin.repo.assetmanagement.AssetCategoryRepo;

@Service
public class AssetManagementServiceImpl implements AssetManagementService {

	@Autowired
	private AssetCategoryRepo assetCategoryRepo;

	@Override
	@Transactional
	public AssetCategory save(AssetCategory assetCategory) {
		if (assetCategory == null) {
			throw new IllegalArgumentException("Asset category request is required");
		}

		assetCategory.setAssetType(trimRequired(assetCategory.getAssetType(), "Asset type"));
		assetCategory.setAssetCode(trimRequired(assetCategory.getAssetCode(), "Asset code"));
		assetCategory.setAssetName(trimRequired(assetCategory.getAssetName(), "Asset name"));

		Integer assetCategoryId = assetCategory.getAssetCategoryId();
		AssetCategory recordToSave;
		if (assetCategoryId == null) {
			recordToSave = assetCategory;
			recordToSave.setStatus(assetCategory.getStatus() == null ? "Active" : assetCategory.getStatus());
			recordToSave.setDeleted(false);
		} else {
			recordToSave = assetCategoryRepo.findById(assetCategoryId)
					.orElseThrow(() -> new NoSuchElementException("Asset category not found"));
			recordToSave.setAssetType(assetCategory.getAssetType());
			recordToSave.setAssetCode(assetCategory.getAssetCode());
			recordToSave.setAssetName(assetCategory.getAssetName());
			recordToSave.setAssetDescription(assetCategory.getAssetDescription());
			if (assetCategory.getStatus() != null) {
				recordToSave.setStatus(assetCategory.getStatus());
			}
			recordToSave.setModifiedBy(assetCategory.getModifiedBy());
		}

		if (hasDuplicateCode(assetCategory.getAssetCode(), assetCategoryId)) {
			throw new IllegalArgumentException("Asset code must be unique");
		}
		if (hasDuplicateName(assetCategory.getAssetType(), assetCategory.getAssetName(), assetCategoryId)) {
			throw new IllegalArgumentException("Asset name must be unique within its asset type");
		}

		return assetCategoryRepo.save(recordToSave);
	}

	@Override
	@Transactional(readOnly = true)
	public List<AssetCategory> getAll(String assetType) {
		if (assetType == null || assetType.trim().isEmpty()) {
			return assetCategoryRepo.findByDeletedFalseOrderByAssetTypeAscAssetNameAsc();
		}
		return assetCategoryRepo.findByAssetTypeAndDeletedFalseOrderByAssetNameAsc(assetType.trim());
	}

	private boolean hasDuplicateCode(String assetCode, Integer assetCategoryId) {
		return assetCategoryId == null
				? assetCategoryRepo.existsByAssetCode(assetCode)
				: assetCategoryRepo.existsByAssetCodeAndAssetCategoryIdNot(assetCode, assetCategoryId);
	}

	private boolean hasDuplicateName(String assetType, String assetName, Integer assetCategoryId) {
		return assetCategoryId == null
				? assetCategoryRepo.existsByAssetTypeAndAssetName(assetType, assetName)
				: assetCategoryRepo.existsByAssetTypeAndAssetNameAndAssetCategoryIdNot(assetType, assetName,
						assetCategoryId);
	}

	private String trimRequired(String value, String fieldName) {
		if (value == null || value.trim().isEmpty()) {
			throw new IllegalArgumentException(fieldName + " is required");
		}
		return value.trim();
	}
}
