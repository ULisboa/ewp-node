import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AdminDashboardCommunicationsLogsPageComponent } from './communications-logs-page.component';

describe('AdminDashboardCommunicationsLogsPageComponent', () => {
  let component: AdminDashboardCommunicationsLogsPageComponent;
  let fixture: ComponentFixture<AdminDashboardCommunicationsLogsPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AdminDashboardCommunicationsLogsPageComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(
      AdminDashboardCommunicationsLogsPageComponent
    );
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
