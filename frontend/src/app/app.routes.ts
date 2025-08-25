import { Routes } from '@angular/router';
import { TextAnalysisComponent } from './text-analysis/text-analysis.component';

export const routes: Routes = [
  { path: '', redirectTo: '/analyze', pathMatch: 'full' },
  { 
    path: 'analyze',
    component: TextAnalysisComponent,
    title: 'TextRover'
  },
  { path: '**', redirectTo: '/analyze' }
];
