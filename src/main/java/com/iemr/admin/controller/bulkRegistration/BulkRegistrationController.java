package com.iemr.admin.controller.bulkRegistration;

import com.iemr.admin.repo.employeemaster.EmployeeMasterRepoo;
import com.iemr.admin.service.bulkRegistration.BulkRegistrationService;
import com.iemr.admin.service.bulkRegistration.BulkRegistrationServiceImpl;
import com.iemr.admin.service.bulkRegistration.EmployeeXmlService;
import com.iemr.admin.service.locationmaster.LocationMasterServiceInter;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
   private Map<String, Object> response  = new HashMap<>();


    @CrossOrigin()
    @RequestMapping(value = "/bulkRegistration",method = RequestMethod.POST,headers = "Authorization")
    public ResponseEntity<Map<String, Object>> registerBulkUser(@RequestBody String m_user) throws Exception {
        logger.info("M_user Request"+m_user.toString());
        try {
            bulkRegistrationService.registerBulkUser(m_user);
            response.put("status","Success");
            response.put("statusCode",200);
            response.put("totalUser",bulkRegistrationServiceimpl.totalEmployeeListSize);
            response.put("registeredUser",bulkRegistrationServiceimpl.m_bulkUser.size());
            response.put("error",bulkRegistrationServiceimpl.errorLogs.toString());

            bulkRegistrationServiceimpl.m_bulkUser.clear();
            bulkRegistrationServiceimpl.m_UserDemographics.clear();
            bulkRegistrationServiceimpl.errorLogs.clear();
            bulkRegistrationServiceimpl.validationErrors.clear();

        } catch (Exception e) {
            response.put("status","Fail");
            response.put("statusCode",500);
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(response);

    }
     @CrossOrigin()
     @Operation(description = "Download formatted Excel sheet")
    @RequestMapping(value = {"/downloadExcelSheet"},method = {RequestMethod.GET},consumes = {"application/octet-stream"})
    public  ResponseEntity<?> exportIntoExcelFile(){

         // Load the Excel file from resources
         ClassPathResource excelFile = new ClassPathResource("xlsxfile/bulkuser_excel_sheet.xlsx");

         // Return the Excel file as a response with proper headers
         return ResponseEntity.ok()
                 .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                 .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + excelFile.getFilename() + "\"")
                 .body(excelFile);
     }



}
