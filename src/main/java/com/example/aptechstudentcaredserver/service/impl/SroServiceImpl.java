package com.example.aptechstudentcaredserver.service.impl;

import com.example.aptechstudentcaredserver.bean.request.SroRequest;
import com.example.aptechstudentcaredserver.bean.response.SroResponse;
import com.example.aptechstudentcaredserver.entity.Role;
import com.example.aptechstudentcaredserver.entity.User;
import com.example.aptechstudentcaredserver.entity.UserDetail;
import com.example.aptechstudentcaredserver.enums.Status;
import com.example.aptechstudentcaredserver.repository.RoleRepository;
import com.example.aptechstudentcaredserver.repository.UserDetailRepository;
import com.example.aptechstudentcaredserver.repository.UserRepository;
import com.example.aptechstudentcaredserver.service.SroService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SroServiceImpl implements SroService {

    private final UserRepository userRepository;
    private final UserDetailRepository userDetailRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public void registerSro(SroRequest sroRequest) {
        Optional<User> existingUser = Optional.ofNullable(userRepository.findByEmail(sroRequest.getEmail()));
        if (existingUser.isPresent()) {
            throw new RuntimeException("Email '" + sroRequest.getEmail() + "' already exists.");
        }
        Role role = findOrCreateRole("SRO");
        String email = sroRequest.getEmail();
        User user = createUser(sroRequest, role, email);
        userRepository.save(user);
        UserDetail userDetail = createUserDetail(sroRequest, user);
        userDetailRepository.save(userDetail);
    }

    @Override
    public List<SroResponse> findAllSro() {
        Role sroRole = roleRepository.findByRoleName("SRO");
        if (sroRole == null) {
            return Collections.emptyList();
        }

        List<User> sros = userRepository.findByRole(sroRole);
        return sros.stream()
                .map(this::convertToSroResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SroResponse findSroById(int sroId) {
        User user = userRepository.findById(sroId)
                .orElseThrow(() -> new RuntimeException("Staff not found with id: " + sroId));

        if (!"SRO".equals(user.getRole().getRoleName())) {
            throw new RuntimeException("User with id " + sroId + " is not a sro.");
        }

        return convertToSroResponse(user);
    }

    @Override
    public SroResponse updateSro(int sroId, SroRequest sroRequest) {
        User user = userRepository.findById(sroId)
                .orElseThrow(() -> new RuntimeException("Staff not found with id: " + sroId));

        if (!"SRO".equals(user.getRole().getRoleName())) {
            throw new RuntimeException("User with id " + sroId + " is not a sro.");
        }

        if (sroRequest.getEmail() != null && !sroRequest.getEmail().equals(user.getEmail())) {
            Optional<User> existingUser = Optional.ofNullable(userRepository.findByEmail(sroRequest.getEmail()));
            if (existingUser.isPresent()) {
                throw new RuntimeException("Email '" + sroRequest.getEmail() + "' already exists.");
            }
            user.setEmail(sroRequest.getEmail());
        }

        UserDetail userDetail = user.getUserDetail();

        updateSroDetail(user, userDetail, sroRequest);

        userRepository.save(user);
        userDetailRepository.save(userDetail);

        return convertToSroResponse(user);
    }

    @Override
    public void deleteSro(int sroId) {
        User user = userRepository.findById(sroId)
                .orElseThrow(() -> new RuntimeException("Staff not found with id: " + sroId));

        if (!"SRO".equals(user.getRole().getRoleName())) {
            throw new RuntimeException("User with id " + sroId + " is not a sro.");
        }

        userRepository.delete(user);
        if (user.getUserDetail() != null) {
            userDetailRepository.delete(user.getUserDetail());
        }
    }

    private Role findOrCreateRole(String roleName) {
        return Optional.ofNullable(roleRepository.findByRoleName(roleName))
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setRoleName(roleName);
                    return roleRepository.save(role);
                });
    }

    private User createUser(SroRequest sroRequest, Role role, String email) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("@123456789"));
        user.setRole(role);
        user.setStatus(Status.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

    private UserDetail createUserDetail(SroRequest sroRequest, User user) {
        UserDetail userDetail = new UserDetail();
        userDetail.setFullName(sroRequest.getFullName());
        userDetail.setPhone(sroRequest.getPhoneNumber());
        userDetail.setDob(sroRequest.getDob());
        userDetail.setAddress(sroRequest.getAddress());
        userDetail.setImage(null);
        userDetail.setUser(user);
        userDetailRepository.save(userDetail);
        return userDetail;
    }

    private void updateSroDetail(User user, UserDetail userDetail, SroRequest sroRequest) {
        if (sroRequest.getFullName() != null) {
            userDetail.setFullName(sroRequest.getFullName());
        }
        if (sroRequest.getImage() != null) {
            userDetail.setImage(sroRequest.getImage());
        }
        if (sroRequest.getPhoneNumber() != null) {
            userDetail.setPhone(sroRequest.getPhoneNumber());
        }
        if (sroRequest.getDob() != null) {
            userDetail.setDob(sroRequest.getDob());
        }
        if (sroRequest.getAddress() != null) {
            userDetail.setAddress(sroRequest.getAddress());
        }
        if (sroRequest.getStatus() != null) {
            user.setStatus(Status.valueOf(sroRequest.getStatus()));
        }
        if (sroRequest.getEmail() != null) {
            user.setEmail(sroRequest.getEmail());
        }
    }

    private SroResponse convertToSroResponse(User user) {
        if (user == null) {
            return new SroResponse();
        }

        return new SroResponse(
                user.getId(),
                user.getEmail(),
                user.getUserDetail() != null ? user.getUserDetail().getFullName() : null,
                user.getUserDetail() != null ? user.getUserDetail().getPhone() : null,
                user.getUserDetail() != null ? user.getUserDetail().getAddress() : null,
                user.getUserDetail() != null ? user.getUserDetail().getDob() : null,
                user.getUserDetail() != null ? user.getUserDetail().getGender() : null,
                user.getRole().getRoleName(),
                user.getStatus().name(),
                user.getUserDetail() != null ? user.getUserDetail().getImage() : null,
                user.getCreatedAt()
        );
    }
}
