import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from 'app/shared/shared.module';
import { HOME_ROUTE } from './planning.route';
import { PlanningComponent } from './planning.component';

@NgModule({
  imports: [SharedModule, RouterModule.forChild([HOME_ROUTE])],
  declarations: [PlanningComponent],
})
export class PlanningModule {}
