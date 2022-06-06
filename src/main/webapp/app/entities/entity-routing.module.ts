import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'shift',
        data: { pageTitle: 'notartztApp.shift.home.title' },
        loadChildren: () => import('./shift/shift.module').then(m => m.ShiftModule),
      },
      {
        path: 'location',
        data: { pageTitle: 'notartztApp.location.home.title' },
        loadChildren: () => import('./location/location.module').then(m => m.LocationModule),
      },
      {
        path: 'shift-type',
        data: { pageTitle: 'notartztApp.shiftType.home.title' },
        loadChildren: () => import('./shift-type/shift-type.module').then(m => m.ShiftTypeModule),
      },
      {
        path: 'work-log',
        data: { pageTitle: 'notartztApp.workLog.home.title' },
        loadChildren: () => import('./work-log/work-log.module').then(m => m.WorkLogModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
