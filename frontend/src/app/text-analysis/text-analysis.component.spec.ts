import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { HttpClientTestingModule } from '@angular/common/http/testing';

import { TextAnalysisComponent } from './text-analysis.component';

describe('TextAnalysisComponent', () => {
  let component: TextAnalysisComponent;
  let fixture: ComponentFixture<TextAnalysisComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        TextAnalysisComponent,
        NoopAnimationsModule,
        MatSnackBarModule,
        HttpClientTestingModule
      ]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(TextAnalysisComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have default values', () => {
    expect(component.inputText).toBe('');
    expect(component.analysisType).toBe('vowels');
    expect(component.isOnlineMode).toBe(true);
    expect(component.maxTextLength).toBe(10000);
  });

  it('should calculate remaining characters correctly', () => {
    component.inputText = 'hello';
    expect(component.getRemainingCharacters()).toBe(9995);
  });

  it('should return correct character count color', () => {
    component.inputText = 'a'.repeat(9950);
    expect(component.getCharacterCountColor()).toBe('warn');
    
    component.inputText = 'a'.repeat(9600);
    expect(component.getCharacterCountColor()).toBe('accent');
    
    component.inputText = 'hello';
    expect(component.getCharacterCountColor()).toBe('primary');
  });
});
