import { FilterMetadata } from "primeng/api";

export function convertFilters(filters: { [s: string]: FilterMetadata | FilterMetadata[] | undefined } | undefined) {
    if (!filters) {
        return null;
    }
    const resultFilter: { type: string, subFilters: {}[] } = {
        type: 'CONJUNCTION',
        subFilters: []
    };
    for (let field in filters) {
        const filterMetadata = filters[field];
        if (filterMetadata) {
            if (Array.isArray(filterMetadata)) {
                resultFilter.subFilters.push({
                    type: 'CONJUNCTION',
                    subFilters: filterMetadata.map(fm => convertFilterMetadata(field, fm)).filter(fm => fm)
                });
            } else {
                const resultSubFilter = convertFilterMetadata(field, filterMetadata);
                if (resultSubFilter) {
                    resultFilter.subFilters.push(resultSubFilter);
                }
            }
        }
    }
    return resultFilter;
}

function convertFilterMetadata(field: string, filterMetadata : FilterMetadata): {} | null {
    const value = filterMetadata.value;
    if (!value) {
        return null;
    }
    switch (filterMetadata.matchMode) {
        case 'equals':
            return {
                type: 'EQUALS',
                field: field,
                value: value
            };

        case 'notEquals':
            return {
                type: 'NOT',
                subFilter: {
                    type: 'EQUALS',
                    field: field,
                    value: value
                }
            };

        case 'lt':
            return {
                type: 'LESS-THAN',
                field: field,
                value: value
            };

        case 'lte':
            return {
                type: 'LESS-THAN-OR-EQUAL',
                field: field,
                value: value
            };

        case 'gt':
            return {
                type: 'GREATER-THAN',
                field: field,
                value: value
            };

        case 'gte':
            return {
                type: 'GREATER-THAN-OR-EQUAL',
                field: field,
                value: value
            };

        case 'in':
            return {
                type: 'IN',
                field: field,
                values: value
            };

        case 'communicationTypeIsOneOfSet':
            return {
                type: 'COMMUNICATION-LOG-TYPE-IS-ONE-OF-SET',
                values: value
            };

        case 'communicationFromHeiId':
            return {
                type: 'HTTP-COMMUNICATION-FROM-EWP-NODE-IS-FROM-HEI-ID',
                value: value
            };

        case 'communicationToHeiId':
            return {
                type: 'HTTP-COMMUNICATION-TO-EWP-NODE-IS-TO-HEI-ID',
                value: value
            };

        default:
            console.error('Unknown match mode: ' + filterMetadata.matchMode);
            return null;
    }
}