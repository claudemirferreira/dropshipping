import {
  Component,
  inject,
  signal,
  computed,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  FormArray,
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { TableModule, TableLazyLoadEvent } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { TagModule } from 'primeng/tag';
import { Toast } from 'primeng/toast';
import { MessageService, ConfirmationService } from 'primeng/api';
import { ConfirmDialog } from 'primeng/confirmdialog';
import { TooltipModule } from 'primeng/tooltip';
import { DialogModule } from 'primeng/dialog';
import { DropdownModule } from 'primeng/dropdown';
import { TabViewModule } from 'primeng/tabview';
import { CheckboxModule } from 'primeng/checkbox';
import { TextareaModule } from 'primeng/textarea';
import {
  ProductsService,
  type Product,
  type ProductDetail,
  type ProductImage,
  type ProductStatus,
  type CreateProductRequest,
  type CreateProductImageRequest,
} from '../../../core/services/products.service';

const STATUS_OPTIONS = [
  { label: 'Rascunho', value: 'DRAFT' },
  { label: 'Ativo', value: 'ACTIVE' },
  { label: 'Inativo', value: 'INACTIVE' },
  { label: 'Sem estoque', value: 'OUT_OF_STOCK' },
];

const CURRENCY_OPTIONS = [
  { label: 'BRL', value: 'BRL' },
  { label: 'USD', value: 'USD' },
];

function slugify(text: string): string {
  return text
    .trim()
    .toLowerCase()
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .replace(/\s+/g, '-')
    .replace(/[^a-z0-9-]/g, '');
}

@Component({
  selector: 'app-products-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    TableModule,
    ButtonModule,
    InputTextModule,
    InputNumberModule,
    TagModule,
    Toast,
    ConfirmDialog,
    TooltipModule,
    DialogModule,
    DropdownModule,
    TabViewModule,
    CheckboxModule,
    TextareaModule,
  ],
  providers: [MessageService, ConfirmationService],
  templateUrl: './products-list.html',
  styleUrl: './products-list.scss',
})
export class ProductsListComponent {
  private readonly fb = inject(FormBuilder);
  private readonly productsService = inject(ProductsService);
  private readonly messageService = inject(MessageService);
  private readonly confirmationService = inject(ConfirmationService);

  products = signal<Product[]>([]);
  totalRecords = signal(0);
  loading = signal(false);
  saving = signal(false);
  uploading = signal(false);
  dialogVisible = signal(false);
  editingProduct = signal<ProductDetail | null>(null);

  searchControl = this.fb.control('');
  statusFilterControl = this.fb.control<string | null>(null);

  statusOptions = STATUS_OPTIONS;
  currencyOptions = CURRENCY_OPTIONS;

  private currentPage = 0;
  private currentSize = 10;

  productForm = this.fb.nonNullable.group({
    sku: ['', [Validators.required, Validators.maxLength(100)]],
    name: ['', [Validators.required, Validators.maxLength(255)]],
    shortDescription: ['', [Validators.required, Validators.maxLength(500)]],
    fullDescription: [''],
    salePrice: [0, [Validators.required, Validators.min(0.01)]],
    costPrice: [0, [Validators.required, Validators.min(0)]],
    compareAtPrice: [null as number | null],
    currency: ['BRL', Validators.required],
    status: ['DRAFT' as ProductStatus, Validators.required],
    supplierSku: [''],
    supplierName: [''],
    supplierProductUrl: [''],
    leadTimeDays: [null as number | null],
    isDropship: [true],
    weight: [null as number | null],
    length: [null as number | null],
    width: [null as number | null],
    height: [null as number | null],
    slug: ['', [Validators.required, Validators.maxLength(255)]],
    brand: [''],
    metaTitle: [''],
    metaDescription: [''],
    stockQuantity: [null as number | null],
    images: this.fb.array<FormGroup>([]),
  });

  imagesFormArray = this.productForm.get('images') as FormArray;

  isEditMode = computed(() => this.editingProduct() !== null);

  newImageMain = this.fb.control(false);

  ngOnInit(): void {
    this.loadProducts(this.currentPage, this.currentSize);
    this.productForm.get('name')?.valueChanges.subscribe((name) => {
      if (!this.isEditMode() && name && !this.productForm.get('slug')?.dirty) {
        this.productForm
          .get('slug')
          ?.setValue(slugify(name), { emitEvent: false });
      }
    });
    this.statusFilterControl.valueChanges.subscribe(() => {
      this.loadProducts(0, this.currentSize);
    });
  }

