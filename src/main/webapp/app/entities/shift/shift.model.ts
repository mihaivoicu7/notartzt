import dayjs from 'dayjs/esm';
import { IWorkLog } from 'app/entities/work-log/work-log.model';
import { IShiftType } from 'app/entities/shift-type/shift-type.model';
import { ShiftStatus } from 'app/entities/enumerations/shift-status.model';

export interface IShift {
  id?: number;
  date?: dayjs.Dayjs;
  status?: ShiftStatus | null;
  workLogs?: IWorkLog[] | null;
  type?: IShiftType | null;
}

export class Shift implements IShift {
  constructor(
    public id?: number,
    public date?: dayjs.Dayjs,
    public status?: ShiftStatus | null,
    public workLogs?: IWorkLog[] | null,
    public type?: IShiftType | null
  ) {}
}

export function getShiftIdentifier(shift: IShift): number | undefined {
  return shift.id;
}
