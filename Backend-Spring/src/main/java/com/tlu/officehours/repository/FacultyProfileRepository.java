package com.tlu.officehours.repository;

import com.tlu.officehours.entity.FacultyProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacultyProfileRepository extends JpaRepository<FacultyProfile, Long> {

    Optional<FacultyProfile> findByFacultyUserId(Long userId);

    @Query("SELECT fp FROM FacultyProfile fp JOIN FETCH fp.department LEFT JOIN FETCH fp.user")
    List<FacultyProfile> findAllWithDepartmentAndUser();

    @Query("SELECT fp FROM FacultyProfile fp JOIN FETCH fp.department LEFT JOIN FETCH fp.user " +
           "WHERE fp.departmentId = :departmentId")
    List<FacultyProfile> findByDepartmentIdWithDetails(@Param("departmentId") String departmentId);

    @Query("SELECT fp FROM FacultyProfile fp JOIN FETCH fp.department LEFT JOIN FETCH fp.user " +
           "WHERE LOWER(fp.facultyName) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<FacultyProfile> searchByName(@Param("query") String query);

    @Query("SELECT fp FROM FacultyProfile fp JOIN FETCH fp.department LEFT JOIN FETCH fp.user " +
           "WHERE fp.facultyUserId = :id")
    Optional<FacultyProfile> findByIdWithDetails(@Param("id") Long id);
}
