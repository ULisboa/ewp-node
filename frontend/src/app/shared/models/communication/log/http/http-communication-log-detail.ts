import { Type } from "class-transformer";
import { CommunicationLogDetail } from "../communication-log-detail";
import { HttpRequestLog } from "./http-request-log";
import { HttpResponseLog } from "./http-response-log";

export class HttpCommunicationLogDetail extends CommunicationLogDetail {
    @Type(() => HttpRequestLog)
    request!: HttpRequestLog;

    @Type(() => HttpResponseLog)
    response?: HttpResponseLog;
}