package pt.ulisboa.ewp.node.service.ewp.mapping.sync;

import java.time.Instant;
import org.springframework.scheduling.TriggerContext;

public interface EwpMappingSyncService extends Runnable {

  Instant getNextExecutionInstant(TriggerContext context);

}
