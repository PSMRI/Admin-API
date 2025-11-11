package com.iemr.admin.service.bulkRegistration;

public interface  BulkRegistrationService {
    void registerBulkUser(String user,String authorization,String userName,Integer serviceProviderID,Integer serviceProviderMapID);
}
