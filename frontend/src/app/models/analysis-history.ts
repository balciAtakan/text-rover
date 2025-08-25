export interface AnalysisHistoryResponse {
  content: AnalysisHistoryItem[];
  pageable: {
    sort: {
      empty: boolean;
      sorted: boolean;
      unsorted: boolean;
    };
    offset: number;
    pageSize: number;
    pageNumber: number;
    paged: boolean;
    unpaged: boolean;
  };
  last: boolean;
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
  sort: {
    empty: boolean;
    sorted: boolean;
    unsorted: boolean;
  };
  first: boolean;
  numberOfElements: number;
  empty: boolean;
}

export interface AnalysisHistoryItem {
  id: number;
  inputText: string;
  analysisType: 'VOWELS' | 'CONSONANTS';
  mode: 'ONLINE' | 'OFFLINE';
  characterCounts: { [key: string]: number };
  statistics: {
    totalLetters: number;
    totalVowels: number;
    totalConsonants: number;
    totalDigits: number;
    totalSymbols: number;
    wordCount: number;
    vowelPercentage: number;
    consonantPercentage: number;
    mostFrequentCharacter: string;
    mostFrequentCount: number;
  };
  createdAt: string;
}
