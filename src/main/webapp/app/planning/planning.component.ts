/* eslint-disable */
import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';
import { IUser } from '../admin/user-management/user-management.model';
import dayjs from 'dayjs/esm';
import { NgbDatepickerNavigateEvent } from '@ng-bootstrap/ng-bootstrap';
import { LocationService } from '../entities/location/service/location.service';
import { ILocation } from '../entities/location/location.model';
import { IShiftType } from '../entities/shift-type/shift-type.model';

@Component({
  selector: 'jhi-planning',
  templateUrl: './planning.component.html',
  styleUrls: ['./planning.component.scss'],
})
export class PlanningComponent implements OnInit, OnDestroy {
  account: Account | null = null;
  days: any[] = [];
  users: IUser | null = null;
  fromDate: dayjs.Dayjs = dayjs();
  toDate: dayjs.Dayjs = dayjs();
  locations: ILocation[] | null = [];
  selectedLocation: ILocation | null = null;

  private readonly destroy$ = new Subject<void>();

  constructor(private accountService: AccountService, private router: Router,
              private locationService: LocationService) {
  }

  ngOnInit(): void {
    this.accountService
      .getAuthenticationState()
      .pipe(takeUntil(this.destroy$))
      .subscribe(account => (this.account = account));
    this.locationService.query().subscribe(next => {
      this.locations = next.body;
      if (this.locations && this.locations.length > 0) {
        this.selectedLocation = this.locations[0];
      }
    });
    this.fromDate = dayjs();
    this.createDaysArray();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  getLineClass = (day: any): string => {
    if (['Sa', 'So'].includes(day.dayOfWeek)) {
      return 'table-active';
    }
    return '';
  };

  dateNavigate = ($event: NgbDatepickerNavigateEvent): void => {
    this.fromDate = this.fromDate.year($event.next.year);
    this.fromDate = this.fromDate.month($event.next.month - 1);
    this.fromDate = this.fromDate.startOf('month');
    this.toDate = this.fromDate.endOf('month');
    this.createDaysArray();
  };

  createDaysArray = (): void => {
    this.days = [];
    const endOfMonth = this.fromDate.endOf('month');
    const daysOfWeek = ['Mo', 'Di', 'Mi', 'Do', 'Fr', 'Sa', 'So'];
    let currentDay = this.fromDate.startOf('month');
    while (!currentDay.isAfter(endOfMonth)) {
      this.days.push({
        date: currentDay.format('DD.MM.YYYY'),
        dayOfWeek: daysOfWeek[currentDay.day()],
      });
      currentDay = currentDay.add(1, 'days');
    }
  };

  getShiftNameTitle = (shiftType: IShiftType): string => {
    return `${shiftType.name} ${this.formatTime(shiftType.startHour, shiftType.startMinute)} - ${this.formatTime(
      shiftType.endHour, shiftType.endMinute)}`;
  };

  formatTime = (startHour?: number, startMinute?: number): string => {
    return `${this.padStartWithZero(startHour)}:${this.padStartWithZero(startMinute)}`
  };

  padStartWithZero = (unit?: number): string => {
    return String(unit).padStart(2, '0');
  }
}
