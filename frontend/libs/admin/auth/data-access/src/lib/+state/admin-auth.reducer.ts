import { User } from "@ewp-node-frontend/admin/shared/api-interfaces";
import { createFeature, createReducer, on } from "@ngrx/store";
import { adminAuthActions } from "./admin-auth.actions";

export interface AuthState {
    authenticated: boolean;
    user: User;
    status: Status;
  }

export enum Status {
    INITIALIZED = 'INITIALIZED',
    IN_PROGRESS = 'IN_PROGRESS',
}

export const authInitialState: AuthState = {
    authenticated: false,
    status: Status.INITIALIZED,
    user: {
        username: ''
    },
};

export const adminAuthFeature = createFeature({
    name: 'adminAuth',
    reducer: createReducer(
        authInitialState,
        on(adminAuthActions.init, (state) => ({
            ...state,
            authenticated: false,
            status: Status.IN_PROGRESS
          })),
        on(adminAuthActions.getUserSuccess, (state, action) => ({
            ...state,
            authenticated: true,
            status: Status.INITIALIZED,
            user: action.user,
          })),
        on(adminAuthActions.loginFailure, adminAuthActions.getUserFailure, adminAuthActions.logout, () => authInitialState),
        on(adminAuthActions.login, (state) => ({
            ...state,
            status: Status.IN_PROGRESS,
        })),
        on(adminAuthActions.loginSuccess, adminAuthActions.getUserSuccess, (state, action) => ({
            ...state,
            authenticated: true,
            status: Status.INITIALIZED,
            user: action.user,
        }))
    ),
});