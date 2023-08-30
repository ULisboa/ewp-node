package pt.ulisboa.ewp.node.domain.dto.filter.communication.log.http.ewp;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import pt.ulisboa.ewp.node.domain.dto.filter.FilterDto;
import pt.ulisboa.ewp.node.domain.entity.communication.log.CommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.ewp.HttpCommunicationFromEwpNodeLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.ewp.HttpCommunicationFromEwpNodeLog_;

public class HttpCommunicationFromEwpNodeCoveredHeiIdsContainsFilterDto extends FilterDto<CommunicationLog> {

    private final String value;

    public HttpCommunicationFromEwpNodeCoveredHeiIdsContainsFilterDto(String value) {
        this.value = value;
    }

    @Override
    public Predicate createPredicate(CriteriaBuilder criteriaBuilder, Root<CommunicationLog> selection) {
        return criteriaBuilder.isMember(value, criteriaBuilder.treat(selection, HttpCommunicationFromEwpNodeLog.class).get(
            HttpCommunicationFromEwpNodeLog_.heiIdsCoveredByClient));

    }
}
