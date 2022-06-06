import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IShiftType, ShiftType } from '../shift-type.model';
import { ShiftTypeService } from '../service/shift-type.service';
import { ILocation } from 'app/entities/location/location.model';
import { LocationService } from 'app/entities/location/service/location.service';

@Component({
  selector: 'jhi-shift-type-update',
  templateUrl: './shift-type-update.component.html',
})
export class ShiftTypeUpdateComponent implements OnInit {
  isSaving = false;

  locationsSharedCollection: ILocation[] = [];

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    startHour: [null, [Validators.required, Validators.min(0), Validators.max(23)]],
    startMinute: [null, [Validators.required, Validators.min(0), Validators.max(59)]],
    endHour: [null, [Validators.required, Validators.min(0), Validators.max(23)]],
    endMinute: [null, [Validators.required, Validators.min(0), Validators.max(59)]],
    location: [],
  });

  constructor(
    protected shiftTypeService: ShiftTypeService,
    protected locationService: LocationService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ shiftType }) => {
      this.updateForm(shiftType);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const shiftType = this.createFromForm();
    if (shiftType.id !== undefined) {
      this.subscribeToSaveResponse(this.shiftTypeService.update(shiftType));
    } else {
      this.subscribeToSaveResponse(this.shiftTypeService.create(shiftType));
    }
  }

  trackLocationById(_index: number, item: ILocation): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IShiftType>>): void {
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

  protected updateForm(shiftType: IShiftType): void {
    this.editForm.patchValue({
      id: shiftType.id,
      name: shiftType.name,
      startHour: shiftType.startHour,
      startMinute: shiftType.startMinute,
      endHour: shiftType.endHour,
      endMinute: shiftType.endMinute,
      location: shiftType.location,
    });

    this.locationsSharedCollection = this.locationService.addLocationToCollectionIfMissing(
      this.locationsSharedCollection,
      shiftType.location
    );
  }

  protected loadRelationshipsOptions(): void {
    this.locationService
      .query()
      .pipe(map((res: HttpResponse<ILocation[]>) => res.body ?? []))
      .pipe(
        map((locations: ILocation[]) =>
          this.locationService.addLocationToCollectionIfMissing(locations, this.editForm.get('location')!.value)
        )
      )
      .subscribe((locations: ILocation[]) => (this.locationsSharedCollection = locations));
  }

  protected createFromForm(): IShiftType {
    return {
      ...new ShiftType(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      startHour: this.editForm.get(['startHour'])!.value,
      startMinute: this.editForm.get(['startMinute'])!.value,
      endHour: this.editForm.get(['endHour'])!.value,
      endMinute: this.editForm.get(['endMinute'])!.value,
      location: this.editForm.get(['location'])!.value,
    };
  }
}
