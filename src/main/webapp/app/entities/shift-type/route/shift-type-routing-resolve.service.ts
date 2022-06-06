import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IShiftType, ShiftType } from '../shift-type.model';
import { ShiftTypeService } from '../service/shift-type.service';

@Injectable({ providedIn: 'root' })
export class ShiftTypeRoutingResolveService implements Resolve<IShiftType> {
  constructor(protected service: ShiftTypeService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IShiftType> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((shiftType: HttpResponse<ShiftType>) => {
          if (shiftType.body) {
            return of(shiftType.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new ShiftType());
  }
}
