import { Type } from "class-transformer";
import { CommunicationLogSummary } from "./communication-log-summary";

export class CommunicationLogDetail extends CommunicationLogSummary {
    exceptionStacktrace?: string;
    observations?: string;

    @Type(() => CommunicationLogSummary)
    sortedChildrenCommunications!: CommunicationLogSummary[];

    ewpChangeNotificationIds!: number[];
}
