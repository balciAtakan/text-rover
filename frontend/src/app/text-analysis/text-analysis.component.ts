import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {MatCardModule} from '@angular/material/card';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatSelectModule} from '@angular/material/select';
import {MatButtonModule} from '@angular/material/button';
import {MatSlideToggleModule} from '@angular/material/slide-toggle';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatExpansionModule} from '@angular/material/expansion';
import {MatIconModule} from '@angular/material/icon';
import {MatChipsModule} from '@angular/material/chips';
import {MatTooltipModule} from '@angular/material/tooltip';
import {MatSnackBar} from '@angular/material/snack-bar';
import {TextAnalysisService} from '../services/text-analysis.service';
import {AnalysisHistoryService} from '../services/analysis-history.service';
import {AnalysisResult, AnalysisStatistics} from '../models/analysis-result';
import {AnalysisType} from '../models/analysis-type';
import {AnalysisHistoryItem} from '../models/analysis-history';

@Component({
    selector: 'app-text-analysis',
    standalone: true,
    imports: [
        CommonModule,
        FormsModule,
        MatCardModule,
        MatFormFieldModule,
        MatInputModule,
        MatSelectModule,
        MatButtonModule,
        MatSlideToggleModule,
        MatProgressSpinnerModule,
        MatExpansionModule,
        MatIconModule,
        MatChipsModule,
        MatTooltipModule
    ],
    templateUrl: './text-analysis.component.html',
    styleUrls: ['./text-analysis.component.scss']
})
export class TextAnalysisComponent implements OnInit {
    inputText = '';
    analysisType: AnalysisType = 'vowels';
    isOnlineMode = true;
    isLoading = false;
    isLoadingHistory = false;
    isDeletingHistory = false;
    hasMoreHistory = false;
    analysisHistory: AnalysisResult[] = [];
    maxTextLength = 10000;

    // Pagination properties
    currentPage = 0;
    pageSize = 20;
    totalElements = 0;
    isLoadingMore = false;
    hasMoreData = true;

    constructor(
        private textAnalysisService: TextAnalysisService,
        private analysisHistoryService: AnalysisHistoryService,
        private snackBar: MatSnackBar
    ) {
    }

    ngOnInit(): void {
        this.loadInitialHistory();
    }

    analyzeText(): void {
        if (!this.inputText.trim()) {
            this.snackBar.open('Please enter some text to analyze.', 'Close', {
                duration: 3000
            });
            return;
        }

        if (this.inputText.length > this.maxTextLength) {
            this.snackBar.open(`Text exceeds maximum length of ${this.maxTextLength} characters.`, 'Close', {
                duration: 3000
            });
            return;
        }

        this.isLoading = true;

        if (this.isOnlineMode) {
            this.textAnalysisService.analyzeTextOnline(this.analysisType, this.inputText)
                .subscribe({
                    next: (result) => {
                        this.addToHistory(result, 'online');
                        this.isLoading = false;
                    },
                    error: () => {
                        // Error handled by interceptor
                        this.isLoading = false;
                    }
                });
        } else {
            const result = this.textAnalysisService.analyzeTextOffline(this.analysisType, this.inputText);
            this.addToHistory(result, 'offline');
            this.isLoading = false;
        }
    }

    private addToHistory(result: AnalysisResult, mode: 'online' | 'offline'): void {
        const enhancedResult: AnalysisResult = {
            ...result,
            timestamp: new Date(),
            mode: mode
        };
        this.analysisHistory.unshift(enhancedResult);
    }

    /**
     * Load initial history from database on component initialization
     */
    loadInitialHistory(): void {
        this.analysisHistoryService.getAnalysisHistory(0, this.pageSize)
            .subscribe({
                next: (response) => {
                    this.analysisHistory = this.convertHistoryItems(response.content);
                    this.totalElements = response.totalElements;
                    this.currentPage = 0;
                    this.hasMoreData = !response.last;
                },
                error: (error) => {
                    console.error('Failed to load initial history:', error);
                    // Error handled by interceptor
                }
            });
    }

    /**
     * Load more history items (pagination)
     */
    loadMoreHistory(): void {
        if (this.isLoadingMore || !this.hasMoreData) return;

        this.isLoadingMore = true;
        const nextPage = this.currentPage + 1;

        this.analysisHistoryService.getAnalysisHistory(nextPage, this.pageSize)
            .subscribe({
                next: (response) => {
                    const newItems = this.convertHistoryItems(response.content);
                    this.analysisHistory.push(...newItems);
                    this.currentPage = nextPage;
                    this.hasMoreData = !response.last;
                    this.isLoadingMore = false;
                },
                error: (error) => {
                    console.error('Failed to load more history:', error);
                    this.isLoadingMore = false;
                    // Error handled by interceptor
                }
            });
    }

