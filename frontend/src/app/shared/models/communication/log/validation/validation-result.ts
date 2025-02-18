import { Type } from "class-transformer";
import { ValidationEntry } from "./validation-entry";

export class ValidationResult {
    valid!: boolean;
    
    @Type(() => ValidationEntry)
    validationEntries!: ValidationEntry[];

    get lineNumbersAsString() {
        return this.validationEntries.filter(e => e.lineNumber).map(e => e.lineNumber).join(',');
    }
}