  applySearch(): void {
    this.loadProducts(0, this.currentSize);
  }

  refresh(): void {
    this.loadProducts(this.currentPage, this.currentSize);
  }

  onLazyLoad(event: TableLazyLoadEvent): void {
    const first = event.first ?? 0;
    const rows = event.rows ?? 10;
    this.currentPage = Math.floor(first / rows);
    this.currentSize = rows;
    this.loadProducts(this.currentPage, rows);
  }

  private loadProducts(page: number, size: number): void {
    this.loading.set(true);
    const name = this.searchControl.value?.trim() || undefined;
    const status = this.statusFilterControl.value || undefined;
    this.productsService
      .list({
        page,
        size,
        sort: 'name,asc',
        name: name || undefined,
        status: status || undefined,
      })
      .subscribe({
        next: (res) => {
          this.products.set(res.content);
          this.totalRecords.set(res.totalElements);
        },
        error: () => {
          this.messageService.add({
            severity: 'error',
            summary: 'Erro',
            detail: 'Não foi possível carregar os produtos.',
          });
        },
        complete: () => this.loading.set(false),
      });
  }

  openCreateDialog(): void {
    this.editingProduct.set(null);
    this.resetForm();
    this.imagesFormArray.clear();
    this.dialogVisible.set(true);
  }

  openEditDialog(product: Product): void {
    this.productsService.getById(product.id).subscribe({
      next: (detail) => {
        this.editingProduct.set(detail);
        this.patchForm(detail);
        this.dialogVisible.set(true);
      },
      error: () => {
        this.messageService.add({
          severity: 'error',
          summary: 'Erro',
          detail: 'Não foi possível carregar o produto.',
        });
      },
    });
  }

  private patchForm(p: ProductDetail): void {
    this.productForm.patchValue({
      sku: p.sku,
      name: p.name,
      shortDescription: p.shortDescription,
      fullDescription: p.fullDescription ?? '',
      salePrice: p.salePrice,
      costPrice: p.costPrice,
      compareAtPrice: p.compareAtPrice ?? null,
        currency: p.currency as string,
      status: p.status,
      supplierSku: p.supplierSku ?? '',
      supplierName: p.supplierName ?? '',
      supplierProductUrl: p.supplierProductUrl ?? '',
      leadTimeDays: p.leadTimeDays ?? null,
      isDropship: p.isDropship,
      weight: p.weight ?? null,
      length: p.length ?? null,
      width: p.width ?? null,
      height: p.height ?? null,
      slug: p.slug,
      brand: p.brand ?? '',
      metaTitle: p.metaTitle ?? '',
      metaDescription: p.metaDescription ?? '',
      stockQuantity: p.stockQuantity ?? null,
    });
    this.imagesFormArray.clear();
    p.images?.forEach((img) => {
      this.imagesFormArray.push(
        this.fb.nonNullable.group({
          id: [img.id],
          url: [img.url, Validators.required],
          main: [img.main],
          altText: [img.altText ?? ''],
        })
      );
    });
  }

