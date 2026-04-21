import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export type Marketplace = 'mercado_livre' | 'shopee';

export interface Seller {
  id: string;
  userId: string;
  marketplace: Marketplace;
  accessToken: string;
  tokenType: string;
  expiresIn: number;
  scope: string;
  marketplaceId: number;
  refreshToken: string;
  createdAt: string;
  updatedAt: string;
}

export interface PageSellerResponse {
  content: Seller[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

/** Payload no formato snake_case aceito pelo backend (resposta OAuth). */
export interface CreateSellerRequest {
  userId: string;
  marketplace: Marketplace;
  access_token: string;
  token_type: string;
  expires_in: number;
  scope: string;
  marketplace_id: number;
  refresh_token: string;
}

export interface UpdateSellerRequest {
  userId: string;
  marketplace: Marketplace;
  access_token: string;
  token_type: string;
  expires_in: number;
  scope: string;
  marketplace_id: number;
  refresh_token: string;
}

export interface ListSellersParams {
  marketplaceId?: number;
  page?: number;
  size?: number;
  sort?: string;
}

@Injectable({ providedIn: 'root' })
export class SellersService {
  private readonly api = `${environment.apiUrl}/api/v1/sellers`;

  constructor(private http: HttpClient) {}

  list(params: ListSellersParams = {}): Observable<PageSellerResponse> {
    let httpParams = new HttpParams();
    if (params.marketplaceId != null)
      httpParams = httpParams.set('marketplaceId', params.marketplaceId);
    if (params.page != null) httpParams = httpParams.set('page', params.page);
    if (params.size != null) httpParams = httpParams.set('size', params.size);
    if (params.sort) httpParams = httpParams.set('sort', params.sort);

    return this.http.get<PageSellerResponse>(this.api, { params: httpParams });
  }

  getById(id: string): Observable<Seller> {
    return this.http.get<Seller>(`${this.api}/${id}`);
  }

  create(data: CreateSellerRequest): Observable<Seller> {
    return this.http.post<Seller>(this.api, data);
  }

  update(id: string, data: UpdateSellerRequest): Observable<Seller> {
    return this.http.put<Seller>(`${this.api}/${id}`, data);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }
}
