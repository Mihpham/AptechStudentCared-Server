package com.example.aptechstudentcaredserver.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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
import com.example.aptechstudentcaredserver.repository.UserDetailRepository;
import com.example.aptechstudentcaredserver.repository.UserRepository;
import com.example.aptechstudentcaredserver.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AuthServiceTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailRepository userDetailRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Test trường hợp khi người dùng đã tồn tại trong hệ thống.
     * Phương thức nên ném ra ngoại lệ DuplicateException với thông báo lỗi phù hợp.
     */
    @Test
    public void testRegisterUser_UserAlreadyExists() {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setEmail("test@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(new User());

        DuplicateException thrown = assertThrows(DuplicateException.class, () -> {
            authService.registerUser(request);
        });
        assertEquals("Email is already exists with another account", thrown.getMessage());
    }

    /**
     * Test trường hợp khi vai trò mới cần được tạo ra trong hệ thống.
     * Phương thức nên lưu vai trò mới và trả về phản hồi đăng ký thành công.
     */
    @Test
    public void testRegisterUser_NewRoleCreated() {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");
        request.setFullName("Test User");
        request.setPhone("1234567890");
        request.setAddress("Test Address");
        request.setRoleName("NEW_ROLE");

        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(roleRepository.findByRoleName(anyString())).thenReturn(null); // New role should be created
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(jwtService.loadUserByUsername(anyString())).thenReturn(mock(UserDetails.class));
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("jwtToken");

        AuthResponse response = authService.registerUser(request);

        assertEquals("Registration successful!", response.getMessage());
        assertEquals("jwtToken", response.getJwt());
        assertEquals("USER", response.getRole()); // Adjust as needed
        verify(userRepository).save(any(User.class));
        verify(userDetailRepository).save(any(UserDetail.class));
        verify(roleRepository).save(any(Role.class)); // Ensure the new role is saved
    }

    /**
     * Test trường hợp khi vai trò đặc biệt (`admin`, `sro`, `teacher`) không liên kết với `Parent`.
     * Phương thức nên trả về phản hồi đăng ký thành công.
     */
    @Test
    public void testRegisterUser_SpecialRole() {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setEmail("admin@example.com");
        request.setPassword("password");
        request.setFullName("Admin User");
        request.setPhone("1234567890");
        request.setAddress("Admin Address");
        request.setRoleName("ADMIN");

        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(roleRepository.findByRoleName(anyString())).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(jwtService.loadUserByUsername(anyString())).thenReturn(mock(UserDetails.class));
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("jwtToken");

        AuthResponse response = authService.registerUser(request);

        assertEquals("Registration successful!", response.getMessage());
        assertEquals("jwtToken", response.getJwt());
        assertEquals("USER", response.getRole()); // Adjust as needed
        verify(userRepository).save(any(User.class));
        verify(userDetailRepository).save(any(UserDetail.class));
    }

    /**
     * Test trường hợp khi định dạng email không hợp lệ.
     * Phương thức nên ném ra ngoại lệ EmailFormatException với thông báo lỗi phù hợp.
     */
    @Test
    public void testLoginUser_InvalidEmailFormat() {
        AuthRequest request = new AuthRequest();
        request.setEmail("invalid-email");
        request.setPassword("password");

        EmailFormatException thrown = assertThrows(EmailFormatException.class, () -> {
            authService.loginUser(request);
        });
        assertEquals("Email format is invalid.", thrown.getMessage());
    }

    /**
     * Test trường hợp khi email không tồn tại trong hệ thống.
     * Phương thức nên ném ra ngoại lệ NotFoundException với thông báo lỗi phù hợp.
     */
    @Test
    public void testLoginUser_EmailNotFound() {
        AuthRequest request = new AuthRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");

        when(jwtService.loadUserByUsername(anyString())).thenThrow(UsernameNotFoundException.class);

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            authService.loginUser(request);
        });
        assertEquals("Email not found.", thrown.getMessage());
    }

    /**
     * Test trường hợp khi mật khẩu không khớp với mật khẩu mã hóa trong hệ thống.
     * Phương thức nên ném ra ngoại lệ InvalidCredentialsException với thông báo lỗi phù hợp.
     */
    @Test
    public void testLoginUser_InvalidPassword() {
        AuthRequest request = new AuthRequest();
        request.setEmail("test@example.com");
        request.setPassword("wrongPassword");

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getPassword()).thenReturn("encodedPassword");
        when(jwtService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        InvalidCredentialsException thrown = assertThrows(InvalidCredentialsException.class, () -> {
            authService.loginUser(request);
        });
        assertEquals("Invalid password.", thrown.getMessage());
    }

    /**
     * Test trường hợp khi thông tin đăng nhập hợp lệ và người dùng đăng nhập thành công.
     * Phương thức nên trả về phản hồi đăng nhập thành công với token JWT.
     */
    @Test
    public void testLoginUser_SuccessfulLogin() {
        AuthRequest request = new AuthRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getPassword()).thenReturn("encodedPassword");
        when(jwtService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("jwtToken");

        AuthResponse response = authService.loginUser(request);

        assertEquals("Login successful!", response.getMessage());
        assertEquals("jwtToken", response.getJwt());
        assertEquals("USER", response.getRole()); // Adjust as needed
    }
}