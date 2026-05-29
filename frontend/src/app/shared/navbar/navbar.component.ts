import { Component, computed, inject } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, CommonModule],
  templateUrl: './navbar.component.html'
})
export class NavbarComponent {

  private auth = inject(AuthService);

  readonly isAdmin      = computed(() => this.auth.hasRole('ADMIN'));
  readonly isLibrarian  = computed(() => this.auth.hasRole('ADMIN', 'LIBRARIAN'));
  readonly isLoggedIn   = this.auth.isLoggedIn;
  readonly username     = this.auth.username;

  logout(): void {
    this.auth.logout();
  }
}
