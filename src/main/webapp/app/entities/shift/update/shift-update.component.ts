import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IShift, Shift } from '../shift.model';
import { ShiftService } from '../service/shift.service';
import { IShiftType } from 'app/entities/shift-type/shift-type.model';
import { ShiftTypeService } from 'app/entities/shift-type/service/shift-type.service';
import { ShiftStatus } from 'app/entities/enumerations/shift-status.model';

@Component({
  selector: 'jhi-shift-update',
  templateUrl: './shift-update.component.html',
})
export class ShiftUpdateComponent implements OnInit {
  isSaving = false;
  shiftStatusValues = Object.keys(ShiftStatus);

  shiftTypesSharedCollection: IShiftType[] = [];

  editForm = this.fb.group({
    id: [],
    date: [null, [Validators.required]],
    status: [],
    type: [],
  });

  constructor(
    protected shiftService: ShiftService,
    protected shiftTypeService: ShiftTypeService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ shift }) => {
      this.updateForm(shift);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const shift = this.createFromForm();
    if (shift.id !== undefined) {
      this.subscribeToSaveResponse(this.shiftService.update(shift));
    } else {
      this.subscribeToSaveResponse(this.shiftService.create(shift));
    }
  }

  trackShiftTypeById(_index: number, item: IShiftType): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IShift>>): void {
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

  protected updateForm(shift: IShift): void {
    this.editForm.patchValue({
      id: shift.id,
      date: shift.date,
      status: shift.status,
      type: shift.type,
    });

    this.shiftTypesSharedCollection = this.shiftTypeService.addShiftTypeToCollectionIfMissing(this.shiftTypesSharedCollection, shift.type);
  }

  protected loadRelationshipsOptions(): void {
    this.shiftTypeService
      .query()
      .pipe(map((res: HttpResponse<IShiftType[]>) => res.body ?? []))
      .pipe(
        map((shiftTypes: IShiftType[]) =>
          this.shiftTypeService.addShiftTypeToCollectionIfMissing(shiftTypes, this.editForm.get('type')!.value)
        )
      )
      .subscribe((shiftTypes: IShiftType[]) => (this.shiftTypesSharedCollection = shiftTypes));
  }

  protected createFromForm(): IShift {
    return {
      ...new Shift(),
      id: this.editForm.get(['id'])!.value,
      date: this.editForm.get(['date'])!.value,
      status: this.editForm.get(['status'])!.value,
      type: this.editForm.get(['type'])!.value,
    };
  }
}
