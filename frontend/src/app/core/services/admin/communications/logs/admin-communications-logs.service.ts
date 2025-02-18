import { HttpErrorResponse, HttpClient } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { AdminApiResponseWithObjectData, CommunicationLogDetail, CommunicationLogDetailWrapper, CommunicationLogSummary, OperationResult } from "@ewp-node-frontend/shared/models";
import { Type, plainToClassFromExist } from "class-transformer";
import { Observable, catchError, map, throwError } from "rxjs";

export class GetCommunicationsLogsResponse {
    @Type(() => CommunicationLogSummary)
    communicationLogs!: CommunicationLogSummary[];

    totalResults!: number;
}

@Injectable({ providedIn: 'root' })
export class AdminCommunicationsLogsService {
    http = inject(HttpClient);

    getCommunicationsLogs(filter: object | null, offset: number, limit: number): Observable<AdminApiResponseWithObjectData<GetCommunicationsLogsResponse>> {
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

    getCommunicationsLogInDetail(id: number): Observable<CommunicationLogDetailWrapper> {
        return this.http
            .get<AdminApiResponseWithObjectData<CommunicationLogDetail>>(`/api/admin/communications/logs/${id}`)
            .pipe(
                map(response => {
                    return plainToClassFromExist(new CommunicationLogDetailWrapper(), { data: response.data });
                }),
                catchError((errorResponse: HttpErrorResponse) => {
                    return this.throwErrorFromErrorResponse(errorResponse);
                })
            );
    }

    reportCommunicationToMonitoring(id: number, clientMessage?: string | null): Observable<OperationResult> {
        return this.http
            .post<AdminApiResponseWithObjectData<CommunicationLogDetail>>(`/api/admin/communications/logs/${id}/monitoring/report`, {
                clientMessage: clientMessage
            })
            .pipe(
                map(response => {
                    return plainToClassFromExist(new OperationResult(), { data: response.data });
                }),
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