import { IShiftType } from 'app/entities/shift-type/shift-type.model';

export interface ILocation {
  id?: number;
  name?: string;
  shiftTypes?: IShiftType[] | null;
}

export class Location implements ILocation {
  constructor(public id?: number, public name?: string, public shiftTypes?: IShiftType[] | null) {}
}

export function getLocationIdentifier(location: ILocation): number | undefined {
  return location.id;
}
