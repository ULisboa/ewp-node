package pt.ulisboa.ewp.node.client.ewp.iias;

import eu.erasmuswithoutpaper.api.iias.v3.IiasV3;
import eu.erasmuswithoutpaper.api.iias.v3.endpoints.IiasGetResponseV3;
import eu.erasmuswithoutpaper.api.iias.v3.endpoints.IiasIndexResponseV3;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiUtils;
import pt.ulisboa.ewp.node.client.ewp.EwpClient;
import pt.ulisboa.ewp.node.client.ewp.exception.AbstractEwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.operation.result.success.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpInterinstitutionalAgreementApiConfiguration;

@Service
public class EwpInterInstitutionalAgreementsV3Client
    extends AbstractEwpInterInstitutionalAgreementsClient {

  public EwpInterInstitutionalAgreementsV3Client(
      RegistryClient registryClient, EwpClient ewpClient) {
    super(registryClient, ewpClient);
  }

  public EwpSuccessOperationResult<IiasIndexResponseV3> findAllByHeiIds(
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
        IiasIndexResponseV3.class);
  }

  public EwpSuccessOperationResult<IiasGetResponseV3> findByHeiIdAndIiaIds(
      String heiId, Collection<String> iiaIds, Boolean sendPdf)
      throws AbstractEwpClientErrorException {
    EwpInterinstitutionalAgreementApiConfiguration apiConfiguration = getApiConfiguration(heiId);
    return super.findByHeiIdAndIiaIds(
        heiId, iiaIds, sendPdf, apiConfiguration, IiasGetResponseV3.class);
  }

  public EwpSuccessOperationResult<IiasGetResponseV3> findByHeiIdAndIiaCodes(
      String heiId, Collection<String> iiaCodes, Boolean sendPdf)
      throws AbstractEwpClientErrorException {
    EwpInterinstitutionalAgreementApiConfiguration apiConfiguration = getApiConfiguration(heiId);
    return super.findByHeiIdAndIiaIds(
        heiId, iiaCodes, sendPdf, apiConfiguration, IiasGetResponseV3.class);
  }

  public EwpInterinstitutionalAgreementApiConfiguration getApiConfiguration(String heiId) {
    return getApiConfiguration(
        heiId,
        3,
        IiasV3.class,
        EwpInterInstitutionalAgreementsV3Client::readApiConfigurationElement);
  }

  private static EwpInterinstitutionalAgreementApiConfiguration readApiConfigurationElement(
      IiasV3 apiElement) {
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
