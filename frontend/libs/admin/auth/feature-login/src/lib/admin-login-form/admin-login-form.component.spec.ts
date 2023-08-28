import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AdminLoginFormComponent } from './admin-login-form.component';

describe('AdminLoginFormComponent', () => {
  let component: AdminLoginFormComponent;
  let fixture: ComponentFixture<AdminLoginFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AdminLoginFormComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminLoginFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
