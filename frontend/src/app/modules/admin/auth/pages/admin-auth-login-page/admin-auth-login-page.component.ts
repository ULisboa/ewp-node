import { Component, OnDestroy, inject } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { ToastMessageOptions } from 'primeng/api';
import { Store } from '@ngrx/store';
import { Actions, ofType } from '@ngrx/effects';
import { Subject, takeUntil } from 'rxjs';
import { convertMessagesToPrimengFormat, MessageInput } from '@ewp-node-frontend/shared/utils/message';
import { adminAuthActions } from '@ewp-node-frontend/shared/states';
import { Credentials } from '@ewp-node-frontend/core';

@Component({
    selector: 'app-admin-auth-login-page',
    templateUrl: './admin-auth-login-page.component.html',
    standalone: false
})
export class AdminAuthLoginPageComponent implements OnDestroy {
  router = inject(Router);
  store = inject(Store);
  actions$ = inject(Actions);

  destroyed$ = new Subject<boolean>();

  form = new FormGroup({
    username: new FormControl(''),
    password: new FormControl('')
  })

  pendingResult = false;

  messages: ToastMessageOptions[] = [];

  constructor() {
    this.actions$.pipe(
      ofType(adminAuthActions.loginFailure),
      takeUntil(this.destroyed$)
    ).subscribe(action => {
      this.messages = convertMessagesToPrimengFormat(action.messages as MessageInput[]);
      this.pendingResult = false;
    });
  }

  login() {
    if (this.form.valid) {
      this.pendingResult = true;
      this.messages = [];

      this.store.dispatch(adminAuthActions.login(this.form.value as Credentials));
    }
  }

  ngOnDestroy() {
    this.destroyed$.next(true);
    this.destroyed$.complete();
  }

}
