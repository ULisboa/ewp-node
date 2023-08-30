import { Type } from "class-transformer";
import { CommunicationLogDetail } from "../../communication-log-detail";
import { FunctionCallArgumentLog } from "./function-call-argument-log";

export class FunctionCallCommunicationLogDetail extends CommunicationLogDetail {
    className!: string;
    method!: string;

    @Type(() => FunctionCallArgumentLog)
    sortedArguments!: FunctionCallArgumentLog[];

    resultType?: string;
    result?: string;

    get invocation() {
        return this.className + '.' + this.method + '(' +
            this.sortedArguments.map(a => a.value).join(', ') +
            ')';
    }
}