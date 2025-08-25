package com.textrover.service;

import com.textrover.dto.AnalysisRequestDTO;
import com.textrover.dto.AnalysisResponseDTO;
import com.textrover.dto.AnalysisStatisticsDTO;
import com.textrover.dto.AnalysisTypeDTO;
import com.textrover.exception.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class TextAnalysisService {

    private static final Logger log = LogManager.getLogger(TextAnalysisService.class);
    // Basic vowels and consonants
    // Basic implementation, left here for presentation purposes.
    private static final Set<Character> VOWELS = Set.of('a', 'e', 'i', 'o', 'u');
    private static final Set<Character> CONSONANTS = "bcdfghjklmnpqrstvwxyz".chars()
            .mapToObj(c -> (char) c)
            .collect(Collectors.toSet());

    // Extended vowels with accents
    private static final Set<Character> EXTENDED_VOWELS = Set.of(
            'a', 'e', 'i', 'o', 'u',
            'à', 'á', 'â', 'ã', 'ä', 'å', 'æ',
            'è', 'é', 'ê', 'ë',
            'ì', 'í', 'î', 'ï',
            'ò', 'ó', 'ô', 'õ', 'ö', 'ø',
            'ù', 'ú', 'û', 'ü',
            'ý', 'ÿ'
    );

    // Extended consonants with accents
    private static final Set<Character> EXTENDED_CONSONANTS = Set.of(
            'b', 'c', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'm', 'n', 'p', 'q', 'r', 's', 't', 'v', 'w', 'x', 'z',
            'ç', 'ñ', 'ß'
    );

    public AnalysisResponseDTO analyzeText(AnalysisRequestDTO request) {
        if (request == null || request.getText() == null || request.getText().trim().isEmpty()) {
            throw new ValidationException("Text cannot be empty or null");
        }

        String text = request.getText();
        AnalysisTypeDTO type = request.getType();

        long startTime = System.currentTimeMillis();
        String lowerText = text.toLowerCase();
        Map<Character, Integer> result = new HashMap<>();

        // Use extended character sets for better international support
        Predicate<Character> filter = type == AnalysisTypeDTO.VOWELS
                ? EXTENDED_VOWELS::contains
                : EXTENDED_CONSONANTS::contains;

        // Calculate comprehensive statistics
        AnalysisStatisticsDTO statistics = calculateStatistics(text);

        long letterCount = lowerText.chars()
                .mapToObj(c -> (char) c)
                .filter(Character::isLetter)
                .filter(filter)
                .peek(c -> log.trace("Processing character: '{}'", c))
                .peek(c -> result.merge(c, 1, Integer::sum))
                .count();

        long processingTime = System.currentTimeMillis() - startTime;
        log.info("Analysis completed - Type: {}, Letters processed: {}, Processing time: {}ms",
                type, letterCount, processingTime);

        AnalysisResponseDTO response = new AnalysisResponseDTO();
        response.setType(type);
        response.setText(text);
        response.setResult(result);
        response.setStatistics(statistics);
        return response;
    }

    private AnalysisStatisticsDTO calculateStatistics(String text) {
        AnalysisStatisticsDTO stats = new AnalysisStatisticsDTO();
        String lowerText = text.toLowerCase();

        // Count different character types
        int totalLetters = 0;
        int totalVowels = 0;
        int totalConsonants = 0;
        int totalDigits = 0;
        int totalSymbols = 0;

        Map<Character, Integer> allCharCounts = new HashMap<>();

        for (char c : lowerText.toCharArray()) {
            allCharCounts.merge(c, 1, Integer::sum);

            if (Character.isLetter(c)) {
                totalLetters++;
                if (EXTENDED_VOWELS.contains(c)) {
                    totalVowels++;
                } else if (EXTENDED_CONSONANTS.contains(c)) {
                    totalConsonants++;
                }
            } else if (Character.isDigit(c)) {
                totalDigits++;
            } else if (!Character.isWhitespace(c)) {
                totalSymbols++;
            }
        }

        // Calculate word count
        int wordCount = text.trim().isEmpty() ? 0 : text.trim().split("\\s+").length;

        // Find most frequent character (excluding spaces)
        Character mostFrequentChar = null;
        int mostFrequentCount = 0;
        for (Map.Entry<Character, Integer> entry : allCharCounts.entrySet()) {
            if (!Character.isWhitespace(entry.getKey()) && entry.getValue() > mostFrequentCount) {
                mostFrequentChar = entry.getKey();
                mostFrequentCount = entry.getValue();
            }
        }

        // Calculate percentages
        double vowelPercentage = totalLetters > 0 ? (double) totalVowels / totalLetters * 100 : 0;
        double consonantPercentage = totalLetters > 0 ? (double) totalConsonants / totalLetters * 100 : 0;

        // Set all statistics
        stats.setTotalLetters(totalLetters);
        stats.setTotalVowels(totalVowels);
        stats.setTotalConsonants(totalConsonants);
        stats.setTotalDigits(totalDigits);
        stats.setTotalSymbols(totalSymbols);
        stats.setWordCount(wordCount);
        stats.setVowelPercentage(Math.round(vowelPercentage * 100.0) / 100.0);
        stats.setConsonantPercentage(Math.round(consonantPercentage * 100.0) / 100.0);
        stats.setMostFrequentCharacter(mostFrequentChar);
        stats.setMostFrequentCount(mostFrequentCount);

        return stats;
    }
}
