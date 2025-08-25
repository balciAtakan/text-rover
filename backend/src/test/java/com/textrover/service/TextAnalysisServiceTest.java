package com.textrover.service;

import com.textrover.dto.AnalysisRequestDTO;
import com.textrover.dto.AnalysisResponseDTO;
import com.textrover.dto.AnalysisStatisticsDTO;
import com.textrover.dto.AnalysisTypeDTO;
import com.textrover.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TextAnalysisServiceTest {

    private TextAnalysisService textAnalysisService;

    @BeforeEach
    void setUp() {
        textAnalysisService = new TextAnalysisService();
    }

    @Test
    void analyzeText_vowels_shouldCountVowelsCorrectly() {
        // Given
        String text = "Hello World";
        AnalysisRequestDTO request = new AnalysisRequestDTO();
        request.setType(AnalysisTypeDTO.VOWELS);
        request.setText(text);

        // When
        AnalysisResponseDTO response = textAnalysisService.analyzeText(request);

        // Then
        assertEquals(AnalysisTypeDTO.VOWELS, response.getType());
        assertEquals(text, response.getText());
        assertEquals(1, response.getResult().get('e'));
        assertEquals(2, response.getResult().get('o'));
        assertNull(response.getResult().get('h')); // consonant should not be included
    }

    @Test
    void analyzeText_consonants_shouldCountConsonantsCorrectly() {
        // Given
        String text = "Hello World";
        AnalysisRequestDTO request = new AnalysisRequestDTO();
        request.setType(AnalysisTypeDTO.CONSONANTS);
        request.setText(text);

        // When
        AnalysisResponseDTO response = textAnalysisService.analyzeText(request);

        // Then
        assertEquals(AnalysisTypeDTO.CONSONANTS, response.getType());
        assertEquals(text, response.getText());
        assertEquals(1, response.getResult().get('h'));
        assertEquals(3, response.getResult().get('l'));
        assertEquals(1, response.getResult().get('w'));
        assertEquals(1, response.getResult().get('r'));
        assertEquals(1, response.getResult().get('d'));
        assertNull(response.getResult().get('e')); // vowel should not be included
    }

    @Test
    void analyzeText_emptyText_shouldThrowValidationException() {
        // Given
        AnalysisRequestDTO request = new AnalysisRequestDTO();
        request.setType(AnalysisTypeDTO.VOWELS);
        request.setText("");

        // When & Then
        assertThrows(ValidationException.class, 
                () -> textAnalysisService.analyzeText(request));
    }

    @Test
    void analyzeText_noTargetLetters_shouldReturnEmptyResult() {
        // Given
        String text = "xyz123!@#";
        AnalysisRequestDTO request = new AnalysisRequestDTO();
        request.setType(AnalysisTypeDTO.VOWELS);
        request.setText(text);

        // When
        AnalysisResponseDTO response = textAnalysisService.analyzeText(request);

        // Then
        assertEquals(AnalysisTypeDTO.VOWELS, response.getType());
        assertEquals(text, response.getText());
        assertTrue(response.getResult().isEmpty());
    }

    @Test
    void analyzeText_mixedCase_shouldBeCaseInsensitive() {
        // Given
        String text = "AeIoU";
        AnalysisRequestDTO request = new AnalysisRequestDTO();
        request.setType(AnalysisTypeDTO.VOWELS);
        request.setText(text);

        // When
        AnalysisResponseDTO response = textAnalysisService.analyzeText(request);

        // Then
        assertEquals(AnalysisTypeDTO.VOWELS, response.getType());
        assertEquals(text, response.getText());
        assertEquals(1, response.getResult().get('a'));
        assertEquals(1, response.getResult().get('e'));
        assertEquals(1, response.getResult().get('i'));
        assertEquals(1, response.getResult().get('o'));
        assertEquals(1, response.getResult().get('u'));
    }

    @Test
    void analyzeText_numbersAndSpecialChars_shouldIgnoreNonLetters() {
        // Given
        String text = "a1b2c3!@#e";
        AnalysisRequestDTO request = new AnalysisRequestDTO();
        request.setType(AnalysisTypeDTO.VOWELS);
        request.setText(text);

        // When
        AnalysisResponseDTO response = textAnalysisService.analyzeText(request);

        // Then
        assertEquals(AnalysisTypeDTO.VOWELS, response.getType());
        assertEquals(text, response.getText());
        assertEquals(1, response.getResult().get('a'));
        assertEquals(1, response.getResult().get('e'));
        assertEquals(2, response.getResult().size());
    }

    @Test
    void analyzeText_repeatedLetters_shouldCountCorrectly() {
        // Given
        String text = "aaaaeeeeiiiioooo";
        AnalysisRequestDTO request = new AnalysisRequestDTO();
        request.setType(AnalysisTypeDTO.VOWELS);
        request.setText(text);

        // When
        AnalysisResponseDTO response = textAnalysisService.analyzeText(request);

        // Then
        assertEquals(AnalysisTypeDTO.VOWELS, response.getType());
        assertEquals(text, response.getText());
        assertEquals(4, response.getResult().get('a'));
        assertEquals(4, response.getResult().get('e'));
        assertEquals(4, response.getResult().get('i'));
        assertEquals(4, response.getResult().get('o'));
    }

    @Test
    void analyzeText_nullText_shouldThrowValidationException() {
        // Given
        AnalysisRequestDTO request = new AnalysisRequestDTO();
        request.setType(AnalysisTypeDTO.VOWELS);
        request.setText(null);

        // When & Then
        assertThrows(ValidationException.class,
                () -> textAnalysisService.analyzeText(request));
    }

    @Test
    void analyzeText_nullRequest_shouldThrowValidationException() {
        // When & Then
        assertThrows(ValidationException.class,
                () -> textAnalysisService.analyzeText(null));
    }

    @Test
    void analyzeText_whitespaceOnlyText_shouldThrowValidationException() {
        // Given
        AnalysisRequestDTO request = new AnalysisRequestDTO();
        request.setType(AnalysisTypeDTO.VOWELS);
        request.setText("   \t\n  ");

        // When & Then
        assertThrows(ValidationException.class,
                () -> textAnalysisService.analyzeText(request));
    }

    @Test
    void analyzeText_extendedVowels_shouldCountAccentedVowels() {
        // Given
        String text = "café résumé naïve";
        AnalysisRequestDTO request = new AnalysisRequestDTO();
        request.setType(AnalysisTypeDTO.VOWELS);
        request.setText(text);

        // When
        AnalysisResponseDTO response = textAnalysisService.analyzeText(request);

        // Then
        assertEquals(2, response.getResult().get('a'));
        assertEquals(3, response.getResult().get('é'));
        assertEquals(1, response.getResult().get('e'));
        assertEquals(1, response.getResult().get('ï'));
    }

    @Test
    void analyzeText_extendedConsonants_shouldCountAccentedConsonants() {
        // Given
        String text = "niño señor";
        AnalysisRequestDTO request = new AnalysisRequestDTO();
        request.setType(AnalysisTypeDTO.CONSONANTS);
        request.setText(text);

        // When
        AnalysisResponseDTO response = textAnalysisService.analyzeText(request);

        // Then
        assertEquals(1, response.getResult().get('n'));
        assertEquals(2, response.getResult().get('ñ'));
        assertEquals(1, response.getResult().get('s'));
        assertEquals(1, response.getResult().get('r'));
    }

    @Test
    void analyzeText_shouldIncludeStatistics() {
        // Given
        String text = "Hello World! 123";
        AnalysisRequestDTO request = new AnalysisRequestDTO();
        request.setType(AnalysisTypeDTO.VOWELS);
        request.setText(text);

        // When
        AnalysisResponseDTO response = textAnalysisService.analyzeText(request);

        // Then
        AnalysisStatisticsDTO stats = response.getStatistics();
        assertNotNull(stats);
        assertEquals(10, stats.getTotalLetters());
        assertEquals(3, stats.getTotalVowels());
        assertEquals(7, stats.getTotalConsonants());
        assertEquals(3, stats.getTotalDigits());
        assertEquals(1, stats.getTotalSymbols());
        assertEquals(3, stats.getWordCount());
        assertTrue(stats.getVowelPercentage() > 0);
        assertTrue(stats.getConsonantPercentage() > 0);
        assertNotNull(stats.getMostFrequentCharacter());
        assertTrue(stats.getMostFrequentCount() > 0);
    }

    @Test
    void analyzeText_singleCharacter_shouldAnalyzeCorrectly() {
        // Given
        String text = "a";
        AnalysisRequestDTO request = new AnalysisRequestDTO();
        request.setType(AnalysisTypeDTO.VOWELS);
        request.setText(text);

        // When
        AnalysisResponseDTO response = textAnalysisService.analyzeText(request);

        // Then
        assertEquals(1, response.getResult().get('a'));
        assertEquals(1, response.getResult().size());
        assertEquals(1, response.getStatistics().getTotalLetters());
        assertEquals(1, response.getStatistics().getTotalVowels());
        assertEquals(0, response.getStatistics().getTotalConsonants());
    }

    @Test
    void analyzeText_longText_shouldHandleEfficiently() {
        // Given
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("Hello World ");
        }
        String text = sb.toString();
        AnalysisRequestDTO request = new AnalysisRequestDTO();
        request.setType(AnalysisTypeDTO.VOWELS);
        request.setText(text);

        // When
        long startTime = System.currentTimeMillis();
        AnalysisResponseDTO response = textAnalysisService.analyzeText(request);
        long endTime = System.currentTimeMillis();

        // Then
        assertNotNull(response);
        assertTrue(response.getResult().size() > 0);
        assertTrue(endTime - startTime < 5000); // Should complete within 5 seconds
    }

    @Test
    void analyzeText_specialCharactersOnly_shouldReturnEmptyResult() {
        // Given
        String text = "!@#$%^&*()_+-={}[]|\\:;\"'<>?,./";
        AnalysisRequestDTO request = new AnalysisRequestDTO();
        request.setType(AnalysisTypeDTO.VOWELS);
        request.setText(text);

        // When
        AnalysisResponseDTO response = textAnalysisService.analyzeText(request);

        // Then
        assertTrue(response.getResult().isEmpty());
        assertEquals(0, response.getStatistics().getTotalLetters());
        assertEquals(0, response.getStatistics().getTotalVowels());
        assertEquals(0, response.getStatistics().getTotalConsonants());
        assertTrue(response.getStatistics().getTotalSymbols() > 0);
    }

    @Test
    void analyzeText_numbersOnly_shouldReturnEmptyResult() {
        // Given
        String text = "1234567890";
        AnalysisRequestDTO request = new AnalysisRequestDTO();
        request.setType(AnalysisTypeDTO.CONSONANTS);
        request.setText(text);

        // When
        AnalysisResponseDTO response = textAnalysisService.analyzeText(request);

        // Then
        assertTrue(response.getResult().isEmpty());
        assertEquals(0, response.getStatistics().getTotalLetters());
        assertEquals(10, response.getStatistics().getTotalDigits());
    }
}
