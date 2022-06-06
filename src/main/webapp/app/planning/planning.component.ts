import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';
import { IUser } from '../admin/user-management/user-management.model';
import dayjs from 'dayjs/esm';

@Component({
  selector: 'jhi-planning',
  templateUrl: './planning.component.html',
  styleUrls: ['./planning.component.scss'],
})
export class PlanningComponent implements OnInit, OnDestroy {
  account: Account | null = null;
  days: any[] = [];
  users: IUser | null = null;

  private readonly destroy$ = new Subject<void>();

  constructor(private accountService: AccountService, private router: Router) {
  }

  ngOnInit(): void {
    this.accountService
      .getAuthenticationState()
      .pipe(takeUntil(this.destroy$))
      .subscribe(account => (this.account = account));
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

  private createDaysArray = (): void => {
    this.days = [];
    const now = dayjs();
    const endOfMonth = now.endOf('month');
    const daysOfWeek = ['Mo', 'Di', 'Mi', 'Do', 'Fr', 'Sa', 'So'];
    let currentDay = now.startOf('month');
    while (!currentDay.isAfter(endOfMonth)) {
      this.days.push({
        date: currentDay.format('DD.MM.YYYY'),
        dayOfWeek: daysOfWeek[currentDay.day()],
      });
      currentDay = currentDay.add(1, 'days');
    }
  };
}
