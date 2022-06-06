import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IWorkLog, WorkLog } from '../work-log.model';
import { WorkLogService } from '../service/work-log.service';
import { IShift } from 'app/entities/shift/shift.model';
import { ShiftService } from 'app/entities/shift/service/shift.service';

@Component({
  selector: 'jhi-work-log-update',
  templateUrl: './work-log-update.component.html',
})
export class WorkLogUpdateComponent implements OnInit {
  isSaving = false;

  shiftsSharedCollection: IShift[] = [];

  editForm = this.fb.group({
    id: [],
    note: [],
    startHour: [null, [Validators.required, Validators.min(0), Validators.max(23)]],
    startMinute: [null, [Validators.required, Validators.min(0), Validators.max(59)]],
    endHour: [null, [Validators.required, Validators.min(0), Validators.max(23)]],
    endMinute: [null, [Validators.required, Validators.min(0), Validators.max(59)]],
    optional: [],
    shift: [],
  });

  constructor(
    protected workLogService: WorkLogService,
    protected shiftService: ShiftService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ workLog }) => {
      this.updateForm(workLog);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const workLog = this.createFromForm();
    if (workLog.id !== undefined) {
      this.subscribeToSaveResponse(this.workLogService.update(workLog));
    } else {
      this.subscribeToSaveResponse(this.workLogService.create(workLog));
    }
  }

  trackShiftById(_index: number, item: IShift): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IWorkLog>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(workLog: IWorkLog): void {
    this.editForm.patchValue({
      id: workLog.id,
      note: workLog.note,
      startHour: workLog.startHour,
      startMinute: workLog.startMinute,
      endHour: workLog.endHour,
      endMinute: workLog.endMinute,
      optional: workLog.optional,
      shift: workLog.shift,
    });

    this.shiftsSharedCollection = this.shiftService.addShiftToCollectionIfMissing(this.shiftsSharedCollection, workLog.shift);
  }

  protected loadRelationshipsOptions(): void {
    this.shiftService
      .query()
      .pipe(map((res: HttpResponse<IShift[]>) => res.body ?? []))
      .pipe(map((shifts: IShift[]) => this.shiftService.addShiftToCollectionIfMissing(shifts, this.editForm.get('shift')!.value)))
      .subscribe((shifts: IShift[]) => (this.shiftsSharedCollection = shifts));
  }

  protected createFromForm(): IWorkLog {
    return {
      ...new WorkLog(),
      id: this.editForm.get(['id'])!.value,
      note: this.editForm.get(['note'])!.value,
      startHour: this.editForm.get(['startHour'])!.value,
      startMinute: this.editForm.get(['startMinute'])!.value,
      endHour: this.editForm.get(['endHour'])!.value,
      endMinute: this.editForm.get(['endMinute'])!.value,
      optional: this.editForm.get(['optional'])!.value,
      shift: this.editForm.get(['shift'])!.value,
    };
  }
}
