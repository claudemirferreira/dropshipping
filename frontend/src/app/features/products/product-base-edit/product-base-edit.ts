import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { ButtonModule } from 'primeng/button';
import { CheckboxModule } from 'primeng/checkbox';
import { TextareaModule } from 'primeng/textarea';
import { StepperModule } from 'primeng/stepper';
import { MessageService } from 'primeng/api';
import { AdminProductsService, ProductBaseDetail, UpdateBaseProductRequest } from '../../../core/services/admin-products.service';

@Component({
  selector: 'app-product-base-edit',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    InputTextModule,
    InputNumberModule,
    ButtonModule,
    CheckboxModule,
    TextareaModule,
    RouterLink,
    StepperModule
  ],
  templateUrl: './product-base-edit.html',
  styleUrls: ['./product-base-edit.scss'],
})
export class ProductBaseEditComponent implements OnInit {
  private fb = inject(FormBuilder);
  private adminService = inject(AdminProductsService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private messageService = inject(MessageService);

  productId = '';
  loading = signal(false);
  saving = signal(false);

  form: FormGroup = this.fb.group({
    nome: ['', [Validators.required, Validators.maxLength(60)]],
    slug: [''],
    sku: ['', [Validators.required]],
    categoria_id: [''],
    marca: ['', [Validators.required]],
    descricao_curta: ['', [Validators.required, Validators.maxLength(255)]],
    descricao_completa: ['', [Validators.required]],
    logistica: this.fb.group({
      peso_kg: [null, [Validators.required]],
      altura_cm: [null, [Validators.required]],
      largura_cm: [null, [Validators.required]],
      comprimento_cm: [null, [Validators.required]],
      lead_time_envio_dias: [null, [Validators.required, Validators.min(1)]],
    }),
    estoque: this.fb.group({
      atual: [0, [Validators.required, Validators.min(0)]],
      minimo: [0, [Validators.required, Validators.min(0)]],
    }),
    comercial: this.fb.group({
      valor_custo: [null, [Validators.required]],
      percentual_taxa_seller: [null],
      garantia: ['', [Validators.required]],
    }),
    codigos: this.fb.group({
      ean: [''],
      is_ean_interno: [false],
    }),
    tagsInput: [''],
  });

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.messageService.add({
        severity: 'error',
        summary: 'Erro',
        detail: 'ID do produto não informado.',
      });
      this.router.navigate(['/produtos']);
      return;
    }
    this.productId = id;
    this.loadProduct(id);
  }

  private loadProduct(id: string): void {
    this.loading.set(true);
    this.adminService.getDetail(id).subscribe({
      next: (detail) => this.patchForm(detail),
      error: () => {
        this.messageService.add({
          severity: 'error',
          summary: 'Erro',
          detail: 'Não foi possível carregar os dados do produto.',
        });
        this.router.navigate(['/produtos']);
      },
      complete: () => this.loading.set(false),
    });
  }

  private patchForm(detail: ProductBaseDetail): void {
    this.form.patchValue({
      nome: detail.nome,
      slug: detail.slug,
      sku: detail.sku,
      categoria_id: detail.categoria_id,
      marca: detail.marca,
      descricao_curta: detail.descricao_curta,
      descricao_completa: detail.descricao_completa,
      tagsInput: (detail.tags || []).join(', '),
      logistica: {
        peso_kg: detail.logistica?.peso_kg,
        altura_cm: detail.logistica?.altura_cm,
        largura_cm: detail.logistica?.largura_cm,
        comprimento_cm: detail.logistica?.comprimento_cm,
        lead_time_envio_dias: detail.logistica?.lead_time_envio_dias,
      },
      estoque: {
        atual: detail.estoque?.atual ?? 0,
        minimo: detail.estoque?.minimo ?? 0,
      },
      comercial: {
        valor_custo: detail.comercial?.valor_custo,
        percentual_taxa_seller: detail.comercial?.percentual_taxa_seller ?? null,
        garantia: detail.comercial?.garantia || '',
      },
      codigos: {
        ean: detail.codigos?.ean || '',
        is_ean_interno: detail.codigos?.is_ean_interno ?? false,
      },
    });
  }

  isStepValid(step: number): boolean {
    switch (step) {
      case 1:
        return (
          this.form.get('nome')!.valid &&
          this.form.get('sku')!.valid &&
          this.form.get('marca')!.valid &&
          this.form.get('descricao_curta')!.valid &&
          this.form.get('descricao_completa')!.valid
        );
      case 2:
        return this.form.get('logistica')!.valid && this.form.get('estoque')!.valid;
      case 3:
        return this.form.get('comercial')!.valid && this.form.get('codigos')!.valid;
      case 4:
        return this.form.valid;
      default:
        return false;
    }
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const v = this.form.getRawValue();
    const tags: string[] | undefined = v.tagsInput
      ? String(v.tagsInput)
        .split(',')
        .map((s: string) => s.trim())
        .filter((s: string) => s.length > 0)
      : undefined;
    const payload: UpdateBaseProductRequest = {
      nome: v.nome,
      slug: v.slug || null,
      sku: v.sku,
      categoria_id: v.categoria_id ? String(v.categoria_id).trim() : undefined,
      marca: v.marca,
      descricao_curta: v.descricao_curta,
      descricao_completa: v.descricao_completa,
      logistica: {
        peso_kg: Number(v.logistica.peso_kg),
        altura_cm: Number(v.logistica.altura_cm),
        largura_cm: Number(v.logistica.largura_cm),
        comprimento_cm: Number(v.logistica.comprimento_cm),
        lead_time_envio_dias: Number(v.logistica.lead_time_envio_dias),
      },
      estoque: {
        atual: Number(v.estoque.atual),
        minimo: Number(v.estoque.minimo),
      },
      comercial: {
        valor_custo: Number(v.comercial.valor_custo),
        percentual_taxa_seller:
          v.comercial.percentual_taxa_seller != null && v.comercial.percentual_taxa_seller !== ''
            ? Number(v.comercial.percentual_taxa_seller)
            : undefined,
        garantia: v.comercial.garantia,
      },
      codigos: v.codigos
        ? {
          ean: v.codigos.ean || undefined,
          is_ean_interno: !!v.codigos.is_ean_interno,
        }
        : undefined,
      tags,
    };

    this.saving.set(true);
    this.adminService.update(this.productId, payload).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Sucesso',
          detail: 'Produto atualizado com sucesso.',
        });
        this.router.navigate(['/produtos']);
      },
      error: (err) => {
        const msg =
          err?.error?.message ||
          err?.error?.detail ||
          'Não foi possível atualizar o produto.';
        this.messageService.add({
          severity: 'error',
          summary: 'Erro',
          detail: msg,
        });
      },
      complete: () => this.saving.set(false),
    });
  }
}
