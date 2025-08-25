package com.textrover.mapper;

import com.textrover.dto.AnalysisRequestDTO;
import com.textrover.dto.AnalysisResponseDTO;
import com.textrover.dto.AnalysisStatisticsDTO;
import com.textrover.dto.AnalysisTypeDTO;
import com.textrover.dto.AnalysisHistoryDTO;
import com.textrover.dto.AnalysisHistoryPageDTO;
import com.textrover.dto.generated.AnalysisRequest;
import com.textrover.dto.generated.AnalysisResponse;
import com.textrover.dto.generated.AnalysisStatistics;
import com.textrover.dto.generated.AnalysisType;
import com.textrover.dto.generated.AnalysisHistoryResponse;
import com.textrover.dto.generated.AnalysisHistoryItem;
import com.textrover.entity.AnalysisResultEntity;
import com.textrover.entity.AnalysisCharacterCountEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Mapper class to convert between generated DTOs (API layer) and internal DTOs (service layer).
 * This separation ensures clean architecture and independence from generated code.
 */
@Component
public class AnalysisMapper {

    /**
     * Convert generated AnalysisRequest to internal AnalysisRequestInternal
     */
    public AnalysisRequestDTO toInternal(AnalysisRequest request) {
        if (request == null) {
            return null;
        }

        AnalysisRequestDTO internal = new AnalysisRequestDTO();
        internal.setType(toInternal(request.getType()));
        internal.setText(request.getText());
        return internal;
    }

    /**
     * Convert internal AnalysisResponseInternal to generated AnalysisResponse
     */
    public AnalysisResponse toGenerated(AnalysisResponseDTO internal) {
        if (internal == null) {
            return null;
        }

        AnalysisResponse response = new AnalysisResponse();
        response.setType(toGenerated(internal.getType()));
        response.setText(internal.getText());

        // Convert Character keys to String keys for generated DTO
        if (internal.getResult() != null) {
            Map<String, Integer> stringResult = internal.getResult().entrySet().stream()
                    .collect(Collectors.toMap(
                            entry -> entry.getKey().toString(),
                            Map.Entry::getValue
                    ));
            response.setResult(stringResult);
        }

        response.setStatistics(toGenerated(internal.getStatistics()));
        return response;
    }

    /**
     * Convert generated AnalysisType to internal AnalysisTypeInternal
     */
    public AnalysisTypeDTO toInternal(AnalysisType type) {
        if (type == null) {
            return null;
        }

        return switch (type) {
            case VOWELS -> AnalysisTypeDTO.VOWELS;
            case CONSONANTS -> AnalysisTypeDTO.CONSONANTS;
            default -> throw new IllegalArgumentException("Unknown AnalysisType: " + type);
        };
    }

    /**
     * Convert internal AnalysisTypeInternal to generated AnalysisType
     */
    public AnalysisResponse.TypeEnum toGenerated(AnalysisTypeDTO type) {
        if (type == null) {
            return null;
        }

        return switch (type) {
            case VOWELS -> AnalysisResponse.TypeEnum.VOWELS;
            case CONSONANTS -> AnalysisResponse.TypeEnum.CONSONANTS;
            default -> throw new IllegalArgumentException("Unknown AnalysisTypeInternal: " + type);
        };
    }

    /**
     * Convert internal AnalysisStatisticsInternal to generated AnalysisStatistics
     */
    public AnalysisStatistics toGenerated(AnalysisStatisticsDTO internal) {
        if (internal == null) {
            return null;
        }

        AnalysisStatistics stats = new AnalysisStatistics();
        stats.setTotalLetters(internal.getTotalLetters());
        stats.setTotalVowels(internal.getTotalVowels());
        stats.setTotalConsonants(internal.getTotalConsonants());
        stats.setTotalDigits(internal.getTotalDigits());

        stats.setTotalSymbols(internal.getTotalSymbols());
        stats.setWordCount(internal.getWordCount());
        stats.setVowelPercentage(internal.getVowelPercentage());
        stats.setConsonantPercentage(internal.getConsonantPercentage());
        stats.setMostFrequentCharacter(String.valueOf(internal.getMostFrequentCharacter()));
        stats.setMostFrequentCount(internal.getMostFrequentCount());

        return stats;
    }

    /**
     * Convert generated AnalysisStatistics to internal AnalysisStatisticsInternal
     */
    public AnalysisStatisticsDTO toInternal(AnalysisStatistics stats) {
        if (stats == null) {
            return null;
        }

        AnalysisStatisticsDTO internal = new AnalysisStatisticsDTO();
        internal.setTotalLetters(stats.getTotalLetters());
        internal.setTotalVowels(stats.getTotalVowels());
        internal.setTotalConsonants(stats.getTotalConsonants());
        internal.setTotalDigits(stats.getTotalDigits());

        return internal;
    }

