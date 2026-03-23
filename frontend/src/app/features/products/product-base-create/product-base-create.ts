import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { ButtonModule } from 'primeng/button';
import { CheckboxModule } from 'primeng/checkbox';
import { TextareaModule } from 'primeng/textarea';
import { StepperModule } from 'primeng/stepper';
import { MessageService } from 'primeng/api';
import { AdminProductsService, CreateBaseProductRequest } from '../../../core/services/admin-products.service';

@Component({
  selector: 'app-product-base-create',
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
  templateUrl: './product-base-create.html',
  styleUrls: ['./product-base-create.scss'],
})
export class ProductBaseCreateComponent {
  private fb = inject(FormBuilder);
  private adminService = inject(AdminProductsService);
  private router = inject(Router);
  private messageService = inject(MessageService);

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

  isStepValid(step: number): boolean {
    switch (step) {
      case 1:
        return (
          this.form.get('nome')!.valid &&
          this.form.get('sku')!.valid &&
          this.form.get('categoria_id')!.valid &&
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
    const payload: CreateBaseProductRequest = {
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

    this.adminService.create(payload).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Sucesso',
          detail: 'Produto criado com sucesso.',
        });
        this.router.navigate(['/produtos']);
      },
      error: (err) => {
        const msg =
          err?.error?.message ||
          err?.error?.detail ||
          'Não foi possível criar o produto.';
        this.messageService.add({
          severity: 'error',
          summary: 'Erro',
          detail: msg,
        });
      },
    });
  }
}
