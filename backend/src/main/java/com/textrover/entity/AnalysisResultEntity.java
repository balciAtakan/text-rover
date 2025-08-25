package com.textrover.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Entity representing analysis results stored in the database.
 */
@Entity
@Table(name = "analysis_results", schema = "textrover")
public class AnalysisResultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "input_text", nullable = false, columnDefinition = "TEXT")
    private String inputText;

    @Column(name = "analysis_type", nullable = false, length = 20)
    private String analysisType;

    @Column(name = "mode", nullable = false, length = 10)
    private String mode;

    @Column(name = "total_letters", nullable = false)
    private Integer totalLetters;

    @Column(name = "total_vowels", nullable = false)
    private Integer totalVowels;

    @Column(name = "total_consonants", nullable = false)
    private Integer totalConsonants;

    @Column(name = "total_digits", nullable = false)
    private Integer totalDigits;

    @Column(name = "total_symbols", nullable = false)
    private Integer totalSymbols;

    @Column(name = "word_count", nullable = false)
    private Integer wordCount;

    @Column(name = "vowel_percentage", nullable = false)
    private Double vowelPercentage;

    @Column(name = "consonant_percentage", nullable = false)
    private Double consonantPercentage;

    @Column(name = "most_frequent_character", length = 1)
    private Character mostFrequentCharacter;

    @Column(name = "most_frequent_count")
    private Integer mostFrequentCount;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP")
    private OffsetDateTime createdAt;

    @OneToMany(mappedBy = "analysisResult", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<AnalysisCharacterCountEntity> characterCounts;

    // Default constructor
    public AnalysisResultEntity() {
    }

    // Constructor with required fields
    public AnalysisResultEntity(String inputText, String analysisType, String mode, 
                               Integer totalLetters, Integer totalVowels, Integer totalConsonants,
                               Integer totalDigits, Integer totalSymbols, Integer wordCount,
                               Double vowelPercentage, Double consonantPercentage) {
        this.inputText = inputText;
        this.analysisType = analysisType;
        this.mode = mode;
        this.totalLetters = totalLetters;
        this.totalVowels = totalVowels;
        this.totalConsonants = totalConsonants;
        this.totalDigits = totalDigits;
        this.totalSymbols = totalSymbols;
        this.wordCount = wordCount;
        this.vowelPercentage = vowelPercentage;
        this.consonantPercentage = consonantPercentage;
        this.createdAt = OffsetDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInputText() {
        return inputText;
    }

    public void setInputText(String inputText) {
        this.inputText = inputText;
    }

    public String getAnalysisType() {
        return analysisType;
    }

    public void setAnalysisType(String analysisType) {
        this.analysisType = analysisType;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Integer getTotalLetters() {
        return totalLetters;
    }

    public void setTotalLetters(Integer totalLetters) {
        this.totalLetters = totalLetters;
    }

    public Integer getTotalVowels() {
        return totalVowels;
    }

    public void setTotalVowels(Integer totalVowels) {
        this.totalVowels = totalVowels;
    }

    public Integer getTotalConsonants() {
        return totalConsonants;
    }

    public void setTotalConsonants(Integer totalConsonants) {
        this.totalConsonants = totalConsonants;
    }

    public Integer getTotalDigits() {
        return totalDigits;
    }

    public void setTotalDigits(Integer totalDigits) {
        this.totalDigits = totalDigits;
    }

    public Integer getTotalSymbols() {
        return totalSymbols;
    }

    public void setTotalSymbols(Integer totalSymbols) {
        this.totalSymbols = totalSymbols;
    }

    public Integer getWordCount() {
        return wordCount;
    }

    public void setWordCount(Integer wordCount) {
        this.wordCount = wordCount;
    }

    public Double getVowelPercentage() {
        return vowelPercentage;
    }

    public void setVowelPercentage(Double vowelPercentage) {
        this.vowelPercentage = vowelPercentage;
    }

    public Double getConsonantPercentage() {
        return consonantPercentage;
    }

    public void setConsonantPercentage(Double consonantPercentage) {
        this.consonantPercentage = consonantPercentage;
    }

    public Character getMostFrequentCharacter() {
        return mostFrequentCharacter;
    }

    public void setMostFrequentCharacter(Character mostFrequentCharacter) {
        this.mostFrequentCharacter = mostFrequentCharacter;
    }

    public Integer getMostFrequentCount() {
        return mostFrequentCount;
    }

    public void setMostFrequentCount(Integer mostFrequentCount) {
        this.mostFrequentCount = mostFrequentCount;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<AnalysisCharacterCountEntity> getCharacterCounts() {
        return characterCounts;
    }

    public void setCharacterCounts(List<AnalysisCharacterCountEntity> characterCounts) {
        this.characterCounts = characterCounts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AnalysisResultEntity)) return false;
        AnalysisResultEntity that = (AnalysisResultEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AnalysisResultEntity{" +
                "id=" + id +
                ", analysisType='" + analysisType + '\'' +
                ", mode='" + mode + '\'' +
                ", totalLetters=" + totalLetters +
                ", totalVowels=" + totalVowels +
                ", totalConsonants=" + totalConsonants +
                ", createdAt=" + createdAt +
                '}';
    }
}
