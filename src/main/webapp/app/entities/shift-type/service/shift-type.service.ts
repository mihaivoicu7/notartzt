import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IShiftType, getShiftTypeIdentifier } from '../shift-type.model';

export type EntityResponseType = HttpResponse<IShiftType>;
export type EntityArrayResponseType = HttpResponse<IShiftType[]>;

@Injectable({ providedIn: 'root' })
export class ShiftTypeService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/shift-types');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(shiftType: IShiftType): Observable<EntityResponseType> {
    return this.http.post<IShiftType>(this.resourceUrl, shiftType, { observe: 'response' });
  }

  update(shiftType: IShiftType): Observable<EntityResponseType> {
    return this.http.put<IShiftType>(`${this.resourceUrl}/${getShiftTypeIdentifier(shiftType) as number}`, shiftType, {
      observe: 'response',
    });
  }

  partialUpdate(shiftType: IShiftType): Observable<EntityResponseType> {
    return this.http.patch<IShiftType>(`${this.resourceUrl}/${getShiftTypeIdentifier(shiftType) as number}`, shiftType, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IShiftType>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IShiftType[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addShiftTypeToCollectionIfMissing(
    shiftTypeCollection: IShiftType[],
    ...shiftTypesToCheck: (IShiftType | null | undefined)[]
  ): IShiftType[] {
    const shiftTypes: IShiftType[] = shiftTypesToCheck.filter(isPresent);
    if (shiftTypes.length > 0) {
      const shiftTypeCollectionIdentifiers = shiftTypeCollection.map(shiftTypeItem => getShiftTypeIdentifier(shiftTypeItem)!);
      const shiftTypesToAdd = shiftTypes.filter(shiftTypeItem => {
        const shiftTypeIdentifier = getShiftTypeIdentifier(shiftTypeItem);
        if (shiftTypeIdentifier == null || shiftTypeCollectionIdentifiers.includes(shiftTypeIdentifier)) {
          return false;
        }
        shiftTypeCollectionIdentifiers.push(shiftTypeIdentifier);
        return true;
      });
      return [...shiftTypesToAdd, ...shiftTypeCollection];
    }
    return shiftTypeCollection;
  }
}
