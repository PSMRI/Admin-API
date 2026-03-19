package com.iemr.admin.service.bulkRegistration;


import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.iemr.admin.data.bulkuser.EmployeeList;
import org.springframework.stereotype.Service;

@Service
public class EmployeeXmlService {

    private final XmlMapper xmlMapper = new XmlMapper();

    // Convert XML to EmployeeList (Java Object)
    public EmployeeList parseXml(String xmlData) throws Exception {
        System.out.println("user_xml_date"+xmlData.toString());

        return xmlMapper.readValue(xmlData, EmployeeList.class);
    }


}
