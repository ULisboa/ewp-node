import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CommunicationsLogsSearchFormComponent } from './communications-logs-search-form.component';

describe('CommunicationsLogsSearchFormComponent', () => {
  let component: CommunicationsLogsSearchFormComponent;
  let fixture: ComponentFixture<CommunicationsLogsSearchFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CommunicationsLogsSearchFormComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(CommunicationsLogsSearchFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
