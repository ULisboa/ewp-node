import { Type } from "class-transformer";
import { HttpHeaderLog } from "./http-header-log";
import { ValidationResult } from "../validation/validation-result";

export class HttpResponseLog {
    statusCode!: number;

    @Type(() => HttpHeaderLog)
    headers!: HttpHeaderLog[];
    
    body!: string;

    @Type(() => ValidationResult)
    bodyValidation?: ValidationResult;

    getLine(lineNumber: number): string {
        return this.body.split('\n')[lineNumber - 1];
    }
}