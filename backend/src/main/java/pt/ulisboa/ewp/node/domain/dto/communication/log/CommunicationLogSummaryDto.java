package pt.ulisboa.ewp.node.domain.dto.communication.log;

import java.time.ZonedDateTime;

public class CommunicationLogSummaryDto {
    
    private long id;
    private String type;
    private String status;
    private ZonedDateTime startProcessingDateTime;
    private ZonedDateTime endProcessingDateTime;
    private String source;
    private String target;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ZonedDateTime getStartProcessingDateTime() {
        return startProcessingDateTime;
    }

    public void setStartProcessingDateTime(ZonedDateTime startProcessingDateTime) {
        this.startProcessingDateTime = startProcessingDateTime;
    }

    public ZonedDateTime getEndProcessingDateTime() {
        return endProcessingDateTime;
    }

    public void setEndProcessingDateTime(ZonedDateTime endProcessingDateTime) {
        this.endProcessingDateTime = endProcessingDateTime;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
