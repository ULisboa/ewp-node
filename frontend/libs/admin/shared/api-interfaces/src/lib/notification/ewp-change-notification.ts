import { Type } from "class-transformer";
import { CommunicationLogSummary } from "../communication/log/communication-log-summary";

export class EwpChangeNotification {
    id!: number;
    creationDateTime!: Date;

    @Type(() => CommunicationLogSummary)
    sortedCommunicationLogs!: CommunicationLogSummary[];

    attemptNumber!: number;
    nextAttemptDateTime!: Date;
    status!: string;
    extraVariables!: { key: string, value: string}[];

    isSuccess() {
        return this.status === 'SUCCESS';
    }

    isMerged() {
        return this.status === 'MERGED';
    }

    isPending() {
        return this.status === 'PENDING';
    }

    isFailure() {
        return !this.isSuccess() && !this.isMerged() && !this.isPending();
    }
}
