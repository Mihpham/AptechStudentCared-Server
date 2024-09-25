package com.example.aptechstudentcaredserver.util;

import com.example.aptechstudentcaredserver.bean.request.StudentExamScoreRequest;
import com.example.aptechstudentcaredserver.bean.request.StudentRequest;
import com.example.aptechstudentcaredserver.bean.request.SubjectRequest;
import com.example.aptechstudentcaredserver.bean.response.ImportResponse;
import com.example.aptechstudentcaredserver.service.ExamDetailService;
import com.example.aptechstudentcaredserver.service.StudentService;
import com.example.aptechstudentcaredserver.service.SubjectService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExcelUtils {

    public static List<ImportResponse> parseStudentExcelFile(MultipartFile file, StudentService studentService) throws IOException {
        List<ImportResponse> importResults = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                throw new IllegalArgumentException("No sheets found in the file");
            }

            // Assume the first row is always present and serves as header if the file has one
            Row firstRow = sheet.getRow(0);
            if (firstRow == null) {
                importResults.add(new ImportResponse("File format is incorrect.", -1));
                return importResults;
            }
            int requiredColumns = 13; // Adjust this based on the number of columns you expect

            // Check if the first row contains the required number of columns
            if (firstRow.getPhysicalNumberOfCells() < requiredColumns) {
                importResults.add(new ImportResponse("File format is incorrect. The file does not contain enough columns. Expected at least " + requiredColumns +"\"STT\"," +
                        "\"Roll Number\"," +
                        "\"Full Name\"," +
                        "\"Gender\"," +
                        "\"Class Name\"," +
                        "\"Date of Birth\"," +
                        "\"Phone Number\"," +
                        "\"Address\"," +
                        "\"Courses\"," +
                        "\"Parent Full Name\"," +
                        "\"Student Relation\"," +
                        "\"Parent Phone\"," +
                        "\"Parent Gender\".", -1));
                return importResults;
            }

            int rollNumberIndex = 1; // Bỏ qua cột đầu tiên (index 0)
            int fullNameIndex = 2;   // Điều chỉnh các chỉ số cột theo sau
            int genderIndex = 3;
            int classNameIndex = 4;
            int dobIndex = 5;
            int phoneNumberIndex = 6;
            int addressIndex = 7;
            int coursesIndex = 8;
            int parentFullNameIndex = 9;
            int studentRelationIndex = 10;
            int parentPhoneIndex = 11;
            int parentGenderIndex = 12;

            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) { // Bỏ qua dòng tiêu đề
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                ImportResponse importResponse = new ImportResponse("Success", i); // Row number (1-based)
                StringBuilder errorBuilder = new StringBuilder();

                try {
                    StudentRequest student = new StudentRequest();
                    student.setRollNumber(getCellValue(row.getCell(rollNumberIndex)));
                    student.setFullName(getCellValue(row.getCell(fullNameIndex)));
                    student.setGender(getCellValue(row.getCell(genderIndex)));
                    student.setClassName(getCellValue(row.getCell(classNameIndex)));
                    student.setDob(getCellValue(row.getCell(dobIndex)));
                    student.setPhoneNumber(getCellValue(row.getCell(phoneNumberIndex)));
                    student.setAddress(getCellValue(row.getCell(addressIndex)));

                    // Assuming courses are comma-separated in one cell
                    String coursesString = getCellValue(row.getCell(coursesIndex));
                    Set<String> courses = new HashSet<>();
                    if (coursesString != null) {
                        String[] coursesArray = coursesString.split(",");
                        for (String course : coursesArray) {
                            courses.add(course.trim());
                        }
                    }
                    student.setCourses(courses);

                    student.setParentFullName(getCellValue(row.getCell(parentFullNameIndex)));
                    student.setStudentRelation(getCellValue(row.getCell(studentRelationIndex)));
                    student.setParentPhone(getCellValue(row.getCell(parentPhoneIndex)));
                    student.setParentGender(getCellValue(row.getCell(parentGenderIndex)));
                    System.out.println("Processing student: " + student);
                    studentService.createStudent(student);
                    // Validate student data if needed
                    String validationMessage = validateStudent(student);
                    if (validationMessage != null) {
                        importResponse.setMessage(validationMessage);
                    }

                } catch (Exception e) {
                    errorBuilder.append(e.getMessage());
                    importResponse.setMessage(errorBuilder.toString());
                }

                if (errorBuilder.length() > 0) {
                    importResults.add(new ImportResponse(errorBuilder.toString(), i + 1));
                } else {
                    importResults.add(importResponse);
                }
            }

            if (importResults.isEmpty()) {
                importResults.add(new ImportResponse("File format is incorrect.", -1));
            }
        }

        return importResults;
    }

    private static String getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString(); // Convert date to string
                } else {
                    String numericValue = String.valueOf(cell.getNumericCellValue());
                    if (numericValue.matches(".*[eE].*")) {
                        double number = cell.getNumericCellValue();
                        return String.format("%.0f", number);
                    }
                    return numericValue;
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }


    private static String validateStudent(StudentRequest student) {
        StringBuilder errors = new StringBuilder();

        // Validate Roll Number
        if (student.getRollNumber() == null || student.getRollNumber().isEmpty()) {
            errors.append("Roll Number is missing. ");
        }

        // Validate Full Name
        if (student.getFullName() == null || student.getFullName().isEmpty()) {
            errors.append("Full Name is missing. ");
        }

        // Validate Gender
        if (student.getGender() == null || student.getGender().isEmpty()) {
            errors.append("Gender is missing. ");
        } else if (!(student.getGender().equalsIgnoreCase("Male") || student.getGender().equalsIgnoreCase("Female"))) {
            errors.append("Gender must be 'Male' or 'Female'. ");
        }

        // Validate Class Name
        if (student.getClassName() == null || student.getClassName().isEmpty()) {
            errors.append("Class Name is missing. ");
        }

        // Validate Date of Birth
        if (student.getDob() == null || student.getDob().isEmpty()) {
            errors.append("Date of Birth is missing. ");
        } else {
            try {
                // Optional: Check if the date format is valid
                // Example: If expected format is "yyyy-MM-dd", you can use a SimpleDateFormat to validate it
                // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                // sdf.parse(student.getDob());
            } catch (Exception e) {
                errors.append("Date of Birth format is incorrect. ");
            }
        }

        // Validate Phone Number
        if (student.getPhoneNumber() == null || student.getPhoneNumber().isEmpty()) {
            errors.append("Phone Number is missing. ");
        } else if (!student.getPhoneNumber().matches("\\d{10}")) {
            errors.append("Phone Number must be 10 digits. ");
        }

        // Validate Address
        if (student.getAddress() == null || student.getAddress().isEmpty()) {
            errors.append("Address is missing. ");
        }

        // Validate Courses
        if (student.getCourses() == null || student.getCourses().isEmpty()) {
            errors.append("Courses are missing. ");
        }

        // Validate Parent Full Name
        if (student.getParentFullName() == null || student.getParentFullName().isEmpty()) {
            errors.append("Parent Full Name is missing. ");
        }

        // Validate Student Relation
        if (student.getStudentRelation() == null || student.getStudentRelation().isEmpty()) {
            errors.append("Student Relation is missing. ");
        }

        // Validate Parent Phone
        if (student.getParentPhone() == null || student.getParentPhone().isEmpty()) {
            errors.append("Parent Phone is missing. ");
        } else if (!student.getParentPhone().matches("\\d{10}")) {
            errors.append("Parent Phone must be 10 digits. ");
        }

        // Validate Parent Gender
        if (student.getParentGender() == null || student.getParentGender().isEmpty()) {
            errors.append("Parent Gender is missing. ");
        } else if (!(student.getParentGender().equalsIgnoreCase("Male") || student.getParentGender().equalsIgnoreCase("Female"))) {
            errors.append("Parent Gender must be 'Male' or 'Female'. ");
        }

        // Return accumulated errors or null if no errors
        return errors.length() > 0 ? errors.toString() : null;
    }

    public static List<ImportResponse> parseExamExcelFile(MultipartFile file, ExamDetailService examDetailService,int classId) throws IOException {
        List<ImportResponse> importResults = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                throw new IllegalArgumentException("No sheets found in the file");
            }

            Row firstRow = sheet.getRow(0);
            if (firstRow == null) {
                importResults.add(new ImportResponse("File format is incorrect.", -1));
                return importResults;
            }

            int requiredColumns = 5;
            if (firstRow.getPhysicalNumberOfCells() < requiredColumns) {
                importResults.add(new ImportResponse("File format is incorrect. The file does not contain enough columns. Expected at least " + requiredColumns + ".", -1));
                return importResults;
            }

            // Cấu hình các cột trong file Excel
            int studentNameCol = 1;
            int subjectNameCol = 2;
            int theoreticalScoreCol = 3;
            int practicalScoreCol = 4;
            int classIdCol = 5;

            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) { // Bỏ qua dòng tiêu đề
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue; // Bỏ qua dòng trống
                }

                ImportResponse importResponse = new ImportResponse("Success", i + 1);
                StringBuilder errorBuilder = new StringBuilder();

                try {
                    StudentExamScoreRequest examDetailRequest = new StudentExamScoreRequest();
                    examDetailRequest.setRollNumber(getCellValue(row.getCell(studentNameCol)));
                    examDetailRequest.setSubjectCode(getCellValue(row.getCell(subjectNameCol)));

                    // Lấy điểm lý thuyết
                    String theoreticalScoreString = getCellValue(row.getCell(theoreticalScoreCol));
                    if (theoreticalScoreString != null && !theoreticalScoreString.trim().isEmpty()) {
                        BigDecimal theoreticalScore = new BigDecimal(theoreticalScoreString);
                        examDetailRequest.setTheoreticalScore(theoreticalScore);
                    }

                    // Lấy điểm thực hành
                    String practicalScoreString = getCellValue(row.getCell(practicalScoreCol));
                    if (practicalScoreString != null && !practicalScoreString.trim().isEmpty()) {
                        BigDecimal practicalScore = new BigDecimal(practicalScoreString);
                        examDetailRequest.setPracticalScore(practicalScore);
                    }

                    // Set classId từ tham số
                    examDetailRequest.setClassId(classId);

                    // Kiểm tra và xử lý import
                    String validationMessage = validateExamMark(examDetailRequest);
                    if (validationMessage != null) {
                        importResponse.setMessage(validationMessage);
                        importResults.add(importResponse);
                        continue;
                    }

                    examDetailService.updateStudentExamScore(examDetailRequest, classId);
                } catch (Exception e) {
                    errorBuilder.append("Error in row ").append(i + 1).append(": ").append(e.getMessage()).append("; ");
                    importResponse.setMessage(errorBuilder.toString());
                }

                importResults.add(importResponse);
            }

            return importResults;
        }
    }
    private static String validateExamMark(StudentExamScoreRequest examDetailRequest) {
        StringBuilder errorMessage = new StringBuilder();

        // Kiểm tra studentName
        if (examDetailRequest.getRollNumber() == null || examDetailRequest.getRollNumber().trim().isEmpty()) {
            errorMessage.append("Student name cannot be empty. ");
        }

        // Kiểm tra subjectName
        if (examDetailRequest.getSubjectCode() == null || examDetailRequest.getSubjectCode().trim().isEmpty()) {
            errorMessage.append("Subject name cannot be empty. ");
        }

        // Kiểm tra theoreticalScore
        if (examDetailRequest.getTheoreticalScore() == null) {
            errorMessage.append("Theoretical score cannot be null. ");
        } else if (examDetailRequest.getTheoreticalScore().compareTo(BigDecimal.ZERO) < 0) {
            errorMessage.append("Theoretical score cannot be negative. ");
        }

        // Kiểm tra practicalScore
        if (examDetailRequest.getPracticalScore() == null) {
            errorMessage.append("Practical score cannot be null. ");
        } else if (examDetailRequest.getPracticalScore().compareTo(BigDecimal.ZERO) < 0) {
            errorMessage.append("Practical score cannot be negative. ");
        }

        // Kiểm tra classId
        if (examDetailRequest.getClassId() <= 0) {
            errorMessage.append("Class ID must be greater than 0. ");
        }

        return errorMessage.length() > 0 ? errorMessage.toString() : null;
    }

    public static List<ImportResponse> parseSubjectExcelFile(MultipartFile file, SubjectService subjectService) throws IOException {
        List<ImportResponse> importResults = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                throw new IllegalArgumentException("No sheets found in the file");
            }

            Row firstRow = sheet.getRow(0);
            if (firstRow == null) {
                importResults.add(new ImportResponse("File format is incorrect.", -1));
                return importResults;
            }

            int requiredColumns = 3; // Adjust based on the number of columns you expect for subjects

            if (firstRow.getPhysicalNumberOfCells() < requiredColumns) {
                importResults.add(new ImportResponse("File format is incorrect. The file does not contain enough columns. Expected at least " + requiredColumns + " (Subject Name, Subject Code, Total Hours).", -1));
                return importResults;
            }

            int subjectNameIndex = 1;
            int subjectCodeIndex = 2;
            int totalHoursIndex = 3;

            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) { // Skip header row
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                ImportResponse importResponse = new ImportResponse("Success", i); // Row number (1-based)
                StringBuilder errorBuilder = new StringBuilder();

                try {
                    SubjectRequest subject = new SubjectRequest();
                    subject.setSubjectName(getCellValue(row.getCell(subjectNameIndex)));
                    subject.setSubjectCode(getCellValue(row.getCell(subjectCodeIndex)));

                    String totalHoursString = getCellValue(row.getCell(totalHoursIndex));
                    if (totalHoursString != null && !totalHoursString.trim().isEmpty()) {
                        try { 
                            double totalHoursDouble = Double.parseDouble(totalHoursString);
                            subject.setTotalHours((int) totalHoursDouble); // Cast to int
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid total hours: " + totalHoursString);
                        }
                    }


                    // Validate subject data
                    String validationMessage = validateSubject(subject);
                    if (validationMessage != null) {
                        importResponse.setMessage(validationMessage);
                    } else {
                        subjectService.createSubject(subject); // Assuming you have a method to create a subject
                    }

                } catch (Exception e) {
                    errorBuilder.append(e.getMessage());
                    importResponse.setMessage(errorBuilder.toString());
                }

                if (errorBuilder.length() > 0) {
                    importResults.add(new ImportResponse(errorBuilder.toString(), i + 1));
                } else {
                    importResults.add(importResponse);
                }
            }

            if (importResults.isEmpty()) {
                importResults.add(new ImportResponse("File format is incorrect.", -1));
            }
        }

        return importResults;
    }

    private static String validateSubject(SubjectRequest subject) {
        StringBuilder errors = new StringBuilder();

        // Validate Subject Name
        if (subject.getSubjectName() == null || subject.getSubjectName().isEmpty()) {
            errors.append("Subject Name is missing. ");
        }

        // Validate Subject Code
        if (subject.getSubjectCode() == null || subject.getSubjectCode().isEmpty()) {
            errors.append("Subject Code is missing. ");
        }

        // Validate Total Hours
        if (subject.getTotalHours() == null) {
            errors.append("Total Hours is missing. ");
        } else if (subject.getTotalHours() <= 0) {
            errors.append("Total Hours must be greater than 0. ");
        }

        return errors.length() > 0 ? errors.toString() : null;
    }

}

