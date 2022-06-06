import { IShift } from 'app/entities/shift/shift.model';
import { ILocation } from 'app/entities/location/location.model';

export interface IShiftType {
  id?: number;
  name?: string;
  startHour?: number;
  startMinute?: number;
  endHour?: number;
  endMinute?: number;
  shifts?: IShift[] | null;
  location?: ILocation | null;
}

export class ShiftType implements IShiftType {
  constructor(
    public id?: number,
    public name?: string,
    public startHour?: number,
    public startMinute?: number,
    public endHour?: number,
    public endMinute?: number,
    public shifts?: IShift[] | null,
    public location?: ILocation | null
  ) {}
}

export function getShiftTypeIdentifier(shiftType: IShiftType): number | undefined {
  return shiftType.id;
}
