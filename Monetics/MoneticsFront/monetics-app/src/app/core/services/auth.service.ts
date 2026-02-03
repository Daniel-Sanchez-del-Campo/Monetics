import { HttpClient } from '@angular/common/http';
import { Inject, Injectable, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { BehaviorSubject, Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { Usuario } from '../models';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private apiUrl = '/api/auth';
  private currentUserSubject: BehaviorSubject<Usuario | null>;
  public currentUser$: Observable<Usuario | null>;
  private isBrowser: boolean;

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) platformId: Object
  ) {
    this.isBrowser = isPlatformBrowser(platformId);
    // Inicializar con el usuario guardado en localStorage (si existe y estamos en el navegador)
    const savedUser = this.getUserFromStorage();
    this.currentUserSubject = new BehaviorSubject<Usuario | null>(savedUser);
    this.currentUser$ = this.currentUserSubject.asObservable();
  }

  private getUserFromStorage(): Usuario | null {
    if (!this.isBrowser) {
      return null;
    }
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  }

  get currentUserValue(): Usuario | null {
    return this.currentUserSubject.value;
  }

  get isManager(): boolean {
    return this.currentUserValue?.rol === 'ROLE_MANAGER';
  }

  get isAdmin(): boolean {
    return this.currentUserValue?.rol === 'ROLE_ADMIN';
  }

  get isAuthenticated(): boolean {
    return !!this.getToken() && !!this.currentUserValue;
  }

  login(credentials: { email: string; password: string }) {
    return this.http.post<any>(`${this.apiUrl}/login`, credentials).pipe(
      tap(response => {
        this.saveToken(response.token);
        this.saveUser(response);
      })
    );
  }

  register(userData: { nombre: string; email: string; password: string; idDepartamento: number }) {
    return this.http.post<any>(`${this.apiUrl}/register`, userData).pipe(
      tap(response => {
        this.saveToken(response.token);
        this.saveUser(response);
      })
    );
  }

  saveToken(token: string) {
    if (this.isBrowser) {
      localStorage.setItem('token', token);
    }
  }

  private saveUser(response: any) {
    if (!this.isBrowser) {
      return;
    }
    // El backend devuelve { token, usuario: {...} }
    const user: Usuario = response.usuario;
    localStorage.setItem('user', JSON.stringify(user));
    this.currentUserSubject.next(user);
  }

  getToken(): string | null {
    if (!this.isBrowser) {
      return null;
    }
    return localStorage.getItem('token');
  }

  getUser(): Usuario | null {
    return this.getUserFromStorage();
  }

  logout() {
    if (this.isBrowser) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
    }
    this.currentUserSubject.next(null);
  }
}
