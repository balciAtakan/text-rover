package com.textrover.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * Internal DTO for analysis responses used within the service layer.
 * Separated from generated DTOs to maintain clean architecture.
 */
@NoArgsConstructor
@Getter
@Setter
public class AnalysisResponseDTO {
    private AnalysisTypeDTO type;
    private String text;
    private Map<Character, Integer> result;
    private AnalysisStatisticsDTO statistics;
}
