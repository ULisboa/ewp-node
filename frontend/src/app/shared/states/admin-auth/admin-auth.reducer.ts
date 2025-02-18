import { createFeature, createReducer, on } from "@ngrx/store";
import { adminAuthActions } from "./admin-auth.actions";
import { User } from "../../models";

export interface AdminAuthState {
    authenticated: boolean;
    user: User;
    status: Status;
  }

export enum Status {
    INITIALIZED = 'INITIALIZED',
    IN_PROGRESS = 'IN_PROGRESS',
}

export const authInitialState: AdminAuthState = {
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
        on(adminAuthActions.loginFailure, adminAuthActions.getUserFailure, adminAuthActions.logoutSuccess, () => authInitialState),
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