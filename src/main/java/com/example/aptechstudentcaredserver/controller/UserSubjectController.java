package com.example.aptechstudentcaredserver.controller;

import com.example.aptechstudentcaredserver.service.SubjectService;
import com.example.aptechstudentcaredserver.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/teacherSubjects")
@AllArgsConstructor
public class UserSubjectController {

    private final UserService userService;
    private final SubjectService subjectService;

//    @PostMapping
//    public ResponseEntity<Void> assignSubjectToUser(@RequestBody UserSubjectRequest request) {
//        try {
//            userService.assignSubjectToTeacher(request.getUserId(), request.getSubjectId());
//            return ResponseEntity.ok().build();
//        } catch (NotFoundException e) {
//            return ResponseEntity.notFound().build();
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
}

