import { TestBed } from '@angular/core/testing';

import { CleaningRequestsService } from './cleaning-requests.service';

describe('CleaningRequestsService', () => {
  let service: CleaningRequestsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CleaningRequestsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
