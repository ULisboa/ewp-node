import { Type } from "class-transformer";
import { CommunicationLogDetail } from "./communication-log-detail";
import { HostPluginFunctionCallCommunicationLogDetail } from "./host/plugin/host-plugin-function-call-communication-log-detail";
import { HttpCommunicationFromEwpNodeLogDetail } from "./http/ewp/http-communication-from-ewp-node-log-detail";
import { HostHttpCommunicationLogDetail } from "./http/host/host-http-communication-log-detail";
import { HttpCommunicationToEwpNodeLogDetail } from "./http/ewp/http-communication-to-ewp-node-log-detail";

export class CommunicationLogDetailWrapper {
    @Type(() => CommunicationLogDetail, {
        discriminator: {
            property: 'type',
            subTypes: [
                { value: HostPluginFunctionCallCommunicationLogDetail, name: 'HOST_PLUGIN_FUNCTION_CALL' },
                { value: HttpCommunicationFromEwpNodeLogDetail, name: 'EWP_IN' },
                { value: HttpCommunicationToEwpNodeLogDetail, name: 'EWP_OUT' },
                { value: HostHttpCommunicationLogDetail, name: 'HOST_IN' },
                { value: HostHttpCommunicationLogDetail, name: 'HOST_OUT' }
            ]
        },
        keepDiscriminatorProperty: true
    })
    data!: CommunicationLogDetail;
}