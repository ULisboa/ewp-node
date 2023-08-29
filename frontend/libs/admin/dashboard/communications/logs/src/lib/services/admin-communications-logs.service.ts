import { HttpErrorResponse, HttpParams, HttpClient } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { AdminApiResponseWithObjectData, CommunicationLogSummary } from "@ewp-node-frontend/admin/shared/api-interfaces";
import { Type } from "class-transformer";
import { Observable, catchError, throwError } from "rxjs";

export class GetCommunicationsLogsResponse {
    @Type(() => CommunicationLogSummary)
    communicationLogs!: CommunicationLogSummary[];

    totalResults!: number;
}

@Injectable({ providedIn: 'root' })
export class AdminCommunicationsLogsService {
    http = inject(HttpClient);

    getCommunicationsLogs(filter: { format: string, filters: { [k: string]: object | undefined } }, offset: number, limit: number): Observable<AdminApiResponseWithObjectData<GetCommunicationsLogsResponse>> {
        return this.http
            .post<AdminApiResponseWithObjectData<GetCommunicationsLogsResponse>>(`/api/admin/communications/logs`, {
                filter, offset, limit
            })
            .pipe(
                catchError((errorResponse: HttpErrorResponse) => {
                    return this.throwErrorFromErrorResponse(errorResponse);
                })
            );
    }

    createHttpParams(obj: object) {
        let params = new HttpParams();
        if (obj) {
            for (const [key, value] of Object.entries(obj)) {
                params = params.set(key, value != null ? value : '');
            }
        }
        return params;
    }

    throwErrorFromErrorResponse(errorResponse: HttpErrorResponse) {
        console.log(errorResponse);
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