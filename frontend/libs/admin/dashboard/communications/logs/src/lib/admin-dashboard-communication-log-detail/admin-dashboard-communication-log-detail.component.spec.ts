import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AdminDashboardCommunicationLogDetailComponent } from './admin-dashboard-communication-log-detail.component';

describe('AdminDashboardCommunicationLogDetailComponent', () => {
  let component: AdminDashboardCommunicationLogDetailComponent;
  let fixture: ComponentFixture<AdminDashboardCommunicationLogDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AdminDashboardCommunicationLogDetailComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(
      AdminDashboardCommunicationLogDetailComponent
    );
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
