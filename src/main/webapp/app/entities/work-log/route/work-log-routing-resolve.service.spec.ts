import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IWorkLog, WorkLog } from '../work-log.model';
import { WorkLogService } from '../service/work-log.service';

import { WorkLogRoutingResolveService } from './work-log-routing-resolve.service';

describe('WorkLog routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: WorkLogRoutingResolveService;
  let service: WorkLogService;
  let resultWorkLog: IWorkLog | undefined;

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
    routingResolveService = TestBed.inject(WorkLogRoutingResolveService);
    service = TestBed.inject(WorkLogService);
    resultWorkLog = undefined;
  });

  describe('resolve', () => {
    it('should return IWorkLog returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultWorkLog = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultWorkLog).toEqual({ id: 123 });
    });

    it('should return new IWorkLog if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultWorkLog = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultWorkLog).toEqual(new WorkLog());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as WorkLog })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultWorkLog = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultWorkLog).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
