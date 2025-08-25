package com.textrover.service;

import com.textrover.dto.AnalysisRequestDTO;
import com.textrover.dto.AnalysisResponseDTO;
import com.textrover.dto.AnalysisTypeDTO;
import com.textrover.exception.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TextAnalysisService Comprehensive Tests")
class TextAnalysisServiceComprehensiveTest {

    @InjectMocks
    private TextAnalysisService textAnalysisService;

    private AnalysisRequestDTO createRequest(AnalysisTypeDTO type, String text) {
        AnalysisRequestDTO request = new AnalysisRequestDTO();
        request.setType(type);
        request.setText(text);
        return request;
    }

    @Nested
    @DisplayName("Vowel Analysis Tests")
    class VowelAnalysisTests {

        @Test
        @DisplayName("Should count basic vowels correctly")
        void shouldCountBasicVowelsCorrectly() {
            // Given
            String text = "Hello World";

            // When
            AnalysisResponseDTO result = textAnalysisService.analyzeText(createRequest(AnalysisTypeDTO.VOWELS, text));

            // Then
            assertNotNull(result);
            assertEquals(AnalysisTypeDTO.VOWELS, result.getType());
            assertEquals(text, result.getText());
            assertNotNull(result.getResult());

            // Verify vowel counts: e=1, o=2
            assertEquals(1, result.getResult().get('e'));
            assertEquals(2, result.getResult().get('o'));
            assertEquals(2, result.getResult().size());
        }

        @Test
        @DisplayName("Should handle accented vowels")
        void shouldHandleAccentedVowels() {
            // Given
            String text = "café résumé naïve";

            // When
            AnalysisResponseDTO result = textAnalysisService.analyzeText(createRequest(AnalysisTypeDTO.VOWELS, text));

            // Then
            assertNotNull(result);
            assertTrue(result.getResult().containsKey('é'));
            assertTrue(result.getResult().containsKey('ï'));
            assertTrue(result.getResult().containsKey('a'));
            assertTrue(result.getResult().containsKey('e'));
        }

        @Test
        @DisplayName("Should handle empty vowel result")
        void shouldHandleEmptyVowelResult() {
            // Given
            String text = "bcdfg";

            // When
            AnalysisResponseDTO result = textAnalysisService.analyzeText(createRequest(AnalysisTypeDTO.VOWELS, text));

            // Then
            assertNotNull(result);
            assertTrue(result.getResult().isEmpty());
        }

        @ParameterizedTest
        @ValueSource(strings = {"aeiou", "AEIOU", "AeIoU"})
        @DisplayName("Should handle different vowel cases")
        void shouldHandleDifferentVowelCases(String text) {
            // When
            AnalysisResponseDTO result = textAnalysisService.analyzeText(createRequest(AnalysisTypeDTO.VOWELS, text));

            // Then
            assertNotNull(result);
            assertEquals(5, result.getResult().size());
            result.getResult().values().forEach(count -> assertEquals(1, count));
        }
    }

    @Nested
    @DisplayName("Consonant Analysis Tests")
    class ConsonantAnalysisTests {

        @Test
        @DisplayName("Should count basic consonants correctly")
        void shouldCountBasicConsonantsCorrectly() {
            // Given
            String text = "Hello World";

            // When
            AnalysisResponseDTO result = textAnalysisService.analyzeText(createRequest(AnalysisTypeDTO.CONSONANTS, text));

            // Then
            assertNotNull(result);
            assertEquals(AnalysisTypeDTO.CONSONANTS, result.getType());
            assertEquals(text, result.getText());

            // Verify consonant counts: h=1, l=3, w=1, r=1, d=1
            assertEquals(1, result.getResult().get('h'));
            assertEquals(3, result.getResult().get('l'));
            assertEquals(1, result.getResult().get('w'));
            assertEquals(1, result.getResult().get('r'));
            assertEquals(1, result.getResult().get('d'));
        }

        @Test
        @DisplayName("Should handle accented consonants")
        void shouldHandleAccentedConsonants() {
            // Given
            String text = "niño señor";

            // When
            AnalysisResponseDTO result = textAnalysisService.analyzeText(createRequest(AnalysisTypeDTO.CONSONANTS, text));

            // Then
            assertNotNull(result);
            assertTrue(result.getResult().containsKey('ñ'));
            assertTrue(result.getResult().containsKey('n'));
            assertTrue(result.getResult().containsKey('s'));
            assertTrue(result.getResult().containsKey('r'));
        }

        @Test
        @DisplayName("Should handle empty consonant result")
        void shouldHandleEmptyConsonantResult() {
            // Given
            String text = "aeiou";

            // When
            AnalysisResponseDTO result = textAnalysisService.analyzeText(createRequest(AnalysisTypeDTO.CONSONANTS, text));

            // Then
            assertNotNull(result);
            assertTrue(result.getResult().isEmpty());
        }
    }

    @Nested
    @DisplayName("Input Validation Tests")
    class InputValidationTests {

