import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { TableModule, TableLazyLoadEvent } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { DropdownModule } from 'primeng/dropdown';
import { DialogModule } from 'primeng/dialog';
import { TooltipModule } from 'primeng/tooltip';
import { TagModule } from 'primeng/tag';
import { ConfirmDialog } from 'primeng/confirmdialog';
import { MessageService, ConfirmationService } from 'primeng/api';
import {
  SellersService,
  Seller,
  CreateSellerRequest,
  UpdateSellerRequest,
  Marketplace,
} from '../../../core/services/sellers.service';
import { UsersService } from '../../../core/services/users.service';

interface UserOption {
  label: string;
  value: string;
}

const MARKETPLACE_OPTIONS: { label: string; value: Marketplace }[] = [
  { label: 'Mercado Livre', value: 'mercado_livre' },
  { label: 'Shopee', value: 'shopee' },
];

@Component({
  selector: 'app-sellers-list',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    TableModule,
    ButtonModule,
    InputTextModule,
    InputNumberModule,
    DropdownModule,
    DialogModule,
    TooltipModule,
    TagModule,
    ConfirmDialog,
  ],
  providers: [ConfirmationService],
  templateUrl: './sellers-list.html',
  styleUrl: './sellers-list.scss',
})
export class SellersListComponent {
  private fb = inject(FormBuilder);
  private sellersService = inject(SellersService);
  private usersService = inject(UsersService);
  private messageService = inject(MessageService);
  private confirmationService = inject(ConfirmationService);

  sellers = signal<Seller[]>([]);
  totalRecords = signal(0);
  loading = signal(false);
  saving = signal(false);
  dialogVisible = false;
  editId = signal<string | null>(null);
  userOptions = signal<UserOption[]>([]);
  marketplaceOptions = MARKETPLACE_OPTIONS;

  marketplaceIdFilter = new FormControl<number | null>(null);

  form = this.fb.nonNullable.group({
    userId: ['', Validators.required],
    marketplace: ['mercado_livre' as Marketplace, Validators.required],
    access_token: ['', Validators.required],
    token_type: ['Bearer', Validators.required],
    expires_in: [0, [Validators.required, Validators.min(0)]],
    scope: ['', Validators.required],
    marketplace_id: [0, [Validators.required, Validators.min(0)]],
    refresh_token: ['', Validators.required],
  });

  private currentPage = 0;
  private currentSize = 10;

  applyFilter(): void {
    this.loadSellers(0, this.currentSize);
  }

  clearFilter(): void {
    this.marketplaceIdFilter.setValue(null);
    this.loadSellers(0, this.currentSize);
  }

  onLazyLoad(event: TableLazyLoadEvent): void {
    const first = event.first ?? 0;
    const size = event.rows ?? 10;
    this.currentPage = Math.floor(first / size);
    this.currentSize = size;
    this.loadSellers(this.currentPage, size);
  }

  private loadSellers(page: number, size: number): void {
    this.loading.set(true);
    const marketplaceId = this.marketplaceIdFilter.value;
    const params: Record<string, unknown> = {
      page,
      size,
      sort: 'createdAt,desc',
    };
    if (marketplaceId != null) params['marketplaceId'] = marketplaceId;
    this.sellersService.list(params).subscribe({
      next: (res) => {
        this.sellers.set(res.content);
        this.totalRecords.set(res.totalElements);
      },
      error: () => {
        this.messageService.add({
          severity: 'error',
          summary: 'Erro',
          detail: 'Não foi possível carregar os sellers.',
        });
      },
      complete: () => this.loading.set(false),
    });
  }

  private loadUserOptions(): void {
    this.usersService.list({ size: 500, sort: 'name,asc' }).subscribe({
      next: (res) => {
        this.userOptions.set(
          res.content.map((u) => ({
            label: `${u.name} (${u.email})`,
            value: u.id,
          }))
        );
      },
      error: () => this.userOptions.set([]),
    });
  }

  marketplaceLabel(value: Marketplace): string {
    return MARKETPLACE_OPTIONS.find((o) => o.value === value)?.label ?? value;
  }

  userLabel(userId: string): string {
    return this.userOptions().find((u) => u.value === userId)?.label ?? userId;
  }

  openCreateDialog(): void {
    this.editId.set(null);
    this.form.reset({
      userId: '',
      marketplace: 'mercado_livre',
      access_token: '',
      token_type: 'Bearer',
      expires_in: 0,
      scope: '',
      marketplace_id: 0,
      refresh_token: '',
    });
    this.form.get('userId')?.enable();
    this.loadUserOptions();
    this.dialogVisible = true;
  }

  openEditDialog(row: Seller): void {
    this.editId.set(row.id);
    this.loadUserOptions();
    this.sellersService.getById(row.id).subscribe({
      next: (seller) => {
        this.form.reset({
          userId: seller.userId,
          marketplace: seller.marketplace,
          access_token: seller.accessToken,
          token_type: seller.tokenType,
          expires_in: seller.expiresIn,
          scope: seller.scope,
          marketplace_id: seller.marketplaceId,
          refresh_token: seller.refreshToken,
        });
        this.form.get('userId')?.disable();
        this.dialogVisible = true;
      },
      error: () => {
        this.messageService.add({
          severity: 'error',
          summary: 'Erro',
          detail: 'Não foi possível carregar o seller.',
        });
      },
    });
  }

  closeDialog(): void {
    this.dialogVisible = false;
    this.editId.set(null);
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const value = this.form.getRawValue();
    const id = this.editId();
    this.saving.set(true);
    const payload: CreateSellerRequest | UpdateSellerRequest = {
      userId: value.userId,
      marketplace: value.marketplace,
      access_token: value.access_token.trim(),
      token_type: value.token_type.trim(),
      expires_in: value.expires_in,
      scope: value.scope.trim(),
      marketplace_id: value.marketplace_id,
      refresh_token: value.refresh_token.trim(),
    };
    const req = id
      ? this.sellersService.update(id, payload)
      : this.sellersService.create(payload);
    req.subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Sucesso',
          detail: id ? 'Seller atualizado.' : 'Seller cadastrado.',
        });
        this.closeDialog();
        this.loadSellers(this.currentPage, this.currentSize);
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Erro',
          detail: err.error?.message ?? 'Não foi possível salvar o seller.',
        });
      },
      complete: () => this.saving.set(false),
    });
  }

  confirmDelete(row: Seller): void {
    this.confirmationService.confirm({
      message: `Deseja excluir o seller vinculado ao marketplace ${this.marketplaceLabel(row.marketplace)} (id ${row.marketplaceId})?`,
      header: 'Confirmar exclusão',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sim',
      rejectLabel: 'Não',
      accept: () => {
        this.sellersService.delete(row.id).subscribe({
          next: () => {
            this.messageService.add({
              severity: 'success',
              summary: 'Sucesso',
              detail: 'Seller excluído.',
            });
            this.loadSellers(this.currentPage, this.currentSize);
          },
          error: (err) => {
            this.messageService.add({
              severity: 'error',
              summary: 'Erro',
              detail: err.error?.message ?? 'Não foi possível excluir o seller.',
            });
          },
        });
      },
    });
  }
}
