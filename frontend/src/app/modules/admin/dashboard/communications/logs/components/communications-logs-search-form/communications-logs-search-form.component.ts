import { AfterViewInit, Component, EventEmitter, Output } from '@angular/core';
import { FormBuilder, UntypedFormArray, Validators } from '@angular/forms';

@Component({
  selector: 'app-admin-communications-logs-search-form',
  templateUrl: './communications-logs-search-form.component.html'
})
export class AdminDashboardCommunicationsLogsSearchFormComponent implements AfterViewInit {

  FILTER_TYPE_COMMUNICATION_LOG_IS_ROOT = 'COMMUNICATION-LOG-IS-ROOT';
  FILTER_TYPE_HTTP_COMMUNICATION_FORM_PARAMETER_STARTS_WITH_VALUE = 'HTTP-COMMUNICATION-FORM-PARAMETER-STARTS-WITH-VALUE';
  FILTER_TYPE_HTTP_COMMUNICATION_RESPONSE_WITH_STATUS_CODE = 'HTTP-COMMUNICATION-RESPONSE-WITH-STATUS-CODE';
  FILTER_TYPE_HTTP_COMMUNICATION_TO_API_ENDPOINT = 'HTTP-COMMUNICATION-TO-API-ENDPOINT';

  @Output()
  filterHandler = new EventEmitter<object>();

  searchForm = this.formBuilder.group({
    type: this.formBuilder.control('CONJUNCTION', [Validators.required]),
    subFilters: this.formBuilder.array([])
  });

  filterTypes: { name: string, value: string }[] = [
    { name: 'Communication log is a root (has no parent communication defined)', value: this.FILTER_TYPE_COMMUNICATION_LOG_IS_ROOT },
    { name: 'HTTP Request has form parameter with value', value: this.FILTER_TYPE_HTTP_COMMUNICATION_FORM_PARAMETER_STARTS_WITH_VALUE },
    { name: 'HTTP Response has a specific status code', value: this.FILTER_TYPE_HTTP_COMMUNICATION_RESPONSE_WITH_STATUS_CODE },
    { name: 'Target is an API Endpoint', value: this.FILTER_TYPE_HTTP_COMMUNICATION_TO_API_ENDPOINT }
  ];

  parameterNames = [
    { name: 'File ID', value: 'file_id' },
    { name: 'HEI ID', value: 'hei_id' },
    { name: 'IIA ID', value: 'iia_id' },
    { name: 'LOS ID', value: 'los_id' },
    { name: 'Mobility ID', value: 'omobility_id' },
    { name: 'Organizational Unit ID', value: 'ounit_id' }
  ];

  filterTypeSelected?: { name: string, value: string };

  constructor(private formBuilder: FormBuilder) {}

  ngAfterViewInit() {
    // NOTE setTimeout is used to skip a tick so there is no error of component changed on rendering
    setTimeout(() => {
      this.addSubFilter(this.FILTER_TYPE_COMMUNICATION_LOG_IS_ROOT);
      this.search();
    });
  }

  get subFilters() {
    return this.searchForm.controls['subFilters'] as UntypedFormArray;
  }

  addSubFilter(type?: string) {
    // NOTE: this form group has all possible control field names
    const filterForm = this.formBuilder.group({
      type: this.formBuilder.control('', [Validators.required]),
      parameter: this.formBuilder.control('', []),
      value: this.formBuilder.control('', []),
      apiName: this.formBuilder.control('', []),
      apiVersion: this.formBuilder.control('', []),
      endpointName: this.formBuilder.control('', [])
    });
    filterForm.patchValue({
      type: type
    });

    this.subFilters.push(filterForm);
  }

  deleteSubFilter(filterIndex: number) {
    this.subFilters.removeAt(filterIndex);
  }

  search() {
    this.filterHandler.emit(this.searchForm.value);
  }

}
