package com.textrover.entity;

import jakarta.persistence.*;
import java.util.Objects;

/**
 * Entity representing character count details for analysis results.
 */
@Entity
@Table(name = "analysis_character_counts", schema = "textrover")
public class AnalysisCharacterCountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "analysis_result_id", nullable = false)
    private AnalysisResultEntity analysisResult;

    @Column(name = "character", nullable = false, length = 1)
    private Character character;

    @Column(name = "count", nullable = false)
    private Integer count;

    // Default constructor
    public AnalysisCharacterCountEntity() {
    }

    // Constructor with required fields
    public AnalysisCharacterCountEntity(AnalysisResultEntity analysisResult, Character character, Integer count) {
        this.analysisResult = analysisResult;
        this.character = character;
        this.count = count;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AnalysisResultEntity getAnalysisResult() {
        return analysisResult;
    }

    public void setAnalysisResult(AnalysisResultEntity analysisResult) {
        this.analysisResult = analysisResult;
    }

    public Character getCharacter() {
        return character;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AnalysisCharacterCountEntity)) return false;
        AnalysisCharacterCountEntity that = (AnalysisCharacterCountEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AnalysisCharacterCountEntity{" +
                "id=" + id +
                ", character=" + character +
                ", count=" + count +
                '}';
    }
}
