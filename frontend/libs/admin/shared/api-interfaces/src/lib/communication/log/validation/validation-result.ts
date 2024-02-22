import { Type } from "class-transformer";
import { ValidationEntry } from "./validation-entry";

export class ValidationResult {
    valid!: boolean;
    
    @Type(() => ValidationEntry)
    validationEntries!: ValidationEntry[];
}