  private resetForm(): void {
    this.productForm.reset({
      sku: '',
      name: '',
      shortDescription: '',
      fullDescription: '',
      salePrice: 0,
      costPrice: 0,
      compareAtPrice: null,
      currency: 'BRL',
      status: 'DRAFT',
      supplierSku: '',
      supplierName: '',
      supplierProductUrl: '',
      leadTimeDays: null,
      isDropship: true,
      weight: null,
      length: null,
      width: null,
      height: null,
      slug: '',
      brand: '',
      metaTitle: '',
      metaDescription: '',
      stockQuantity: null,
    });
    this.newImageMain.setValue(false);
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const files = input.files;
    if (!files?.length) return;

    const fileArray = Array.from(files);
    const validTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
    const toUpload = fileArray.filter((f) => validTypes.includes(f.type));

    if (toUpload.length === 0) {
      this.messageService.add({
        severity: 'warn',
        summary: 'Formato inválido',
        detail: 'Use imagens JPG, PNG, GIF ou WebP.',
      });
      input.value = '';
      return;
    }

    this.uploading.set(true);
    let completed = 0;
    const total = toUpload.length;

    const tryComplete = () => {
      completed++;
      if (completed >= total) {
        this.uploading.set(false);
        input.value = '';
      }
    };

    for (const file of toUpload) {
      this.productsService.uploadImage(file).subscribe({
        next: (res) => {
          const url = res.url;
          const isMain = this.newImageMain.value ?? false;
          if (isMain) {
            this.imagesFormArray.controls.forEach((c) =>
              c.patchValue({ main: false })
            );
          }
          if (this.isEditMode()) {
            const product = this.editingProduct();
            if (!product) {
              tryComplete();
              return;
            }
            this.productsService
              .addImage(product.id, {
                url,
                position: this.imagesFormArray.length,
                isMain,
                altText: undefined,
              })
              .subscribe({
                next: (img) => {
                  this.imagesFormArray.push(
                    this.fb.nonNullable.group({
                      id: [img.id],
                      url: [img.url, Validators.required],
                      main: [img.main],
                      altText: [img.altText ?? ''],
                    })
                  );
                  this.newImageMain.setValue(false);
                  this.messageService.add({
                    severity: 'success',
                    summary: 'Sucesso',
                    detail: 'Foto adicionada.',
                  });
                  tryComplete();
                },
                error: (err) => {
                  this.messageService.add({
                    severity: 'error',
                    summary: 'Erro',
                    detail:
                      err.error?.message ?? 'Não foi possível adicionar a foto.',
                  });
                  tryComplete();
                },
              });
          } else {
            this.imagesFormArray.push(
              this.fb.nonNullable.group({
                id: [''],
                url: [url, Validators.required],
                main: [isMain],
                altText: [''],
              })
            );
            this.newImageMain.setValue(false);
            tryComplete();
          }
        },
        error: (err) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Erro',
            detail:
              err.error?.message ?? 'Não foi possível enviar a imagem.',
          });
          tryComplete();
        },
      });
    }
  }

  removeImageAt(index: number): void {
    this.imagesFormArray.removeAt(index);
  }

  setMainImageAt(index: number): void {
    this.imagesFormArray.controls.forEach((c, i) =>
      c.patchValue({ main: i === index })
    );
  }

  removeImageInEditMode(index: number, productId: string): void {
    const group = this.imagesFormArray.at(index) as FormGroup;
    const id = group.get('id')?.value;
    if (!id) {
      this.imagesFormArray.removeAt(index);
      return;
    }
    this.confirmationService.confirm({
      message: 'Remover esta foto do produto?',
      header: 'Confirmar',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.productsService.removeImage(productId, id).subscribe({
          next: () => {
            this.imagesFormArray.removeAt(index);
            this.messageService.add({
              severity: 'success',
              summary: 'Sucesso',
              detail: 'Foto removida.',
            });
          },
          error: (err) => {
            this.messageService.add({
              severity: 'error',
              summary: 'Erro',
              detail: err.error?.message ?? 'Não foi possível remover a foto.',
            });
          },
        });
      },
    });
  }

  setMainInEditMode(index: number, productId: string): void {
    const group = this.imagesFormArray.at(index) as FormGroup;
    const id = group.get('id')?.value;
    if (!id) return;
    this.productsService
      .updateImage(productId, id, { isMain: true })
      .subscribe({
        next: () => {
          this.imagesFormArray.controls.forEach((c, i) =>
            c.patchValue({ main: i === index })
          );
          this.messageService.add({
            severity: 'success',
            summary: 'Sucesso',
            detail: 'Foto principal atualizada.',
          });
        },
        error: (err) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Erro',
            detail:
              err.error?.message ?? 'Não foi possível definir a foto principal.',
          });
        },
      });
  }

  submit(): void {
    if (this.productForm.invalid) {
      this.productForm.markAllAsTouched();
      return;
    }
    const editing = this.editingProduct();
    if (editing) {
      this.submitUpdate(editing.id);
    } else {
      this.submitCreate();
    }
  }

  private submitCreate(): void {
    const v = this.productForm.getRawValue();
    const images = this.imagesFormArray.controls.map((c) => {
      const g = c.getRawValue();
      return {
        url: g.url,
        position: 0,
        isMain: g.main,
        altText: g.altText || undefined,
      } satisfies CreateProductImageRequest;
    });
    if (images.length === 0) {
      this.messageService.add({
        severity: 'warn',
        summary: 'Atenção',
        detail: 'Adicione ao menos uma foto ao produto.',
      });
      return;
    }
    const hasMain = images.some((i) => i.isMain);
    if (!hasMain) images[0]!.isMain = true;

    const data: CreateProductRequest = {
      sku: v.sku,
      name: v.name,
      shortDescription: v.shortDescription,
      fullDescription: v.fullDescription || undefined,
      salePrice: v.salePrice,
      costPrice: v.costPrice,
      currency: v.currency,
      status: v.status,
      slug: v.slug,
      supplierSku: v.supplierSku || undefined,
      supplierName: v.supplierName || undefined,
      supplierProductUrl: v.supplierProductUrl || undefined,
      leadTimeDays: v.leadTimeDays ?? undefined,
      isDropship: v.isDropship,
      weight: v.weight ?? undefined,
      length: v.length ?? undefined,
      width: v.width ?? undefined,
      height: v.height ?? undefined,
      brand: v.brand || undefined,
      metaTitle: v.metaTitle || undefined,
      metaDescription: v.metaDescription || undefined,
      compareAtPrice: v.compareAtPrice ?? undefined,
      stockQuantity: v.stockQuantity ?? undefined,
      images,
    };
    this.saving.set(true);
    this.productsService.create(data).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Sucesso',
          detail: 'Produto cadastrado com sucesso.',
        });
        this.dialogVisible.set(false);
        this.loadProducts(this.currentPage, this.currentSize);
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Erro',
          detail: err.error?.message ?? 'Não foi possível cadastrar o produto.',
        });
      },
      complete: () => this.saving.set(false),
    });
  }

  private submitUpdate(id: string): void {
    const v = this.productForm.getRawValue();
    this.saving.set(true);
    this.productsService
      .update(id, {
        name: v.name,
        shortDescription: v.shortDescription,
        fullDescription: v.fullDescription || undefined,
        salePrice: v.salePrice,
        costPrice: v.costPrice,
        currency: v.currency,
        status: v.status,
        slug: v.slug,
        supplierSku: v.supplierSku || undefined,
        supplierName: v.supplierName || undefined,
        supplierProductUrl: v.supplierProductUrl || undefined,
        leadTimeDays: v.leadTimeDays ?? undefined,
        isDropship: v.isDropship,
        weight: v.weight ?? undefined,
        length: v.length ?? undefined,
        width: v.width ?? undefined,
        height: v.height ?? undefined,
        brand: v.brand || undefined,
        metaTitle: v.metaTitle || undefined,
        metaDescription: v.metaDescription || undefined,
        compareAtPrice: v.compareAtPrice ?? undefined,
        stockQuantity: v.stockQuantity ?? undefined,
      })
      .subscribe({
        next: (updated) => {
          this.editingProduct.set(updated);
          this.messageService.add({
            severity: 'success',
            summary: 'Sucesso',
            detail: 'Produto atualizado com sucesso.',
          });
          this.dialogVisible.set(false);
          this.loadProducts(this.currentPage, this.currentSize);
        },
        error: (err) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Erro',
            detail: err.error?.message ?? 'Não foi possível atualizar o produto.',
          });
        },
        complete: () => this.saving.set(false),
      });
  }

  updateStatus(product: Product, status: ProductStatus): void {
    this.productsService.updateStatus(product.id, status).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Sucesso',
          detail: 'Status atualizado.',
        });
        this.loadProducts(this.currentPage, this.currentSize);
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Erro',
          detail: err.error?.message ?? 'Não foi possível atualizar o status.',
        });
      },
    });
  }

  deleteProduct(product: Product): void {
    this.confirmationService.confirm({
      message: `Excluir o produto "${product.name}"? Esta ação não pode ser desfeita.`,
      header: 'Confirmar exclusão',
      icon: 'pi pi-exclamation-triangle',
      acceptButtonStyleClass: 'p-button-danger',
      accept: () => {
        this.productsService.delete(product.id).subscribe({
          next: () => {
            this.messageService.add({
              severity: 'success',
              summary: 'Sucesso',
              detail: 'Produto excluído.',
            });
            this.loadProducts(this.currentPage, this.currentSize);
          },
          error: (err) => {
            this.messageService.add({
              severity: 'error',
              summary: 'Erro',
              detail: err.error?.message ?? 'Não foi possível excluir o produto.',
            });
          },
        });
      },
    });
  }

  onDialogHide(): void {
    this.editingProduct.set(null);
    this.resetForm();
  }

  formatPrice(value: number, currency: string): string {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency,
    }).format(value);
  }

  getStatusSeverity(status: ProductStatus): string {
    const map: Record<ProductStatus, string> = {
      DRAFT: 'secondary',
      ACTIVE: 'success',
      INACTIVE: 'warn',
      OUT_OF_STOCK: 'danger',
    };
    return map[status] ?? 'secondary';
  }

  getStatusLabel(status: ProductStatus): string {
    return STATUS_OPTIONS.find((o) => o.value === status)?.label ?? status;
  }
}