        @Test
        @DisplayName("Should throw exception for null text")
        void shouldThrowExceptionForNullText() {
            // When & Then
            ValidationException exception = assertThrows(
                    ValidationException.class,
                    () -> textAnalysisService.analyzeText(createRequest(AnalysisTypeDTO.VOWELS, null))
            );
            assertEquals("Text cannot be empty or null", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception for empty text")
        void shouldThrowExceptionForEmptyText() {
            // When & Then
            ValidationException exception = assertThrows(
                    ValidationException.class,
                    () -> textAnalysisService.analyzeText(createRequest(AnalysisTypeDTO.VOWELS, ""))
            );
            assertEquals("Text cannot be empty or null", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception for whitespace-only text")
        void shouldThrowExceptionForWhitespaceOnlyText() {
            // When & Then
            ValidationException exception = assertThrows(
                    ValidationException.class,
                    () -> textAnalysisService.analyzeText(createRequest(AnalysisTypeDTO.VOWELS, "   "))
            );
            assertEquals("Text cannot be empty or null", exception.getMessage());
        }

        @Test
        @DisplayName("Should handle single character input")
        void shouldHandleSingleCharacterInput() {
            // Given
            String text = "a";

            // When
            AnalysisResponseDTO result = textAnalysisService.analyzeText(createRequest(AnalysisTypeDTO.VOWELS, text));

            // Then
            assertNotNull(result);
            assertEquals(1, result.getResult().size());
            assertEquals(1, result.getResult().get('a'));
        }
    }

    @Nested
    @DisplayName("Special Character Tests")
    class SpecialCharacterTests {

        @Test
        @DisplayName("Should ignore numbers and symbols")
        void shouldIgnoreNumbersAndSymbols() {
            // Given
            String text = "Hello123!@# World456$%^";

            // When
            AnalysisResponseDTO vowelResult = textAnalysisService.analyzeText(createRequest(AnalysisTypeDTO.VOWELS, text));
            AnalysisResponseDTO result = textAnalysisService.analyzeText(createRequest(AnalysisTypeDTO.CONSONANTS, text));

            // Then
            assertNotNull(vowelResult);
            assertNotNull(result);

            // Should only contain letters, no numbers or symbols
            vowelResult.getResult().keySet().forEach(c -> assertTrue(Character.isLetter(c)));
            result.getResult().keySet().forEach(c -> assertTrue(Character.isLetter(c)));
        }

        @Test
        @DisplayName("Should handle mixed case correctly")
        void shouldHandleMixedCaseCorrectly() {
            // Given
            String text = "HeLLo WoRLd";

            // When
            AnalysisResponseDTO result = textAnalysisService.analyzeText(createRequest(AnalysisTypeDTO.VOWELS, text));

            // Then
            assertNotNull(result);
            // All characters should be lowercase in result
            result.getResult().keySet().forEach(c -> assertTrue(Character.isLowerCase(c)));
        }

        @Test
        @DisplayName("Should handle Unicode characters")
        void shouldHandleUnicodeCharacters() {
            // Given
            String text = "Héllö Wörld 你好";

            // When
            AnalysisResponseDTO vowelResult = textAnalysisService.analyzeText(createRequest(AnalysisTypeDTO.VOWELS, text));
            AnalysisResponseDTO result = textAnalysisService.analyzeText(createRequest(AnalysisTypeDTO.CONSONANTS, text));

            // Then
            assertNotNull(vowelResult);
            assertNotNull(result);

            // Should handle accented characters
            assertTrue(vowelResult.getResult().containsKey('é') || vowelResult.getResult().containsKey('e'));
            assertTrue(vowelResult.getResult().containsKey('ö') || vowelResult.getResult().containsKey('o'));
        }
    }

    @Nested
    @DisplayName("Performance Tests")
    class PerformanceTests {

        @Test
        @DisplayName("Should handle large text efficiently")
        void shouldHandleLargeTextEfficiently() {
            // Given
            StringBuilder largeText = new StringBuilder();
            for (int i = 0; i < 10000; i++) {
                largeText.append("Hello World ");
            }

            // When
            long startTime = System.currentTimeMillis();
            AnalysisResponseDTO result = textAnalysisService.analyzeText(createRequest(AnalysisTypeDTO.VOWELS, largeText.toString()));
            long endTime = System.currentTimeMillis();

            // Then
            assertNotNull(result);
            assertTrue(endTime - startTime < 5000, "Analysis should complete within 5 seconds");
            assertFalse(result.getResult().isEmpty());
        }

        @Test
        @DisplayName("Should handle repeated characters efficiently")
        void shouldHandleRepeatedCharactersEfficiently() {
            // Given
            String repeatedText = "a".repeat(10000);

            // When
            AnalysisResponseDTO result = textAnalysisService.analyzeText(createRequest(AnalysisTypeDTO.VOWELS, repeatedText));

            // Then
            assertNotNull(result);
            assertEquals(1, result.getResult().size());
            assertEquals(10000, result.getResult().get('a'));
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle text with only spaces")
        void shouldHandleTextWithOnlySpaces() {
            // When & Then
            assertThrows(ValidationException.class,
                    () -> textAnalysisService.analyzeText(createRequest(AnalysisTypeDTO.VOWELS, "     ")));
        }

        @Test
        @DisplayName("Should handle text with newlines and tabs")
        void shouldHandleTextWithNewlinesAndTabs() {
            // Given
            String text = "Hello\nWorld\tTest";

            // When
            AnalysisResponseDTO result = textAnalysisService.analyzeText(createRequest(AnalysisTypeDTO.VOWELS, text));

            // Then
            assertNotNull(result);
            assertFalse(result.getResult().isEmpty());
            // Should not contain whitespace characters
            result.getResult().keySet().forEach(c -> assertFalse(Character.isWhitespace(c)));
        }

        @Test
        @DisplayName("Should handle extremely long words")
        void shouldHandleExtremelyLongWords() {
            // Given
            String longWord = "supercalifragilisticexpialidocious".repeat(100);

            // When
            AnalysisResponseDTO result = textAnalysisService.analyzeText(createRequest(AnalysisTypeDTO.VOWELS, longWord));

            // Then
            assertNotNull(result);
            assertFalse(result.getResult().isEmpty());
        }
    }
}
