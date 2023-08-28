import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AdminDashboardHomeComponent } from './admin-dashboard-home.component';

describe('AdminDashboardHomeComponent', () => {
  let component: AdminDashboardHomeComponent;
  let fixture: ComponentFixture<AdminDashboardHomeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AdminDashboardHomeComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminDashboardHomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
