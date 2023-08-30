import { Type } from "class-transformer";
import { HttpHeaderLog } from "./http-header-log";

export class HttpResponseLog {
    statusCode!: number;

    @Type(() => HttpHeaderLog)
    headers!: HttpHeaderLog[];
    
    body!: string;
}