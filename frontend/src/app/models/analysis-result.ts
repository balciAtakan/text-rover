export interface AnalysisResult {
  type: string;
  text: string;
  result: { [key: string]: number };
  statistics?: AnalysisStatistics;
  timestamp?: Date;
  mode?: 'online' | 'offline';
}

export interface AnalysisStatistics {
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
}
