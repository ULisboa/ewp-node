import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CommunicationLogReportToMonitoringDialogComponent } from './communication-log-report-to-monitoring-form.component';

describe('CommunicationLogReportToMonitoringDialogComponent', () => {
  let component: CommunicationLogReportToMonitoringDialogComponent;
  let fixture: ComponentFixture<CommunicationLogReportToMonitoringDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CommunicationLogReportToMonitoringDialogComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(
      CommunicationLogReportToMonitoringDialogComponent
    );
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
