package pt.ulisboa.ewp.node.api.ewp.security.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;
import pt.ulisboa.ewp.node.domain.entity.communication.log.CommunicationLog;
import pt.ulisboa.ewp.node.service.communication.context.CommunicationContextHolder;
import pt.ulisboa.ewp.node.utils.http.HttpConstants;

/** Filter that fills responses with the corresponding communication ID. */
public class EwpApiResponseCommunicationIdFillerFilter extends OncePerRequestFilter {

  private static final Logger LOG =
      LoggerFactory.getLogger(EwpApiResponseCommunicationIdFillerFilter.class);

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    try {
      chain.doFilter(request, response);
    } finally {
      CommunicationLog currentCommunicationLog =
          CommunicationContextHolder.getContext().getCurrentCommunicationLog();
      if (currentCommunicationLog != null) {
        long currentCommunicationLogId = currentCommunicationLog.getId();
        response.addHeader(
            HttpConstants.HEADER_X_EWP_NODE_COMMUNICATION_ID,
            String.valueOf(currentCommunicationLogId));
      } else {
        LOG.info("Expected an existing current communication log, but found none...");
      }
    }
  }
}
