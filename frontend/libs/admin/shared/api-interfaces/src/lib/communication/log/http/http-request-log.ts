import { Type } from "class-transformer";
import { HttpHeaderLog } from "./http-header-log";

export class HttpRequestLog {
    method!: string;
    url!: string;

    @Type(() => HttpHeaderLog)
    headers!: HttpHeaderLog[];
    
    body!: string;
}