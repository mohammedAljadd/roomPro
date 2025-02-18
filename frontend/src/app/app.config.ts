import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { provideHttpClient } from '@angular/common/http';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';


import { provideAnimations } from '@angular/platform-browser/animations';
import { provideToastr } from 'ngx-toastr';


export const appConfig: ApplicationConfig = {
  providers: [provideHttpClient(), provideZoneChangeDetection({ eventCoalescing: true }), provideRouter(routes), provideAnimationsAsync(),
    provideAnimations(), // required animations providers
    provideToastr({
      preventDuplicates: true,
    }),
  ]
};
