import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IWorkLog } from '../work-log.model';
import { WorkLogService } from '../service/work-log.service';

@Component({
  templateUrl: './work-log-delete-dialog.component.html',
})
export class WorkLogDeleteDialogComponent {
  workLog?: IWorkLog;

  constructor(protected workLogService: WorkLogService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.workLogService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
