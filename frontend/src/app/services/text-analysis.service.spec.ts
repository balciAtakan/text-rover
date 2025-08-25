import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TextAnalysisService } from './text-analysis.service';
import { AnalysisResult } from '../models/analysis-result';

describe('TextAnalysisService', () => {
  let service: TextAnalysisService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [TextAnalysisService]
    });
    service = TestBed.inject(TextAnalysisService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('analyzeTextOffline', () => {
    it('should count vowels correctly', () => {
      const result = service.analyzeTextOffline('vowels', 'Hello World');
      expect(result.type).toBe('vowels');
      expect(result.text).toBe('Hello World');
      expect(result.result['e']).toBe(1);
      expect(result.result['o']).toBe(2);
      expect(result.statistics).toBeDefined();
      expect(result.statistics!.totalLetters).toBe(10);
      expect(result.statistics!.totalVowels).toBe(3);
    });

    it('should count consonants correctly', () => {
      const result = service.analyzeTextOffline('consonants', 'Hello World');
      expect(result.type).toBe('consonants');
      expect(result.text).toBe('Hello World');
      expect(result.result['h']).toBe(1);
      expect(result.result['l']).toBe(3);
      expect(result.result['w']).toBe(1);
      expect(result.result['r']).toBe(1);
      expect(result.result['d']).toBe(1);
      expect(result.statistics).toBeDefined();
      expect(result.statistics!.totalConsonants).toBe(7);
    });

    it('should handle empty text', () => {
      const result = service.analyzeTextOffline('vowels', '');
      expect(result.result).toEqual({});
      expect(result.statistics!.totalLetters).toBe(0);
      expect(result.statistics!.wordCount).toBe(0);
    });

    it('should handle text with no target letters', () => {
      const result = service.analyzeTextOffline('vowels', 'xyz');
      expect(result.result).toEqual({});
      expect(result.statistics!.totalVowels).toBe(0);
      expect(result.statistics!.totalConsonants).toBe(3);
    });

    it('should be case insensitive', () => {
      const result = service.analyzeTextOffline('vowels', 'AeIoU');
      expect(result.result['a']).toBe(1);
      expect(result.result['e']).toBe(1);
      expect(result.result['i']).toBe(1);
      expect(result.result['o']).toBe(1);
      expect(result.result['u']).toBe(1);
    });

    it('should handle extended vowels with accents', () => {
      const result = service.analyzeTextOffline('vowels', 'café résumé naïve');
      expect(result.result['a']).toBe(2);
      expect(result.result['é']).toBe(3);
      expect(result.result['e']).toBe(1);
      expect(result.result['ï']).toBe(1);
    });

    it('should handle extended consonants with accents', () => {
      const result = service.analyzeTextOffline('consonants', 'niño señor');
      expect(result.result['n']).toBe(1);
      expect(result.result['ñ']).toBe(2);
      expect(result.result['s']).toBe(1);
      expect(result.result['r']).toBe(1);
    });

    it('should calculate statistics correctly', () => {
      const result = service.analyzeTextOffline('vowels', 'Hello World! 123');
      expect(result.statistics!.totalLetters).toBe(10);
      expect(result.statistics!.totalVowels).toBe(3);
      expect(result.statistics!.totalConsonants).toBe(7);
      expect(result.statistics!.totalDigits).toBe(3);
      expect(result.statistics!.totalSymbols).toBe(1);
      expect(result.statistics!.wordCount).toBe(3);
      expect(result.statistics!.vowelPercentage).toBe(30);
      expect(result.statistics!.consonantPercentage).toBe(70);
      expect(result.statistics!.mostFrequentCharacter).toBe('l');
      expect(result.statistics!.mostFrequentCount).toBe(3);
    });

    it('should handle text with only numbers', () => {
      const result = service.analyzeTextOffline('vowels', '12345');
      expect(result.result).toEqual({});
      expect(result.statistics!.totalLetters).toBe(0);
      expect(result.statistics!.totalDigits).toBe(5);
      expect(result.statistics!.wordCount).toBe(1);
    });

    it('should handle text with only symbols', () => {
      const result = service.analyzeTextOffline('consonants', '!@#$%');
      expect(result.result).toEqual({});
      expect(result.statistics!.totalLetters).toBe(0);
      expect(result.statistics!.totalSymbols).toBe(5);
      expect(result.statistics!.wordCount).toBe(1);
    });

    it('should handle mixed content correctly', () => {
      const result = service.analyzeTextOffline('vowels', 'Test123!@# áéíóú');
      expect(result.result['e']).toBe(1);
      expect(result.result['á']).toBe(1);
      expect(result.result['é']).toBe(1);
      expect(result.result['í']).toBe(1);
      expect(result.result['ó']).toBe(1);
      expect(result.result['ú']).toBe(1);
      expect(result.statistics!.totalLetters).toBe(9);
      expect(result.statistics!.totalDigits).toBe(3);
      expect(result.statistics!.totalSymbols).toBe(3);
    });

    it('should handle single character input', () => {
      const result = service.analyzeTextOffline('vowels', 'a');
      expect(result.result['a']).toBe(1);
      expect(result.statistics!.totalLetters).toBe(1);
      expect(result.statistics!.totalVowels).toBe(1);
      expect(result.statistics!.wordCount).toBe(1);
    });
  });

  describe('analyzeTextOnline', () => {
    it('should make HTTP POST request with correct data', () => {
      const mockResponse: AnalysisResult = {
        type: 'vowels',
        text: 'test',
        result: { 'e': 1 }
      };

      service.analyzeTextOnline('vowels', 'test').subscribe(result => {
        expect(result).toEqual(mockResponse);
      });

      const req = httpMock.expectOne('http://localhost:8080/api/analyze');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual({
        type: 'VOWELS',
        text: 'test'
      });
      req.flush(mockResponse);
    });
  });
});
