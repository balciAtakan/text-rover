export * from './analysisHistory.service';
import { AnalysisHistoryService } from './analysisHistory.service';
export * from './analysisHistory.serviceInterface';
export * from './textAnalysis.service';
import { TextAnalysisService } from './textAnalysis.service';
export * from './textAnalysis.serviceInterface';
export const APIS = [AnalysisHistoryService, TextAnalysisService];
