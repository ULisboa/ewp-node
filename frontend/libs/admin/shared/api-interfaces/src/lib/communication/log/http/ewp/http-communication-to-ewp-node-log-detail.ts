import { EwpHttpCommunicationLogDetail } from "./ewp-http-communication-log-detail";

export class HttpCommunicationToEwpNodeLogDetail extends EwpHttpCommunicationLogDetail {
    targetHeiId!: string;
    apiName!: string;
    apiVersion!: string;
}