import { IShift } from 'app/entities/shift/shift.model';

export interface IWorkLog {
  id?: number;
  note?: string | null;
  startHour?: number;
  startMinute?: number;
  endHour?: number;
  endMinute?: number;
  optional?: boolean | null;
  shift?: IShift | null;
}

export class WorkLog implements IWorkLog {
  constructor(
    public id?: number,
    public note?: string | null,
    public startHour?: number,
    public startMinute?: number,
    public endHour?: number,
    public endMinute?: number,
    public optional?: boolean | null,
    public shift?: IShift | null
  ) {
    this.optional = this.optional ?? false;
  }
}

export function getWorkLogIdentifier(workLog: IWorkLog): number | undefined {
  return workLog.id;
}
