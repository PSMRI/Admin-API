package com.iemr.admin.controller.assetManagment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.iemr.admin.data.assetmanagement.AssetCategory;
import com.iemr.admin.service.assetmanagement.AssetManagementService;
import com.iemr.admin.utils.response.OutputResponse;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/assetManagement")
public class AssetManagement {
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @Autowired
    private AssetManagementService assetManagementService;

    @Operation(summary = "Save an asset category")
    @PostMapping(value = "/save", headers = "Authorization", produces = "application/json")
    public String save(@RequestBody AssetCategory assetCategory) {
        OutputResponse response = new OutputResponse();
        try {
            AssetCategory savedAssetCategory = assetManagementService.save(assetCategory);
            response.setResponse(savedAssetCategory.toString());
        } catch (IllegalArgumentException e) {
            response.setError(400, e.getMessage());
        } catch (DataIntegrityViolationException e) {
            logger.error("Asset category save failed because of a database constraint", e);
            response.setError(400, "Asset code or asset name already exists");
        } catch (Exception e) {
            logger.error("Unable to save asset category", e);
            response.setError(e);
        }
        return response.toString();
    }

    @Operation(summary = "Get all asset categories")
    @GetMapping(value = "/getAll", headers = "Authorization", produces = "application/json")
    public String getAll(@RequestParam(required = false) String assetType) {
        OutputResponse response = new OutputResponse();
        try {
            response.setResponse(assetManagementService.getAll(assetType).toString());
        } catch (Exception e) {
            logger.error("Unable to get asset categories", e);
            response.setError(e);
        }
        return response.toString();
    }
}
