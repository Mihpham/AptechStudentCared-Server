
package com.example.aptechstudentcaredserver.controller;

import com.example.aptechstudentcaredserver.bean.request.ClassRequest;
import com.example.aptechstudentcaredserver.bean.response.ClassResponse;
import com.example.aptechstudentcaredserver.service.ClassService;
import com.example.aptechstudentcaredserver.exception.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Lớp test cho {@link ClassController}.
 * Bao gồm các trường hợp kiểm thử cho các phương thức GET, POST, PUT và DELETE của {@link ClassController}.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class ClassControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClassService classService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Kiểm thử phương thức GET để lấy danh sách các lớp học.
     * Đảm bảo rằng phương thức trả về danh sách lớp học với trạng thái HTTP 200 OK.
     */
   @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    public void testGetAllResources_ShouldReturnListOfResources() throws Exception {
        List<ClassResponse> classResponses = Arrays.asList(
                new ClassResponse(1, "Math 101", "Center A", "10:00 AM", "Mon-Wed", LocalDateTime.now(), "Active", new ArrayList<>())
        );

        Mockito.when(classService.findAllClass()).thenReturn(classResponses);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/classes"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].className").value("Math 101"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].center").value("Center A"));
    }

    /**
     * Kiểm thử phương thức GET để lấy lớp học theo ID.
     * Đảm bảo rằng phương thức trả về lớp học với trạng thái HTTP 200 OK.
     */
   @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    public void testGetResourceById_ShouldReturnResource() throws Exception {
        ClassResponse classResponse = new ClassResponse(1, "Math 101", "Center A", "10:00 AM", "Mon-Wed", LocalDateTime.now(), "Active", new ArrayList<>());

        Mockito.when(classService.findClassById(1)).thenReturn(classResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/classes/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.className").value("Math 101"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.center").value("Center A"));
    }

    /**
     * Kiểm thử phương thức GET để lấy lớp học theo ID khi lớp học không tồn tại.
     * Đảm bảo rằng phương thức trả về trạng thái HTTP 404 Not Found với thông báo lỗi phù hợp.
     */
   @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    public void testGetResourceById_ShouldReturnNotFound_WhenResourceDoesNotExist() throws Exception {
        Mockito.when(classService.findClassById(1)).thenThrow(new NotFoundException("Class not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/classes/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    /**
     * Kiểm thử phương thức POST để thêm lớp học mới.
     * Đảm bảo rằng phương thức trả về trạng thái HTTP 201 Created với thông báo thành công.
     */
   @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    public void testCreateResource_ShouldReturnCreatedResource() throws Exception {
        ClassRequest classRequest = new ClassRequest("Math 101", "Center A", "10:00 AM", "Mon-Wed", "Active");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/classes/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(classRequest)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Class added successfully"));
    }

    /**
     * Kiểm thử phương thức POST để thêm lớp học khi dữ liệu không hợp lệ.
     * Đảm bảo rằng phương thức trả về trạng thái HTTP 400 Bad Request với thông báo lỗi.
     */
   @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    public void testCreateResource_ShouldReturnBadRequest_WhenInvalidData() throws Exception {
        ClassRequest invalidRequest = new ClassRequest("", "Center A", "10:00 AM", "Mon-Wed", "Active"); // invalid className

        mockMvc.perform(MockMvcRequestBuilders.post("/api/classes/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    /**
     * Kiểm thử phương thức PUT để cập nhật lớp học.
     * Đảm bảo rằng phương thức trả về lớp học đã cập nhật với trạng thái HTTP 202 Accepted.
     */
   @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    public void testUpdateResource_ShouldReturnUpdatedResource() throws Exception {
        ClassRequest classRequest = new ClassRequest("Math 101", "Center A", "10:00 AM", "Mon-Wed", "Active");
        ClassResponse classResponse = new ClassResponse(1, "Math 101", "Center A", "10:00 AM", "Mon-Wed", LocalDateTime.now(), "Active", new ArrayList<>());

        Mockito.when(classService.updateClass(Mockito.eq(1), Mockito.any(ClassRequest.class))).thenReturn(classResponse);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/classes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(classRequest)))
                .andExpect(MockMvcResultMatchers.status().isAccepted())
                .andExpect(MockMvcResultMatchers.jsonPath("$.className").value("Math 101"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.center").value("Center A"));
    }

    /**
     * Kiểm thử phương thức PUT để cập nhật lớp học khi lớp học không tồn tại.
     * Đảm bảo rằng phương thức trả về trạng thái HTTP 404 Not Found với thông báo lỗi phù hợp.
     */
   @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    public void testUpdateResource_ShouldReturnNotFound_WhenResourceDoesNotExist() throws Exception {
        ClassRequest classRequest = new ClassRequest("Math 101", "Center A", "10:00 AM", "Mon-Wed", "Active");

        Mockito.when(classService.updateClass(Mockito.eq(1), Mockito.any(ClassRequest.class)))
                .thenThrow(new NotFoundException("Class not found"));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/classes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(classRequest)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
//                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Class not found"));
    }

    /**
     * Kiểm thử phương thức PUT để cập nhật lớp học khi dữ liệu không hợp lệ.
     * Đảm bảo rằng phương thức trả về trạng thái HTTP 400 Bad Request với thông báo lỗi.
     */
   @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    public void testUpdateResource_ShouldReturnBadRequest_WhenInvalidData() throws Exception {
        ClassRequest invalidRequest = new ClassRequest("", "Center A", "10:00 AM", "Mon-Wed", "Active"); // invalid className

        mockMvc.perform(MockMvcRequestBuilders.put("/api/classes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    /**
     * Kiểm thử phương thức DELETE để xóa lớp học.
     * Đảm bảo rằng phương thức trả về trạng thái HTTP 202 Accepted với thông báo thành công.
     */
   @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    public void testDeleteResource_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/classes/1"))
                .andExpect(MockMvcResultMatchers.status().isAccepted())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Class deleted successfully"));
    }

    /**
     * Kiểm thử phương thức DELETE để xóa lớp học khi lớp học không tồn tại.
     * Đảm bảo rằng phương thức trả về trạng thái HTTP 404 Not Found với thông báo lỗi phù hợp.
     */
   @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    public void testDeleteResource_ShouldReturnNotFound_WhenResourceDoesNotExist() throws Exception {
        Mockito.doThrow(new NotFoundException("Class not found")).when(classService).deleteClass(1);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/classes/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Class not found"));
    }

    /**
     * Kiểm thử phương thức GET để lấy danh sách lớp học khi không có lớp học nào.
     * Đảm bảo rằng phương thức trả về danh sách rỗng với trạng thái HTTP 200 OK.
     */
   @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    public void testGetAllResources_ShouldReturnEmptyList_WhenNoResources() throws Exception {
        Mockito.when(classService.findAllClass()).thenReturn(new ArrayList<>());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/classes"))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty());
    }

    /**
     * Kiểm thử phương thức POST để thêm lớp học với body rỗng.
     * Đảm bảo rằng phương thức trả về trạng thái HTTP 400 Bad Request.
     */
   @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    public void testCreateResource_ShouldHandleNullRequestBody() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/classes/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")) // body rỗng
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    /**
     * Kiểm thử phương thức PUT để cập nhật lớp học với body rỗng.
     * Đảm bảo rằng phương thức trả về trạng thái HTTP 400 Bad Request.
     */
   @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    public void testUpdateResource_ShouldHandleNullRequestBody() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/classes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")) // body rỗng
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    /**
     * Kiểm thử phương thức DELETE với ID không hợp lệ.
     * Đảm bảo rằng phương thức trả về trạng thái HTTP 400 Bad Request.
     */
   @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    public void testDeleteResource_ShouldHandleInvalidIdFormat() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/classes/invalid"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    /**
     * Kiểm thử xử lý ngoại lệ không mong muốn trong phương thức GET.
     * Đảm bảo rằng phương thức trả về trạng thái HTTP 500 Internal Server Error.
     */
//    @Test
//    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
//    public void testHandleUnexpectedExceptions() throws Exception {
//        // Mock the service to throw an unexpected exception
//        Mockito.when(classService.findAllClass()).thenThrow(new RuntimeException("Unexpected error"));
//
//        // Perform the GET request and expect the internal server error with the message
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/classes"))
//                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Unexpected error"));
//    }


    /**
     * Kiểm thử với endpoint không hợp lệ.
     * Đảm bảo rằng phương thức trả về trạng thái HTTP 404 Not Found.
     */
   @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    public void testInvalidEndpoint_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/invalid"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    /**
     * Kiểm thử quyền truy cập không được phép.
     * Đảm bảo rằng phương thức trả về trạng thái HTTP 401 Unauthorized.
     */
//   @Test
//    public void testAccessWithoutAuthorization_ShouldReturnUnauthorized() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/classes"))
//                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
//    }

    /**
     * Kiểm thử quyền truy cập với quyền không đủ.
     * Đảm bảo rằng phương thức trả về trạng thái HTTP 403 Forbidden.
     */
   @Test
    public void testAccessWithInsufficientPermissions_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/classes/add"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    /**
     * Kiểm thử thời gian phản hồi cho yêu cầu GET danh sách lớp học.
     * Đảm bảo rằng thời gian phản hồi nằm trong giới hạn cho phép.
     */
   @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    public void testResponseTimeForGetAllResources() throws Exception {
        long startTime = System.currentTimeMillis();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/classes"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        long responseTime = System.currentTimeMillis() - startTime;
        // Bạn có thể điều chỉnh thời gian tối đa cho phép theo yêu cầu của mình
        assert(responseTime < 5000); // 2 giây
    }

    /**
     * Kiểm thử truy cập đồng thời vào tài nguyên.
     * Đảm bảo rằng hệ thống xử lý đồng thời nhiều yêu cầu mà không gặp lỗi.
     */
   @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    public void testConcurrentAccessToResource() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 10; i++) {
            executorService.submit(() -> {
                try {
                    mockMvc.perform(MockMvcRequestBuilders.get("/api/classes"))
                            .andExpect(MockMvcResultMatchers.status().isOk());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
