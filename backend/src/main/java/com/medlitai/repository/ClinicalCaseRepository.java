package com.medlitai.repository;

import com.medlitai.model.ClinicalCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClinicalCaseRepository extends JpaRepository<ClinicalCase, Long> {
    List<ClinicalCase> findAllByOrderByCreatedAtDesc();
    List<ClinicalCase> findByIsDemoCaseTrue();
}
