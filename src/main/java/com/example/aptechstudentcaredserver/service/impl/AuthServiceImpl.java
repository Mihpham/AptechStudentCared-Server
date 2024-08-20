package com.example.aptechstudentcaredserver.service.impl;

import com.example.aptechstudentcaredserver.bean.request.AuthRequest;
import com.example.aptechstudentcaredserver.bean.request.RegisterUserRequest;
import com.example.aptechstudentcaredserver.bean.response.AuthResponse;
import com.example.aptechstudentcaredserver.entity.Role;
import com.example.aptechstudentcaredserver.entity.User;
import com.example.aptechstudentcaredserver.entity.UserDetail;
import com.example.aptechstudentcaredserver.exception.DuplicateException;
import com.example.aptechstudentcaredserver.exception.EmailFormatException;
import com.example.aptechstudentcaredserver.exception.InvalidCredentialsException;
import com.example.aptechstudentcaredserver.exception.NotFoundException;
import com.example.aptechstudentcaredserver.repository.RoleRepository;
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

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse registerUser(RegisterUserRequest registerUserRequest) {
        // Check if the email already exists
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

        User user = new User();
        user.setEmail(registerUserRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerUserRequest.getPassword()));
        user.setRole(role);

        UserDetail userDetail = new UserDetail();
        userDetail.setFullName(registerUserRequest.getFullName());
        userDetail.setPhone(registerUserRequest.getPhone());
        userDetail.setAddress(registerUserRequest.getAddress());
        userDetail.setUser(user);

        user.setUserDetail(userDetail);

        userRepository.save(user);

        // Authenticate user
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                registerUserRequest.getEmail(),
                registerUserRequest.getPassword()
        );
        authenticationManager.authenticate(authenticationToken);

        // Generate JWT
        UserDetails userDetails = jwtService.loadUserByUsername(registerUserRequest.getEmail());
        String jwt = jwtService.generateToken(userDetails);

        // Get user role
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

        // Validate email format
        if (!isValidEmailFormat(email)) {
            throw new EmailFormatException("Email format is invalid.");
        }

        // Check if user exists
        UserDetails userDetails;
        try {
            userDetails = jwtService.loadUserByUsername(email);
        } catch (UsernameNotFoundException e) {
            throw new NotFoundException("Email not found.");
        }

        // Validate password
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

        // Generate JWT
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
