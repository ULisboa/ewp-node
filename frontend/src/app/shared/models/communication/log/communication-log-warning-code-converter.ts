
export function convertToMessage(warningCode: string) {
    switch(warningCode) {
        case 'ERROR_NOT_REPORTED_TO_MONITORING':
            return 'Communication error was not previously reported to monitoring';

        default:
            return 'Unknown error!';
    }
}