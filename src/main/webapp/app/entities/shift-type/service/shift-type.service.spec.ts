import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IShiftType, ShiftType } from '../shift-type.model';

import { ShiftTypeService } from './shift-type.service';

describe('ShiftType Service', () => {
  let service: ShiftTypeService;
  let httpMock: HttpTestingController;
  let elemDefault: IShiftType;
  let expectedResult: IShiftType | IShiftType[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ShiftTypeService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      name: 'AAAAAAA',
      startHour: 0,
      startMinute: 0,
      endHour: 0,
      endMinute: 0,
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

    it('should create a ShiftType', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new ShiftType()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ShiftType', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          startHour: 1,
          startMinute: 1,
          endHour: 1,
          endMinute: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ShiftType', () => {
      const patchObject = Object.assign(
        {
          name: 'BBBBBB',
          endHour: 1,
        },
        new ShiftType()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ShiftType', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          startHour: 1,
          startMinute: 1,
          endHour: 1,
          endMinute: 1,
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

    it('should delete a ShiftType', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addShiftTypeToCollectionIfMissing', () => {
      it('should add a ShiftType to an empty array', () => {
        const shiftType: IShiftType = { id: 123 };
        expectedResult = service.addShiftTypeToCollectionIfMissing([], shiftType);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(shiftType);
      });

      it('should not add a ShiftType to an array that contains it', () => {
        const shiftType: IShiftType = { id: 123 };
        const shiftTypeCollection: IShiftType[] = [
          {
            ...shiftType,
          },
          { id: 456 },
        ];
        expectedResult = service.addShiftTypeToCollectionIfMissing(shiftTypeCollection, shiftType);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ShiftType to an array that doesn't contain it", () => {
        const shiftType: IShiftType = { id: 123 };
        const shiftTypeCollection: IShiftType[] = [{ id: 456 }];
        expectedResult = service.addShiftTypeToCollectionIfMissing(shiftTypeCollection, shiftType);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(shiftType);
      });

      it('should add only unique ShiftType to an array', () => {
        const shiftTypeArray: IShiftType[] = [{ id: 123 }, { id: 456 }, { id: 28367 }];
        const shiftTypeCollection: IShiftType[] = [{ id: 123 }];
        expectedResult = service.addShiftTypeToCollectionIfMissing(shiftTypeCollection, ...shiftTypeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const shiftType: IShiftType = { id: 123 };
        const shiftType2: IShiftType = { id: 456 };
        expectedResult = service.addShiftTypeToCollectionIfMissing([], shiftType, shiftType2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(shiftType);
        expect(expectedResult).toContain(shiftType2);
      });

      it('should accept null and undefined values', () => {
        const shiftType: IShiftType = { id: 123 };
        expectedResult = service.addShiftTypeToCollectionIfMissing([], null, shiftType, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(shiftType);
      });

      it('should return initial array if no ShiftType is added', () => {
        const shiftTypeCollection: IShiftType[] = [{ id: 123 }];
        expectedResult = service.addShiftTypeToCollectionIfMissing(shiftTypeCollection, undefined, null);
        expect(expectedResult).toEqual(shiftTypeCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
