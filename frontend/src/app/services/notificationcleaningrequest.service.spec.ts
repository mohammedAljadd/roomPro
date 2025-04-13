import { TestBed } from '@angular/core/testing';

import { NotificationcleaningrequestService } from './notificationcleaningrequest.service';

describe('NotificationcleaningrequestService', () => {
  let service: NotificationcleaningrequestService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(NotificationcleaningrequestService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
