package com.textrover.service;

import com.textrover.dto.AnalysisHistoryDTO;
import com.textrover.dto.AnalysisHistoryPageDTO;
import com.textrover.entity.AnalysisCharacterCountEntity;
import com.textrover.entity.AnalysisResultEntity;
import com.textrover.mapper.AnalysisMapper;
import com.textrover.repository.AnalysisResultRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalysisHistoryServiceTest {

    @Mock
    private AnalysisResultRepository analysisResultRepository;

    @Mock
    private AnalysisMapper analysisMapper;

    @InjectMocks
    private AnalysisHistoryService analysisHistoryService;

    private AnalysisResultEntity sampleEntity;
    private AnalysisHistoryDTO sampleHistoryDTO;
    private Map<Character, Integer> sampleCharacterCounts;

    @BeforeEach
    void setUp() {
        sampleCharacterCounts = new HashMap<>();
        sampleCharacterCounts.put('a', 3);
        sampleCharacterCounts.put('e', 2);
        sampleCharacterCounts.put('o', 1);

        sampleEntity = new AnalysisResultEntity(
                "Hello World",
                "VOWELS",
                "online",
                10,
                6,
                4,
                0,
                1,
                2,
                60.0,
                40.0
        );
        sampleEntity.setId(1L);
        sampleEntity.setMostFrequentCharacter('l');
        sampleEntity.setMostFrequentCount(3);
        sampleEntity.setCreatedAt(OffsetDateTime.now());

        List<AnalysisCharacterCountEntity> characterCounts = new ArrayList<>();
        for (Map.Entry<Character, Integer> entry : sampleCharacterCounts.entrySet()) {
            AnalysisCharacterCountEntity countEntity = new AnalysisCharacterCountEntity(
                    sampleEntity,
                    entry.getKey(),
                    entry.getValue()
            );
            characterCounts.add(countEntity);
        }
        sampleEntity.setCharacterCounts(characterCounts);

        sampleHistoryDTO = AnalysisHistoryDTO.builder()
                .id(1L)
                .inputText("Hello World")
                .analysisType("VOWELS")
                .mode("online")
                .totalLetters(10)
                .totalVowels(6)
                .totalConsonants(4)
                .totalDigits(0)
                .totalSymbols(1)
                .wordCount(2)
                .vowelPercentage(60.0)
                .consonantPercentage(40.0)
                .mostFrequentCharacter('l')
                .mostFrequentCount(3)
                .createdAt(OffsetDateTime.now())
                .characterCounts(sampleCharacterCounts)
                .build();
    }

    @Test
    void saveAnalysisResult_shouldSaveSuccessfully() {
        // Given
        when(analysisResultRepository.save(any(AnalysisResultEntity.class)))
                .thenReturn(sampleEntity);

        // When
        AnalysisResultEntity result = analysisHistoryService.saveAnalysisResult(
                "Hello World",
                "VOWELS",
                "online",
                sampleCharacterCounts,
                10,
                6,
                4,
                0,
                1,
                2,
                60.0,
                40.0,
                'l',
                3
        );

        // Then
        assertNotNull(result);
        assertEquals("Hello World", result.getInputText());
        assertEquals("VOWELS", result.getAnalysisType());
        assertEquals("online", result.getMode());
        assertEquals(10, result.getTotalLetters());
        assertEquals(6, result.getTotalVowels());
        assertEquals(4, result.getTotalConsonants());
        assertEquals('l', result.getMostFrequentCharacter());
        assertEquals(3, result.getMostFrequentCount());
        assertEquals(3, result.getCharacterCounts().size());

        verify(analysisResultRepository, times(1)).save(any(AnalysisResultEntity.class));
    }

    @Test
    void saveAnalysisResult_withEmptyCharacterCounts_shouldSaveSuccessfully() {
        // Given
        Map<Character, Integer> emptyCharacterCounts = new HashMap<>();
        AnalysisResultEntity entityWithEmptyCounts = new AnalysisResultEntity(
                "123!@#",
                "VOWELS",
                "offline",
                0, 0, 0, 3, 3, 1,
                0.0, 0.0
        );
        entityWithEmptyCounts.setCharacterCounts(new ArrayList<>());

        when(analysisResultRepository.save(any(AnalysisResultEntity.class)))
                .thenReturn(entityWithEmptyCounts);

        // When
        AnalysisResultEntity result = analysisHistoryService.saveAnalysisResult(
                "123!@#",
                "VOWELS",
                "offline",
                emptyCharacterCounts,
                0, 0, 0, 3, 3, 1,
                0.0, 0.0,
                null, 0
        );

        // Then
        assertNotNull(result);
        assertEquals("123!@#", result.getInputText());
        assertEquals(0, result.getCharacterCounts().size());
        verify(analysisResultRepository, times(1)).save(any(AnalysisResultEntity.class));
    }

    @Test
    void getAnalysisHistory_shouldReturnPaginatedResults() {
        // Given
        List<AnalysisResultEntity> entities = List.of(sampleEntity);
        Page<AnalysisResultEntity> page = new PageImpl<>(entities, PageRequest.of(0, 10), 1);
        
        when(analysisResultRepository.findAllByOrderByCreatedAtDesc(any(Pageable.class)))
                .thenReturn(page);
        when(analysisMapper.toHistoryDTO(sampleEntity))
                .thenReturn(sampleHistoryDTO);

        // When
        AnalysisHistoryPageDTO result = analysisHistoryService.getAnalysisHistory(0, 10);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(10, result.getSize());
        assertEquals(0, result.getNumber());
        assertTrue(result.getFirst());
        assertTrue(result.getLast());
        assertFalse(result.getHasNext());

        AnalysisHistoryDTO dto = result.getContent().get(0);
        assertEquals(sampleHistoryDTO.getId(), dto.getId());
        assertEquals(sampleHistoryDTO.getInputText(), dto.getInputText());
        assertEquals(sampleHistoryDTO.getAnalysisType(), dto.getAnalysisType());

        verify(analysisResultRepository, times(1))
                .findAllByOrderByCreatedAtDesc(any(Pageable.class));
        verify(analysisMapper, times(1)).toHistoryDTO(sampleEntity);
    }

    @Test
    void getAnalysisHistory_withMultiplePages_shouldReturnCorrectPageInfo() {
        // Given
        List<AnalysisResultEntity> entities = List.of(sampleEntity);
        Page<AnalysisResultEntity> page = new PageImpl<>(entities, PageRequest.of(1, 5), 15);
        
        when(analysisResultRepository.findAllByOrderByCreatedAtDesc(any(Pageable.class)))
                .thenReturn(page);
        when(analysisMapper.toHistoryDTO(sampleEntity))
                .thenReturn(sampleHistoryDTO);

        // When
        AnalysisHistoryPageDTO result = analysisHistoryService.getAnalysisHistory(1, 5);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(15, result.getTotalElements());
        assertEquals(3, result.getTotalPages());
        assertEquals(5, result.getSize());
        assertEquals(1, result.getNumber());
        assertFalse(result.getFirst());
        assertFalse(result.getLast());
        assertTrue(result.getHasNext());
    }

    @Test
    void getAnalysisHistory_withEmptyResults_shouldReturnEmptyPage() {
        // Given
        Page<AnalysisResultEntity> emptyPage = new PageImpl<>(new ArrayList<>(), PageRequest.of(0, 10), 0);
        
        when(analysisResultRepository.findAllByOrderByCreatedAtDesc(any(Pageable.class)))
                .thenReturn(emptyPage);

        // When
        AnalysisHistoryPageDTO result = analysisHistoryService.getAnalysisHistory(0, 10);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getContent().size());
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
        assertTrue(result.getFirst());
        assertTrue(result.getLast());
        assertFalse(result.getHasNext());

        verify(analysisResultRepository, times(1))
                .findAllByOrderByCreatedAtDesc(any(Pageable.class));
        verify(analysisMapper, never()).toHistoryDTO(any());
    }

    @Test
    void deleteAllHistory_shouldDeleteAllRecords() {
        // Given
        when(analysisResultRepository.count()).thenReturn(5L);

        // When
        analysisHistoryService.deleteAllHistory();

        // Then
        verify(analysisResultRepository, times(1)).count();
        verify(analysisResultRepository, times(1)).deleteAllResults();
    }

    @Test
    void deleteAllHistory_withNoRecords_shouldStillCallDelete() {
        // Given
        when(analysisResultRepository.count()).thenReturn(0L);

        // When
        analysisHistoryService.deleteAllHistory();

        // Then
        verify(analysisResultRepository, times(1)).count();
        verify(analysisResultRepository, times(1)).deleteAllResults();
    }

    @Test
    void getTotalCount_shouldReturnCorrectCount() {
        // Given
        when(analysisResultRepository.count()).thenReturn(42L);

        // When
        long result = analysisHistoryService.getTotalCount();

        // Then
        assertEquals(42L, result);
        verify(analysisResultRepository, times(1)).count();
    }

    @Test
    void getTotalCount_withZeroRecords_shouldReturnZero() {
        // Given
        when(analysisResultRepository.count()).thenReturn(0L);

        // When
        long result = analysisHistoryService.getTotalCount();

        // Then
        assertEquals(0L, result);
        verify(analysisResultRepository, times(1)).count();
    }

    @Test
    void saveAnalysisResult_withNullMostFrequentCharacter_shouldHandleGracefully() {
        // Given
        AnalysisResultEntity entityWithNullChar = new AnalysisResultEntity(
                "123",
                "VOWELS",
                "online",
                0, 0, 0, 3, 0, 1,
                0.0, 0.0
        );
        entityWithNullChar.setMostFrequentCharacter(null);
        entityWithNullChar.setMostFrequentCount(0);
        entityWithNullChar.setCharacterCounts(new ArrayList<>());

        when(analysisResultRepository.save(any(AnalysisResultEntity.class)))
                .thenReturn(entityWithNullChar);

        // When
        AnalysisResultEntity result = analysisHistoryService.saveAnalysisResult(
                "123",
                "VOWELS",
                "online",
                new HashMap<>(),
                0, 0, 0, 3, 0, 1,
                0.0, 0.0,
                null, 0
        );

        // Then
        assertNotNull(result);
        assertNull(result.getMostFrequentCharacter());
        assertEquals(0, result.getMostFrequentCount());
    }

    @Test
    void saveAnalysisResult_withLargeCharacterCounts_shouldHandleEfficiently() {
        // Given
        Map<Character, Integer> largeCharacterCounts = new HashMap<>();
        for (char c = 'a'; c <= 'z'; c++) {
            largeCharacterCounts.put(c, (int) (c - 'a' + 1));
        }

        AnalysisResultEntity entityWithLargeCounts = new AnalysisResultEntity(
                "Large text with many characters",
                "CONSONANTS",
                "offline",
                100, 30, 70, 0, 5, 6,
                30.0, 70.0
        );
        
        List<AnalysisCharacterCountEntity> largeCounts = new ArrayList<>();
        for (Map.Entry<Character, Integer> entry : largeCharacterCounts.entrySet()) {
            largeCounts.add(new AnalysisCharacterCountEntity(
                    entityWithLargeCounts, entry.getKey(), entry.getValue()));
        }
        entityWithLargeCounts.setCharacterCounts(largeCounts);

        when(analysisResultRepository.save(any(AnalysisResultEntity.class)))
                .thenReturn(entityWithLargeCounts);

        // When
        AnalysisResultEntity result = analysisHistoryService.saveAnalysisResult(
                "Large text with many characters",
                "CONSONANTS",
                "offline",
                largeCharacterCounts,
                100, 30, 70, 0, 5, 6,
                30.0, 70.0,
                'z', 26
        );

        // Then
        assertNotNull(result);
        assertEquals(26, result.getCharacterCounts().size());
        assertEquals("CONSONANTS", result.getAnalysisType());
    }

    @Test
    void getAnalysisHistory_withDifferentPageSizes_shouldRespectPageSize() {
        // Given
        List<AnalysisResultEntity> entities = List.of(sampleEntity);
        Page<AnalysisResultEntity> page = new PageImpl<>(entities, PageRequest.of(0, 1), 10);
        
        when(analysisResultRepository.findAllByOrderByCreatedAtDesc(any(Pageable.class)))
                .thenReturn(page);
        when(analysisMapper.toHistoryDTO(sampleEntity))
                .thenReturn(sampleHistoryDTO);

        // When
        AnalysisHistoryPageDTO result = analysisHistoryService.getAnalysisHistory(0, 1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getSize());
        assertEquals(10, result.getTotalPages());
        
        verify(analysisResultRepository, times(1))
                .findAllByOrderByCreatedAtDesc(eq(PageRequest.of(0, 1)));
    }
}
