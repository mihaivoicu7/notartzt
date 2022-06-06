import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ShiftDetailComponent } from './shift-detail.component';

describe('Shift Management Detail Component', () => {
  let comp: ShiftDetailComponent;
  let fixture: ComponentFixture<ShiftDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ShiftDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ shift: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ShiftDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ShiftDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load shift on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.shift).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
