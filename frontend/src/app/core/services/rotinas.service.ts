import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface Rotina {
  id: string;
  code: string;
  name: string;
  description: string | null;
  icon: string | null;
  path: string | null;
  active: boolean;
  displayOrder: number;
  createdAt: string;
  updatedAt: string;
}

export interface PageRotinaResponse {
  content: Rotina[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

export interface CreateRotinaRequest {
  code: string;
  name: string;
  description?: string;
  icon?: string;
  path?: string;
  active?: boolean;
}

export interface UpdateRotinaRequest {
  code: string;
  name: string;
  description?: string;
  icon?: string;
  path?: string;
  active?: boolean;
}

export interface ListRotinasParams {
  code?: string;
  name?: string;
  active?: boolean;
  page?: number;
  size?: number;
  sort?: string;
}

@Injectable({ providedIn: 'root' })
export class RotinasService {
  private readonly api = `${environment.apiUrl}/api/v1/rotinas`;

  constructor(private http: HttpClient) {}

  list(params: ListRotinasParams = {}): Observable<PageRotinaResponse> {
    let httpParams = new HttpParams();
    if (params.code) httpParams = httpParams.set('code', params.code);
    if (params.name) httpParams = httpParams.set('name', params.name);
    if (params.active != null) httpParams = httpParams.set('active', params.active);
    if (params.page != null) httpParams = httpParams.set('page', params.page);
    if (params.size != null) httpParams = httpParams.set('size', params.size);
    if (params.sort) httpParams = httpParams.set('sort', params.sort);

    return this.http.get<PageRotinaResponse>(this.api, { params: httpParams });
  }

  getById(id: string): Observable<Rotina> {
    return this.http.get<Rotina>(`${this.api}/${id}`);
  }

  create(data: CreateRotinaRequest): Observable<Rotina> {
    return this.http.post<Rotina>(this.api, data);
  }

  update(id: string, data: UpdateRotinaRequest): Observable<Rotina> {
    return this.http.put<Rotina>(`${this.api}/${id}`, data);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }
}
