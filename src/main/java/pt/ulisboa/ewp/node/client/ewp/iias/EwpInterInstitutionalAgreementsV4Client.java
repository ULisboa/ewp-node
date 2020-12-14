package pt.ulisboa.ewp.node.client.ewp.iias;

import eu.erasmuswithoutpaper.api.iias.v4.IiasV4;
import eu.erasmuswithoutpaper.api.iias.v4.endpoints.IiasGetResponseV4;
import eu.erasmuswithoutpaper.api.iias.v4.endpoints.IiasIndexResponseV4;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiUtils;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.iias.ForwardEwpApiInterInstitutionalAgreementsApiSpecificationResponseDTO;
import pt.ulisboa.ewp.node.client.ewp.EwpClient;
import pt.ulisboa.ewp.node.client.ewp.exception.AbstractEwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.operation.result.success.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpInterinstitutionalAgreementApiConfiguration;

@Service
public class EwpInterInstitutionalAgreementsV4Client
    extends AbstractEwpInterInstitutionalAgreementsClient {

  private static final int API_MAJOR_VERSION = 3;

  public EwpInterInstitutionalAgreementsV4Client(
      RegistryClient registryClient, EwpClient ewpClient) {
    super(registryClient, ewpClient);
  }

  public ForwardEwpApiInterInstitutionalAgreementsApiSpecificationResponseDTO getApiSpecification(
      String heiId) {
    EwpInterinstitutionalAgreementApiConfiguration apiConfiguration = getApiConfiguration(heiId);
    return new ForwardEwpApiInterInstitutionalAgreementsApiSpecificationResponseDTO(
        apiConfiguration.getMaxIiaIds().intValueExact(),
        apiConfiguration.getMaxIiaCodes().intValueExact());
  }

  public EwpSuccessOperationResult<IiasIndexResponseV4> findAllByHeiIds(
      String heiId,
      String partnerHeiId,
      List<String> receivingAcademicYearIds,
      ZonedDateTime modifiedSince)
      throws AbstractEwpClientErrorException {
    EwpInterinstitutionalAgreementApiConfiguration apiConfiguration = getApiConfiguration(heiId);
    return super.findAllByHeiIds(
        heiId,
        partnerHeiId,
        receivingAcademicYearIds,
        modifiedSince,
        apiConfiguration,
        IiasIndexResponseV4.class);
  }

  public EwpSuccessOperationResult<IiasGetResponseV4> findByHeiIdAndIiaIds(
      String heiId, Collection<String> iiaIds, Boolean sendPdf)
      throws AbstractEwpClientErrorException {
    EwpInterinstitutionalAgreementApiConfiguration apiConfiguration = getApiConfiguration(heiId);
    return super.findByHeiIdAndIiaIds(
        heiId, iiaIds, sendPdf, apiConfiguration, IiasGetResponseV4.class);
  }

  public EwpSuccessOperationResult<IiasGetResponseV4> findByHeiIdAndIiaCodes(
      String heiId, Collection<String> iiaCodes, Boolean sendPdf)
      throws AbstractEwpClientErrorException {
    EwpInterinstitutionalAgreementApiConfiguration apiConfiguration = getApiConfiguration(heiId);
    return super.findByHeiIdAndIiaCodes(
        heiId, iiaCodes, sendPdf, apiConfiguration, IiasGetResponseV4.class);
  }

  public EwpInterinstitutionalAgreementApiConfiguration getApiConfiguration(String heiId) {
    return getApiConfiguration(
        heiId,
        API_MAJOR_VERSION,
        IiasV4.class,
        EwpInterInstitutionalAgreementsV4Client::readApiConfigurationElement);
  }

  private static EwpInterinstitutionalAgreementApiConfiguration readApiConfigurationElement(
      IiasV4 apiElement) {
    return new EwpInterinstitutionalAgreementApiConfiguration(
        apiElement.getIndexUrl(),
        apiElement.getGetUrl(),
        EwpApiUtils.getSupportedClientAuthenticationMethods(apiElement.getHttpSecurity()),
        EwpApiUtils.getSupportedServerAuthenticationMethods(apiElement.getHttpSecurity()),
        apiElement.getMaxIiaIds(),
        apiElement.getMaxIiaCodes(),
        apiElement.getSendsNotifications() != null);
  }
}
