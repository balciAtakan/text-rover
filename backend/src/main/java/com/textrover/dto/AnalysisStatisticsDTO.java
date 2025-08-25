package com.textrover.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Internal DTO for analysis statistics used within the service layer.
 * Separated from generated DTOs to maintain clean architecture.
 */
@NoArgsConstructor
@Getter
@Setter
public class AnalysisStatisticsDTO {

    private int totalLetters;
    private int totalVowels;
    private int totalConsonants;
    private int totalDigits;
    private int totalSymbols;
    private int wordCount;
    private double vowelPercentage;
    private double consonantPercentage;
    private Character mostFrequentCharacter;
    private int mostFrequentCount;
}
