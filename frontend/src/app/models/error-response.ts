export interface ErrorResponse {
  error: string;
  message: string;
  timestamp: string;
  path?: string;
  validationErrors?: ValidationError[];
}

export interface ValidationError {
  field: string;
  message: string;
  rejectedValue: any;
}
