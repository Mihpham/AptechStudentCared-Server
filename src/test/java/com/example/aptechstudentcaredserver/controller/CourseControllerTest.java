//package com.example.aptechstudentcaredserver.controller;
//import com.example.aptechstudentcaredserver.bean.request.CourseRequest;
//import com.example.aptechstudentcaredserver.bean.response.CourseResponse;
//import com.example.aptechstudentcaredserver.exception.NotFoundException;
//import com.example.aptechstudentcaredserver.service.CourseService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import java.util.List;
//
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
///**
// * Lớp kiểm thử cho lớp {@link CourseController}.
// * Các test case trong lớp này bao gồm các yêu cầu GET, POST, PUT, DELETE,
// * xử lý các tình huống ngoại lệ và lỗi, bảo mật và hiệu suất.
// */
//@SpringBootTest
//@AutoConfigureMockMvc
//public class CourseControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Mock
//    private CourseService courseService;
//
//    @InjectMocks
//    private CourseController courseController;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        mockMvc = MockMvcBuilders.standaloneSetup(courseController).build();
//    }
//
//    /**
//     * Kiểm tra việc trả về danh sách tất cả các khóa học.
//     *
//     * @throws Exception nếu có lỗi xảy ra khi thực hiện yêu cầu
//     */
//    @Test
//    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
//    public void testGetAllCourses_ShouldReturnListOfCourses() throws Exception {
//        // Arrange
//        CourseResponse courseResponse = new CourseResponse(1, "Course 1", "C001", "30 days", null);
//        when(courseService.getAllCourses()).thenReturn(List.of(courseResponse));
//
//        // Act & Assert
//        mockMvc.perform(get("/api/courses")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isNoContent())
//                .andExpect(MockMvcResultMatchers.jsonPath("$[0].courseName").value("Course 1"));
//    }
//
//    /**
//     * Kiểm tra việc trả về thông tin khóa học theo ID.
//     *
//     * @throws Exception nếu có lỗi xảy ra khi thực hiện yêu cầu
//     */
//    @Test
//    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
//    public void testGetCourseById_ShouldReturnCourse() throws Exception {
//        // Arrange
//        CourseResponse courseResponse = new CourseResponse(1, "Course 1", "C001", "30 days", null);
//        when(courseService.getCourseById(1)).thenReturn(courseResponse);
//
//        // Act & Assert
//        mockMvc.perform(get("/api/courses/1")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isOk());
////                .andExpect(MockMvcResultMatchers.jsonPath("$.courseName").value("Course 1"));
//    }
//
//    /**
//     * Kiểm tra việc trả về lỗi khi khóa học không tồn tại.
//     *
//     * @throws Exception nếu có lỗi xảy ra khi thực hiện yêu cầu
//     */
////    @Test
////    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
////    public void testGetCourseById_ShouldReturnNotFound_WhenCourseDoesNotExist() throws Exception {
////        // Arrange
////        Mockito.when(courseService.getCourseById(100000)).thenThrow(new NotFoundException("Course not found"));
////
////        // Act & Assert
////        mockMvc.perform(MockMvcRequestBuilders.get("/api/courses/10000"))
////                .andExpect(MockMvcResultMatchers.status().isNotFound());
//////                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Course not found"));
////    }
//
//    /**
//     * Kiểm tra việc tạo khóa học mới thành công.
//     *
//     * @throws Exception nếu có lỗi xảy ra khi thực hiện yêu cầu
//     */
//    @Test
//    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
//    public void testCreateCourse_ShouldReturnCreatedCourse() throws Exception {
//        // Arrange
//        CourseRequest courseRequest = new CourseRequest(1, "Course 1", "C001", "30 days", null);
//
//        // Act & Assert
//        mockMvc.perform(post("/api/courses/add")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(courseRequest)))
//                .andExpect(MockMvcResultMatchers.status().isCreated())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Course added successfully"));
//    }
//
//
//    /**
//     * Kiểm tra việc trả về lỗi khi dữ liệu không hợp lệ trong yêu cầu tạo khóa học.
//     *
//     * @throws Exception nếu có lỗi xảy ra khi thực hiện yêu cầu
//     */
////    @Test
////    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
////    public void testCreateCourse_ShouldReturnBadRequest_WhenInvalidData() throws Exception {
////        // Arrange
////        CourseRequest courseRequest = new CourseRequest(1,"","","",null); // Missing required fields
////          mockMvc.perform(post("/api/courses/add")
////                        .contentType(MediaType.APPLICATION_JSON)
////                        .content(objectMapper.writeValueAsString(courseRequest)))
////                .andExpect(MockMvcResultMatchers.status().isBadRequest())
////                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Invalid data"));
////    }
//
//    /**
//     * Kiểm tra việc cập nhật thông tin khóa học thành công.
//     *
//     * @throws Exception nếu có lỗi xảy ra khi thực hiện yêu cầu
//     */
//    @Test
//    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
//    public void testUpdateCourse_ShouldReturnUpdatedCourse() throws Exception {
//        // Arrange
//        CourseRequest courseRequest = new CourseRequest(1, "Updated Course", "C002", "60 days", null);
//
//        // Act & Assert
//        mockMvc.perform(put("/api/courses/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(courseRequest)))
//                .andExpect(MockMvcResultMatchers.status().isAccepted())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Course updated successfully"));
//    }
//
//    /**
//     * Kiểm tra việc trả về lỗi khi khóa học không tồn tại trong yêu cầu cập nhật.
//     *
//     * @throws Exception nếu có lỗi xảy ra khi thực hiện yêu cầu
//     */
//    @Test
//    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
//    public void testUpdateCourse_ShouldReturnNotFound_WhenCourseDoesNotExist() throws Exception {
//        // Arrange
//        CourseRequest courseRequest = new CourseRequest(1, "Updated Course", "C002", "60 days", null);
//        when(courseService.updateCourse(eq(1), any(CourseRequest.class))).thenThrow(new NotFoundException("Course not found"));
//
//        // Act & Assert
//        mockMvc.perform(put("/api/courses/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(courseRequest)))
//                .andExpect(MockMvcResultMatchers.status().isNotFound())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Course not found"));
//    }
//
//    /**
//     * Kiểm tra việc trả về lỗi khi dữ liệu không hợp lệ trong yêu cầu cập nhật.
//     *
//     * @throws Exception nếu có lỗi xảy ra khi thực hiện yêu cầu
//     */
//    @Test
//    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
//    public void testUpdateCourse_ShouldReturnBadRequest_WhenInvalidData() throws Exception {
//        // Arrange
//        CourseRequest courseRequest = new CourseRequest(); // Missing required fields
//        when(courseService.updateCourse(eq(1), any(CourseRequest.class))).thenThrow(new RuntimeException("Invalid data"));
//
//        // Act & Assert
//        mockMvc.perform(put("/api/courses/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(courseRequest)))
//                .andExpect(MockMvcResultMatchers.status().isBadRequest())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Invalid data"));
//    }
//
//    /**
//     * Kiểm tra việc xóa khóa học thành công.
//     *
//     * @throws Exception nếu có lỗi xảy ra khi thực hiện yêu cầu
//     */
//    @Test
//    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
//    public void testDeleteCourse_ShouldReturnNoContent() throws Exception {
//        // Arrange
//        doNothing().when(courseService).deleteCourse(1);
//
//        // Act & Assert
//        mockMvc.perform(delete("/api/courses/1")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isAccepted())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Course deleted successfully"));
//    }
//
//    /**
//     * Kiểm tra việc trả về lỗi khi khóa học không tồn tại trong yêu cầu xóa.
//     *
//     * @throws Exception nếu có lỗi xảy ra khi thực hiện yêu cầu
//     */
//    @Test
//    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
//    public void testDeleteCourse_ShouldReturnNotFound_WhenCourseDoesNotExist() throws Exception {
//        // Arrange
//        doThrow(new NotFoundException("Course not found")).when(courseService).deleteCourse(1);
//
//        // Act & Assert
//        mockMvc.perform(delete("/api/courses/1")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isNotFound())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Course not found"));
//    }
//
//    /**
//     * Kiểm tra việc xử lý các ngoại lệ không mong muốn.
//     *
//     * @throws Exception nếu có lỗi xảy ra khi thực hiện yêu cầu
//     */
////    @Test
////    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
////    public void testHandleUnexpectedExceptions() throws Exception {
////        // Arrange
////        when(courseService.getAllCourses()).thenThrow(new RuntimeException("Unexpected error"));
////
////        // Act & Assert
////        mockMvc.perform(get("/api/courses")
////                        .contentType(MediaType.APPLICATION_JSON))
////                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
////                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Unexpected error"));
////    }
//
//    /**
//     * Kiểm tra việc trả về lỗi khi gọi đến endpoint không hợp lệ.
//     *
//     * @throws Exception nếu có lỗi xảy ra khi thực hiện yêu cầu
//     */
//    @Test
//    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
//    public void testInvalidEndpoint_ShouldReturnNotFound() throws Exception {
//        // Act & Assert
//        mockMvc.perform(get("/api/invalidEndpoint")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isNotFound());
//    }
//
//    /**
//     * Kiểm tra việc trả về lỗi không có quyền truy cập.
//     *
//     * @throws Exception nếu có lỗi xảy ra khi thực hiện yêu cầu
//     */
////    @Test
////    public void testAccessWithoutAuthorization_ShouldReturnUnauthorized() throws Exception {
////        // Act & Assert
////        mockMvc.perform(get("/api/courses")
////                        .header("Authorization", "InvalidToken")
////                        .contentType(MediaType.APPLICATION_JSON))
////                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
////    }
//
//    /**
//     * Kiểm tra việc trả về lỗi khi quyền truy cập không đủ.
//     *
//     * @throws Exception nếu có lỗi xảy ra khi thực hiện yêu cầu
//     */
////    @Test
////    public void testAccessWithInsufficientPermissions_ShouldReturnForbidden() throws Exception {
////        // Act & Assert
////        mockMvc.perform(get("/api/courses")
////                        .header("Authorization", "ValidTokenWithLimitedAccess")
////                        .contentType(MediaType.APPLICATION_JSON))
////                .andExpect(MockMvcResultMatchers.status().isForbidden());
////    }
//
//    /**
//     * Kiểm tra thời gian phản hồi cho yêu cầu lấy tất cả các khóa học.
//     *
//     * @throws Exception nếu có lỗi xảy ra khi thực hiện yêu cầu
//     */
//    @Test
//    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
//    public void testResponseTimeForGetAllCourses() throws Exception {
//        long startTime = System.currentTimeMillis();
//        mockMvc.perform(get("/api/courses")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isNoContent());
//        long endTime = System.currentTimeMillis();
//        long duration = endTime - startTime;
//        // Assert that the response time is within acceptable limits (e.g., less than 200 ms)
//        assert(duration < 200);
//    }
//
//    /**
//     * Kiểm tra khả năng xử lý truy cập đồng thời vào tài nguyên.
//     *
//     * @throws Exception nếu có lỗi xảy ra khi thực hiện yêu cầu
//     */
//    @Test
//    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
//    public void testConcurrentAccessToResource() throws Exception {
//        Runnable task = () -> {
//            try {
//                mockMvc.perform(post("/api/courses/add")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(new CourseRequest(1, "Concurrent Course", "C003", "90 days", null))))
//                        .andExpect(MockMvcResultMatchers.status().isCreated());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        };
//
//        Thread thread1 = new Thread(task);
//        Thread thread2 = new Thread(task);
//        thread1.start();
//        thread2.start();
//
//        thread1.join();
//        thread2.join();
//    }
//}
//
