export class Message {
    context?: string;
    severity!: string;
    summary!: string;
    uuid?: string;
}

export const SEVERITY_FATAL = 'FATAL';
export const SEVERITY_ERROR = 'ERROR';
export const SEVERITY_WARN = 'WARN';
export const SEVERITY_INFO = 'INFO';
