import { inject } from '@angular/core';
import { CanMatchFn } from '@angular/router';
import { selectAuthenticated, selectStatus } from '../+state/admin-auth.selectors';
import { Store } from '@ngrx/store';
import { skipWhile, switchMap, tap } from 'rxjs';
import { Status } from '../+state/admin-auth.reducer';

export const adminAuthCanMatchGuard: CanMatchFn = (route, segments) => {
  const store = inject(Store);
  return store.select(selectStatus).pipe(
    skipWhile(status => status !== Status.INITIALIZED),
    switchMap(_ => store.select(selectAuthenticated))
  );
}
