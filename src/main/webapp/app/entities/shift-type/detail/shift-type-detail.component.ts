import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IShiftType } from '../shift-type.model';

@Component({
  selector: 'jhi-shift-type-detail',
  templateUrl: './shift-type-detail.component.html',
})
export class ShiftTypeDetailComponent implements OnInit {
  shiftType: IShiftType | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ shiftType }) => {
      this.shiftType = shiftType;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
