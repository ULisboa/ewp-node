import { TestBed } from '@angular/core/testing';
import { CanMatchFn } from '@angular/router';

import { adminAuthCanMatchGuard } from './admin-auth.guard';

describe('adminAuthCanMatchGuard', () => {
  const executeGuard: CanMatchFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => adminAuthCanMatchGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
