package com.iemr.admin.data.bulkuser;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.util.List;

@Data
@JacksonXmlRootElement(localName = "Employees")
public class EmployeeList {

    @JsonProperty("Employee")
    @JacksonXmlElementWrapper(useWrapping = false) // To avoid extra nested array in XML
    private List<Employee> employees;
}
