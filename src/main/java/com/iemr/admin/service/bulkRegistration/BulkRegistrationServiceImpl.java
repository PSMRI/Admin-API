package com.iemr.admin.service.bulkRegistration;

import com.iemr.admin.data.bulkuser.Employee;
import com.iemr.admin.data.bulkuser.EmployeeList;
import com.iemr.admin.data.employeemaster.*;
import com.iemr.admin.data.locationmaster.M_District;
import com.iemr.admin.data.rolemaster.StateMasterForRole;
import com.iemr.admin.service.employeemaster.EmployeeMasterInter;
import com.iemr.admin.service.locationmaster.LocationMasterServiceInter;
import com.iemr.admin.service.rolemaster.Role_MasterInter;
import com.iemr.admin.utils.JwtUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.Date;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class BulkRegistrationServiceImpl implements BulkRegistrationService {
    private static final String PAN_REGEX = "^[A-Z]{8}[0-9]{3}$";
    private static final String EMAIL_REGEX = "^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,}$";
    public Integer totalEmployeeListSize = 0;
    private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    private EmployeeMasterInter employeeMasterInter;
    @Autowired
    private Role_MasterInter roleMasterInter;
    @Autowired
    private LocationMasterServiceInter locationMasterServiceInter;

    ByteArrayOutputStream out;
    @Autowired
    EmployeeXmlService employeeXmlService;
    public List<String> validationErrors = new ArrayList<>();

    public List<String> errorLogs = new ArrayList<>();
    public ArrayList<M_User1> m_bulkUser = new ArrayList<>();
    public ArrayList<M_UserDemographics> m_UserDemographics = new ArrayList<>();

    private List<M_District> m_districts;

    public List<String> getValidationError() {
        return validationErrors;

    }


    @Override
    public void registerBulkUser(String xml, String authorization) {
        try {
            EmployeeList employeeList = employeeXmlService.parseXml(xml);
            logger.info("employee_list" + employeeList.getEmployees().toString());
            totalEmployeeListSize = employeeList.getEmployees().size();
            for (int i = 0; i < employeeList.getEmployees().size(); i++) {
                saveUserUser(employeeList.getEmployees().get(i), i, authorization);

            }


        } catch (Exception e) {
            logger.error("Exception:" + e.getMessage());
        }


    }


    private void saveUserUser(Employee employee, Integer row, String authorization) throws Exception {
        logger.info("employee_list after for loop" + employee.toString());

        if (employee.getTitle() == null || employee.getTitle().isEmpty() || getTitleId(employee.getTitle()) == 0) {
            validationErrors.add("Title is missing or invalid.");
        }
        if (employee.getFirstName() == null || employee.getFirstName().isEmpty()) {
            validationErrors.add("First Name is missing.");
        }
        if (employee.getLastName() == null || employee.getLastName().isEmpty()) {
            validationErrors.add("Last Name is missing.");
        }
        if (employee.getEmail() == null || !employee.getEmail().matches(EMAIL_REGEX)) {
            validationErrors.add("Invalid Email format.");
        }
        if (!isValidPhoneNumber(String.valueOf(employee.getContactNo()))) {
            validationErrors.add("Contact Number must be exactly 10 digits.");
        }
        if (!isValidPhoneNumber(String.valueOf(employee.getEmergencyContactNo()))) {
            validationErrors.add("Emergency Contact Number must be exactly 10 digits.");
        }
        if (!String.valueOf(employee.getAge()).matches("\\d{1,2}")) {
            validationErrors.add("Age should be a 1 or 2-digit number.");
        }
        if (employee.getDob() == null || employee.getDob().toString().isEmpty()) {
            validationErrors.add("Date of Birth is invalid.");
        }
        if (employee.getDateOfJoining() == null || employee.getDateOfJoining().toString().isEmpty()) {
            validationErrors.add("Date of Joining is invalid.");
        }
        if (getStateId(employee.getState()) == 0) {
            validationErrors.add("State is invalid.");
        }
        if (getDistrictId(employee.getDistrict()) == 0) {
            validationErrors.add("District is invalid.");
        }

        if (getStateId(employee.getPermanentState()) == 0) {
            validationErrors.add("Permanent State is invalid.");
        }
        if (getDistrictId(employee.getPermanentDistrict()) == 0) {
            validationErrors.add("Permanent District is invalid.");
        }
        if (employee.getPincode() == null || employee.getPincode().isEmpty()) {
            validationErrors.add("Pincode is invalid.");

        }
        if (employee.getPermanentPincode() == null || employee.getPermanentPincode().isEmpty()) {
            validationErrors.add("Permanent Pincode is invalid.");

        }
        if (!isValidPAN(employee.getPan())) {
            validationErrors.add("PAN is invalid.");

        }

        if (!validationErrors.isEmpty()) {
            errorLogs.add("Row " + (row + 1) + ": " + String.join(", ", validationErrors));
        }


        try {

            M_User1 mUser = new M_User1();
            M_UserDemographics mUserDemographics = new M_UserDemographics();

            mUser.setTitleID(getTitleId(employee.getTitle()));
            mUser.setDesignationID(getDesignationId(employee.getDesignation()));
            mUser.setDesignationName("ASHA");
            mUser.setFirstName(employee.getFirstName());
            mUser.setLastName(employee.getLastName());
            mUser.setUserName(employee.getContactNo());
            mUser.setdOB(convertStringIntoDate(employee.getDob()));
            mUser.setEmployeeID(employee.getContactNo());
            mUser.setEmergencyContactNo(String.valueOf(employee.getEmergencyContactNo()));
            mUser.setContactNo(String.valueOf(employee.getContactNo()));
            mUser.setMiddleName(employee.getMiddleName());
            mUser.setAadhaarNo(String.valueOf(employee.getAadhaarNo()));
            mUser.setpAN(employee.getPan());
            mUser.setMaritalStatusID(1);
            mUser.setEmailID(employee.getEmail());
            mUser.setGenderID(Short.parseShort(String.valueOf(getGenderId(employee.getGender()))));
            mUser.setQualificationID(getQualificationId(employee.getQualification()));
            mUser.setdOJ(convertStringIntoDate(employee.getDateOfJoining()));
            mUser.setCreatedBy(jwtUtil.extractUsername(authorization));
            mUser.setModifiedBy(jwtUtil.extractUsername(authorization));
            mUser.setIsSupervisor(false);
            mUser.setServiceProviderID(15);
            mUser.setPassword(generateStrongPassword("Test@123"));
            M_User1 bulkUserID = employeeMasterInter.saveBulkUserEmployee(mUser);
            mUserDemographics.setUserID(bulkUserID.getUserID());
            mUserDemographics.setCountryID(91);
            mUserDemographics.setCommunityID(getCommunityId(employee.getCommunity()));
            mUserDemographics.setReligionID(getReligionStringId(employee.getReligion()));
            mUserDemographics.setFathersName(employee.getFatherName());
            mUserDemographics.setCreatedBy(jwtUtil.extractUsername(authorization));
            mUserDemographics.setAddressLine1(employee.getAddressLine1());
            mUserDemographics.setPermAddressLine1(employee.getPermanentAddressLine1());
            mUserDemographics.setPermStateID(getStateId(employee.getPermanentState()));
            mUserDemographics.setPermDistrictID(getDistrictId(employee.getPermanentDistrict()));
            mUserDemographics.setIsPermanent(false);
            mUserDemographics.setPermPinCode(Integer.valueOf(employee.getPermanentPincode()));
            mUserDemographics.setMothersName(employee.getMotherName());
            mUserDemographics.setAddressLine1(employee.getPermanentAddressLine1());
            mUserDemographics.setStateID(getStateId(employee.getState()));
            mUserDemographics.setIsPresent(false);
            mUserDemographics.setStateID(getStateId(employee.getState()));
            mUserDemographics.setDistrictID(getDistrictId(employee.getDistrict()));
            mUserDemographics.setPinCode(employee.getPincode().toString());
            employeeMasterInter.saveDemography(mUserDemographics);
            m_bulkUser.add(mUser);
            m_UserDemographics.add(mUserDemographics);

        } catch (Exception e) {
            errorLogs.add("Row : " + (row + 1) + e.getMessage());
        }


    }


    public int getGenderId(String genderString) {
        int genderId = employeeMasterInter.getAllGender()
                .stream()
                .filter(gender -> gender.getGenderName().equalsIgnoreCase(genderString))
                .map(M_Gender::getGenderID)
                .findFirst()
                .orElse(0);

        if (genderId == 0) {
            System.out.println("Gender name not found: " + genderString);
        } else {
            System.out.println("Gender ID: " + genderId);
        }

        return genderId;
    }


    public int getTitleId(String titleString) {
        int titleId = employeeMasterInter.getAllTitle()
                .stream()
                .filter(title -> title.getTitleName().equalsIgnoreCase(titleString))
                .map(M_Title::getTitleID)
                .findFirst()
                .orElse(0);


        return titleId;
    }


    public int getDesignationId(String designationString) {

        return 20;
    }


    public int getCommunityId(String communityString) {
        int communityId = employeeMasterInter.getAllCommunity()
                .stream()
                .filter(community -> community.getCommunityType().equalsIgnoreCase(communityString))
                .map(M_Community::getCommunityID)
                .findFirst()
                .orElse(0);


        return communityId;
    }

    public int getQualificationId(String qualificationString) {
        int qualificationId = employeeMasterInter.getQualification()
                .stream()
                .filter(q -> q.getUserQualificationDesc().equalsIgnoreCase(qualificationString))
                .map(M_Userqualification::getQualificationID)
                .findFirst()
                .orElse(0);


        return qualificationId;
    }

    //Religion
    public int getReligionStringId(String religionString) {
        return employeeMasterInter.getAllReligion()
                .stream()
                .filter(religion -> religion.getReligionType().equalsIgnoreCase(religionString))
                .map(M_Religion::getReligionID)
                .findFirst()
                .orElse(0);
    }


    public int getDistrictId(String districtName) {
        return m_districts.stream()
                .filter(m_district -> m_district.getDistrictName().equalsIgnoreCase(districtName))
                .map(M_District::getDistrictID)
                .findFirst()
                .orElse(0);

    }


    public ArrayList<StateMasterForRole> getAllState() {
        return roleMasterInter.getAllState();
    }


    public int getStateId(String stateName) {
        int stateId = roleMasterInter.getAllState()
                .stream()
                .filter(state -> state.getStateName().equalsIgnoreCase(stateName))
                .map(StateMasterForRole::getStateID)
                .findFirst()
                .orElse(0);

        if (stateId == 0) {
            logger.info("State name not found: " + stateName);
        } else {
            System.out.println("State ID: " + stateId);
            m_districts = locationMasterServiceInter.getAllDistrictByStateId(stateId);
        }

        return stateId;
    }

    private boolean isValidPAN(String pan) {
        // Check if the PAN matches the regex
        return Pattern.matches(PAN_REGEX, pan);
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        // Correct regex: Phone number must start with 6, 7, 8, or 9 and be exactly 10 digits
        System.out.println("Phone Number" + phoneNumber);
        String regex = "^[6789]\\d{9}$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phoneNumber);

        return matcher.matches();
    }

    private Map<String, Integer> getHeaderMap(Row headerRow) {
        Map<String, Integer> headerMap = new HashMap<>();
        for (Cell cell : headerRow) {
            headerMap.put(cell.getStringCellValue().trim(), cell.getColumnIndex());
        }
        return headerMap;
    }


    public Date convertStringIntoDate(String dateString) throws ParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(dateString + " " + "00:00:00", formatter);


        // Convert String to Date
        return Date.valueOf(String.valueOf(dateTime));

    }


    public String generateStrongPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        int iterations = 1001;
        char[] chars = password.toCharArray();
        byte[] salt = getSalt();

        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 512);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return iterations + ":" + toHex(salt) + ":" + toHex(hash);
    }

    private byte[] getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

    private String toHex(byte[] array) throws NoSuchAlgorithmException {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = array.length * 2 - hex.length();
        if (paddingLength > 0) {
            return String.format(new StringBuilder().append("%0").append(paddingLength).append("d").toString(),
                    new Object[]{Integer.valueOf(0)}) + hex;
        }
        return hex;
    }


}