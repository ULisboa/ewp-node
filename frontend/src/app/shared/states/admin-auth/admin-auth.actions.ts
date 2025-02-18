import { Message, User } from "../../models";
import { createActionGroup, emptyProps, props } from "@ngrx/store";
import { Credentials } from "../../../core/services/admin/admin-auth.service";

export const adminAuthActions = createActionGroup({
    source: 'Admin Auth',
    events: {
        init: emptyProps(),
        getUser: emptyProps(),
        getUserSuccess: props<{ user: User }>(),
        getUserFailure: props<{ messages: Message[] }>(),
        login: props<Credentials>(),
        loginSuccess: props<{ user: User }>(),
        loginFailure: props<{ messages: Message[] }>(),
        logout: emptyProps(),
        logoutSuccess: emptyProps(),
        logoutFailure: props<{ messages: Message[] }>(),
    },
});