import { convertToMessage } from "./communication-log-warning-code-converter";

export class CommunicationLogSummary {
    id!: number;
    type!: string;
    status!: string;
    startProcessingDateTime!: Date;
    endProcessingDateTime?: Date;
    source!: string;
    target!: string;
    warningCodes!: string[];

    get warningMessages() {
        return this.warningCodes.map(c => convertToMessage(c));
    }
}