import { AbstractType, Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'instanceof',
    pure: true,
    standalone: false
})
export class InstanceOfPipe implements PipeTransform {
  transform<V, R>(value: V, type: AbstractType<R>): R | undefined {
    return value instanceof type ? value : undefined;
  }
}
