import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IShift, Shift } from '../shift.model';
import { ShiftService } from '../service/shift.service';

@Injectable({ providedIn: 'root' })
export class ShiftRoutingResolveService implements Resolve<IShift> {
  constructor(protected service: ShiftService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IShift> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((shift: HttpResponse<Shift>) => {
          if (shift.body) {
            return of(shift.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Shift());
  }
}
