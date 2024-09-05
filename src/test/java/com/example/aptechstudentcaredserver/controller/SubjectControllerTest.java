package com.example.aptechstudentcaredserver.controller;

import com.example.aptechstudentcaredserver.bean.request.SubjectRequest;
import com.example.aptechstudentcaredserver.bean.response.SubjectResponse;
import com.example.aptechstudentcaredserver.exception.NotFoundException;
import com.example.aptechstudentcaredserver.service.SubjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Các bài kiểm tra cho SubjectController.
 * <p>
 * Các bài kiểm tra này đảm bảo rằng các điểm cuối của SubjectController hoạt động chính xác với các phản hồi mong đợi.
 * </p>
 */
@SpringBootTest
@AutoConfigureMockMvc
public class SubjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SubjectService subjectService;

    private SubjectResponse subjectResponse;
    private List<SubjectResponse> subjectResponses;

    /**
     * Thiết lập dữ liệu mẫu trước khi mỗi bài kiểm tra chạy.
     * <p>
     * Tạo các đối tượng SubjectResponse mẫu và danh sách các đối tượng này để sử dụng trong các bài kiểm tra.
     * </p>
     */
    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        SubjectResponse subject1 = new SubjectResponse();
        subject1.setId(1);
        subject1.setSubjectName("Web Component Development using Java");
        subject1.setSubjectCode("WCD");
        subject1.setTotalHours(40);
        subject1.setCreatedAt(now);
        subject1.setUpdatedAt(now);

        SubjectResponse subject2 = new SubjectResponse();
        subject2.setId(2);
        subject2.setSubjectName("Integrating Applications with Spring Framework");
        subject2.setSubjectCode("IASF");
        subject2.setTotalHours(30);
        subject2.setCreatedAt(now);
        subject2.setUpdatedAt(now);

        subjectResponse = subject1;
        subjectResponses = Arrays.asList(subject1, subject2);
    }

    /**
     * Kiểm tra phương thức GET /api/subjects với các chủ đề có sẵn.
     * <p>
     * Giả lập hành vi của subjectService.findAllSubject() để trả về danh sách các chủ đề mẫu và kiểm tra phản hồi HTTP.
     * </p>
     *
     * @throws Exception nếu có lỗi xảy ra khi thực hiện yêu cầu HTTP.
     */
    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void getAllSubjects_success() throws Exception {
        Mockito.when(subjectService.findAllSubject()).thenReturn(subjectResponses);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/subjects")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(subjectResponses.size()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].subjectName").value("Web Component Development using Java"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].subjectCode").value("WCD"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].totalHours").value(40))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].subjectName").value("Integrating Applications with Spring Framework"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].subjectCode").value("IASF"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].totalHours").value(30));
    }

    /**
     * Kiểm tra phương thức GET /api/subjects/{id} với ID chủ đề hợp lệ.
     * <p>
     * Giả lập hành vi của subjectService.findSubjectById() để trả về chủ đề mẫu và kiểm tra phản hồi HTTP.
     * </p>
     *
     * @throws Exception nếu có lỗi xảy ra khi thực hiện yêu cầu HTTP.
     */
    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void getSubjectById_existingSubject_success() throws Exception {
        Mockito.when(subjectService.findSubjectById(1)).thenReturn(subjectResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/subjects/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.subjectName").value("Web Component Development using Java"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.subjectCode").value("WCD"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalHours").value(40));
    }

    /**
     * Kiểm tra phương thức GET /api/subjects/{id} với ID chủ đề không tồn tại.
     * <p>
     * Giả lập hành vi của subjectService.findSubjectById() để ném ra ngoại lệ khi chủ đề không tồn tại và kiểm tra phản hồi HTTP.
     * </p>
     *
     * @throws Exception nếu có lỗi xảy ra khi thực hiện yêu cầu HTTP.
     */
    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void getSubjectById_nonExistingSubject_notFound() throws Exception {
        Mockito.when(subjectService.findSubjectById(999)).thenThrow(new RuntimeException("Subject not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/subjects/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    /**
     * Kiểm tra phương thức GET /api/subjects/{id} với ID không hợp lệ.
     * <p>
     * Kiểm tra phản hồi HTTP với ID không hợp lệ.
     * </p>
     *
     * @throws Exception nếu có lỗi xảy ra khi thực hiện yêu cầu HTTP.
     */
    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void getSubjectById_invalidId_badRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/subjects/abc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    /**
     * Kiểm tra phương thức POST /api/subjects/add với dữ liệu hợp lệ.
     * <p>
     * Giả lập hành vi của subjectService.createSubject() và kiểm tra phản hồi HTTP.
     * </p>
     *
     * @throws Exception nếu có lỗi xảy ra khi thực hiện yêu cầu HTTP.
     */
    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void addSubject_success() throws Exception {
        SubjectRequest subjectRequest = new SubjectRequest();
        subjectRequest.setSubjectName("Database Systems");
        subjectRequest.setSubjectCode("DBS");
        subjectRequest.setTotalHours(50);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/subjects/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"subjectName\": \"Database Systems\", \"subjectCode\": \"DBS\", \"totalHours\": 50}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Subject added successfully"));
    }

    /**
     * Kiểm tra phương thức POST /api/subjects/add với dữ liệu thiếu thông tin.
     * <p>
     * Giả lập hành vi của subjectService.createSubject() và kiểm tra phản hồi HTTP với dữ liệu không hợp lệ.
     * </p>
     *
     * @throws Exception nếu có lỗi xảy ra khi thực hiện yêu cầu HTTP.
     */
    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void addSubject_missingField_badRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/subjects/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"subjectName\": \"Database Systems\", \"totalHours\": 50}")) // Thiếu subjectCode
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    /**
     * Kiểm tra phương thức PUT /api/subjects/{subjectId} với dữ liệu hợp lệ.
     * <p>
     * Giả lập hành vi của subjectService.updateSubject() và kiểm tra phản hồi HTTP.
     * </p>
     *
     * @throws Exception nếu có lỗi xảy ra khi thực hiện yêu cầu HTTP.
     */
    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void updateSubject_success() throws Exception {
        SubjectRequest subjectRequest = new SubjectRequest();
        subjectRequest.setSubjectName("Advanced Web Development");
        subjectRequest.setSubjectCode("AWD");
        subjectRequest.setTotalHours(60);

        SubjectResponse updatedSubject = new SubjectResponse();
        updatedSubject.setId(1);
        updatedSubject.setSubjectName("Advanced Web Development");
        updatedSubject.setSubjectCode("AWD");
        updatedSubject.setTotalHours(60);
        updatedSubject.setCreatedAt(LocalDateTime.now());
        updatedSubject.setUpdatedAt(LocalDateTime.now());

        Mockito.when(subjectService.updateSubject(Mockito.anyInt(), Mockito.any(SubjectRequest.class)))
                .thenReturn(updatedSubject);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/subjects/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"subjectName\": \"Advanced Web Development\", \"subjectCode\": \"AWD\", \"totalHours\": 60}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.subjectName").value("Advanced Web Development"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.subjectCode").value("AWD"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalHours").value(60));
    }

    /**
     * Kiểm tra phương thức PUT /api/subjects/{subjectId} với ID không hợp lệ.
     * <p>
     * Kiểm tra phản hồi HTTP khi ID không hợp lệ được cung cấp cho phương thức PUT.
     * </p>
     *
     * @throws Exception nếu có lỗi xảy ra khi thực hiện yêu cầu HTTP.
     */
    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void updateSubject_invalidId_badRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/subjects/abc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"subjectName\": \"Advanced Web Development\", \"subjectCode\": \"AWD\", \"totalHours\": 60}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    /**
     * Kiểm tra phương thức DELETE /api/subjects/{id} với ID hợp lệ.
     * <p>
     * Giả lập hành vi của subjectService.deleteSubject() và kiểm tra phản hồi HTTP.
     * </p>
     *
     * @throws Exception nếu có lỗi xảy ra khi thực hiện yêu cầu HTTP.
     */
    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void deleteSubject_success() throws Exception {
        Mockito.doNothing().when(subjectService).deleteSubject(1);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/subjects/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Subject deleted successfully"));
    }

    /**
     * Kiểm tra phương thức DELETE /api/subjects/{id} với ID không hợp lệ.
     * <p>
     * Giả lập hành vi của subjectService.deleteSubject() để ném ra ngoại lệ khi ID không hợp lệ và kiểm tra phản hồi HTTP.
     * </p>
     *
     * @throws Exception nếu có lỗi xảy ra khi thực hiện yêu cầu HTTP.
     */
    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void deleteSubject_nonExistingId_notFound() throws Exception {
        // Simulate the exception thrown by the service layer
        Mockito.doThrow(new RuntimeException("Subject not found")).when(subjectService).deleteSubject(999);

        // Perform the request and verify the response
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/subjects/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

}
