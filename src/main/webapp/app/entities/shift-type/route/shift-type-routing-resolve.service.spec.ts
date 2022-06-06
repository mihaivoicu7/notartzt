import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IShiftType, ShiftType } from '../shift-type.model';
import { ShiftTypeService } from '../service/shift-type.service';

import { ShiftTypeRoutingResolveService } from './shift-type-routing-resolve.service';

describe('ShiftType routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: ShiftTypeRoutingResolveService;
  let service: ShiftTypeService;
  let resultShiftType: IShiftType | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({}),
            },
          },
        },
      ],
    });
    mockRouter = TestBed.inject(Router);
    jest.spyOn(mockRouter, 'navigate').mockImplementation(() => Promise.resolve(true));
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRoute).snapshot;
    routingResolveService = TestBed.inject(ShiftTypeRoutingResolveService);
    service = TestBed.inject(ShiftTypeService);
    resultShiftType = undefined;
  });

  describe('resolve', () => {
    it('should return IShiftType returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultShiftType = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultShiftType).toEqual({ id: 123 });
    });

    it('should return new IShiftType if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultShiftType = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultShiftType).toEqual(new ShiftType());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as ShiftType })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultShiftType = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultShiftType).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
