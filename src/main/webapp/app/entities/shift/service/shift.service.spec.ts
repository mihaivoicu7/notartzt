import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ShiftStatus } from 'app/entities/enumerations/shift-status.model';
import { IShift, Shift } from '../shift.model';

import { ShiftService } from './shift.service';

describe('Shift Service', () => {
  let service: ShiftService;
  let httpMock: HttpTestingController;
  let elemDefault: IShift;
  let expectedResult: IShift | IShift[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ShiftService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      date: currentDate,
      status: ShiftStatus.OPEN,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          date: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Shift', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
          date: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          date: currentDate,
        },
        returnedFromService
      );

      service.create(new Shift()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Shift', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          date: currentDate.format(DATE_FORMAT),
          status: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          date: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Shift', () => {
      const patchObject = Object.assign(
        {
          status: 'BBBBBB',
        },
        new Shift()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          date: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Shift', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          date: currentDate.format(DATE_FORMAT),
          status: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          date: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Shift', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addShiftToCollectionIfMissing', () => {
      it('should add a Shift to an empty array', () => {
        const shift: IShift = { id: 123 };
        expectedResult = service.addShiftToCollectionIfMissing([], shift);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(shift);
      });

      it('should not add a Shift to an array that contains it', () => {
        const shift: IShift = { id: 123 };
        const shiftCollection: IShift[] = [
          {
            ...shift,
          },
          { id: 456 },
        ];
        expectedResult = service.addShiftToCollectionIfMissing(shiftCollection, shift);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Shift to an array that doesn't contain it", () => {
        const shift: IShift = { id: 123 };
        const shiftCollection: IShift[] = [{ id: 456 }];
        expectedResult = service.addShiftToCollectionIfMissing(shiftCollection, shift);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(shift);
      });

      it('should add only unique Shift to an array', () => {
        const shiftArray: IShift[] = [{ id: 123 }, { id: 456 }, { id: 56935 }];
        const shiftCollection: IShift[] = [{ id: 123 }];
        expectedResult = service.addShiftToCollectionIfMissing(shiftCollection, ...shiftArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const shift: IShift = { id: 123 };
        const shift2: IShift = { id: 456 };
        expectedResult = service.addShiftToCollectionIfMissing([], shift, shift2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(shift);
        expect(expectedResult).toContain(shift2);
      });

      it('should accept null and undefined values', () => {
        const shift: IShift = { id: 123 };
        expectedResult = service.addShiftToCollectionIfMissing([], null, shift, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(shift);
      });

      it('should return initial array if no Shift is added', () => {
        const shiftCollection: IShift[] = [{ id: 123 }];
        expectedResult = service.addShiftToCollectionIfMissing(shiftCollection, undefined, null);
        expect(expectedResult).toEqual(shiftCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
