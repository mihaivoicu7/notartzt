import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ShiftService } from '../service/shift.service';
import { IShift, Shift } from '../shift.model';
import { IShiftType } from 'app/entities/shift-type/shift-type.model';
import { ShiftTypeService } from 'app/entities/shift-type/service/shift-type.service';

import { ShiftUpdateComponent } from './shift-update.component';

describe('Shift Management Update Component', () => {
  let comp: ShiftUpdateComponent;
  let fixture: ComponentFixture<ShiftUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let shiftService: ShiftService;
  let shiftTypeService: ShiftTypeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ShiftUpdateComponent],
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
      .overrideTemplate(ShiftUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ShiftUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    shiftService = TestBed.inject(ShiftService);
    shiftTypeService = TestBed.inject(ShiftTypeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call ShiftType query and add missing value', () => {
      const shift: IShift = { id: 456 };
      const type: IShiftType = { id: 11697 };
      shift.type = type;

      const shiftTypeCollection: IShiftType[] = [{ id: 9652 }];
      jest.spyOn(shiftTypeService, 'query').mockReturnValue(of(new HttpResponse({ body: shiftTypeCollection })));
      const additionalShiftTypes = [type];
      const expectedCollection: IShiftType[] = [...additionalShiftTypes, ...shiftTypeCollection];
      jest.spyOn(shiftTypeService, 'addShiftTypeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ shift });
      comp.ngOnInit();

      expect(shiftTypeService.query).toHaveBeenCalled();
      expect(shiftTypeService.addShiftTypeToCollectionIfMissing).toHaveBeenCalledWith(shiftTypeCollection, ...additionalShiftTypes);
      expect(comp.shiftTypesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const shift: IShift = { id: 456 };
      const type: IShiftType = { id: 57370 };
      shift.type = type;

      activatedRoute.data = of({ shift });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(shift));
      expect(comp.shiftTypesSharedCollection).toContain(type);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Shift>>();
      const shift = { id: 123 };
      jest.spyOn(shiftService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ shift });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: shift }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(shiftService.update).toHaveBeenCalledWith(shift);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Shift>>();
      const shift = new Shift();
      jest.spyOn(shiftService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ shift });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: shift }));
      saveSubject.complete();

      // THEN
      expect(shiftService.create).toHaveBeenCalledWith(shift);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Shift>>();
      const shift = { id: 123 };
      jest.spyOn(shiftService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ shift });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(shiftService.update).toHaveBeenCalledWith(shift);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackShiftTypeById', () => {
      it('Should return tracked ShiftType primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackShiftTypeById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
