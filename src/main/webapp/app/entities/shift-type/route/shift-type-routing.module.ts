import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ShiftTypeComponent } from '../list/shift-type.component';
import { ShiftTypeDetailComponent } from '../detail/shift-type-detail.component';
import { ShiftTypeUpdateComponent } from '../update/shift-type-update.component';
import { ShiftTypeRoutingResolveService } from './shift-type-routing-resolve.service';

const shiftTypeRoute: Routes = [
  {
    path: '',
    component: ShiftTypeComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ShiftTypeDetailComponent,
    resolve: {
      shiftType: ShiftTypeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ShiftTypeUpdateComponent,
    resolve: {
      shiftType: ShiftTypeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ShiftTypeUpdateComponent,
    resolve: {
      shiftType: ShiftTypeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(shiftTypeRoute)],
  exports: [RouterModule],
})
export class ShiftTypeRoutingModule {}
