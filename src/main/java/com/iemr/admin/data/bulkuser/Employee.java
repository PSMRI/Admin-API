package com.iemr.admin.data.bulkuser;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "Employee")
public class Employee {
    
    @JacksonXmlProperty(localName = "Title")
    private String title;

    @JacksonXmlProperty(localName = "FirstName")
    private String firstName;

    @JacksonXmlProperty(localName = "MiddleName")
    private String middleName;

    @JacksonXmlProperty(localName = "LastName")
    private String lastName;

    @JacksonXmlProperty(localName = "Gender")
    private String gender;

    @JacksonXmlProperty(localName = "ContactNo")
    private String contactNo;

    @JacksonXmlProperty(localName = "Designation")
    private String designation;

    @JacksonXmlProperty(localName = "EmergencyContactNo")
    private String emergencyContactNo;

    @JacksonXmlProperty(localName = "DOB")
    private String dob;

    @JacksonXmlProperty(localName = "Age")
    private int age;

    @JacksonXmlProperty(localName = "Email")
    private String email;

    @JacksonXmlProperty(localName = "MaritalStatus")
    private String maritalStatus;

    @JacksonXmlProperty(localName = "AadhaarNo")
    private String aadhaarNo;

    @JacksonXmlProperty(localName = "PAN")
    private String pan;

    @JacksonXmlProperty(localName = "Qualification")
    private String qualification;

    @JacksonXmlProperty(localName = "FatherName")
    private String fatherName;

    @JacksonXmlProperty(localName = "MotherName")
    private String motherName;

    @JacksonXmlProperty(localName = "Community")
    private String community;

    @JacksonXmlProperty(localName = "Religion")
    private String religion;

    @JacksonXmlProperty(localName = "AddressLine1")
    private String addressLine1;

    @JacksonXmlProperty(localName = "State")
    private String state;

    @JacksonXmlProperty(localName = "District")
    private String district;

    @JacksonXmlProperty(localName = "Pincode")
    private String pincode;

    @JacksonXmlProperty(localName = "PermanentAddressLine1")
    private String permanentAddressLine1;

    @JacksonXmlProperty(localName = "PermanentState")
    private String permanentState;

    @JacksonXmlProperty(localName = "PermanentDistrict")
    private String permanentDistrict;

    @JacksonXmlProperty(localName = "PermanentPincode")
    private String permanentPincode;

    @JacksonXmlProperty(localName = "DateOfJoining")
    private String dateOfJoining;
}
