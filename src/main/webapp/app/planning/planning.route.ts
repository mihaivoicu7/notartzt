import { Route } from '@angular/router';

import { PlanningComponent } from './planning.component';

export const HOME_ROUTE: Route = {
  path: 'planning',
  component: PlanningComponent,
  data: {
    pageTitle: 'home.title',
  },
};
