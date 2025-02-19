import { ToastMessageOptions } from "primeng/api";

export interface MessageInput {
    context?: string;
    severity?: string;
    summary?: string;
    uuid?: string;
};

export function convertMessagesToPrimengFormat(messages: MessageInput[]) {
    return messages.map(m => convertMessageToPrimengFormat(m));
}

export function convertMessageToPrimengFormat(message: MessageInput): ToastMessageOptions {
    return {
        key: message.context,
        severity: message.severity ? message.severity.toLowerCase() : undefined,
        summary: message.summary,
        id: message.uuid
    }
}