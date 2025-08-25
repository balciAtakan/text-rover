import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ErrorResponse } from '../models/error-response';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const snackBar = inject(MatSnackBar);

  return next(req).pipe(
    catchError((error) => {
      let errorMessage = 'An unexpected error occurred';
      
      if (error.status === 0) {
        errorMessage = 'Unable to connect to the server. Please check your internet connection.';
      } else if (error.status === 400) {
        // Handle validation errors
        const errorResponse: ErrorResponse = error.error;
        if (errorResponse?.message) {
          errorMessage = errorResponse.message;
        } else if (errorResponse?.validationErrors) {
          errorMessage = errorResponse.validationErrors.map(err => err.message).join(', ');
        } else {
          errorMessage = 'Invalid request. Please check your input.';
        }
      } else if (error.status === 429) {
        errorMessage = 'Too many requests. Please wait a moment before trying again.';
      } else if (error.status >= 500) {
        errorMessage = 'Server error. Please try again later.';
      } else if (error.error?.message) {
        errorMessage = error.error.message;
      }

      // Show error snackbar with top-end positioning
      snackBar.open(errorMessage, 'Close', {
        duration: 96000,
        verticalPosition: 'top',
        horizontalPosition: 'end',
        panelClass: ['error-snackbar']
      });

      return throwError(() => error);
    })
  );
};
