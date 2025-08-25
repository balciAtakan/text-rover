 import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AnalysisResult } from '../models/analysis-result';
import { AnalysisType } from '../models/analysis-type';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class TextAnalysisService {
  private apiUrl = `${environment.apiUrl}/analyze`;

  constructor(private http: HttpClient) {}

  analyzeTextOnline(type: AnalysisType, text: string): Observable<AnalysisResult> {
    const request = {
      type: type.toUpperCase(),
      text: text
    };
    return this.http.post<AnalysisResult>(this.apiUrl, request);
  }

  analyzeTextOffline(type: AnalysisType, text: string): AnalysisResult {
    // Extended character sets for better international support
    const extendedVowels = new Set([
      'a', 'e', 'i', 'o', 'u',
      'à', 'á', 'â', 'ã', 'ä', 'å', 'æ',
      'è', 'é', 'ê', 'ë',
      'ì', 'í', 'î', 'ï',
      'ò', 'ó', 'ô', 'õ', 'ö', 'ø',
      'ù', 'ú', 'û', 'ü',
      'ý', 'ÿ'
    ]);
    
    const extendedConsonants = new Set([
      'b', 'c', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'm', 'n', 'p', 'q', 'r', 's', 't', 'v', 'w', 'x', 'y', 'z',
      'ç', 'ñ', 'ß'
    ]);
    
    const lowerText = text.toLowerCase();
    const result: { [key: string]: number } = {};
    
    const targetSet = type === 'vowels' ? extendedVowels : extendedConsonants;
    
    // Calculate comprehensive statistics
    const statistics = this.calculateOfflineStatistics(text, extendedVowels, extendedConsonants);
    
    for (const char of lowerText) {
      if (/\p{L}/u.test(char) && targetSet.has(char)) {
        result[char] = (result[char] || 0) + 1;
      }
    }
    
    return {
      type: type,
      text: text,
      result: result,
      statistics: statistics
    };
  }

  private calculateOfflineStatistics(text: string, vowels: Set<string>, consonants: Set<string>) {
    const lowerText = text.toLowerCase();
    let totalLetters = 0;
    let totalVowels = 0;
    let totalConsonants = 0;
    let totalDigits = 0;
    let totalSymbols = 0;
    
    const allCharCounts: { [key: string]: number } = {};
    
    for (const char of lowerText) {
      allCharCounts[char] = (allCharCounts[char] || 0) + 1;
      
      if (/\p{L}/u.test(char)) {
        totalLetters++;
        if (vowels.has(char)) {
          totalVowels++;
        } else if (consonants.has(char)) {
          totalConsonants++;
        }
      } else if (/\d/.test(char)) {
        totalDigits++;
      } else if (!/\s/.test(char)) {
        totalSymbols++;
      }
    }
    
    // Calculate word count
    const wordCount = text.trim() === '' ? 0 : text.trim().split(/\s+/).length;
    
    // Find most frequent character (excluding spaces)
    let mostFrequentCharacter = '';
    let mostFrequentCount = 0;
    for (const [char, count] of Object.entries(allCharCounts)) {
      if (char !== ' ' && count > mostFrequentCount) {
        mostFrequentCharacter = char;
        mostFrequentCount = count;
      }
    }
    
    // Calculate percentages
    const vowelPercentage = totalLetters > 0 ? Math.round((totalVowels / totalLetters) * 10000) / 100 : 0;
    const consonantPercentage = totalLetters > 0 ? Math.round((totalConsonants / totalLetters) * 10000) / 100 : 0;
    
    return {
      totalLetters,
      totalVowels,
      totalConsonants,
      totalDigits,
      totalSymbols,
      wordCount,
      vowelPercentage,
      consonantPercentage,
      mostFrequentCharacter,
      mostFrequentCount
    };
  }
}
