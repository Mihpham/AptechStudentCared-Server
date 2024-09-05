//package com.example.aptechstudentcaredserver.mapper;
//
//import com.example.aptechstudentcaredserver.bean.response.UserResponse;
//import com.example.aptechstudentcaredserver.entity.User;
//import com.example.aptechstudentcaredserver.entity.UserDetail;
//import com.example.aptechstudentcaredserver.exception.NotFoundException;
//
//import java.time.LocalDateTime;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//public class UserMapper {
//
//    /**
//     * Convert User entity to UserResponse.
//     *
//     * @param user The User entity to convert
//     * @return The UserResponse object
//     */
//    public static UserResponse convertToUserResponse(User user) {
//        if (user == null) {
//            throw new NotFoundException("User is null");
//        }
//
//        // Validate and fix UserDetail if necessary
//        UserDetail userDetail = Optional.ofNullable(user.getUserDetail()).orElse(new UserDetail());
//
//        // Ensure that required fields are not null
//        String email = Optional.ofNullable(user.getEmail()).orElseThrow(() -> new NotFoundException("User email is null"));
//        String fullName = Optional.ofNullable(userDetail.getFullName()).orElse("N/A");
//        String phone = Optional.ofNullable(userDetail.getPhone()).orElse("N/A");
//        String address = Optional.ofNullable(userDetail.getAddress()).orElse("N/A");
//        String roleName = Optional.ofNullable(user.getRole()).map(role -> role.getRoleName()).orElse("N/A");
//        String roleNumber = Optional.ofNullable(userDetail.getRollNumber()).orElse("N/A");
//        String image = Optional.ofNullable(userDetail.getImage()).orElse("https://static.vecteezy.com/system/resources/previews/043/900/708/non_2x/user-profile-icon-illustration-vector.jpg");
//        LocalDateTime createdAt = Optional.ofNullable(user.getCreatedAt()).orElse(LocalDateTime.now());
//
//        List<String> classNames = Optional.ofNullable(user.getGroupClasses()).orElse(Collections.emptyList())
//                .stream()
//                .map(groupClass -> Optional.ofNullable(groupClass.getClasses()).map(classes -> classes.getClassName()).orElse("N/A"))
//                .collect(Collectors.toList());
//
//        // Build UserResponse using builder
//        return UserResponse.builder()
//                .id(user.getId())
//                .email(email)
//                .fullName(fullName)
//                .phone(phone)
//                .address(address)
//                .roleName(roleName)
//                .classes(classNames)
//                .status(Optional.ofNullable(user.getStatus()).map(Enum::name).orElse("N/A"))
//                .roleNumber(roleNumber)
//                .image(image)
//                .createdAt(createdAt)
//                .build();
//    }
//}
