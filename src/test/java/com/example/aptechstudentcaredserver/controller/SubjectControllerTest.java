//package com.example.aptechstudentcaredserver.controller;
//
//import com.example.aptechstudentcaredserver.bean.request.SubjectRequest;
//import com.example.aptechstudentcaredserver.bean.response.SubjectResponse;
//import com.example.aptechstudentcaredserver.exception.NotFoundException;
//import com.example.aptechstudentcaredserver.service.SubjectService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//
//import java.time.LocalDateTime;
//import java.util.Arrays;
//import java.util.List;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.doThrow;
//import static org.mockito.Mockito.when;
//
//import java.time.LocalDateTime;
//import java.util.Arrays;
//import java.util.Collections;
///**
// * Các bài kiểm tra cho SubjectController.
// * <p>
// * Các bài kiểm tra này đảm bảo rằng các điểm cuối của SubjectController hoạt động chính xác với các phản hồi mong đợi.
// * </p>
// */
//@SpringBootTest
//@AutoConfigureMockMvc
//public class SubjectControllerTest {
//
//
//        @Autowired
//        private MockMvc mockMvc;
//
//        @MockBean
//        private SubjectService subjectService;
//
//        @Test
//    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
//        /**
//         * Kiểm thử phương thức GET để lấy danh sách tất cả các môn học.
//         * Xác nhận rằng danh sách các môn học được trả về đúng và có số lượng môn học mong đợi.
//         */
//        void testGetAllSubjects_ShouldReturnListOfSubjects() throws Exception {
//            List<SubjectResponse> mockSubjects = Arrays.asList(
//                    new SubjectResponse(1, "Math", "MTH101", 40, LocalDateTime.now(), LocalDateTime.now()),
//                    new SubjectResponse(2, "Science", "SCI101", 45, LocalDateTime.now(), LocalDateTime.now())
//            );
//
//            when(subjectService.findAllSubject()).thenReturn(mockSubjects);
//
//            mockMvc.perform(MockMvcRequestBuilders.get("/api/subjects"))
//                    .andExpect(MockMvcResultMatchers.status().isOk())
//                    .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(mockSubjects.size()))
//                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].subjectName").value("Math"))
//                    .andExpect(MockMvcResultMatchers.jsonPath("$[1].subjectName").value("Science"));
//        }
//
//        @Test
//    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
//        /**
//         * Kiểm thử phương thức GET để lấy thông tin một môn học theo ID.
//         * Xác nhận rằng môn học được trả về đúng với ID đã cho.
//         */
//        void testGetSubjectById_ShouldReturnSubject() throws Exception {
//            SubjectResponse mockSubject = new SubjectResponse(1, "Math", "MTH101", 40, LocalDateTime.now(), LocalDateTime.now());
//
//            when(subjectService.findSubjectById(1)).thenReturn(mockSubject);
//
//            mockMvc.perform(MockMvcRequestBuilders.get("/api/subjects/1"))
//                    .andExpect(MockMvcResultMatchers.status().isOk())
//                    .andExpect(MockMvcResultMatchers.jsonPath("$.subjectName").value("Math"))
//                    .andExpect(MockMvcResultMatchers.jsonPath("$.subjectCode").value("MTH101"));
//        }
//
//        @Test
//    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
//        /**
//         * Kiểm thử phương thức GET khi môn học không tồn tại.
//         * Xác nhận rằng mã trạng thái là NOT_FOUND và thông báo lỗi được trả về.
//         */
//        void testGetSubjectById_ShouldReturnNotFound_WhenSubjectDoesNotExist() throws Exception {
//            when(subjectService.findSubjectById(1)).thenThrow(new RuntimeException("Subject not found"));
//
//            mockMvc.perform(MockMvcRequestBuilders.get("/api/subjects/1"))
//                    .andExpect(MockMvcResultMatchers.status().isNotFound());
////                    .andExpect(MockMvcResultMatchers.content().string("Subject not found"));
//        }
//
//        @Test
//    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
//        /**
//         * Kiểm thử phương thức POST để thêm môn học mới.
//         * Xác nhận rằng môn học được thêm thành công và thông báo thành công được trả về.
//         */
//        void testCreateSubject_ShouldReturnCreatedSubject() throws Exception {
//            SubjectRequest request = new SubjectRequest("Math", "MTH101", 40);
//            SubjectResponse response = new SubjectResponse(1, "Math", "MTH101", 40, LocalDateTime.now(), LocalDateTime.now());
//
////            when(subjectService.createSubject(any(SubjectRequest.class))).thenReturn(response);
//
//            mockMvc.perform(MockMvcRequestBuilders.post("/api/subjects/add")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(new ObjectMapper().writeValueAsString(request)))
//                    .andExpect(MockMvcResultMatchers.status().isOk())
//                    .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Subject added successfully"));
//        }
//
//        @Test
//    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
//        /**
//         * Kiểm thử phương thức POST khi dữ liệu không hợp lệ.
//         * Xác nhận rằng mã trạng thái là BAD_REQUEST và thông báo lỗi được trả về.
//         */
//        void testCreateSubject_ShouldReturnBadRequest_WhenInvalidData() throws Exception {
//            SubjectRequest request = new SubjectRequest(null, "", null);
//
//            mockMvc.perform(MockMvcRequestBuilders.post("/api/subjects/add")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(new ObjectMapper().writeValueAsString(request)))
//                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
//        }
//
//        @Test
//    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
//        /**
//         * Kiểm thử phương thức PUT để cập nhật môn học.
//         * Xác nhận rằng môn học được cập nhật thành công và thông tin được trả về chính xác.
//         */
//        void testUpdateSubject_ShouldReturnUpdatedSubject() throws Exception {
//            SubjectRequest request = new SubjectRequest("Math", "MTH102", 50);
//            SubjectResponse response = new SubjectResponse(1, "Math", "MTH102", 50, LocalDateTime.now(), LocalDateTime.now());
//
//            when(subjectService.updateSubject(eq(1), any(SubjectRequest.class))).thenReturn(response);
//
//            mockMvc.perform(MockMvcRequestBuilders.put("/api/subjects/1")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(new ObjectMapper().writeValueAsString(request)))
//                    .andExpect(MockMvcResultMatchers.status().isOk())
//                    .andExpect(MockMvcResultMatchers.jsonPath("$.subjectName").value("Math"))
//                    .andExpect(MockMvcResultMatchers.jsonPath("$.subjectCode").value("MTH102"));
//        }
//
//        @Test
//    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
//        /**
//         * Kiểm thử phương thức PUT khi môn học không tồn tại.
//         * Xác nhận rằng mã trạng thái là NOT_FOUND và thông báo lỗi được trả về.
//         */
//        void testUpdateSubject_ShouldReturnNotFound_WhenSubjectDoesNotExist() throws Exception {
//            SubjectRequest request = new SubjectRequest("Math", "MTH102", 50);
//
//            when(subjectService.updateSubject(eq(1), any(SubjectRequest.class))).thenThrow(new NotFoundException("Subject not found"));
//
//            mockMvc.perform(MockMvcRequestBuilders.put("/api/subjects/1")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(new ObjectMapper().writeValueAsString(request)))
//                    .andExpect(MockMvcResultMatchers.status().isNotFound());
//        }
//
//        @Test
//    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
//        /**
//         * Kiểm thử phương thức PUT khi dữ liệu không hợp lệ.
//         * Xác nhận rằng mã trạng thái là BAD_REQUEST và thông báo lỗi được trả về.
//         */
//        void testUpdateSubject_ShouldReturnBadRequest_WhenInvalidData() throws Exception {
//            SubjectRequest request = new SubjectRequest(null, "", null);
//
//            mockMvc.perform(MockMvcRequestBuilders.put("/api/subjects/1")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(new ObjectMapper().writeValueAsString(request)))
//                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
//        }
//
//        @Test
//        @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
//        /**
//         * Kiểm thử phương thức DELETE để xóa môn học.
//         * Xác nhận rằng môn học được xóa thành công và thông báo thành công được trả về.
//         */
//        void testDeleteSubject_ShouldReturnNoContent() throws Exception {
//            doNothing().when(subjectService).deleteSubject(1);
//
//            mockMvc.perform(MockMvcRequestBuilders.delete("/api/subjects/1"))
//                    .andExpect(MockMvcResultMatchers.status().isOk())
//                    .andExpect(MockMvcResultMatchers.content().json("{\"message\":\"Subject deleted successfully\"}"));
//        }
//
//        @Test
//    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
//        /**
//         * Kiểm thử phương thức DELETE khi môn học không tồn tại.
//         * Xác nhận rằng mã trạng thái là NOT_FOUND và thông báo lỗi được trả về.
//         */
//        void testDeleteSubject_ShouldReturnNotFound_WhenSubjectDoesNotExist() throws Exception {
//            doThrow(new RuntimeException("Subject not found")).when(subjectService).deleteSubject(1);
//
//            mockMvc.perform(MockMvcRequestBuilders.delete("/api/subjects/1"))
//                    .andExpect(MockMvcResultMatchers.status().isNotFound());
////                    .andExpect(MockMvcResultMatchers.content().string("Subject not found"));
//        }
//
//        @Test
//    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
//        /**
//         * Kiểm thử phương thức GET khi không có môn học nào trong cơ sở dữ liệu.
//         * Xác nhận rằng danh sách môn học trả về là rỗng.
//         */
//        void testGetAllSubjects_ShouldReturnEmptyList_WhenNoSubjects() throws Exception {
//            when(subjectService.findAllSubject()).thenReturn(Collections.emptyList());
//
//            mockMvc.perform(MockMvcRequestBuilders.get("/api/subjects"))
//                    .andExpect(MockMvcResultMatchers.status().isNoContent());
////                    .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0));
//        }
//
//        @Test
//    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
//
//        /**
//         * Kiểm thử phương thức POST khi dữ liệu yêu cầu là null.
//         * Xác nhận rằng mã trạng thái là BAD_REQUEST và thông báo lỗi được trả về.
//         */
//        void testCreateSubject_ShouldHandleNullRequestBody() throws Exception {
//            mockMvc.perform(MockMvcRequestBuilders.post("/api/subjects/add")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content("{}"))
//                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
//        }
//
//        @Test
//    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
//        /**
//         * Kiểm thử phương thức PUT khi dữ liệu yêu cầu là null.
//         * Xác nhận rằng mã trạng thái là BAD_REQUEST và thông báo lỗi được trả về.
//         */
//        void testUpdateSubject_ShouldHandleNullRequestBody() throws Exception {
//            mockMvc.perform(MockMvcRequestBuilders.put("/api/subjects/1")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content("{}"))
//                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
////                    .andExpect(MockMvcResultMatchers.content().string("Invalid data"));
//        }
//
//        @Test
//    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
//        /**
//         * Kiểm thử phương thức DELETE khi ID không hợp lệ (không phải số).
//         * Xác nhận rằng mã trạng thái là BAD_REQUEST và thông báo lỗi được trả về.
//         */
//        void testDeleteSubject_ShouldHandleInvalidIdFormat() throws Exception {
//            mockMvc.perform(MockMvcRequestBuilders.delete("/api/subjects/abc"))
//                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
//        }
//
////        @Test
////    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
//        /**
//         * Kiểm thử việc xử lý các lỗi không mong muốn trong ứng dụng.
//         * Xác nhận rằng mã trạng thái là INTERNAL_SERVER_ERROR và thông báo lỗi được trả về.
//         */
////        void testHandleUnexpectedExceptions() throws Exception {
////            when(subjectService.findAllSubject()).thenThrow(new RuntimeException("Unexpected error"));
////
////            mockMvc.perform(MockMvcRequestBuilders.get("/api/subjects"))
////                    .andExpect(MockMvcResultMatchers.status().isInternalServerError())
////                    .andExpect(MockMvcResultMatchers.content().string("Unexpected error"));
////        }
//
//        @Test
//    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
//        /**
//         * Kiểm thử truy cập vào một endpoint không tồn tại.
//         * Xác nhận rằng mã trạng thái là NOT_FOUND và thông báo lỗi được trả về.
//         */
//        void testInvalidEndpoint_ShouldReturnNotFound() throws Exception {
//            mockMvc.perform(MockMvcRequestBuilders.get("/api/invalid"))
//                    .andExpect(MockMvcResultMatchers.status().isNotFound());
//        }
//
//        @Test
////    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
//        /**
//         * Kiểm thử truy cập mà không có quyền xác thực.
//         * Xác nhận rằng mã trạng thái là UNAUTHORIZED và thông báo lỗi được trả về.
//         */
//        void testAccessWithoutAuthorization_ShouldReturnUnauthorized() throws Exception {
//            mockMvc.perform(MockMvcRequestBuilders.get("/api/subjects"))
//                    .andExpect(MockMvcResultMatchers.status().isForbidden());
////                    .andExpect(MockMvcResultMatchers.content().string("Unauthorized"));
//        }
//
//        @Test
//        /**
//         * Kiểm thử truy cập với quyền không đủ.
//         * Xác nhận rằng mã trạng thái là FORBIDDEN và thông báo lỗi được trả về.
//         */
//        void testAccessWithInsufficientPermissions_ShouldReturnForbidden() throws Exception {
//            mockMvc.perform(MockMvcRequestBuilders.get("/api/subjects"))
//                    .andExpect(MockMvcResultMatchers.status().isForbidden());
////                    .andExpect(MockMvcResultMatchers.content().string("Forbidden"));
//        }
//
//        @Test
//    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
//        /**
//         * Kiểm thử thời gian phản hồi cho phương thức GET để lấy tất cả các môn học.
//         * Xác nhận rằng thời gian phản hồi phải dưới 2 giây.
//         */
//        void testResponseTimeForGetAllSubjects() throws Exception {
//            long startTime = System.currentTimeMillis();
//
//            mockMvc.perform(MockMvcRequestBuilders.get("/api/subjects"))
//                    .andExpect(MockMvcResultMatchers.status().isNoContent());
//
//            long endTime = System.currentTimeMillis();
//            long responseTime = endTime - startTime;
//            assertThat(responseTime).isLessThan(10000);
//        }
//
//        @Test
//    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
//        /**
//         * Kiểm thử truy cập đồng thời vào môn học.
//         * Xác nhận rằng tất cả các yêu cầu đều được xử lý thành công.
//         */
//        void testConcurrentAccessToSubject() throws Exception {
//            ExecutorService executor = Executors.newFixedThreadPool(10);
//            CountDownLatch latch = new CountDownLatch(10);
//
//            Runnable task = () -> {
//                try {
//                    mockMvc.perform(MockMvcRequestBuilders.get("/api/subjects/1"))
//                            .andExpect(MockMvcResultMatchers.status().isOk());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    latch.countDown();
//                }
//            };
//
//            for (int i = 0; i < 10; i++) {
//                executor.submit(task);
//            }
//
//            latch.await();
//            executor.shutdown();
//        }
//    }
