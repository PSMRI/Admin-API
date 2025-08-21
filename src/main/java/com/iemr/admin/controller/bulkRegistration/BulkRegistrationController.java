package com.iemr.admin.controller.bulkRegistration;

import com.iemr.admin.repo.employeemaster.EmployeeMasterRepoo;
import com.iemr.admin.service.bulkRegistration.BulkRegistrationService;
import com.iemr.admin.service.bulkRegistration.BulkRegistrationServiceImpl;
import com.iemr.admin.service.bulkRegistration.EmployeeXmlService;
import com.iemr.admin.service.locationmaster.LocationMasterServiceInter;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@RestController
public class BulkRegistrationController {
    @Autowired
    private EmployeeXmlService employeeXmlService;
    @Autowired
    BulkRegistrationServiceImpl bulkRegistrationServiceimpl;
    private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());


    @Autowired
    BulkRegistrationService bulkRegistrationService;

    @Autowired
    private EmployeeMasterRepoo employeeMasterRepoo;
    private Map<String, Object> errorResponse = new HashMap<>();
    @Autowired
    private LocationMasterServiceInter locationMasterServiceInter;
    private Map<String, Object> response = new HashMap<>();

    @PostMapping(value = "/bulkRegistration", headers = "Authorization")
    public ResponseEntity<Map<String, Object>> registerBulkUser(@RequestBody String m_user, @RequestHeader String authorization) {
        bulkRegistrationServiceimpl.bulkRegistrationErrors.clear();
        logger.info("Bulk registration request received. Request payload is omitted from logs.");
        try {
            bulkRegistrationService.registerBulkUser(m_user, authorization);
            response.put("status", "Success");
            response.put("statusCode", 200);
            response.put("totalUser", bulkRegistrationServiceimpl.totalEmployeeListSize);
            response.put("registeredUser", bulkRegistrationServiceimpl.m_bulkUser.size());
            response.put("error", bulkRegistrationServiceimpl.errorLogs.toString());

            bulkRegistrationServiceimpl.m_bulkUser.clear();
            bulkRegistrationServiceimpl.m_UserDemographics.clear();
            bulkRegistrationServiceimpl.errorLogs.clear();
            bulkRegistrationServiceimpl.totalEmployeeListSize=0;

        } catch (Exception e) {
            response.put("message", e.getMessage());
            response.put("statusCode", 500);

        }
        return ResponseEntity.ok(response);

    }

    @GetMapping(value = "/download-error-sheet", headers = "Authorization")
    public ResponseEntity<byte[]> downloadErrorSheet() {
        try {
            byte[] fileContent = bulkRegistrationServiceimpl.insertErrorLog();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=error_log.xlsx");

            if(!bulkRegistrationServiceimpl.bulkRegistrationErrors.isEmpty()){
                bulkRegistrationServiceimpl.bulkRegistrationErrors.clear();
            }
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(fileContent);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }


}
