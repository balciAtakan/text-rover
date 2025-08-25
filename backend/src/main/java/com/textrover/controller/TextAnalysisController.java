package com.textrover.controller;

import com.textrover.dto.generated.AnalysisHistoryResponse;
import com.textrover.dto.generated.AnalysisRequest;
import com.textrover.dto.generated.AnalysisResponse;
import com.textrover.mapper.AnalysisMapper;
import com.textrover.service.AnalysisHistoryService;
import com.textrover.service.TextAnalysisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class TextAnalysisController {

    private static final Logger log = LogManager.getLogger(TextAnalysisController.class);
    private final TextAnalysisService textAnalysisService;
    private final AnalysisHistoryService analysisHistoryService;
    private final AnalysisMapper analysisMapper;

    @PostMapping("/analyze")
    public ResponseEntity<AnalysisResponse> analyzeText(@Valid @RequestBody AnalysisRequest request) {
        log.debug("Request details - Type: {}, Text: {}", request.getType(), request.getText());
        
        try {
            long startTime = System.currentTimeMillis();

            var internalRequest = analysisMapper.toInternal(request);
            var internalResponse = textAnalysisService.analyzeText(internalRequest);

            AnalysisResponse response = analysisMapper.toGenerated(internalResponse);
            
            long responseTime = System.currentTimeMillis() - startTime;

            Map<String, Integer> stringKeyMap = response.getResult();
            
            // Convert String keys to Character keys for the service
            Map<Character, Integer> characterKeyMap = stringKeyMap.entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(
                    entry -> entry.getKey().charAt(0), 
                    Map.Entry::getValue
                ));
            
            // Calculate additional statistics from internal response
            var stats = internalResponse.getStatistics();
            
            analysisHistoryService.saveAnalysisResult(
                request.getText(),
                request.getType().toString(),
                "online", // This is from REST API
                characterKeyMap,
                stats.getTotalLetters(),
                stats.getTotalVowels(),
                stats.getTotalConsonants(),
                stats.getTotalDigits(),
                stats.getTotalSymbols(),
                stats.getWordCount(),
                stats.getVowelPercentage(),
                stats.getConsonantPercentage(),
                stats.getMostFrequentCharacter(),
                stats.getMostFrequentCount()
            );
            
            log.info("Successfully processed analysis request in {}ms", responseTime);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid request: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error processing analysis request: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    @GetMapping("/history")
    public ResponseEntity<AnalysisHistoryResponse> getAnalysisHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.debug("Fetching analysis history - page: {}, size: {}", page, size);
        
        var historyPageDTO = analysisHistoryService.getAnalysisHistory(page, size);
        
        // Map DTO to generated response
        AnalysisHistoryResponse response = analysisMapper.toGeneratedHistoryResponse(historyPageDTO);
        
        log.info("Retrieved {} analysis results from database", historyPageDTO.getContent().size());
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/history")
    public ResponseEntity<Map<String, String>> deleteAllHistory() {
        log.info("Deleting all analysis history");
        
        analysisHistoryService.deleteAllHistory();
        
        return ResponseEntity.ok(Map.of("message", "All analysis history deleted successfully"));
    }
}
