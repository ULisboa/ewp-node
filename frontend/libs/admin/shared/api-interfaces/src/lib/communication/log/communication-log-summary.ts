export class CommunicationLogSummary {
    id!: number;
    type!: string;
    status!: string;
    startProcessingDateTime!: Date;
    endProcessingDateTime?: Date;
    source!: string;
    target!: string;
}