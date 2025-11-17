package com.iemr.admin.data.bulkuser;

import lombok.Data;

import java.util.List;

@Data
public class BulkRegistrationError {
    String userName;
    Integer rowNumber;
    List<String> error;
}
