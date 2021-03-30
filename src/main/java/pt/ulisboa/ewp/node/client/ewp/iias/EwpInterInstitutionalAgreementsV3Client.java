package pt.ulisboa.ewp.node.client.ewp.iias;

import eu.erasmuswithoutpaper.api.iias.v3.endpoints.IiasGetResponseV3;
import eu.erasmuswithoutpaper.api.iias.v3.endpoints.IiasIndexResponseV3;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.iias.ForwardEwpApiInterInstitutionalAgreementsApiSpecificationResponseDTO;
import pt.ulisboa.ewp.node.client.ewp.EwpApiClient;
import pt.ulisboa.ewp.node.client.ewp.EwpClient;
import pt.ulisboa.ewp.node.client.ewp.exception.AbstractEwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.result.success.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpInterinstitutionalAgreementApiConfiguration;
import pt.ulisboa.ewp.node.utils.EwpApiGeneralSpecifications;
import pt.ulisboa.ewp.node.utils.EwpApiGeneralSpecifications.EwpApiGeneralSpecification;
import pt.ulisboa.ewp.node.utils.http.HttpParams;

@Service
public class EwpInterInstitutionalAgreementsV3Client
    extends EwpApiClient<EwpInterinstitutionalAgreementApiConfiguration> {

  public EwpInterInstitutionalAgreementsV3Client(
      RegistryClient registryClient, EwpClient ewpClient) {
    super(registryClient, ewpClient);
  }

  public ForwardEwpApiInterInstitutionalAgreementsApiSpecificationResponseDTO getApiSpecification(
      String heiId) {
    EwpInterinstitutionalAgreementApiConfiguration apiConfiguration =
        getApiConfigurationForHeiId(heiId);
    return new ForwardEwpApiInterInstitutionalAgreementsApiSpecificationResponseDTO(
        apiConfiguration.getMaxIiaIds().intValueExact(),
        apiConfiguration.getMaxIiaCodes().intValueExact());
  }

  public EwpSuccessOperationResult<IiasIndexResponseV3> findAllByHeiIds(
      String heiId,
      String partnerHeiId,
      List<String> receivingAcademicYearIds,
      ZonedDateTime modifiedSince)
      throws AbstractEwpClientErrorException {
    EwpInterinstitutionalAgreementApiConfiguration api = getApiConfigurationForHeiId(heiId);

    HttpParams bodyParams = new HttpParams();
    bodyParams.param(EwpApiParamConstants.HEI_ID, heiId);
    bodyParams.param(EwpApiParamConstants.PARTNER_HEI_ID, partnerHeiId);
    bodyParams.param(EwpApiParamConstants.RECEIVING_ACADEMIC_YEAR_ID, receivingAcademicYearIds);
    bodyParams.param(EwpApiParamConstants.MODIFIED_SINCE, modifiedSince);

    EwpRequest request = EwpRequest.createPost(api, api.getIndexUrl(), bodyParams);
    return ewpClient.executeWithLoggingExpectingSuccess(request, IiasIndexResponseV3.class);
  }

  public EwpSuccessOperationResult<IiasGetResponseV3> findByHeiIdAndIiaIds(
      String heiId, Collection<String> iiaIds, Boolean sendPdf)
      throws AbstractEwpClientErrorException {
    EwpInterinstitutionalAgreementApiConfiguration api = getApiConfigurationForHeiId(heiId);

    HttpParams bodyParams = new HttpParams();
    bodyParams.param(EwpApiParamConstants.HEI_ID, heiId);
    bodyParams.param(EwpApiParamConstants.IIA_ID, iiaIds);
    bodyParams.param(EwpApiParamConstants.SEND_PDF, sendPdf);

    EwpRequest request = EwpRequest.createPost(api, api.getGetUrl(), bodyParams);
    return ewpClient.executeWithLoggingExpectingSuccess(request, IiasGetResponseV3.class);
  }

  public EwpSuccessOperationResult<IiasGetResponseV3> findByHeiIdAndIiaCodes(
      String heiId, Collection<String> iiaCodes, Boolean sendPdf)
      throws AbstractEwpClientErrorException {
    EwpInterinstitutionalAgreementApiConfiguration api = getApiConfigurationForHeiId(heiId);

    HttpParams bodyParams = new HttpParams();
    bodyParams.param(EwpApiParamConstants.HEI_ID, heiId);
    bodyParams.param(EwpApiParamConstants.IIA_CODE, iiaCodes);
    bodyParams.param(EwpApiParamConstants.SEND_PDF, sendPdf);

    EwpRequest request = EwpRequest.createPost(api, api.getGetUrl(), bodyParams);
    return ewpClient.executeWithLoggingExpectingSuccess(request, IiasGetResponseV3.class);
  }

  @Override
  public EwpApiGeneralSpecification<?, EwpInterinstitutionalAgreementApiConfiguration>
  getApiGeneralSpecification() {
    return EwpApiGeneralSpecifications.INTERINSTITUTIONAL_AGREEMENT_V3;
  }
}
