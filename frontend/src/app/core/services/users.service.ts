import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import type { User } from './auth.service';
import type { Perfil } from './perfis.service';

export interface PageUserResponse {
  content: User[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

export interface CreateUserRequest {
  email: string;
  name: string;
  password: string;
  phone?: string;
  profile: 'ADMIN' | 'MANAGER' | 'SELLER' | 'OPERATOR';
}

export interface UpdateUserRequest {
  name: string;
  phone?: string;
}

export interface ListUsersParams {
  name?: string;
  email?: string;
  profile?: string;
  page?: number;
  size?: number;
  sort?: string;
}

@Injectable({ providedIn: 'root' })
export class UsersService {
  private readonly api = `${environment.apiUrl}/api/v1/users`;

  constructor(private http: HttpClient) {}

  list(params: ListUsersParams = {}): Observable<PageUserResponse> {
    let httpParams = new HttpParams();
    if (params.name) httpParams = httpParams.set('name', params.name);
    if (params.email) httpParams = httpParams.set('email', params.email);
    if (params.profile) httpParams = httpParams.set('profile', params.profile);
    if (params.page != null) httpParams = httpParams.set('page', params.page);
    if (params.size != null) httpParams = httpParams.set('size', params.size);
    if (params.sort) httpParams = httpParams.set('sort', params.sort);

    return this.http.get<PageUserResponse>(this.api, { params: httpParams });
  }

  getById(id: string): Observable<User> {
    return this.http.get<User>(`${this.api}/${id}`);
  }

  create(data: CreateUserRequest): Observable<User> {
    return this.http.post<User>(this.api, data);
  }

  update(id: string, data: UpdateUserRequest): Observable<User> {
    return this.http.put<User>(`${this.api}/${id}`, data);
  }

  activate(id: string): Observable<User> {
    return this.http.patch<User>(`${this.api}/${id}/activate`, {});
  }

  deactivate(id: string): Observable<User> {
    return this.http.patch<User>(`${this.api}/${id}/deactivate`, {});
  }

  getPerfis(userId: string): Observable<Perfil[]> {
    return this.http.get<Perfil[]>(`${this.api}/${userId}/perfis`);
  }

  /** Perfis do usuário autenticado (usa token do header) */
  getMyPerfis(): Observable<Perfil[]> {
    return this.http.get<Perfil[]>(`${this.api}/perfis`);
  }

  assignPerfis(userId: string, perfilIds: string[]): Observable<void> {
    return this.http.put<void>(`${this.api}/${userId}/perfis`, { perfilIds });
  }
}
