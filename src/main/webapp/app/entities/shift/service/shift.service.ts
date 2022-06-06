import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IShift, getShiftIdentifier } from '../shift.model';

export type EntityResponseType = HttpResponse<IShift>;
export type EntityArrayResponseType = HttpResponse<IShift[]>;

@Injectable({ providedIn: 'root' })
export class ShiftService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/shifts');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(shift: IShift): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(shift);
    return this.http
      .post<IShift>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(shift: IShift): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(shift);
    return this.http
      .put<IShift>(`${this.resourceUrl}/${getShiftIdentifier(shift) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(shift: IShift): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(shift);
    return this.http
      .patch<IShift>(`${this.resourceUrl}/${getShiftIdentifier(shift) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IShift>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IShift[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addShiftToCollectionIfMissing(shiftCollection: IShift[], ...shiftsToCheck: (IShift | null | undefined)[]): IShift[] {
    const shifts: IShift[] = shiftsToCheck.filter(isPresent);
    if (shifts.length > 0) {
      const shiftCollectionIdentifiers = shiftCollection.map(shiftItem => getShiftIdentifier(shiftItem)!);
      const shiftsToAdd = shifts.filter(shiftItem => {
        const shiftIdentifier = getShiftIdentifier(shiftItem);
        if (shiftIdentifier == null || shiftCollectionIdentifiers.includes(shiftIdentifier)) {
          return false;
        }
        shiftCollectionIdentifiers.push(shiftIdentifier);
        return true;
      });
      return [...shiftsToAdd, ...shiftCollection];
    }
    return shiftCollection;
  }

  protected convertDateFromClient(shift: IShift): IShift {
    return Object.assign({}, shift, {
      date: shift.date?.isValid() ? shift.date.format(DATE_FORMAT) : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.date = res.body.date ? dayjs(res.body.date) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((shift: IShift) => {
        shift.date = shift.date ? dayjs(shift.date) : undefined;
      });
    }
    return res;
  }
}
