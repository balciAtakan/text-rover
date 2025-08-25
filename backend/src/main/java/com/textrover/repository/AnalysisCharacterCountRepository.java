package com.textrover.repository;

import com.textrover.entity.AnalysisCharacterCountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalysisCharacterCountRepository extends JpaRepository<AnalysisCharacterCountEntity, Long> {
    
    /**
     * Delete all character counts for a specific analysis result
     */
    void deleteByAnalysisResultId(Long analysisResultId);
}
