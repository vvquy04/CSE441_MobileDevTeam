package com.tlu.officehours.repository;

import com.tlu.officehours.entity.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {
    Optional<StudentProfile> findByStudentUserId(Long userId);
    boolean existsByStudentCode(String studentCode);
}
