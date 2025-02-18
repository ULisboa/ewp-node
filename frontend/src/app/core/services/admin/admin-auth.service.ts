import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';

import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AdminApiResponse } from '../../../shared/models/response/response';
import { AdminApiResponseWithObjectData } from '../../../shared/models/response/response-with-object-data';
import { User } from '../../../shared/models/user';

export interface Credentials {
    username: string;
    password: string;
}

@Injectable({ providedIn: 'root' })
export class AdminAuthService {
    http = inject(HttpClient);

    login(credentials: Credentials): Observable<AdminApiResponseWithObjectData<User>> {
        const formData = new FormData();
        formData.set('username', credentials.username);
        formData.set('password', credentials.password);

        return this.http
            .post<AdminApiResponseWithObjectData<User>>('/api/admin/auth/login', formData)
            .pipe(
                catchError((errorResponse: HttpErrorResponse) => {
                    return this.throwErrorFromErrorResponse(errorResponse);
                })
            );
    }

    getUser(): Observable<AdminApiResponseWithObjectData<User>> {
        return this.http
            .get<AdminApiResponseWithObjectData<User>>('/api/admin/auth/user')
            .pipe(
                catchError((errorResponse: HttpErrorResponse) => {
                    return this.throwErrorFromErrorResponse(errorResponse);
                })
            );
    }

    logout(): Observable<AdminApiResponse> {
        return this.http
            .post<AdminApiResponse>('/api/admin/auth/logout', null)
            .pipe(
                catchError((errorResponse: HttpErrorResponse) => {
                    return this.throwErrorFromErrorResponse(errorResponse);
                })
            );
    }

    throwErrorFromErrorResponse(errorResponse: HttpErrorResponse) {
        if (errorResponse.error && errorResponse.error.messages) {
            return throwError(() => errorResponse.error);
        }
        return throwError(() => {
            return {
                messages: [{
                    severity: 'ERROR',
                    summary: 'Connection failed'
                }]
            };
        });
    }

}