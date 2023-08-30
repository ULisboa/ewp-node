import { Type } from "class-transformer";
import { CommunicationLogDetail } from "./communication-log-detail";
import { HostPluginFunctionCallCommunicationLogDetail } from "./host/plugin/host-plugin-function-call-communication-log-detail";
import { HttpCommunicationLogDetail } from "./http/http-communication-log-detail";

export class CommunicationLogDetailWrapper {
    @Type(() => CommunicationLogDetail, {
        discriminator: {
            property: 'type',
            subTypes: [
                { value: HostPluginFunctionCallCommunicationLogDetail, name: 'HOST_PLUGIN_FUNCTION_CALL' },
                { value: HttpCommunicationLogDetail, name: 'EWP_IN' },
                { value: HttpCommunicationLogDetail, name: 'EWP_OUT' },
                { value: HttpCommunicationLogDetail, name: 'HOST_OUT' }
            ]
        },
        keepDiscriminatorProperty: true
    })
    data!: CommunicationLogDetail;
}