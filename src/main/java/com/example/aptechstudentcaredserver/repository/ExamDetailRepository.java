package com.example.aptechstudentcaredserver.repository;

import com.example.aptechstudentcaredserver.entity.ExamDetail;
import com.example.aptechstudentcaredserver.enums.MarkType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExamDetailRepository extends JpaRepository<ExamDetail, Integer> {
    Optional<ExamDetail> findByUserIdAndSubjectIdAndExamType(int userId, int subjectId, MarkType examType);
    Optional<ExamDetail> findByUserIdAndExamType(int userId, MarkType examType);

    Optional<ExamDetail> findByUserIdAndExamTypeAndSubjectId(int userId, MarkType examType, int subjectId);

}
