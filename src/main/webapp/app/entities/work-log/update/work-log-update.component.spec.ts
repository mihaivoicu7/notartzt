import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { WorkLogService } from '../service/work-log.service';
import { IWorkLog, WorkLog } from '../work-log.model';
import { IShift } from 'app/entities/shift/shift.model';
import { ShiftService } from 'app/entities/shift/service/shift.service';

import { WorkLogUpdateComponent } from './work-log-update.component';

describe('WorkLog Management Update Component', () => {
  let comp: WorkLogUpdateComponent;
  let fixture: ComponentFixture<WorkLogUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let workLogService: WorkLogService;
  let shiftService: ShiftService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [WorkLogUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(WorkLogUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(WorkLogUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    workLogService = TestBed.inject(WorkLogService);
    shiftService = TestBed.inject(ShiftService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Shift query and add missing value', () => {
      const workLog: IWorkLog = { id: 456 };
      const shift: IShift = { id: 55294 };
      workLog.shift = shift;

      const shiftCollection: IShift[] = [{ id: 44854 }];
      jest.spyOn(shiftService, 'query').mockReturnValue(of(new HttpResponse({ body: shiftCollection })));
      const additionalShifts = [shift];
      const expectedCollection: IShift[] = [...additionalShifts, ...shiftCollection];
      jest.spyOn(shiftService, 'addShiftToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ workLog });
      comp.ngOnInit();

      expect(shiftService.query).toHaveBeenCalled();
      expect(shiftService.addShiftToCollectionIfMissing).toHaveBeenCalledWith(shiftCollection, ...additionalShifts);
      expect(comp.shiftsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const workLog: IWorkLog = { id: 456 };
      const shift: IShift = { id: 75185 };
      workLog.shift = shift;

      activatedRoute.data = of({ workLog });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(workLog));
      expect(comp.shiftsSharedCollection).toContain(shift);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<WorkLog>>();
      const workLog = { id: 123 };
      jest.spyOn(workLogService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ workLog });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: workLog }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(workLogService.update).toHaveBeenCalledWith(workLog);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<WorkLog>>();
      const workLog = new WorkLog();
      jest.spyOn(workLogService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ workLog });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: workLog }));
      saveSubject.complete();

      // THEN
      expect(workLogService.create).toHaveBeenCalledWith(workLog);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<WorkLog>>();
      const workLog = { id: 123 };
      jest.spyOn(workLogService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ workLog });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(workLogService.update).toHaveBeenCalledWith(workLog);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackShiftById', () => {
      it('Should return tracked Shift primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackShiftById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
