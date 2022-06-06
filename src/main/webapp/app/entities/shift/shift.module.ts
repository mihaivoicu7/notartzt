import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ShiftComponent } from './list/shift.component';
import { ShiftDetailComponent } from './detail/shift-detail.component';
import { ShiftUpdateComponent } from './update/shift-update.component';
import { ShiftDeleteDialogComponent } from './delete/shift-delete-dialog.component';
import { ShiftRoutingModule } from './route/shift-routing.module';

@NgModule({
  imports: [SharedModule, ShiftRoutingModule],
  declarations: [ShiftComponent, ShiftDetailComponent, ShiftUpdateComponent, ShiftDeleteDialogComponent],
  entryComponents: [ShiftDeleteDialogComponent],
})
export class ShiftModule {}
