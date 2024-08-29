package com.example.aptechstudentcaredserver.service;

import com.example.aptechstudentcaredserver.bean.response.UserResponse;
import com.example.aptechstudentcaredserver.entity.User;
import com.example.aptechstudentcaredserver.exception.NotFoundException;
import com.example.aptechstudentcaredserver.repository.UserRepository;
import com.example.aptechstudentcaredserver.service.impl.UserServiceImpl;
import com.example.aptechstudentcaredserver.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Lớp kiểm tra cho UserServiceImpl để kiểm tra các phương thức của dịch vụ người dùng.
 */
@SpringBootTest
@SpringJUnitConfig
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtUtil jwtUtil;

    private List<User> users;

    @BeforeEach
    void setUp() {
        // Tạo các đối tượng User mẫu
        User user1 = new User();
        user1.setId(1);
        user1.setEmail("user1@example.com");

        User user2 = new User();
        user2.setId(2);
        user2.setEmail("user2@example.com");

        // Khởi tạo danh sách người dùng
        users = Arrays.asList(user1, user2);
    }

    /**
     * Kiểm tra phương thức findAllUser khi có người dùng trong cơ sở dữ liệu.
     */
    @Test
    void findAllUser_success() {
        // Giả lập hành vi của userRepository.findAll() để trả về danh sách người dùng
        Mockito.when(userRepository.findAll()).thenReturn(users);

        // Gọi phương thức findAllUser và kiểm tra phản hồi
        List<UserResponse> userResponses = userService.findAllUser();

        // Xác nhận danh sách người dùng trả về đúng số lượng và thông tin
        assertEquals(users.size(), userResponses.size());
        assertEquals(users.get(0).getEmail(), userResponses.get(0).getEmail());
    }

    /**
     * Kiểm tra phương thức findAllUser khi không có người dùng trong cơ sở dữ liệu.
     */
    @Test
    void findAllUser_noUsers_throwsNotFoundException() {
        // Giả lập hành vi của userRepository.findAll() để trả về danh sách rỗng
        Mockito.when(userRepository.findAll()).thenReturn(Arrays.asList());

        // Xác nhận phương thức findAllUser ném ngoại lệ NotFoundException
        assertThrows(NotFoundException.class, () -> userService.findAllUser());
    }

    /**
     * Kiểm tra phương thức findUserById khi tìm thấy người dùng theo ID.
     */
    @Test
    void findUserById_success() {
        // Tạo đối tượng User mẫu với ID là 1
        User user = new User();
        user.setId(1);
        user.setEmail("user1@example.com");

        // Giả lập hành vi của userRepository.findById() để trả về người dùng với ID là 1
        Mockito.when(userRepository.findById(1)).thenReturn(java.util.Optional.of(user));

        // Gọi phương thức findUserById và kiểm tra phản hồi
        UserResponse userResponse = userService.findUserById(1);

        // Xác nhận thông tin của người dùng trả về đúng
        assertEquals(user.getId(), userResponse.getId());
        assertEquals(user.getEmail(), userResponse.getEmail());
    }

    /**
     * Kiểm tra phương thức findUserById khi không tìm thấy người dùng theo ID.
     */
    @Test
    void findUserById_notFound_throwsNotFoundException() {
        // Giả lập hành vi của userRepository.findById() để trả về Optional.empty()
        Mockito.when(userRepository.findById(1)).thenReturn(java.util.Optional.empty());

        // Xác nhận phương thức findUserById ném ngoại lệ NotFoundException
        assertThrows(NotFoundException.class, () -> userService.findUserById(1));
    }

    /**
     * Kiểm tra phương thức findUserResponseFromToken khi có người dùng với token hợp lệ.
     */
    @Test
    void findUserResponseFromToken_success() {
        String token = "valid-token";
        String email = "user@example.com";

        // Tạo đối tượng User mẫu với email là user@example.com
        User user = new User();
        user.setEmail(email);

        // Giả lập hành vi của jwtUtil.extractUsername() và userRepository.findByEmail() để trả về người dùng
        Mockito.when(jwtUtil.extractUsername(token)).thenReturn(email);
        Mockito.when(userRepository.findByEmail(email)).thenReturn(user);

        // Gọi phương thức findUserResponseFromToken và kiểm tra phản hồi
        UserResponse userResponse = userService.findUserResponseFromToken(token);

        // Xác nhận thông tin của người dùng trả về đúng
        assertEquals(user.getEmail(), userResponse.getEmail());
    }

    /**
     * Kiểm tra phương thức findUserResponseFromToken khi không tìm thấy người dùng với token hợp lệ.
     */
    @Test
    void findUserResponseFromToken_notFound_throwsNotFoundException() {
        String token = "valid-token";
        String email = "user@example.com";

        // Giả lập hành vi của jwtUtil.extractUsername() và userRepository.findByEmail() để trả về null
        Mockito.when(jwtUtil.extractUsername(token)).thenReturn(email);
        Mockito.when(userRepository.findByEmail(email)).thenReturn(null);

        // Xác nhận phương thức findUserResponseFromToken ném ngoại lệ NotFoundException
        assertThrows(NotFoundException.class, () -> userService.findUserResponseFromToken(token));
    }

    /**
     * Kiểm tra phương thức findUserFromToken khi tìm thấy người dùng với token hợp lệ.
     */
    @Test
    void findUserFromToken_success() {
        String token = "valid-token";
        String email = "user@example.com";

        // Tạo đối tượng User mẫu với email là user@example.com
        User user = new User();
        user.setEmail(email);

        // Giả lập hành vi của jwtUtil.extractUsername() và userRepository.findByEmail() để trả về người dùng
        Mockito.when(jwtUtil.extractUsername(token)).thenReturn(email);
        Mockito.when(userRepository.findByEmail(email)).thenReturn(user);

        // Gọi phương thức findUserFromToken và kiểm tra phản hồi
        User foundUser = userService.findUserFromToken(token);

        // Xác nhận thông tin của người dùng tìm thấy đúng
        assertEquals(email, foundUser.getEmail());
    }

    /**
     * Kiểm tra phương thức findUserFromToken khi không tìm thấy người dùng với token hợp lệ.
     */
    @Test
    void findUserFromToken_notFound_returnsNull() {
        String token = "valid-token";
        String email = "user@example.com";

        // Giả lập hành vi của jwtUtil.extractUsername() và userRepository.findByEmail() để trả về null
        Mockito.when(jwtUtil.extractUsername(token)).thenReturn(email);
        Mockito.when(userRepository.findByEmail(email)).thenReturn(null);

        // Gọi phương thức findUserFromToken và kiểm tra phản hồi
        User foundUser = userService.findUserFromToken(token);

        // Xác nhận người dùng tìm thấy là null
        assertEquals(null, foundUser);
    }
}

