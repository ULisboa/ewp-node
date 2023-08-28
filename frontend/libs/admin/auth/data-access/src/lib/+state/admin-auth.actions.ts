import { Message, User } from "@ewp-node-frontend/admin/shared/api-interfaces";
import { createActionGroup, emptyProps, props } from "@ngrx/store";
import { Credentials } from "../services/admin-auth.service";

export const adminAuthActions = createActionGroup({
    source: 'Auth',
    events: {
        init: emptyProps(),
        getUser: emptyProps(),
        getUserSuccess: props<{ user: User }>(),
        getUserFailure: props<{ messages: Message[] }>(),
        login: props<Credentials>(),
        loginFailure: props<{ messages: Message[] }>(),
        loginSuccess: props<{ user: User }>(),
        logout: emptyProps(),
    },
});