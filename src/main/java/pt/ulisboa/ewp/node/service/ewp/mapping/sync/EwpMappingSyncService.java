package pt.ulisboa.ewp.node.service.ewp.mapping.sync;

import java.util.Date;
import org.springframework.scheduling.TriggerContext;

public interface EwpMappingSyncService extends Runnable {

  Date getNextExecutionTime(TriggerContext context);

}
