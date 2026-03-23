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

@Injectable({ providedIn: 'root' })
export class AdminProductsService {
  private readonly api = `${environment.apiUrl}/api/v1/admin/products`;

  constructor(private http: HttpClient) {}

  create(data: CreateBaseProductRequest): Observable<AdminProductResponse> {
    return this.http.post<AdminProductResponse>(this.api, data);
  }
}
