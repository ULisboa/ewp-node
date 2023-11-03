package pt.ulisboa.ewp.node.api.host.forward.ewp.controller.iias;

import eu.erasmuswithoutpaper.api.iias.v7.endpoints.IiasGetResponseV7;
import eu.erasmuswithoutpaper.api.iias.v7.endpoints.IiasGetResponseV7.Iia;
import eu.erasmuswithoutpaper.api.iias.v7.endpoints.IiasIndexResponseV7;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.node.api.host.forward.ewp.controller.AbstractForwardEwpApiController;
import pt.ulisboa.ewp.node.api.host.forward.ewp.controller.ForwardEwpApi;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponseWithData;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.iias.ForwardEwpApiInterInstitutionalAgreementHashValidationDto;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.iias.ForwardEwpApiInterInstitutionalAgreementV7WithHashValidationResponseDto;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.iias.ForwardEwpApiInterInstitutionalAgreementsApiSpecificationResponseDTO;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.iias.ForwardEwpApiInterInstitutionalAgreementsV7GetRequestDto;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.iias.ForwardEwpApiInterInstitutionalAgreementsV7GetResponseDto;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.iias.ForwardEwpApiInterInstitutionalAgreementsV7IndexRequestDto;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.iias.hash.calculation.request.ForwardEwpApiIiaHashesCalculationV7RequestDTO;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.iias.hash.calculation.response.ForwardEwpApiIiaHashesCalculationResponseDTO;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.ForwardEwpApiSecurityCommonConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiResponseUtils;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.iias.EwpInterInstitutionalAgreementsV7Client;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.exception.ewp.hash.HashCalculationException;
import pt.ulisboa.ewp.node.exception.ewp.hash.HashComparisonException;
import pt.ulisboa.ewp.node.service.ewp.iia.hash.HashCalculationResult;
import pt.ulisboa.ewp.node.service.ewp.iia.hash.HashComparisonResult;
import pt.ulisboa.ewp.node.service.ewp.iia.hash.v7.IiaHashServiceV7;
import pt.ulisboa.ewp.node.utils.EwpApi;

