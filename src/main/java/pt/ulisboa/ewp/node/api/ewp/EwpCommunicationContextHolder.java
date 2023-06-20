package pt.ulisboa.ewp.node.api.ewp;

import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;

public class EwpCommunicationContextHolder {

    private static final String REQUEST_ATTRIBUTE_NAME = "ewp_communication_context_holder";

    private final StringBuffer observationBuffer = new StringBuffer();

    private EwpCommunicationContextHolder() {}

    public static synchronized EwpCommunicationContextHolder getInstance(HttpServletRequest request) {
        EwpCommunicationContextHolder instance;
        Object attributeValue = request.getAttribute(REQUEST_ATTRIBUTE_NAME);
        if (attributeValue == null) {
            instance = new EwpCommunicationContextHolder();
            request.setAttribute(REQUEST_ATTRIBUTE_NAME, instance);

        } else {
            instance = (EwpCommunicationContextHolder) attributeValue;
        }

        return instance;
    }

    public void registerException(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        this.addObservation(stringWriter.toString());
    }

    public void addObservation(String message) {
        if (this.observationBuffer.length() > 0) {
            this.observationBuffer.append(System.lineSeparator());
        }
        this.observationBuffer.append(message);
    }

    public String getObservation() {
        return this.observationBuffer.toString();
    }
}
