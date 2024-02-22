import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UiPrismComponent } from './prism.component';

describe('UiPrismComponent', () => {
  let component: UiPrismComponent;
  let fixture: ComponentFixture<UiPrismComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UiPrismComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(UiPrismComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
