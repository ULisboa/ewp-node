import { HttpErrorResponse, HttpClient } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { AdminApiResponseWithObjectData, EwpChangeNotification } from "@ewp-node-frontend/admin/shared/api-interfaces";
import { Type, plainToClassFromExist } from "class-transformer";
import { Observable, catchError, map, throwError } from "rxjs";

export class GetEwpChangeNotificationsResponse {
    @Type(() => EwpChangeNotification)
    ewpChangeNotifications!: EwpChangeNotification[];

    totalResults!: number;
}

@Injectable({ providedIn: 'root' })
export class AdminEwpChangeNotificationsService {
    http = inject(HttpClient);

    getEwpChangeNotifications(filter: {} | null, offset: number, limit: number): Observable<GetEwpChangeNotificationsResponse> {
        return this.http
            .post<AdminApiResponseWithObjectData<GetEwpChangeNotificationsResponse>>(`/api/admin/ewp/notifications`, {
                filter, offset, limit
            })
            .pipe(
                map(response => {
                    return plainToClassFromExist(new GetEwpChangeNotificationsResponse(), response.data );
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