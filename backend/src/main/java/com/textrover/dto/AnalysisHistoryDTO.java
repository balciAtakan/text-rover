package com.textrover.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisHistoryDTO {
    
    private Long id;
    private String inputText;
    private String analysisType;
    private String mode;
    private OffsetDateTime createdAt;
    
    // Statistics
    private Integer totalLetters;
    private Integer totalVowels;
    private Integer totalConsonants;
    private Integer totalDigits;
    private Integer totalSymbols;
    private Integer wordCount;
    private Double vowelPercentage;
    private Double consonantPercentage;
    private Character mostFrequentCharacter;
    private Integer mostFrequentCount;
    
    // Character counts
    private Map<Character, Integer> characterCounts;
}
