import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './shared/navbar/navbar.component';

/**
 * Componente raíz. Standalone (Angular 19).
 * Actúa de shell: monta la barra de navegación y el router-outlet.
 */
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, NavbarComponent],
  template: `
    <app-navbar />
    <main class="container py-4">
      <router-outlet />
    </main>
  `,
  styles: [`
    :host { display: block; }
  `]
})
export class AppComponent {}
