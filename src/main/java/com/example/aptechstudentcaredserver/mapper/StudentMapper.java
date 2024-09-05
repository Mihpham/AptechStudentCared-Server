//package com.example.aptechstudentcaredserver.mapper;
//
//import com.example.aptechstudentcaredserver.bean.response.StudentResponse;
//import com.example.aptechstudentcaredserver.entity.*;
//import com.example.aptechstudentcaredserver.entity.Class;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.stream.Collectors;
//
//public class StudentMapper {
//
//    public static StudentResponse convertToStudentResponse(User user, GroupClass groupClass) {
//        if (user == null || groupClass == null) {
//            return new StudentResponse(); // Return empty object if user or groupClass is null
//        }
//
//        Class studentClass = groupClass.getClasses();
//        List<String> courses = user.getUserCourses().stream()
//                .map(userCourse -> userCourse.getCourse() != null ? userCourse.getCourse().getCourseName() : "Unknown Course")
//                .collect(Collectors.toList());
//
//        UserDetail userDetail = user.getUserDetail();
//        Parent parent = userDetail != null ? userDetail.getParent() : null;
//
//        return new StudentResponse(
//                user.getId(),
//                userDetail != null ? userDetail.getImage() : null,
//                userDetail != null ? userDetail.getRollNumber() : null,
//                userDetail != null ? userDetail.getFullName() : null,
//                user.getEmail(),
//                userDetail != null ? userDetail.getAddress() : null,
//                studentClass != null ? studentClass.getClassName() : null,
//                user.getUserDetail() != null ? user.getUserDetail().getGender() : null,
//                user.getUserDetail() != null ? LocalDateTime.parse(user.getUserDetail().getDob()) : null,
//                userDetail != null ? userDetail.getPhone() : null,
//                courses,
//                groupClass.getStatus() != null ? groupClass.getStatus().name() : null,
//                parent != null ? parent.getFullName() : null,
//                parent != null ? parent.getStudentRelation() : null,
//                parent != null ? parent.getPhone() : null,
//                parent != null ? parent.getGender() : null
//        );
//    }
//}
//
