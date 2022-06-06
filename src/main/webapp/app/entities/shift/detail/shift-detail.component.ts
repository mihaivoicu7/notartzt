import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IShift } from '../shift.model';

@Component({
  selector: 'jhi-shift-detail',
  templateUrl: './shift-detail.component.html',
})
export class ShiftDetailComponent implements OnInit {
  shift: IShift | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ shift }) => {
      this.shift = shift;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
