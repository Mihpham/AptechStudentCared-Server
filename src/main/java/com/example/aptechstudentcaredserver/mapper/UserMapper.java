//package com.example.aptechstudentcaredserver.mapper;
//
//import com.example.aptechstudentcaredserver.bean.response.UserResponse;
//import com.example.aptechstudentcaredserver.entity.User;
//import com.example.aptechstudentcaredserver.exception.NotFoundException;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.stream.Collectors;
//
//
//public class UserMapper {
//
//        /**
//         * Chuyển đổi đối tượng User thành UserResponse.
//         *
//         * @param user Đối tượng User cần chuyển đổi
//         * @return Đối tượng UserResponse
//         * @throws NotFoundException Nếu các thuộc tính quan trọng của User là null
//         */
//        public static UserResponse convertToUserResponse(User user) {
//            if (user == null) {
//                throw new NotFoundException("User is null");
//            }
//
//            // Kiểm tra các thuộc tính của User và ném ngoại lệ nếu chúng là null
//            String email = user.getEmail();
//            if (email == null) {
//                throw new NotFoundException("User email is null");
//            }
//
//            String fullName = user.getUserDetail() != null ? user.getUserDetail().getFullName() : null;
//            if (fullName == null) {
//                throw new NotFoundException("User full name is null");
//            }
//
//            String phone = user.getUserDetail() != null ? user.getUserDetail().getPhone() : null;
//            if (phone == null) {
//                throw new NotFoundException("User phone is null");
//            }
//
//            String address = user.getUserDetail() != null ? user.getUserDetail().getAddress() : null;
//            if (address == null) {
//                throw new NotFoundException("User address is null");
//            }
//
//            String roleName = user.getRole() != null ? user.getRole().getRoleName() : null;
//            if (roleName == null) {
//                throw new NotFoundException("User role name is null");
//            }
//
//            String roleNumber = user.getUserDetail() != null ? user.getUserDetail().getRollNumber() : null;
//            if (roleNumber == null) {
//                throw new NotFoundException("User role number is null");
//            }
//
//            String image = user.getUserDetail() != null ? user.getUserDetail().getImage() : null;
//            if (image == null) {
//                throw new NotFoundException("User image is null");
//            }
//
//            List<String> classNames = user.getGroupClasses() != null ?
//                    user.getGroupClasses().stream()
//                            .map(groupClass -> groupClass.getClasses() != null ? groupClass.getClasses().getClassName() : null)
//                            .filter(className -> className != null)
//                            .collect(Collectors.toList()) :
//                    Collections.emptyList();
//
//            // Tạo đối tượng UserResponse sau khi kiểm tra tất cả các giá trị
//            return new UserResponse(
//                    user.getId(),
//                    email,
//                    fullName,
//                    phone,
//                    address,
//                    roleName,
//                    classNames,
//                    String.valueOf(user.getStatus()),
//                    roleNumber,
//                    image,
//                    createdAt
//            );
//        }
//    }
//
