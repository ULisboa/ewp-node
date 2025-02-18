import { HttpRequest, HttpInterceptorFn, HttpHandlerFn, HttpXsrfTokenExtractor } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';

export const xsrfInterceptor: HttpInterceptorFn = (request: HttpRequest<unknown>, next: HttpHandlerFn) => {
  const tokenService = inject(HttpXsrfTokenExtractor);
  const originalXsrfToken = tokenService.getToken();
  if (originalXsrfToken) {
    request = request.clone({
      headers: request.headers.set('X-XSRF-Token', originalXsrfToken),
      withCredentials: true
    });
  }

  return next(request).pipe(
    catchError(err => {
      if (err.status === 403) {
        const updatedXsrfToken = tokenService.getToken();
        // Try again if there was no previously a XSRF token, and now there is
        if (!originalXsrfToken && updatedXsrfToken) {
          request = request.clone({
            headers: request.headers.set('X-XSRF-Token', updatedXsrfToken),
            withCredentials: true
          });
          return next(request);
        }
      }
      return throwError(() => err);
    })
  )
}