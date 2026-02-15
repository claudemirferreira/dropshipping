import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export type ProductStatus = 'DRAFT' | 'ACTIVE' | 'INACTIVE' | 'OUT_OF_STOCK';

export interface ProductImage {
  id: string;
  url: string;
  position: number;
  main: boolean;
  altText?: string;
}

export interface Product {
  id: string;
  sku: string;
  name: string;
  shortDescription: string;
  salePrice: number;
  costPrice: number;
  currency: string;
  status: ProductStatus;
  slug: string;
  mainImageUrl?: string;
  createdAt: string;
  updatedAt: string;
}

export interface ProductDetail extends Product {
  fullDescription?: string;
  supplierSku?: string;
  supplierName?: string;
  supplierProductUrl?: string;
  leadTimeDays?: number;
  isDropship: boolean;
  weight?: number;
  length?: number;
  width?: number;
  height?: number;
  categoryId?: string;
  brand?: string;
  metaTitle?: string;
  metaDescription?: string;
  compareAtPrice?: number;
  stockQuantity?: number;
  images: ProductImage[];
}

export interface PageProductResponse {
  content: Product[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

export interface CreateProductImageRequest {
  url: string;
  position?: number;
  isMain?: boolean;
  altText?: string;
}

export interface CreateProductRequest {
  sku: string;
  name: string;
  shortDescription: string;
  fullDescription?: string;
  salePrice: number;
  costPrice: number;
  currency: string;
  status: ProductStatus;
  supplierSku?: string;
  supplierName?: string;
  supplierProductUrl?: string;
  leadTimeDays?: number;
  isDropship?: boolean;
  weight?: number;
  length?: number;
  width?: number;
  height?: number;
  slug: string;
  categoryId?: string;
  brand?: string;
  metaTitle?: string;
  metaDescription?: string;
  compareAtPrice?: number;
  stockQuantity?: number;
  images?: CreateProductImageRequest[];
}

export interface UpdateProductRequest {
  name: string;
  shortDescription: string;
  fullDescription?: string;
  salePrice: number;
  costPrice: number;
  currency: string;
  status: ProductStatus;
  supplierSku?: string;
  supplierName?: string;
  supplierProductUrl?: string;
  leadTimeDays?: number;
  isDropship?: boolean;
  weight?: number;
  length?: number;
  width?: number;
  height?: number;
  slug: string;
  categoryId?: string;
  brand?: string;
  metaTitle?: string;
  metaDescription?: string;
  compareAtPrice?: number;
  stockQuantity?: number;
}

export interface ListProductsParams {
  name?: string;
  status?: string;
  categoryId?: string;
  page?: number;
  size?: number;
  sort?: string;
}

@Injectable({ providedIn: 'root' })
export class ProductsService {
  private readonly api = `${environment.apiUrl}/api/v1/products`;

  constructor(private http: HttpClient) {}

  list(params: ListProductsParams = {}): Observable<PageProductResponse> {
    let httpParams = new HttpParams();
    if (params.name) httpParams = httpParams.set('name', params.name);
    if (params.status) httpParams = httpParams.set('status', params.status);
    if (params.categoryId) httpParams = httpParams.set('categoryId', params.categoryId);
    if (params.page != null) httpParams = httpParams.set('page', params.page);
    if (params.size != null) httpParams = httpParams.set('size', params.size);
    if (params.sort) httpParams = httpParams.set('sort', params.sort);

    return this.http.get<PageProductResponse>(this.api, { params: httpParams });
  }

  getById(id: string): Observable<ProductDetail> {
    return this.http.get<ProductDetail>(`${this.api}/${id}`);
  }

  create(data: CreateProductRequest): Observable<ProductDetail> {
    return this.http.post<ProductDetail>(this.api, data);
  }

  update(id: string, data: UpdateProductRequest): Observable<ProductDetail> {
    return this.http.put<ProductDetail>(`${this.api}/${id}`, data);
  }

  updateStatus(id: string, status: ProductStatus): Observable<ProductDetail> {
    return this.http.patch<ProductDetail>(`${this.api}/${id}/status`, { status });
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }

  addImage(productId: string, data: CreateProductImageRequest): Observable<ProductImage> {
    return this.http.post<ProductImage>(`${this.api}/${productId}/images`, data);
  }

  updateImage(
    productId: string,
    imageId: string,
    data: Partial<{ position: number; isMain: boolean; altText: string }>
  ): Observable<ProductImage> {
    return this.http.put<ProductImage>(
      `${this.api}/${productId}/images/${imageId}`,
      data
    );
  }

  removeImage(productId: string, imageId: string): Observable<void> {
    return this.http.delete<void>(`${this.api}/${productId}/images/${imageId}`);
  }

  /** Upload de imagem e retorna a URL pública. */
  uploadImage(file: File): Observable<{ url: string }> {
    const base = this.api.replace(/\/api\/v1\/products$/, '');
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<{ url: string }>(`${base}/api/v1/upload`, formData);
  }
}
