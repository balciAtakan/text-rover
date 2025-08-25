package com.textrover.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Internal DTO for analysis requests used within the service layer.
 * Separated from generated DTOs to maintain clean architecture.
 */
@NoArgsConstructor
@Getter
@Setter
public class AnalysisRequestDTO {
    private AnalysisTypeDTO type;
    private String text;
}
