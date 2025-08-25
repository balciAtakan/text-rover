package com.textrover.service;

import com.textrover.dto.AnalysisHistoryDTO;
import com.textrover.dto.AnalysisHistoryPageDTO;
import com.textrover.entity.AnalysisResultEntity;
import com.textrover.entity.AnalysisCharacterCountEntity;
import com.textrover.mapper.AnalysisMapper;
import com.textrover.repository.AnalysisResultRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AnalysisHistoryService {
    
    private static final Logger log = LogManager.getLogger(AnalysisHistoryService.class);
    
    private final AnalysisResultRepository analysisResultRepository;
    private final AnalysisMapper analysisMapper;
    
    public AnalysisHistoryService(AnalysisResultRepository analysisResultRepository, AnalysisMapper analysisMapper) {
        this.analysisResultRepository = analysisResultRepository;
        this.analysisMapper = analysisMapper;
    }
    
    /**
     * Save analysis result to database
     */
    @Transactional
    public AnalysisResultEntity saveAnalysisResult(
            String inputText,
            String analysisType,
            String mode,
            Map<Character, Integer> characterCounts,
            Integer totalLetters,
            Integer totalVowels,
            Integer totalConsonants,
            Integer totalDigits,
            Integer totalSymbols,
            Integer wordCount,
            Double vowelPercentage,
            Double consonantPercentage,
            Character mostFrequentCharacter,
            Integer mostFrequentCount) {
        
        // Create the main analysis result entity
        AnalysisResultEntity entity = new AnalysisResultEntity(
                inputText,
                analysisType,
                mode,
                totalLetters,
                totalVowels,
                totalConsonants,
                totalDigits,
                totalSymbols,
                wordCount,
                vowelPercentage,
                consonantPercentage
        );
        
        entity.setMostFrequentCharacter(mostFrequentCharacter);
        entity.setMostFrequentCount(mostFrequentCount);
        
        // Save the main entity first to get the ID
        AnalysisResultEntity saved = analysisResultRepository.save(entity);
        
        // Create character count entities
        List<AnalysisCharacterCountEntity> characterCountEntities = new ArrayList<>();
        for (Map.Entry<Character, Integer> entry : characterCounts.entrySet()) {
            AnalysisCharacterCountEntity countEntity = new AnalysisCharacterCountEntity(
                    saved,
                    entry.getKey(),
                    entry.getValue()
            );
            characterCountEntities.add(countEntity);
        }
        
        saved.setCharacterCounts(characterCountEntities);
        
        log.info("Saved analysis result with ID: {} and {} character counts", 
                saved.getId(), characterCountEntities.size());
        return saved;
    }
    
    /**
     * Get paginated analysis history (latest first)
     */
    public AnalysisHistoryPageDTO getAnalysisHistory(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AnalysisResultEntity> entityPage = analysisResultRepository.findAllByOrderByCreatedAtDesc(pageable);
        
        // Convert entities to DTOs
        List<AnalysisHistoryDTO> dtoList = entityPage.getContent().stream()
                .map(analysisMapper::toHistoryDTO)
                .collect(java.util.stream.Collectors.toList());
        
        return AnalysisHistoryPageDTO.builder()
                .content(dtoList)
                .totalElements((int) entityPage.getTotalElements())
                .totalPages(entityPage.getTotalPages())
                .size(entityPage.getSize())
                .number(entityPage.getNumber())
                .first(entityPage.isFirst())
                .last(entityPage.isLast())
                .hasNext(entityPage.hasNext())
                .build();
    }
    
    /**
     * Delete all analysis history
     */
    @Transactional
    public void deleteAllHistory() {
        long count = analysisResultRepository.count();
        analysisResultRepository.deleteAllResults();
        log.info("Deleted {} analysis results from database", count);
    }
    
    /**
     * Get total count of analysis results
     */
    public long getTotalCount() {
        return analysisResultRepository.count();
    }
}
