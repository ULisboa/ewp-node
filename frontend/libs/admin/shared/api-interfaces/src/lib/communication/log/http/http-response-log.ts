import { Type } from "class-transformer";
import { HttpHeaderLog } from "./http-header-log";
import { ValidationResult } from "../validation/validation-result";
import xmlFormat from 'xml-formatter';

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

    getBodyAsFormattedXml(): string {
        return xmlFormat(this.body, {
            collapseContent: true
        });
    }

    isXmlResponse(): boolean {
        return this.headers.findIndex(h => h.name.toLowerCase() === 'content-type' && 
            (h.value.includes('application/xml') || h.value.includes('text/xml'))
        ) != -1;
    }
}