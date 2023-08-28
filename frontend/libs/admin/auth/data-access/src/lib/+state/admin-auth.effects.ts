import { inject } from "@angular/core";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import { AdminAuthService, Credentials } from "../services/admin-auth.service";
import { adminAuthActions } from "./admin-auth.actions";
import { catchError, map, of, skipWhile, switchMap, take, tap } from "rxjs";
import { Router } from "@angular/router";
import { Message } from "@ewp-node-frontend/admin/shared/api-interfaces";
import { Store } from "@ngrx/store";
import { selectStatus } from "./admin-auth.selectors";
import { Status } from "./admin-auth.reducer";

export const init$ = createEffect(
    (actions$ = inject(Actions), store = inject(Store)) => {
        return actions$.pipe(
            ofType(adminAuthActions.init),
            tap(() => store.dispatch(adminAuthActions.getUser())),
            switchMap(_ => store.select(selectStatus)),
            skipWhile(status => status !== Status.INITIALIZED),
            take(1),
            map(_ => of(true))
        );
    },
    { functional: true, dispatch: false },
);

export const getUser$ = createEffect(
    (actions$ = inject(Actions), adminAuthService = inject(AdminAuthService)) => {
        return actions$.pipe(
            ofType(adminAuthActions.getUser),
            switchMap(_ =>
                adminAuthService.getUser().pipe(
                    map((response) => adminAuthActions.getUserSuccess({ user: response.data })),
                    catchError((error) => of(adminAuthActions.getUserFailure({ messages: error.messages as Message[] })))
                ),
            ),
        );
    },
    { functional: true },
);

export const login$ = createEffect(
    (actions$ = inject(Actions), adminAuthService = inject(AdminAuthService)) => {
        return actions$.pipe(
            ofType(adminAuthActions.login),
            switchMap((action: Credentials) =>
                adminAuthService.login(action).pipe(
                    map((response) => adminAuthActions.loginSuccess({ user: response.data })),
                    catchError((error) => of(adminAuthActions.loginFailure({ messages: error.messages as Message[] })))
                ),
            ),
        );
    },
    { functional: true },
);

export const loginSuccess$ = createEffect(
    (actions$ = inject(Actions), router = inject(Router)) => {
        return actions$.pipe(
            ofType(adminAuthActions.loginSuccess),
            tap((action) => {
                router.navigateByUrl('/admin');
            }),
        );
    },
    { functional: true, dispatch: false },
);