@RestController
@ForwardEwpApi(EwpApi.INTERINSTITUTIONAL_AGREEMENTS)
@RequestMapping(ForwardEwpApiConstants.API_BASE_URI + "iias/v7")
@Secured({ForwardEwpApiSecurityCommonConstants.ROLE_HOST_CLIENT_WITH_PREFIX})
public class ForwardEwpApiInterInstitutionalAgreementsV7Controller
    extends AbstractForwardEwpApiController {

  private final EwpInterInstitutionalAgreementsV7Client client;
  private final IiaHashServiceV7 hashService;

  public ForwardEwpApiInterInstitutionalAgreementsV7Controller(
      RegistryClient registryClient,
      EwpInterInstitutionalAgreementsV7Client client,
      IiaHashServiceV7 hashService) {
    super(registryClient);
    this.client = client;
    this.hashService = hashService;
  }

  @GetMapping(value = "/specification", produces = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity<
          ForwardEwpApiResponseWithData<
              ForwardEwpApiInterInstitutionalAgreementsApiSpecificationResponseDTO>>
      getApiSpecification(@NotEmpty @RequestParam(value = "hei_id") String heiId) {
    return ResponseEntity.ok(
        ForwardEwpApiResponseUtils.createResponseWithMessagesAndData(
            client.getApiSpecification(heiId)));
  }

  @PostMapping(
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
      produces = MediaType.APPLICATION_XML_VALUE,
      value = "/index")
  public ResponseEntity<ForwardEwpApiResponseWithData<IiasIndexResponseV7>> findAllByHeiId(
      @Valid ForwardEwpApiInterInstitutionalAgreementsV7IndexRequestDto requestDto)
      throws EwpClientErrorException {
    EwpSuccessOperationResult<IiasIndexResponseV7> response =
        client.findAllByHeiIds(
            requestDto.getHeiId(), requestDto.getReceivingAcademicYearIds(), 
            requestDto.getModifiedSince());
    return ForwardEwpApiResponseUtils.toSuccessResponseEntity(response);
  }

  @PostMapping(
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
      produces = MediaType.APPLICATION_XML_VALUE,
      value = "/get")
  public ResponseEntity<
          ForwardEwpApiResponseWithData<ForwardEwpApiInterInstitutionalAgreementsV7GetResponseDto>>
      findByHeiIdAndIiaIdsOrCodes(@Valid ForwardEwpApiInterInstitutionalAgreementsV7GetRequestDto requestDto)
          throws EwpClientErrorException, HashComparisonException {
    List<String> iiaIds = requestDto.getIiaIds();
    EwpSuccessOperationResult<IiasGetResponseV7> response = client.findByHeiIdAndIiaIds(requestDto.getHeiId(), iiaIds);

    ForwardEwpApiInterInstitutionalAgreementsV7GetResponseDto getResponse =
        new ForwardEwpApiInterInstitutionalAgreementsV7GetResponseDto();
    byte[] rawBody = response.getResponse().getRawBody().getBytes(StandardCharsets.UTF_8);
    getResponse.setRawXmlInBase64(Base64Utils.encode(rawBody));
    List<HashComparisonResult> hashComparisonResults = this.hashService.checkIiaHashes(rawBody);
    int index = 0;
    for (Iia iia : response.getResponseBody().getIia()) {
      HashComparisonResult hashComparisonResult = hashComparisonResults.get(index);
      ForwardEwpApiInterInstitutionalAgreementHashValidationDto hashValidationDto =
          new ForwardEwpApiInterInstitutionalAgreementHashValidationDto(
              hashComparisonResult.getHashExtracted(),
              hashComparisonResult.getHashExpected(),
              hashComparisonResult.isCorrect());
      ForwardEwpApiInterInstitutionalAgreementV7WithHashValidationResponseDto
          iiaWithHashValidationResponseDto =
              new ForwardEwpApiInterInstitutionalAgreementV7WithHashValidationResponseDto(
                  iia, hashValidationDto);
      getResponse.getIias().add(iiaWithHashValidationResponseDto);
      index++;
    }
    return ForwardEwpApiResponseUtils.toSuccessResponseEntity(getResponse);
  }

  @PostMapping(
      consumes = MediaType.APPLICATION_XML_VALUE,
      produces = MediaType.APPLICATION_XML_VALUE,
      value = "/hashes/calculate")
  public ResponseEntity<ForwardEwpApiResponseWithData<ForwardEwpApiIiaHashesCalculationResponseDTO>>
      calculateIiaHashes(
          @Valid @RequestBody ForwardEwpApiIiaHashesCalculationV7RequestDTO requestData)
          throws HashCalculationException {
    List<HashCalculationResult> hashCalculationResults =
        this.hashService.calculateIiaHashes(requestData.getIias());
    ForwardEwpApiIiaHashesCalculationResponseDTO response =
        new ForwardEwpApiIiaHashesCalculationResponseDTO(
            hashCalculationResults.stream()
                .map(HashCalculationResult::getHash)
                .collect(Collectors.toList()));
    return ForwardEwpApiResponseUtils.toSuccessResponseEntity(response);
  }

  @PostMapping(
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_XML_VALUE,
      value = "/hashes/calculate")
  public ResponseEntity<ForwardEwpApiResponseWithData<ForwardEwpApiIiaHashesCalculationResponseDTO>>
      calculateIiaHashes(
          @RequestPart("xml") byte[] xml,
          @RequestParam(value = "sourceMajorVersion", defaultValue = "7") int sourceMajorVersion)
          throws HashCalculationException {
    List<HashCalculationResult> hashCalculationResults =
        this.hashService.calculateIiaHashes(xml, sourceMajorVersion);
    ForwardEwpApiIiaHashesCalculationResponseDTO response =
        new ForwardEwpApiIiaHashesCalculationResponseDTO(
            hashCalculationResults.stream()
                .map(HashCalculationResult::getHash)
                .collect(Collectors.toList()));
    return ForwardEwpApiResponseUtils.toSuccessResponseEntity(response);
  }
}
