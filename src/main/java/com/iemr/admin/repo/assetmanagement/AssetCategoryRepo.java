package com.iemr.admin.repo.assetmanagement;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.assetmanagement.AssetCategory;

@Repository
public interface AssetCategoryRepo extends CrudRepository<AssetCategory, Integer> {

	boolean existsByAssetCode(String assetCode);

	boolean existsByAssetCodeAndAssetCategoryIdNot(String assetCode, Integer assetCategoryId);

	boolean existsByAssetTypeAndAssetName(String assetType, String assetName);

	boolean existsByAssetTypeAndAssetNameAndAssetCategoryIdNot(String assetType, String assetName,
			Integer assetCategoryId);

	List<AssetCategory> findByDeletedFalseOrderByAssetTypeAscAssetNameAsc();

	List<AssetCategory> findByAssetTypeAndDeletedFalseOrderByAssetNameAsc(String assetType);
}
