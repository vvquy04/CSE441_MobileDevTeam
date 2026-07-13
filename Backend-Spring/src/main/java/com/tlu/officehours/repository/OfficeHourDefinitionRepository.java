package com.tlu.officehours.repository;

import com.tlu.officehours.entity.OfficeHourDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfficeHourDefinitionRepository extends JpaRepository<OfficeHourDefinition, Long> {
}
