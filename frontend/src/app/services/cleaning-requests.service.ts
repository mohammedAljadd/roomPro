import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CleaningRequestsService {

  private _pendingCountUpdated = new BehaviorSubject<void>(undefined);
  pendingCountUpdated$ = this._pendingCountUpdated.asObservable();

  notifyPendingCountChanged() {
    this._pendingCountUpdated.next();
  }
}
