import { Component, OnDestroy, inject } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { Message } from 'primeng/api';
import { MessageInput, convertMessagesToPrimengFormat } from '@ewp-node-frontend/admin/shared/util-primeng';
import { Store } from '@ngrx/store';
import { Actions, ofType } from '@ngrx/effects';
import { Subject, takeUntil } from 'rxjs';
import { Credentials, adminAuthActions } from '@ewp-node-frontend/admin/auth/data-access';

@Component({
  selector: 'lib-admin-login-form',
  templateUrl: './admin-login-form.component.html',
  styleUrls: ['./admin-login-form.component.scss']
})
export class AdminLoginFormComponent implements OnDestroy {
  router = inject(Router);
  store = inject(Store);
  actions$ = inject(Actions);

  destroyed$ = new Subject<boolean>();

  form = new FormGroup({
    username: new FormControl(''),
    password: new FormControl('')
  })

  pendingResult = false;

  messages: Message[] = [];

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
