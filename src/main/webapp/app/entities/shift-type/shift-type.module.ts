import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ShiftTypeComponent } from './list/shift-type.component';
import { ShiftTypeDetailComponent } from './detail/shift-type-detail.component';
import { ShiftTypeUpdateComponent } from './update/shift-type-update.component';
import { ShiftTypeDeleteDialogComponent } from './delete/shift-type-delete-dialog.component';
import { ShiftTypeRoutingModule } from './route/shift-type-routing.module';

@NgModule({
  imports: [SharedModule, ShiftTypeRoutingModule],
  declarations: [ShiftTypeComponent, ShiftTypeDetailComponent, ShiftTypeUpdateComponent, ShiftTypeDeleteDialogComponent],
  entryComponents: [ShiftTypeDeleteDialogComponent],
})
export class ShiftTypeModule {}
