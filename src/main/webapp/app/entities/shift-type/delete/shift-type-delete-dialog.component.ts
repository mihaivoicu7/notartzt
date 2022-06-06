import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IShiftType } from '../shift-type.model';
import { ShiftTypeService } from '../service/shift-type.service';

@Component({
  templateUrl: './shift-type-delete-dialog.component.html',
})
export class ShiftTypeDeleteDialogComponent {
  shiftType?: IShiftType;

  constructor(protected shiftTypeService: ShiftTypeService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.shiftTypeService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