    /**
     * Convert AnalysisResultEntity to AnalysisHistoryDTO
     */
    public AnalysisHistoryDTO toHistoryDTO(AnalysisResultEntity entity) {
        if (entity == null) {
            return null;
        }

        // Convert character counts from entities to map
        Map<Character, Integer> characterCounts = entity.getCharacterCounts().stream()
                .collect(Collectors.toMap(
                        AnalysisCharacterCountEntity::getCharacter,
                        AnalysisCharacterCountEntity::getCount
                ));

        return AnalysisHistoryDTO.builder()
                .id(entity.getId())
                .inputText(entity.getInputText())
                .analysisType(entity.getAnalysisType())
                .mode(entity.getMode())
                .createdAt(entity.getCreatedAt())
                .totalLetters(entity.getTotalLetters())
                .totalVowels(entity.getTotalVowels())
                .totalConsonants(entity.getTotalConsonants())
                .totalDigits(entity.getTotalDigits())
                .totalSymbols(entity.getTotalSymbols())
                .wordCount(entity.getWordCount())
                .vowelPercentage(entity.getVowelPercentage())
                .consonantPercentage(entity.getConsonantPercentage())
                .mostFrequentCharacter(entity.getMostFrequentCharacter())
                .mostFrequentCount(entity.getMostFrequentCount())
                .characterCounts(characterCounts)
                .build();
    }

    /**
     * Convert AnalysisHistoryDTO to AnalysisHistoryItem (generated)
     */
    public AnalysisHistoryItem toGeneratedHistoryItem(AnalysisHistoryDTO dto) {
        if (dto == null) {
            return null;
        }

        AnalysisHistoryItem item = new AnalysisHistoryItem();
        item.setId(dto.getId());
        item.setInputText(dto.getInputText());
        
        // Convert analysis type string to enum
        if (dto.getAnalysisType() != null) {
            item.setAnalysisType(AnalysisHistoryItem.AnalysisTypeEnum.fromValue(dto.getAnalysisType().toLowerCase()));
        }
        
        // Convert mode string to enum
        if (dto.getMode() != null) {
            item.setMode(AnalysisHistoryItem.ModeEnum.fromValue(dto.getMode().toLowerCase()));
        }
        
        item.setCreatedAt(dto.getCreatedAt());

        // Convert Character keys to String keys for generated DTO
        if (dto.getCharacterCounts() != null) {
            Map<String, Integer> stringResult = dto.getCharacterCounts().entrySet().stream()
                    .collect(Collectors.toMap(
                            entry -> entry.getKey().toString(),
                            Map.Entry::getValue
                    ));
            item.setCharacterCounts(stringResult);
        }

        // Create statistics object
        AnalysisStatistics stats = new AnalysisStatistics();
        stats.setTotalLetters(dto.getTotalLetters());
        stats.setTotalVowels(dto.getTotalVowels());
        stats.setTotalConsonants(dto.getTotalConsonants());
        stats.setTotalDigits(dto.getTotalDigits());
        stats.setTotalSymbols(dto.getTotalSymbols());
        stats.setWordCount(dto.getWordCount());
        stats.setVowelPercentage(dto.getVowelPercentage());
        stats.setConsonantPercentage(dto.getConsonantPercentage());
        
        if (dto.getMostFrequentCharacter() != null) {
            stats.setMostFrequentCharacter(dto.getMostFrequentCharacter().toString());
        }
        stats.setMostFrequentCount(dto.getMostFrequentCount());
        
        item.setStatistics(stats);

        return item;
    }

    /**
     * Convert AnalysisHistoryPageDTO to AnalysisHistoryResponse (generated)
     */
    public AnalysisHistoryResponse toGeneratedHistoryResponse(AnalysisHistoryPageDTO pageDTO) {
        if (pageDTO == null) {
            return null;
        }

        AnalysisHistoryResponse response = new AnalysisHistoryResponse();
        
        if (pageDTO.getContent() != null) {
            List<AnalysisHistoryItem> items = pageDTO.getContent().stream()
                    .map(this::toGeneratedHistoryItem)
                    .collect(Collectors.toList());
            response.setContent(items);
        }
        
        response.setTotalElements(pageDTO.getTotalElements());
        response.setTotalPages(pageDTO.getTotalPages());
        response.setSize(pageDTO.getSize());
        response.setNumber(pageDTO.getNumber());
        response.setFirst(pageDTO.getFirst());
        response.setLast(pageDTO.getLast());
        response.setHasNext(pageDTO.getHasNext());

        return response;
    }
}
