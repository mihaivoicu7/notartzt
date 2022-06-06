import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IWorkLog, WorkLog } from '../work-log.model';
import { WorkLogService } from '../service/work-log.service';

@Injectable({ providedIn: 'root' })
export class WorkLogRoutingResolveService implements Resolve<IWorkLog> {
  constructor(protected service: WorkLogService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IWorkLog> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((workLog: HttpResponse<WorkLog>) => {
          if (workLog.body) {
            return of(workLog.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new WorkLog());
  }
}
