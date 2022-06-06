import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ShiftTypeDetailComponent } from './shift-type-detail.component';

describe('ShiftType Management Detail Component', () => {
  let comp: ShiftTypeDetailComponent;
  let fixture: ComponentFixture<ShiftTypeDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ShiftTypeDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ shiftType: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ShiftTypeDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ShiftTypeDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load shiftType on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.shiftType).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
