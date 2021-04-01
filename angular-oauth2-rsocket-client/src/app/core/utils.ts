import { HttpErrorResponse } from "@angular/common/http";

export class Utils {
  public static formatError(errorEvent: HttpErrorResponse): string {
    if (errorEvent.error instanceof ErrorEvent) {
      // A client-side or network error occurred. Handle it accordingly.
      return 'An error occurred: ' + errorEvent.error.message;
    } else {
      // The backend returned an unsuccessful response code.
      // The response body may contain clues as to what went wrong,
      return `Backend returned code ${errorEvent.status}, ${errorEvent.error}`;
    }
  }
}