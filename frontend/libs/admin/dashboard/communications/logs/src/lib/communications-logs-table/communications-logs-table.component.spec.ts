import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AdminDashboardCommunicationsLogsTableComponent } from './communications-logs-table.component';

describe('AdminDashboardCommunicationsLogsTableComponent', () => {
  let component: AdminDashboardCommunicationsLogsTableComponent;
  let fixture: ComponentFixture<AdminDashboardCommunicationsLogsTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AdminDashboardCommunicationsLogsTableComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(
      AdminDashboardCommunicationsLogsTableComponent
    );
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