    /**
     * Convert backend history items to frontend format
     */
    private convertHistoryItems(items: AnalysisHistoryItem[]): AnalysisResult[] {
        return items.map(item => ({
            type: item.analysisType.toLowerCase() as AnalysisType,
            text: item.inputText,
            result: item.characterCounts,
            timestamp: new Date(item.createdAt),
            mode: item.mode.toLowerCase() as 'online' | 'offline',
            statistics: {
                totalLetters: item.statistics.totalLetters,
                totalVowels: item.statistics.totalVowels,
                totalConsonants: item.statistics.totalConsonants,
                totalDigits: item.statistics.totalDigits,
                totalSymbols: item.statistics.totalSymbols,
                wordCount: item.statistics.wordCount,
                vowelPercentage: item.statistics.vowelPercentage,
                consonantPercentage: item.statistics.consonantPercentage,
                mostFrequentCharacter: item.statistics.mostFrequentCharacter,
                mostFrequentCount: item.statistics.mostFrequentCount
            }
        }));
    }

    /**
     * Clear local history only
     */
    clearLocalHistory(): void {
        this.analysisHistory = [];
        this.currentPage = 0;
        this.hasMoreData = true;
        this.snackBar.open('Local history cleared', 'Close', {
            duration: 2000
        });
    }

    /**
     * Delete all history from database
     */
    deleteAllHistory(): void {
        this.analysisHistoryService.deleteAllHistory()
            .subscribe({
                next: (response) => {
                    this.analysisHistory = [];
                    this.currentPage = 0;
                    this.totalElements = 0;
                    this.hasMoreData = false;
                    this.snackBar.open('All history deleted from database', 'Close', {
                        duration: 3000
                    });
                },
                error: (error) => {
                    console.error('Failed to delete all history:', error);
                    // Error handled by interceptor
                }
            });
    }

    getResultEntries(result: { [key: string]: number }): Array<{ key: string, value: number }> {
        return Object.entries(result).map(([key, value]) => ({key, value}));
    }

    getHumanReadableOutput(result: AnalysisResult): string {
        const entries = this.getResultEntries(result.result);
        const totalCount = entries.reduce((sum, entry) => sum + entry.value, 0);

        if (entries.length === 0) {
            return `No ${result.type} found in the text.`;
        }

        const letterCounts = entries
            .sort((a, b) => b.value - a.value)
            .map(entry => `${entry.key}: ${entry.value}`)
            .join(', ');

        return `Found ${totalCount} ${result.type} in total: ${letterCounts}`;
    }

    copyToClipboard(text: string): void {
        navigator.clipboard.writeText(text).then(() => {
            this.snackBar.open('Copied to clipboard!', 'Close', {
                duration: 2000
            });
        }).catch(err => {
            console.error('Failed to copy to clipboard:', err);
            this.fallbackCopyToClipboard(text);
        });
    }

    private fallbackCopyToClipboard(text: string): void {
        const textArea = document.createElement('textarea');
        textArea.value = text;
        textArea.style.position = 'fixed';
        textArea.style.left = '-999999px';
        textArea.style.top = '-999999px';
        document.body.appendChild(textArea);
        textArea.focus();
        textArea.select();

        try {
            document.execCommand('copy');
            this.snackBar.open('Copied to clipboard!', 'Close', {
                duration: 2000
            });
        } catch (err) {
            console.error('Fallback copy failed:', err);
            this.snackBar.open('Failed to copy to clipboard', 'Close', {
                duration: 3000
            });
        }

        document.body.removeChild(textArea);
    }

    getStatisticsText(result: AnalysisResult): string {
        if (!result.statistics) return '';

        const stats = result.statistics;
        return `Text Analysis Statistics:
- Total Letters: ${stats.totalLetters}
- Total Vowels: ${stats.totalVowels} (${stats.vowelPercentage}%)
- Total Consonants: ${stats.totalConsonants} (${stats.consonantPercentage}%)
- Total Digits: ${stats.totalDigits}
- Total Symbols: ${stats.totalSymbols}
- Word Count: ${stats.wordCount}
- Most Frequent Character: '${stats.mostFrequentCharacter}' (${stats.mostFrequentCount} times)

${result.type.charAt(0).toUpperCase() + result.type.slice(1)} Analysis:
${this.getHumanReadableOutput(result)}`;
    }

    getRemainingCharacters(): number {
        return this.maxTextLength - this.inputText.length;
    }

    getCharacterCountColor(): string {
        const remaining = this.getRemainingCharacters();
        if (remaining < 100) return 'warn';
        if (remaining < 500) return 'accent';
        return 'primary';
    }
}
