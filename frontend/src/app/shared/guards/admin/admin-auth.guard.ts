import { inject } from '@angular/core';
import { CanMatchFn, Router } from '@angular/router';
import { selectAuthenticated, selectStatus, Status } from '../../../shared/states';
import { Store } from '@ngrx/store';
import { map, skipWhile, switchMap } from 'rxjs';

export const adminAuthCanMatchGuard: CanMatchFn = (route, segments) => {
  const router = inject(Router);
  const store = inject(Store);
  return store.select(selectStatus).pipe(
    skipWhile(status => status !== Status.INITIALIZED),
    switchMap(_ => store.select(selectAuthenticated)),
    map(authenticated => {
      if (authenticated) {
        return true;
      }
      return router.createUrlTree(['admin', 'auth']);
    })
  );
}
