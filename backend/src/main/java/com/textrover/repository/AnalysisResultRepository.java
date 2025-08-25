package com.textrover.repository;

import com.textrover.entity.AnalysisResultEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface AnalysisResultRepository extends JpaRepository<AnalysisResultEntity, Long> {
    
    /**
     * Find all analysis results ordered by creation date descending (latest first)
     */
    Page<AnalysisResultEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    /**
     * Delete all analysis results
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM AnalysisResultEntity")
    void deleteAllResults();
    
    /**
     * Count total number of analysis results
     */
    long count();
}
