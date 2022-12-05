package pt.ulisboa.ewp.node.client.ewp.iias;

import eu.erasmuswithoutpaper.api.iias.v6.endpoints.IiasGetResponseV6;
import eu.erasmuswithoutpaper.api.iias.v6.endpoints.IiasIndexResponseV6;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.iias.ForwardEwpApiInterInstitutionalAgreementsApiSpecificationResponseDTO;
import pt.ulisboa.ewp.node.client.ewp.EwpApiClient;
import pt.ulisboa.ewp.node.client.ewp.EwpClient;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.request.body.EwpRequestFormDataUrlEncodedBody;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpInterInstitutionalAgreementApiConfiguration;
import pt.ulisboa.ewp.node.utils.EwpApiSpecification.EwpApiVersionSpecification;
import pt.ulisboa.ewp.node.utils.EwpApiSpecification.InterInstitutionalAgreements;
import pt.ulisboa.ewp.node.utils.http.HttpParams;

@Service
public class EwpInterInstitutionalAgreementsV6Client
    extends EwpApiClient<EwpInterInstitutionalAgreementApiConfiguration> {

  public EwpInterInstitutionalAgreementsV6Client(
      RegistryClient registryClient, EwpClient ewpClient) {
    super(registryClient, ewpClient);
  }

  public ForwardEwpApiInterInstitutionalAgreementsApiSpecificationResponseDTO getApiSpecification(
      String heiId) {
    EwpInterInstitutionalAgreementApiConfiguration apiConfiguration =
        getApiConfigurationForHeiId(heiId);
    return new ForwardEwpApiInterInstitutionalAgreementsApiSpecificationResponseDTO(
        apiConfiguration.getMaxIiaIds().intValueExact(),
        apiConfiguration.getMaxIiaCodes().intValueExact());
  }

  public EwpSuccessOperationResult<IiasIndexResponseV6> findAllByHeiIds(
      String heiId,
      String partnerHeiId,
      List<String> receivingAcademicYearIds,
      ZonedDateTime modifiedSince)
      throws EwpClientErrorException {
    EwpInterInstitutionalAgreementApiConfiguration api = getApiConfigurationForHeiId(heiId);

    HttpParams bodyParams = new HttpParams();
    bodyParams.param(EwpApiParamConstants.HEI_ID, heiId);
    bodyParams.param(EwpApiParamConstants.PARTNER_HEI_ID, partnerHeiId);
    bodyParams.param(EwpApiParamConstants.RECEIVING_ACADEMIC_YEAR_ID, receivingAcademicYearIds);
    bodyParams.param(EwpApiParamConstants.MODIFIED_SINCE, modifiedSince);

    EwpRequest request = EwpRequest.createPost(api, api.getIndexUrl(),
        new EwpRequestFormDataUrlEncodedBody(bodyParams));
    return ewpClient.executeAndLog(request, IiasIndexResponseV6.class);
  }

  public EwpSuccessOperationResult<IiasGetResponseV6> findByHeiIdAndIiaIds(
      String heiId, Collection<String> iiaIds, Boolean sendPdf)
      throws EwpClientErrorException {
    EwpInterInstitutionalAgreementApiConfiguration api = getApiConfigurationForHeiId(heiId);

    HttpParams bodyParams = new HttpParams();
    bodyParams.param(EwpApiParamConstants.HEI_ID, heiId);
    bodyParams.param(EwpApiParamConstants.IIA_ID, iiaIds);
    bodyParams.param(EwpApiParamConstants.SEND_PDF, sendPdf);

    EwpRequest request = EwpRequest.createPost(api, api.getGetUrl(),
        new EwpRequestFormDataUrlEncodedBody(bodyParams));
    return ewpClient.executeAndLog(request, IiasGetResponseV6.class);
  }

  public EwpSuccessOperationResult<IiasGetResponseV6> findByHeiIdAndIiaCodes(
      String heiId, Collection<String> iiaCodes, Boolean sendPdf)
      throws EwpClientErrorException {
    EwpInterInstitutionalAgreementApiConfiguration api = getApiConfigurationForHeiId(heiId);

    HttpParams bodyParams = new HttpParams();
    bodyParams.param(EwpApiParamConstants.HEI_ID, heiId);
    bodyParams.param(EwpApiParamConstants.IIA_CODE, iiaCodes);
    bodyParams.param(EwpApiParamConstants.SEND_PDF, sendPdf);

    EwpRequest request = EwpRequest.createPost(api, api.getGetUrl(),
        new EwpRequestFormDataUrlEncodedBody(bodyParams));
    return ewpClient.executeAndLog(request, IiasGetResponseV6.class);
  }

  @Override
  public EwpApiVersionSpecification<?, EwpInterInstitutionalAgreementApiConfiguration>
  getApiVersionSpecification() {
    return InterInstitutionalAgreements.V6;
  }
}
