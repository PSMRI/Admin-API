package com.iemr.admin.service.bulkRegistration;

import com.iemr.admin.data.bulkuser.BulkRegistrationError;
import com.iemr.admin.data.bulkuser.Employee;
import com.iemr.admin.data.bulkuser.EmployeeList;
import com.iemr.admin.data.employeemaster.*;
import com.iemr.admin.data.locationmaster.M_District;
import com.iemr.admin.data.rolemaster.StateMasterForRole;
import com.iemr.admin.data.user.M_UserServiceRoleMapping;
import com.iemr.admin.repo.employeemaster.V_ShowuserRepo;
import com.iemr.admin.service.employeemaster.EmployeeMasterInter;
import com.iemr.admin.service.locationmaster.LocationMasterServiceInter;
import com.iemr.admin.service.rolemaster.Role_MasterInter;
import com.iemr.admin.utils.JwtUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.*;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.Date;
import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
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
    public static final String FILE_PATH = "error_log.xlsx"; // Excel file path
    public List<BulkRegistrationError> bulkRegistrationErrors = new ArrayList<>();


    @Autowired
    private EmployeeMasterInter employeeMasterInter;
    @Autowired
    private Role_MasterInter roleMasterInter;

    @Autowired
    private V_ShowuserRepo showuserRepo;
    @Autowired
    private LocationMasterServiceInter locationMasterServiceInter;

    @Autowired
    EmployeeXmlService employeeXmlService;

    public ArrayList<String> errorLogs = new ArrayList<>();
    public ArrayList<M_User1> m_bulkUser = new ArrayList<>();
    public ArrayList<M_UserDemographics> m_UserDemographics = new ArrayList<>();

    private List<M_District> m_districts;

    @Override
    public void registerBulkUser(String xml, String authorization,String userName) {
        try {
            xml = escapeXmlSpecialChars(xml);

            EmployeeList employeeList = employeeXmlService.parseXml(xml);
            if (!employeeList.getEmployees().isEmpty()) {
                logger.info("employee_list" + employeeList.getEmployees().toString());
                totalEmployeeListSize = employeeList.getEmployees().size();
                for (int i = 0; i < employeeList.getEmployees().size(); i++) {
                    saveUserUser(employeeList.getEmployees().get(i), i, authorization,userName);


                }
            } else {
                errorLogs.add("Data is invalid or empty");

            }


        } catch (Exception e) {
            logger.error("Exception:" + e.getMessage());
            errorLogs.add("Data is invalid or empty");

        }


    }
    public static String escapeXmlSpecialChars(String xml) {
        // Only escape & that are not already part of valid XML entities
        return xml.replaceAll("&(?!amp;|lt;|gt;|apos;|quot;|#\\d+;)", "&amp;");
    }


    private void saveUserUser(Employee employee, Integer row, String authorization,String createdBy) throws Exception {
        List<String> validationErrors = new ArrayList<>();
        BulkRegistrationError bulkRegistrationErrors_ = new BulkRegistrationError();
        M_User1 mUser = new M_User1();
        M_UserDemographics mUserDemographics = new M_UserDemographics();
        M_UserServiceRoleMapping2 m_userServiceRoleMapping = new M_UserServiceRoleMapping2();

        logger.info("employee_list after for loop" + employee.toString());
        if (!employee.getUserName().isEmpty()) {
            String checkUserIsExist = employeeMasterInter.FindEmployeeName(employee.getUserName());
            String checkContactIsExist = employeeMasterInter.FindEmployeeContact(employee.getContactNo());
            logger.info("checkAAdharIsExist" + checkUserIsExist);
            if (checkUserIsExist.equalsIgnoreCase("usernotexist")) {
                if (checkContactIsExist.equalsIgnoreCase("contactnotexist")) {
                    if (employee.getTitle() == null || employee.getTitle().isEmpty()) {
                        validationErrors.add("Title is missing.");
                    }
                    if (!employee.getTitle().isEmpty()) {
                        if (getTitleId(employee.getTitle()) == 0) {
                            validationErrors.add("Title is invalid.");

                        }

                    }

                    if (employee.getFirstName() == null || employee.getFirstName().isEmpty()) {
                        validationErrors.add("First Name is missing.");
                    }
                    if(!employee.getFirstName().isEmpty()){
                        if(employee.getFirstName().length()>50){
                            validationErrors.add("First name is invalid.");

                        }
                        if(isNumeric(employee.getFirstName())){
                            validationErrors.add("First name is invalid.");

                        }
                    }

                    if(!employee.getMiddleName().isEmpty()){
                        if(employee.getMiddleName().length()>50){
                            validationErrors.add("Middle name is invalid.");

                        }
                        if(isNumeric(employee.getMiddleName())){
                            validationErrors.add("Middle name is invalid.");

                        }
                    }
                    if (employee.getLastName() == null || employee.getLastName().isEmpty()) {
                        validationErrors.add("Last Name is missing.");
                    }
                    if(!employee.getLastName().isEmpty()){
                        if(employee.getLastName().length()>50){
                            validationErrors.add("Last name is invalid.");

                        }
                        if(isNumeric(employee.getLastName())){
                            validationErrors.add("Last name is invalid.");

                        }
                    }
                    if (employee.getGender().isEmpty()) {
                        validationErrors.add("Gender is missing");

                    }
                    if (employee.getContactNo().isEmpty()) {
                        validationErrors.add("Contact number missing");

                    }
                    if (!employee.getContactNo().isEmpty()) {
                        if (!isValidPhoneNumber(String.valueOf(employee.getContactNo()))) {
                            validationErrors.add("Contact Number is invalid");
                        }
                    }

                    if (employee.getDesignation().isEmpty()) {
                        validationErrors.add("Designation is missing");

                    }
                    if (employee.getEmergencyContactNo().isEmpty()) {
                        validationErrors.add("Emergency contact number is missing");

                    }
                    if (!employee.getEmergencyContactNo().isEmpty()) {
                        if (!isValidPhoneNumber(String.valueOf(employee.getEmergencyContactNo()))) {
                            validationErrors.add("Emergency Contact Number is invalid.");
                        }
                    }

                    if (employee.getDob().isEmpty()) {
                        validationErrors.add("Date of Birth is missing.");

                    }
                    if(!employee.getDob().isEmpty()){
                        if(!isValidDate(convertStringIntoDate(employee.getDob()).toString())){
                            validationErrors.add("Date of Birth is invalid.");

                        }
                    }

                    if(employee.getEmail().isEmpty()){
                        validationErrors.add("Email is missing.");

                    }
                    if(!employee.getEmail().isEmpty()){
                        if (!employee.getEmail().matches(EMAIL_REGEX)) {
                            validationErrors.add("Invalid Email format.");
                        }
                    }

                    if (employee.getPassword().isEmpty()) {
                        validationErrors.add("Please  Enter valid password.");

                    }

                    if (!employee.getAadhaarNo().isEmpty()) {
                        if (!employeeMasterInter.FindEmployeeAadhaar(employee.getAadhaarNo()).equalsIgnoreCase("aadhaarnotexist")) {
                            validationErrors.add("Duplicate  aadhaar number found");

                        }
                        if(isValidAadhar(employee.getAadhaarNo())){
                            validationErrors.add("Aadhaar number is invalid");

                        }
                    }


                    if (employee.getQualification().isEmpty()) {
                        validationErrors.add("Qualification is missing");

                    }

                    if (employee.getState().isEmpty()) {
                        validationErrors.add("Current State is missing.");
                    }
                    if (!employee.getState().isEmpty()) {
                        if (getStateId(employee.getState()) == 0) {
                            validationErrors.add("Current State is invalid.");

                        }
                    }
                    if (employee.getDistrict().isEmpty()) {
                        validationErrors.add("Current District is missing.");
                    }
                    if (!employee.getDistrict().isEmpty()) {
                        if (getDistrictId(employee.getDistrict()) == 0) {
                            validationErrors.add("Current District is invalid.");

                        }
                    }

                    if (employee.getPermanentState().isEmpty()) {
                        validationErrors.add("Permanent State is missing.");
                    }
                    if (!employee.getPermanentState().isEmpty()) {
                        if (getStateId(employee.getPermanentState()) == 0) {
                            validationErrors.add("Permanent State is invalid.");

                        }
                    }
                    if (employee.getPermanentDistrict().isEmpty()) {
                        validationErrors.add("Permanent District is missing.");
                    }

                    if (!employee.getPermanentDistrict().isEmpty()) {
                        if (getDistrictId(employee.getPermanentDistrict()) == 0) {
                            validationErrors.add("Permanent District is invalid.");

                        }
                    }

                    if(employee.getDateOfJoining().isEmpty()){
                        validationErrors.add("Date of Joining is missing.");

                    }
                    if(!employee.getDateOfJoining().isEmpty()){
                        if(!isValidDate(convertStringIntoDate(employee.getDateOfJoining()).toString())){
                            validationErrors.add("Date of Joining is invalid.");

                        }
                    }



                    if (!validationErrors.isEmpty()) {
                        errorLogs.add("Row " + (row + 1) + ": " + String.join(", ", validationErrors));
                        bulkRegistrationErrors_.setRowNumber((row + 1));
                        bulkRegistrationErrors_.setUserName(employee.getUserName());
                        bulkRegistrationErrors_.setError(validationErrors);
                        bulkRegistrationErrors.add(bulkRegistrationErrors_);



                    }


                    //  showLogger(employee);

                    if (!employee.getTitle().isEmpty() && !employee.getFirstName().isEmpty() && !employee.getLastName().isEmpty() && !employee.getContactNo().isEmpty() && !employee.getEmergencyContactNo().isEmpty() && !employee.getDob().isEmpty() && !employee.getUserName().isEmpty() && !employee.getPassword().isEmpty() && !employee.getState().isEmpty() && !employee.getDistrict().isEmpty() && !employee.getPermanentState().isEmpty() && !employee.getPermanentDistrict().isEmpty() && !employee.getGender().isEmpty() && !employee.getQualification().isEmpty() && isValidDate(convertStringIntoDate(employee.getDob()).toString()) && isValidDate(convertStringIntoDate(employee.getDateOfJoining()).toString())) {
                        try {

                            mUser.setTitleID(getTitleId(employee.getTitle()));
                            mUser.setFirstName(employee.getFirstName());
                            mUser.setLastName(employee.getLastName());
                            mUser.setUserName(employee.getContactNo());
                            mUser.setdOB(convertStringIntoDate(employee.getDob()));
                            mUser.setEmployeeID(employee.getUserName());
                            mUser.setEmergencyContactNo(String.valueOf(employee.getEmergencyContactNo()));
                            mUser.setContactNo(String.valueOf(employee.getContactNo()));
                            if (!employee.getMiddleName().isEmpty()) {
                                mUser.setMiddleName(employee.getMiddleName());

                            }
                            if (!employee.getDesignation().isEmpty()) {
                                mUser.setDesignationID(getDesignationId(employee.getDesignation()));

                            }
                            if (!employee.getDesignation().isEmpty()) {
                                mUser.setDesignationName(employee.getDesignation());

                            }
                            if (!isValidAadhar(employee.getAadhaarNo()) && employeeMasterInter.FindEmployeeAadhaar(employee.getAadhaarNo()).equalsIgnoreCase("aadhaarnotexist")) {
                                mUser.setAadhaarNo(String.valueOf(employee.getAadhaarNo()));

                            }

                            if (!employee.getPan().isEmpty()) {
                                mUser.setpAN(employee.getPan());

                            }
                            mUser.setMaritalStatusID(1);
                            mUser.setEmailID(employee.getEmail());
                            mUser.setGenderID(Short.parseShort(String.valueOf(getGenderId(employee.getGender()))));
                            if (!employee.getQualification().isEmpty()) {
                                mUser.setQualificationID(getQualificationId(employee.getQualification()));

                            }
                            mUser.setdOJ(convertStringIntoDate(employee.getDateOfJoining()));
                            mUser.setCreatedBy(createdBy);
                            mUser.setModifiedBy(createdBy);
                            mUser.setStatusID(2);
                            mUser.setDeleted(false);
                            mUser.setEmployeeID(employee.getUserName());
                            mUser.setServiceProviderID(showuserRepo.findByUserName(createdBy).getServiceProviderID());
                            mUser.setPassword(generateStrongPassword(employee.getPassword()));
                            logger.info("Register_user:" + mUser);
                            M_User1 bulkUserID = employeeMasterInter.saveBulkUserEmployee(mUser);
                            logger.info("BulkUser:" + bulkUserID);
                            m_userServiceRoleMapping.setUserID(bulkUserID.getUserID());
                            m_userServiceRoleMapping.setServiceProviderID(bulkUserID.getServiceProviderID());
                            m_userServiceRoleMapping.setCreatedBy(createdBy);
                            m_userServiceRoleMapping.setRoleID(122);
                            mUserDemographics.setUserID(bulkUserID.getUserID());
                            mUserDemographics.setCountryID(91);
                            if (!employee.getCommunity().isEmpty()) {
                                mUserDemographics.setCommunityID(getCommunityId(employee.getCommunity()));

                            }
                            if (!employee.getReligion().isEmpty()) {
                                mUserDemographics.setReligionID(getReligionStringId(employee.getReligion()));

                            }
                        mUserDemographics.setCreatedBy(createdBy);
                            // Permanent Address
                            if (!employee.getPermanentAddressLine1().isEmpty()) {
                                mUserDemographics.setPermAddressLine1(employee.getPermanentAddressLine1());

                            }
                            if (!employee.getPermanentState().isEmpty()) {
                                mUserDemographics.setPermStateID(getStateId(employee.getPermanentState()));

                            }
                            if (!employee.getPermanentDistrict().isEmpty()) {
                                mUserDemographics.setPermDistrictID(getDistrictId(employee.getPermanentDistrict()));

                            }
                            mUserDemographics.setIsPermanent(false);
                            if (!employee.getPermanentPincode().isEmpty()) {
                                mUserDemographics.setPermPinCode(Integer.valueOf(employee.getPermanentPincode()));

                            }
                            if (!employee.getMotherName().isEmpty()) {
                                mUserDemographics.setMothersName(employee.getMotherName());

                            }
                            if (!employee.getFatherName().isEmpty()) {
                                mUserDemographics.setFathersName(employee.getFatherName());

                            }
                            // correspondence address
                            if (!employee.getAddressLine1().isEmpty()) {
                                mUserDemographics.setAddressLine1(employee.getAddressLine1());

                            }
                            if (!employee.getState().isEmpty()) {
                                mUserDemographics.setStateID(getStateId(employee.getState()));

                            }
                            mUserDemographics.setIsPresent(false);
                            if (!employee.getDistrict().isEmpty()) {
                                mUserDemographics.setDistrictID(getDistrictId(employee.getDistrict()));

                            }
                            if (!employee.getPincode().isEmpty()) {
                                mUserDemographics.setPinCode(employee.getPincode().toString());

                            }
                            employeeMasterInter.saveDemography(mUserDemographics);
                            m_bulkUser.add(mUser);
                            m_UserDemographics.add(mUserDemographics);
//                            employeeMasterInter.saveRoleMappingeditedData(m_userServiceRoleMapping, authorization);

                        } catch (Exception e) {
                            errorLogs.add("Row :" + (row + 1) + e.getMessage());
                            bulkRegistrationErrors_.setRowNumber((row + 1));
                            bulkRegistrationErrors_.setUserName(employee.getUserName());
                            bulkRegistrationErrors_.setError(validationErrors);
                            bulkRegistrationErrors.add(bulkRegistrationErrors_);


                        }
                    }

                } else {
                    validationErrors.add("Contact No Already exist");
                    if (!validationErrors.isEmpty()) {

                        errorLogs.add("Row " + (row + 1) + ": " + String.join(", ", validationErrors));
                        bulkRegistrationErrors_.setRowNumber((row + 1));
                        bulkRegistrationErrors_.setUserName(employee.getUserName());
                        bulkRegistrationErrors_.setError(validationErrors);
                        bulkRegistrationErrors.add(bulkRegistrationErrors_);


                    }
                }

            } else {
                validationErrors.add("User Already exist");
                if (!validationErrors.isEmpty()) {

                    errorLogs.add("Row " + (row + 1) + ": " + String.join(", ", validationErrors));
                    bulkRegistrationErrors_.setRowNumber((row + 1));
                    bulkRegistrationErrors_.setUserName(employee.getUserName());
                    bulkRegistrationErrors_.setError(validationErrors);
                    bulkRegistrationErrors.add(bulkRegistrationErrors_);


                }

            }
        } else {
            validationErrors.add("Please Enter UserName");
            if (!validationErrors.isEmpty()) {
                errorLogs.add("Row " + (row + 1) + ": " + String.join(", ", validationErrors));
                bulkRegistrationErrors_.setRowNumber((row + 1));
                bulkRegistrationErrors_.setUserName(employee.getUserName());
                bulkRegistrationErrors_.setError(validationErrors);
                bulkRegistrationErrors.add(bulkRegistrationErrors_);


            }
        }


    }
    /**
     * Validate employee details.
     */


    private boolean isValidDate(String dateStr) {
        try {
            String[] parts = dateStr.split("-");
            int year = Integer.parseInt(parts[0]);

            if (year > 2025) {
                return false; // Year should not be greater than 2025
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate.parse(dateStr, formatter); // Validates if the full date is correct

            return true; // Valid date within range
        } catch (Exception e) {
            return false; // Invalid date format or parsing error
        }
    }

    private int calculateAge(String dob) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate birthDate = LocalDate.parse(dob, formatter);

        // Get current date
        LocalDate currentDate = LocalDate.now();

        // Calculate age
        return Period.between(birthDate, currentDate).getYears();
    }

    private void showLogger(Employee employee) {
        logger.info("Title: " + employee.getTitle());
        logger.info("Title Id: " + getTitleId(employee.getTitle()));
        logger.info("First Name: " + employee.getFirstName());
        logger.info("Last Name: " + employee.getLastName());
        logger.info("Email: " + employee.getEmail());
        logger.info("Contact No: " + employee.getContactNo());
        logger.info("Emergency Contact No: " + employee.getEmergencyContactNo());
        logger.info("Age: " + employee.getAge());
        logger.info("DOB: " + employee.getDob());
        logger.info("State: " + employee.getState());
        logger.info("State ID: " + getStateId(employee.getState()));
        logger.info("District: " + employee.getDistrict());
        logger.info("District ID: " + getDistrictId(employee.getDistrict()));
        logger.info("Designation: " + employee.getDesignation());
        logger.info("Designation Id: " + getDesignationId(employee.getDesignation()));
        logger.info("Qualification: " + employee.getQualification());
        logger.info("Father Name: " + employee.getFatherName());
        logger.info("Mother Name: " + employee.getMotherName());
        logger.info("Address Line 1: " + employee.getAddressLine1());
        logger.info("Permanent Address: " + employee.getPermanentAddressLine1());
        logger.info("Aadhaar No: " + employee.getAadhaarNo());
        logger.info("PAN: " + employee.getPan());
        logger.info("Gender: " + employee.getGender());
        logger.info("Date of Joining: " + employee.getDateOfJoining());
        logger.info("Religion: " + employee.getReligion());
        logger.info("Community: " + employee.getCommunity());
        logger.info("Pincode: " + employee.getPincode());
        logger.info("Dob_formated: " + convertStringIntoDate(employee.getDob()));
        logger.info("doj_formated: " + convertStringIntoDate(employee.getDateOfJoining()));

    }


    private int getGenderId(String genderString) {
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

    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    private int getTitleId(String titleString) {
        int titleId = 0;

        if (!titleString.isEmpty()) {
            if (titleString.equalsIgnoreCase("Major") || titleString.equalsIgnoreCase("Madame")) {
                titleId = employeeMasterInter.getAllTitle()
                        .stream()
                        .filter(title -> title.getTitleName().equalsIgnoreCase(titleString))
                        .map(M_Title::getTitleID)
                        .findFirst()
                        .orElse(0);
            } else {
                titleId = employeeMasterInter.getAllTitle()
                        .stream()
                        .filter(title -> title.getTitleName().equalsIgnoreCase(titleString + "."))
                        .map(M_Title::getTitleID)
                        .findFirst()
                        .orElse(0);
            }
        }


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
                .filter(q -> q.getName().equalsIgnoreCase(qualificationString))
                .map(M_Userqualification::getQualificationID)
                .findFirst()
                .orElse(0);


        return qualificationId;
    }

    //Religion
    public int getReligionStringId(String religionString) {
        if (religionString.equalsIgnoreCase("Not given")) {
            return 0;
        } else {
            return employeeMasterInter.getAllReligion()
                    .stream()
                    .filter(religion -> religion.getReligionType().equalsIgnoreCase(religionString))
                    .map(M_Religion::getReligionID)
                    .findFirst()
                    .orElse(0);
        }
    }


    public int getDistrictId(String districtName) {
        if (!districtName.isEmpty()) {
            return m_districts.stream()
                    .filter(m_district -> m_district.getDistrictName().equalsIgnoreCase(districtName))
                    .map(M_District::getDistrictID)
                    .findFirst()
                    .orElse(0);
        } else {
            return 0;
        }


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


    public static Date convertStringIntoDate(String date) {

        final long MILLISECONDS_PER_DAY = 86400000L;
        final long EPOCH_OFFSET = 2209161600000L;

        // Calculate milliseconds since epoch
        long javaMillis = (long) (Double.parseDouble(date) * MILLISECONDS_PER_DAY - EPOCH_OFFSET);

        return new Date(javaMillis);


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

    public static boolean isValidAadhar(String aadharNumber) {
        if (!Pattern.matches("\\d{12}", aadharNumber)) {
            return true; // Must be a 12-digit number
        }
        return false;

    }


    public byte[] insertErrorLog() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Error Log");

        try {
            // **Create Header Row (Only Once)**
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("UserName");
            headerRow.createCell(1).setCellValue("Remark");

            // **Start inserting data from row 1**
            int rowIndex = 1; // Start from row 1 (row 0 is header)
            int rowIndex2 = 1; // Start from row 1 (row 0 is header)
            logger.info("Error Size" + bulkRegistrationErrors.size());
            for (BulkRegistrationError bulkRegistrationError : bulkRegistrationErrors) {
                Row row = sheet.createRow(rowIndex++);

                row.createCell(0).setCellValue(bulkRegistrationError.getUserName());
                row.createCell(1).setCellValue(bulkRegistrationError.getError().toString());


            }


            // **Auto-Size Columns for Better Readability**
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);

            // **Write to ByteArrayOutputStream**
            workbook.write(baos);
            workbook.close();

            System.out.println("Error log generated successfully!");

        } catch (IOException e) {
            logger.error("IOException" + e.getMessage());
            e.printStackTrace();
        }

        return baos.toByteArray();
    }


}