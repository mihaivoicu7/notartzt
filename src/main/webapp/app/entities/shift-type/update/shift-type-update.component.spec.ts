import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ShiftTypeService } from '../service/shift-type.service';
import { IShiftType, ShiftType } from '../shift-type.model';
import { ILocation } from 'app/entities/location/location.model';
import { LocationService } from 'app/entities/location/service/location.service';

import { ShiftTypeUpdateComponent } from './shift-type-update.component';

describe('ShiftType Management Update Component', () => {
  let comp: ShiftTypeUpdateComponent;
  let fixture: ComponentFixture<ShiftTypeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let shiftTypeService: ShiftTypeService;
  let locationService: LocationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ShiftTypeUpdateComponent],
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
      .overrideTemplate(ShiftTypeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ShiftTypeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    shiftTypeService = TestBed.inject(ShiftTypeService);
    locationService = TestBed.inject(LocationService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Location query and add missing value', () => {
      const shiftType: IShiftType = { id: 456 };
      const location: ILocation = { id: 87194 };
      shiftType.location = location;

      const locationCollection: ILocation[] = [{ id: 6671 }];
      jest.spyOn(locationService, 'query').mockReturnValue(of(new HttpResponse({ body: locationCollection })));
      const additionalLocations = [location];
      const expectedCollection: ILocation[] = [...additionalLocations, ...locationCollection];
      jest.spyOn(locationService, 'addLocationToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ shiftType });
      comp.ngOnInit();

      expect(locationService.query).toHaveBeenCalled();
      expect(locationService.addLocationToCollectionIfMissing).toHaveBeenCalledWith(locationCollection, ...additionalLocations);
      expect(comp.locationsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const shiftType: IShiftType = { id: 456 };
      const location: ILocation = { id: 4983 };
      shiftType.location = location;

      activatedRoute.data = of({ shiftType });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(shiftType));
      expect(comp.locationsSharedCollection).toContain(location);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ShiftType>>();
      const shiftType = { id: 123 };
      jest.spyOn(shiftTypeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ shiftType });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: shiftType }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(shiftTypeService.update).toHaveBeenCalledWith(shiftType);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ShiftType>>();
      const shiftType = new ShiftType();
      jest.spyOn(shiftTypeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ shiftType });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: shiftType }));
      saveSubject.complete();

      // THEN
      expect(shiftTypeService.create).toHaveBeenCalledWith(shiftType);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ShiftType>>();
      const shiftType = { id: 123 };
      jest.spyOn(shiftTypeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ shiftType });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(shiftTypeService.update).toHaveBeenCalledWith(shiftType);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackLocationById', () => {
      it('Should return tracked Location primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackLocationById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
