export function convertSeverityToPrimengFormat(severity_str: string | undefined): 'success' | 'info' | 'warn' | 'error' | 'secondary' | 'contrast' | undefined | null {
    if (severity_str === undefined) {
      return undefined;
    }

    if (severity_str) {
      severity_str = severity_str.toLowerCase();
    }
    switch (severity_str) {
      case 'success':
      case 'info':
      case 'warn':
      case 'error':
      case 'secondary':
      case 'contrast':
        return severity_str;
    }
    return null;
  }