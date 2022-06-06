import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IShift } from '../shift.model';
import { ShiftService } from '../service/shift.service';

@Component({
  templateUrl: './shift-delete-dialog.component.html',
})
export class ShiftDeleteDialogComponent {
  shift?: IShift;

  constructor(protected shiftService: ShiftService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.shiftService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
