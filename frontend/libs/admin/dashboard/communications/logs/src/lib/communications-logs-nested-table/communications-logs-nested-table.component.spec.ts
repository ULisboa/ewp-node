import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AdminDashboardCommunicationsLogsNestedTableComponent } from './communications-logs-nested-table.component';

describe('AdminDashboardCommunicationsLogsNestedTableComponent', () => {
  let component: AdminDashboardCommunicationsLogsNestedTableComponent;
  let fixture: ComponentFixture<AdminDashboardCommunicationsLogsNestedTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AdminDashboardCommunicationsLogsNestedTableComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(
      AdminDashboardCommunicationsLogsNestedTableComponent
    );
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
