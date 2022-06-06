import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ShiftComponent } from '../list/shift.component';
import { ShiftDetailComponent } from '../detail/shift-detail.component';
import { ShiftUpdateComponent } from '../update/shift-update.component';
import { ShiftRoutingResolveService } from './shift-routing-resolve.service';

const shiftRoute: Routes = [
  {
    path: '',
    component: ShiftComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ShiftDetailComponent,
    resolve: {
      shift: ShiftRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ShiftUpdateComponent,
    resolve: {
      shift: ShiftRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ShiftUpdateComponent,
    resolve: {
      shift: ShiftRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(shiftRoute)],
  exports: [RouterModule],
})
export class ShiftRoutingModule {}
