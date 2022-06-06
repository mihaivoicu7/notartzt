import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IWorkLog, WorkLog } from '../work-log.model';

import { WorkLogService } from './work-log.service';

describe('WorkLog Service', () => {
  let service: WorkLogService;
  let httpMock: HttpTestingController;
  let elemDefault: IWorkLog;
  let expectedResult: IWorkLog | IWorkLog[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(WorkLogService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      note: 'AAAAAAA',
      startHour: 0,
      startMinute: 0,
      endHour: 0,
      endMinute: 0,
      optional: false,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a WorkLog', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new WorkLog()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a WorkLog', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          note: 'BBBBBB',
          startHour: 1,
          startMinute: 1,
          endHour: 1,
          endMinute: 1,
          optional: true,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a WorkLog', () => {
      const patchObject = Object.assign(
        {
          note: 'BBBBBB',
          endHour: 1,
          endMinute: 1,
        },
        new WorkLog()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of WorkLog', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          note: 'BBBBBB',
          startHour: 1,
          startMinute: 1,
          endHour: 1,
          endMinute: 1,
          optional: true,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a WorkLog', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addWorkLogToCollectionIfMissing', () => {
      it('should add a WorkLog to an empty array', () => {
        const workLog: IWorkLog = { id: 123 };
        expectedResult = service.addWorkLogToCollectionIfMissing([], workLog);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(workLog);
      });

      it('should not add a WorkLog to an array that contains it', () => {
        const workLog: IWorkLog = { id: 123 };
        const workLogCollection: IWorkLog[] = [
          {
            ...workLog,
          },
          { id: 456 },
        ];
        expectedResult = service.addWorkLogToCollectionIfMissing(workLogCollection, workLog);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a WorkLog to an array that doesn't contain it", () => {
        const workLog: IWorkLog = { id: 123 };
        const workLogCollection: IWorkLog[] = [{ id: 456 }];
        expectedResult = service.addWorkLogToCollectionIfMissing(workLogCollection, workLog);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(workLog);
      });

      it('should add only unique WorkLog to an array', () => {
        const workLogArray: IWorkLog[] = [{ id: 123 }, { id: 456 }, { id: 2630 }];
        const workLogCollection: IWorkLog[] = [{ id: 123 }];
        expectedResult = service.addWorkLogToCollectionIfMissing(workLogCollection, ...workLogArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const workLog: IWorkLog = { id: 123 };
        const workLog2: IWorkLog = { id: 456 };
        expectedResult = service.addWorkLogToCollectionIfMissing([], workLog, workLog2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(workLog);
        expect(expectedResult).toContain(workLog2);
      });

      it('should accept null and undefined values', () => {
        const workLog: IWorkLog = { id: 123 };
        expectedResult = service.addWorkLogToCollectionIfMissing([], null, workLog, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(workLog);
      });

      it('should return initial array if no WorkLog is added', () => {
        const workLogCollection: IWorkLog[] = [{ id: 123 }];
        expectedResult = service.addWorkLogToCollectionIfMissing(workLogCollection, undefined, null);
        expect(expectedResult).toEqual(workLogCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
