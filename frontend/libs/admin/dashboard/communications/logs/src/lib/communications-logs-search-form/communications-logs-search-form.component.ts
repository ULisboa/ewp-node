import { AfterViewInit, Component, EventEmitter, Output } from '@angular/core';
import { FormBuilder, FormGroup, UntypedFormArray, UntypedFormBuilder, Validators } from '@angular/forms';
import { DropdownChangeEvent } from 'primeng/dropdown';
import { Observable, of } from 'rxjs';

@Component({
  selector: 'lib-admin-communications-logs-search-form',
  templateUrl: './communications-logs-search-form.component.html',
  styleUrl: './communications-logs-search-form.component.scss',
})
export class CommunicationsLogsSearchFormComponent implements AfterViewInit {

  FILTER_TYPE_HTTP_COMMUNICATION_FORM_PARAMETER_STARTS_WITH_VALUE = 'HTTP-COMMUNICATION-FORM-PARAMETER-STARTS-WITH-VALUE';

  @Output()
  filterHandler = new EventEmitter<object>();

  searchForm = this.formBuilder.group({
    type: this.formBuilder.control('CONJUNCTION', [Validators.required]),
    subFilters: this.formBuilder.array([])
  });

  filterTypes: { name: string, value: string }[] = [
    { name: 'HTTP Request has form parameter with value', value: this.FILTER_TYPE_HTTP_COMMUNICATION_FORM_PARAMETER_STARTS_WITH_VALUE }
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
      this.addSubFilter();
    });
  }

  get subFilters() {
    return this.searchForm.controls['subFilters'] as UntypedFormArray;
  }

  addSubFilter() {
    const filterForm = this.formBuilder.group({
      type: this.formBuilder.control('', [Validators.required]),
      parameter: this.formBuilder.control('', []),
      value: this.formBuilder.control('', [])
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
