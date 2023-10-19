package pt.ulisboa.ewp.node.api.host.forward.ewp.controller.iias;

import eu.erasmuswithoutpaper.api.iias.v6.endpoints.IiasGetResponseV6;
import eu.erasmuswithoutpaper.api.iias.v6.endpoints.IiasGetResponseV6.Iia;
import eu.erasmuswithoutpaper.api.iias.v6.endpoints.IiasIndexResponseV6;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.InputSource;
import pt.ulisboa.ewp.node.api.host.forward.ewp.controller.AbstractForwardEwpApiController;
import pt.ulisboa.ewp.node.api.host.forward.ewp.controller.ForwardEwpApi;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponseWithData;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.iias.ForwardEwpApiInterInstitutionalAgreementHashValidationDto;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.iias.ForwardEwpApiInterInstitutionalAgreementV6WithHashValidationResponseDto;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.iias.ForwardEwpApiInterInstitutionalAgreementsApiSpecificationResponseDTO;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.iias.ForwardEwpApiInterInstitutionalAgreementsV6GetResponseDto;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.iias.InterInstitutionalAgreementsGetRequestDto;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.iias.InterInstitutionalAgreementsIndexRequestDto;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.iias.hash.calculation.request.ForwardEwpApiIiaHashesCalculationV6RequestDTO;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.iias.hash.calculation.response.ForwardEwpApiIiaHashesCalculationResponseDTO;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.ForwardEwpApiSecurityCommonConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiResponseUtils;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.iias.EwpInterInstitutionalAgreementsV6Client;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.exception.ewp.hash.HashCalculationException;
import pt.ulisboa.ewp.node.exception.ewp.hash.HashComparisonException;
import pt.ulisboa.ewp.node.service.ewp.iia.hash.HashCalculationResult;
import pt.ulisboa.ewp.node.service.ewp.iia.hash.HashComparisonResult;
import pt.ulisboa.ewp.node.service.ewp.iia.hash.IiaHashService;
import pt.ulisboa.ewp.node.utils.EwpApi;
import pt.ulisboa.ewp.node.utils.EwpApiNamespaces;

@RestController
@ForwardEwpApi(EwpApi.INTERINSTITUTIONAL_AGREEMENTS)
@RequestMapping(ForwardEwpApiConstants.API_BASE_URI + "iias/v6")
@Secured({ForwardEwpApiSecurityCommonConstants.ROLE_HOST_CLIENT_WITH_PREFIX})
public class ForwardEwpApiInterInstitutionalAgreementsV6Controller extends
    AbstractForwardEwpApiController {

  private final EwpInterInstitutionalAgreementsV6Client client;
  private final IiaHashService hashService;

  public ForwardEwpApiInterInstitutionalAgreementsV6Controller(RegistryClient registryClient,
      EwpInterInstitutionalAgreementsV6Client client, IiaHashService hashService) {
    super(registryClient);
    this.client = client;
    this.hashService = hashService;
  }

  @GetMapping(value = "/specification", produces = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity<ForwardEwpApiResponseWithData<ForwardEwpApiInterInstitutionalAgreementsApiSpecificationResponseDTO>> getApiSpecification(
      @NotEmpty @RequestParam(value = "hei_id") String heiId) {
    return ResponseEntity.ok(ForwardEwpApiResponseUtils.createResponseWithMessagesAndData(
        client.getApiSpecification(heiId)));
  }

  @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_XML_VALUE, value = "/index")
  public ResponseEntity<ForwardEwpApiResponseWithData<IiasIndexResponseV6>> findAllByHeiId(
      @Valid InterInstitutionalAgreementsIndexRequestDto requestDto)
      throws EwpClientErrorException {
    EwpSuccessOperationResult<IiasIndexResponseV6> response = client.findAllByHeiIds(
        requestDto.getHeiId(), requestDto.getPartnerHeiId(),
        requestDto.getReceivingAcademicYearIds(), requestDto.getModifiedSince());
    return ForwardEwpApiResponseUtils.toSuccessResponseEntity(response);
  }

  @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_XML_VALUE, value = "/get")
  public ResponseEntity<ForwardEwpApiResponseWithData<ForwardEwpApiInterInstitutionalAgreementsV6GetResponseDto>> findByHeiIdAndIiaIdsOrCodes(
      @Valid InterInstitutionalAgreementsGetRequestDto requestDto)
      throws EwpClientErrorException, HashComparisonException {
    EwpSuccessOperationResult<IiasGetResponseV6> response;
    List<String> iiaIds = requestDto.getIiaIds();
    List<String> iiaCodes = requestDto.getIiaCodes();
    if (!iiaIds.isEmpty()) {
      response = client.findByHeiIdAndIiaIds(requestDto.getHeiId(), iiaIds,
          requestDto.getSendPdf());
    } else {
      response = client.findByHeiIdAndIiaCodes(requestDto.getHeiId(), iiaCodes,
          requestDto.getSendPdf());
    }

    ForwardEwpApiInterInstitutionalAgreementsV6GetResponseDto getResponse = new ForwardEwpApiInterInstitutionalAgreementsV6GetResponseDto();
    byte[] rawBody = response.getResponse().getRawBody().getBytes(StandardCharsets.UTF_8);
    getResponse.setRawXmlInBase64(Base64Utils.encode(rawBody));
    List<HashComparisonResult> hashComparisonResults =
        this.hashService.checkCooperationConditionsHash(
            new InputSource(new ByteArrayInputStream(rawBody)),
            EwpApiNamespaces.IIAS_V6_GET_RESPONSE.getNamespaceUrl());
    int index = 0;
    for (Iia iia : response.getResponseBody().getIia()) {
      HashComparisonResult hashComparisonResult = hashComparisonResults.get(index);
      ForwardEwpApiInterInstitutionalAgreementHashValidationDto hashValidationDto = new ForwardEwpApiInterInstitutionalAgreementHashValidationDto(
          hashComparisonResult.getHashExtracted(), hashComparisonResult.getHashExpected(),
          hashComparisonResult.isCorrect());
      ForwardEwpApiInterInstitutionalAgreementV6WithHashValidationResponseDto iiaWithHashValidationResponseDto = new ForwardEwpApiInterInstitutionalAgreementV6WithHashValidationResponseDto(
          iia, hashValidationDto);
      getResponse.getIias().add(iiaWithHashValidationResponseDto);
      index++;
    }
    return ForwardEwpApiResponseUtils.toSuccessResponseEntity(getResponse);
  }

  @PostMapping(consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE, value = "/hashes/calculate")
  public ResponseEntity<ForwardEwpApiResponseWithData<ForwardEwpApiIiaHashesCalculationResponseDTO>> calculateCooperationConditionsHashes(
      @Valid @RequestBody ForwardEwpApiIiaHashesCalculationV6RequestDTO requestData)
      throws HashCalculationException {
    List<HashCalculationResult> hashCalculationResults = this.hashService.calculateCooperationConditionsHashesForV6(
        requestData.getIias());
    ForwardEwpApiIiaHashesCalculationResponseDTO response = new ForwardEwpApiIiaHashesCalculationResponseDTO(
        hashCalculationResults.stream().map(HashCalculationResult::getHash)
            .collect(Collectors.toList()));
    return ForwardEwpApiResponseUtils.toSuccessResponseEntity(response);
  }
}
