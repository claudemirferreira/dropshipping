import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import type { Rotina } from './rotinas.service';

export interface Perfil {
  id: string;
  code: string;
  name: string;
  description: string | null;
  icon: string | null;
  active: boolean;
  displayOrder: number;
  rotinas: Rotina[];
  createdAt: string;
  updatedAt: string;
}

export interface PagePerfilResponse {
  content: Perfil[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

export interface CreatePerfilRequest {
  code: string;
  name: string;
  description?: string;
  icon?: string;
  active?: boolean;
  rotinaIds?: string[];
}

export interface UpdatePerfilRequest {
  code: string;
  name: string;
  description?: string;
  icon?: string;
  active?: boolean;
  rotinaIds?: string[];
}

export interface ListPerfisParams {
  code?: string;
  name?: string;
  active?: boolean;
  page?: number;
  size?: number;
  sort?: string;
}

@Injectable({ providedIn: 'root' })
export class PerfisService {
  private readonly api = `${environment.apiUrl}/api/v1/perfis`;

  constructor(private http: HttpClient) {}

  list(params: ListPerfisParams = {}): Observable<PagePerfilResponse> {
    let httpParams = new HttpParams();
    if (params.code) httpParams = httpParams.set('code', params.code);
    if (params.name) httpParams = httpParams.set('name', params.name);
    if (params.active != null) httpParams = httpParams.set('active', params.active);
    if (params.page != null) httpParams = httpParams.set('page', params.page);
    if (params.size != null) httpParams = httpParams.set('size', params.size);
    if (params.sort) httpParams = httpParams.set('sort', params.sort);

    return this.http.get<PagePerfilResponse>(this.api, { params: httpParams });
  }

  getById(id: string): Observable<Perfil> {
    return this.http.get<Perfil>(`${this.api}/${id}`);
  }

  create(data: CreatePerfilRequest): Observable<Perfil> {
    const body = {
      ...data,
      rotinaIds: data.rotinaIds ?? [],
    };
    return this.http.post<Perfil>(this.api, body);
  }

  update(id: string, data: UpdatePerfilRequest): Observable<Perfil> {
    const body = {
      ...data,
      rotinaIds: data.rotinaIds ?? [],
    };
    return this.http.put<Perfil>(`${this.api}/${id}`, body);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }
}
