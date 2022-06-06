import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IWorkLog, getWorkLogIdentifier } from '../work-log.model';

export type EntityResponseType = HttpResponse<IWorkLog>;
export type EntityArrayResponseType = HttpResponse<IWorkLog[]>;

@Injectable({ providedIn: 'root' })
export class WorkLogService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/work-logs');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(workLog: IWorkLog): Observable<EntityResponseType> {
    return this.http.post<IWorkLog>(this.resourceUrl, workLog, { observe: 'response' });
  }

  update(workLog: IWorkLog): Observable<EntityResponseType> {
    return this.http.put<IWorkLog>(`${this.resourceUrl}/${getWorkLogIdentifier(workLog) as number}`, workLog, { observe: 'response' });
  }

  partialUpdate(workLog: IWorkLog): Observable<EntityResponseType> {
    return this.http.patch<IWorkLog>(`${this.resourceUrl}/${getWorkLogIdentifier(workLog) as number}`, workLog, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IWorkLog>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IWorkLog[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addWorkLogToCollectionIfMissing(workLogCollection: IWorkLog[], ...workLogsToCheck: (IWorkLog | null | undefined)[]): IWorkLog[] {
    const workLogs: IWorkLog[] = workLogsToCheck.filter(isPresent);
    if (workLogs.length > 0) {
      const workLogCollectionIdentifiers = workLogCollection.map(workLogItem => getWorkLogIdentifier(workLogItem)!);
      const workLogsToAdd = workLogs.filter(workLogItem => {
        const workLogIdentifier = getWorkLogIdentifier(workLogItem);
        if (workLogIdentifier == null || workLogCollectionIdentifiers.includes(workLogIdentifier)) {
          return false;
        }
        workLogCollectionIdentifiers.push(workLogIdentifier);
        return true;
      });
      return [...workLogsToAdd, ...workLogCollection];
    }
    return workLogCollection;
  }
}
