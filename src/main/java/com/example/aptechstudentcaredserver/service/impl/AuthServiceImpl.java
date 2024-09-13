package com.example.aptechstudentcaredserver.service.impl;

import com.example.aptechstudentcaredserver.bean.request.AuthRequest;
import com.example.aptechstudentcaredserver.bean.request.RegisterUserRequest;
import com.example.aptechstudentcaredserver.bean.response.AuthResponse;
import com.example.aptechstudentcaredserver.entity.Role;
import com.example.aptechstudentcaredserver.entity.User;
import com.example.aptechstudentcaredserver.entity.UserDetail;
import com.example.aptechstudentcaredserver.enums.Status;
import com.example.aptechstudentcaredserver.exception.DuplicateException;
import com.example.aptechstudentcaredserver.exception.EmailFormatException;
import com.example.aptechstudentcaredserver.exception.InvalidCredentialsException;
import com.example.aptechstudentcaredserver.exception.NotFoundException;
import com.example.aptechstudentcaredserver.repository.RoleRepository;
import com.example.aptechstudentcaredserver.repository.UserDetailRepository;
import com.example.aptechstudentcaredserver.repository.UserRepository;
import com.example.aptechstudentcaredserver.service.AuthService;
import com.example.aptechstudentcaredserver.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailRepository userDetailRepository;


    @Override
    public AuthResponse registerUser(RegisterUserRequest registerUserRequest) {
        User existingUser = userRepository.findByEmail(registerUserRequest.getEmail());
        if (existingUser != null) {
            throw new DuplicateException("Email is already exists with another account");
        }
        String roleName = registerUserRequest.getRoleName().toUpperCase();
        Role role = roleRepository.findByRoleName(roleName);
        if (role == null) {
            role = new Role();
            role.setRoleName(roleName);
            roleRepository.save(role);
        }

        // User creation
        User user = new User();
        user.setEmail(registerUserRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerUserRequest.getPassword()));
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setStatus(Status.ACTIVE);
        userRepository.save(user);

        // Create UserDetail
        UserDetail userDetail = new UserDetail();
        userDetail.setFullName(registerUserRequest.getFullName());
        userDetail.setPhone(registerUserRequest.getPhone());
        userDetail.setAddress(registerUserRequest.getAddress());
        userDetail.setImage(null);
        userDetail.setUser(user);
        userDetail.setParent(null);

        userDetailRepository.save(userDetail);
        user.setUserDetail(userDetail);
        userRepository.save(user);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                registerUserRequest.getEmail(),
                registerUserRequest.getPassword()
        );
        authenticationManager.authenticate(authenticationToken);
        UserDetails userDetails = jwtService.loadUserByUsername(registerUserRequest.getEmail());
        String jwt = jwtService.generateToken(userDetails);
        String roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("USER");

        return new AuthResponse(jwt, "Registration successful!", roles);
    }


    @Override
    public AuthResponse loginUser(AuthRequest authRequest) {
        String email = authRequest.getEmail();
        String password = authRequest.getPassword();
        if (!isValidEmailFormat(email)) {
            throw new EmailFormatException("Email format is invalid.");
        }
        UserDetails userDetails;
        try {
            userDetails = jwtService.loadUserByUsername(email);
        } catch (UsernameNotFoundException e) {
            throw new NotFoundException("Email not found.");
        }
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new InvalidCredentialsException("Invalid password.");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Invalid credentials.");
        }
        final String jwt = jwtService.generateToken(userDetails);
        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("USER");

        return new AuthResponse(jwt, "Login successful!", role);
    }

    private boolean isValidEmailFormat(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

}
