import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface AdminProductResponse {
  id: string;
  nome: string;
  slug: string;
  sku: string;
  status: 'DRAFT' | 'ACTIVE' | 'INACTIVE';
  ean?: string;
  is_ean_interno: boolean;
  criado_em: string;
}

export interface LogisticaPayload {
  peso_kg: number;
  altura_cm: number;
  largura_cm: number;
  comprimento_cm: number;
  lead_time_envio_dias: number;
}

export interface EstoquePayload {
  atual: number;
  minimo: number;
}

export interface ComercialPayload {
  valor_custo: number;
  percentual_taxa_seller?: number;
  garantia: string;
}

export interface CodigosPayload {
  ean?: string;
  is_ean_interno?: boolean;
}

export interface ProductBaseDetail {
  nome: string;
  slug: string;
  sku: string;
  categoria_id: string;
  marca: string;
  descricao_curta: string;
  descricao_completa: string;
  logistica: {
    peso_kg: number;
    altura_cm: number;
    largura_cm: number;
    comprimento_cm: number;
    lead_time_envio_dias: number;
  };
  estoque: {
    atual: number;
    minimo: number;
  };
  comercial: {
    valor_custo: number;
    percentual_taxa_seller: number | null;
    garantia: string;
  };
  codigos: {
    ean: string | null;
    is_ean_interno: boolean;
  };
  tags: string[];
}

export interface CreateBaseProductRequest {
  nome: string;
  slug?: string | null;
  sku: string;
  categoria_id?: string;
  marca: string;
  descricao_curta: string;
  descricao_completa: string;
  logistica: LogisticaPayload;
  estoque: EstoquePayload;
  comercial: ComercialPayload;
  codigos?: CodigosPayload;
  tags?: string[];
}

export interface UpdateBaseProductRequest extends CreateBaseProductRequest {}

@Injectable({ providedIn: 'root' })
export class AdminProductsService {
  private readonly api = `${environment.apiUrl}/api/v1/admin/products`;

  constructor(private http: HttpClient) {}

  getDetail(id: string): Observable<ProductBaseDetail> {
    return this.http.get<ProductBaseDetail>(`${this.api}/${id}/detail`);
  }

  create(data: CreateBaseProductRequest): Observable<AdminProductResponse> {
    return this.http.post<AdminProductResponse>(this.api, data);
  }

  update(id: string, data: UpdateBaseProductRequest): Observable<AdminProductResponse> {
    return this.http.put<AdminProductResponse>(`${this.api}/${id}`, data);
  }
}
