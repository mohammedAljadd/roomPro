import { TestBed } from '@angular/core/testing';

import { UserbookingsService } from './userbookings.service';

describe('UserbookingsService', () => {
  let service: UserbookingsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UserbookingsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
