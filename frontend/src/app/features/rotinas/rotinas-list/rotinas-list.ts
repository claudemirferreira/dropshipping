import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { TableModule, TableLazyLoadEvent } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { TagModule } from 'primeng/tag';
import { Toast } from 'primeng/toast';
import { MessageService, ConfirmationService } from 'primeng/api';
import { ConfirmDialog } from 'primeng/confirmdialog';
import { TooltipModule } from 'primeng/tooltip';
import { DialogModule } from 'primeng/dialog';
import { DropdownModule } from 'primeng/dropdown';
import { CheckboxModule } from 'primeng/checkbox';
import { TextareaModule } from 'primeng/textarea';
import {
  RotinasService,
  Rotina,
  CreateRotinaRequest,
  UpdateRotinaRequest,
} from '../../../core/services/rotinas.service';

const STATUS_OPTIONS = [
  { label: 'Todos', value: null },
  { label: 'Ativo', value: true },
  { label: 'Inativo', value: false },
];

@Component({
  selector: 'app-rotinas-list',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    TableModule,
    ButtonModule,
    InputTextModule,
    TagModule,
    Toast,
    ConfirmDialog,
    TooltipModule,
    DialogModule,
    DropdownModule,
    CheckboxModule,
    TextareaModule,
  ],
  providers: [MessageService, ConfirmationService],
  template: `
    <p-toast />
    <p-confirmDialog />

    <div class="page-header">
      <div class="page-title-block">
        <h1 class="page-title">Rotinas</h1>
        <p class="page-description">
          Funcionalidades do sistema. Defina quais ações cada perfil pode executar.
        </p>
      </div>
      <div class="page-badge">
        <span class="badge-dot"></span>
        <span class="badge-value">{{ totalRecords() }}</span>
        <span class="badge-label">rotinas cadastradas</span>
      </div>
    </div>

    <div class="page-toolbar">
      <div class="search-wrapper">
        <i class="pi pi-search search-icon"></i>
        <input
          type="text"
          pInputText
          placeholder="Buscar por código ou nome..."
          class="search-input"
          [formControl]="searchControl"
          (keyup.enter)="applySearch()"
        />
      </div>
      <div class="toolbar-filters">
        <p-dropdown
          [options]="statusOptions"
          [formControl]="statusControl"
          optionLabel="label"
          optionValue="value"
          placeholder="Status"
          [showClear]="false"
          styleClass="status-dropdown"
          (onChange)="applySearch()"
        />
      </div>
      <div class="toolbar-actions">
        <p-button
          label="Nova rotina"
          icon="pi pi-plus"
          size="small"
          severity="primary"
          (onClick)="openCreateDialog()"
        />
      </div>
    </div>

    <div class="table-card">
      <p-table
        [value]="rotinas()"
        [lazy]="true"
        [paginator]="true"
        [rows]="10"
        [totalRecords]="totalRecords()"
        [loading]="loading()"
        (onLazyLoad)="onLazyLoad($event)"
        [rowsPerPageOptions]="[10, 25, 50]"
        dataKey="id"
        currentPageReportTemplate="{first} - {last} de {totalRecords}"
        paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport RowsPerPageDropdown"
        styleClass="p-datatable-sm"
      >
        <ng-template pTemplate="header">
          <tr>
            <th style="width: 3rem"></th>
            <th>Código</th>
            <th>Nome</th>
            <th>Descrição</th>
            <th>Path</th>
            <th>Status</th>
            <th style="width: 170px">Ações</th>
          </tr>
        </ng-template>
        <ng-template pTemplate="body" let-row>
          <tr>
            <td>
              <i [class]="row.icon || 'pi pi-circle'" class="rotina-icon"></i>
            </td>
            <td><code class="code-cell">{{ row.code }}</code></td>
            <td class="name-cell">{{ row.name }}</td>
            <td>{{ (row.description || '-') | slice : 0 : 50 }}{{ (row.description?.length ?? 0) > 50 ? '...' : '' }}</td>
            <td>{{ row.path || '-' }}</td>
            <td>
              <p-tag
                [value]="row.active ? 'Ativo' : 'Inativo'"
                [severity]="row.active ? 'success' : 'danger'"
              />
            </td>
            <td class="actions-cell">
              <div class="actions-buttons">
                <p-button
                  icon="pi pi-pencil"
                  [rounded]="true"
                  [text]="true"
                  severity="secondary"
                  size="small"
                  (onClick)="openEditDialog(row)"
                  pTooltip="Editar"
                />
                <p-button
                  icon="pi pi-trash"
                  [rounded]="true"
                  [text]="true"
                  severity="secondary"
                  size="small"
                  (onClick)="confirmDelete(row)"
                  pTooltip="Excluir"
                />
              </div>
            </td>
          </tr>
        </ng-template>
        <ng-template pTemplate="emptymessage">
          <tr>
            <td colspan="7" class="empty-message">Nenhuma rotina encontrada.</td>
          </tr>
        </ng-template>
        <ng-template pTemplate="loadingbody">
          <tr>
            <td colspan="7" class="loading-message">
              <i class="pi pi-spin pi-spinner"></i> Carregando...
            </td>
          </tr>
        </ng-template>
      </p-table>
    </div>

    <p-dialog
      [header]="editId() ? 'Editar rotina' : 'Nova rotina'"
      [(visible)]="dialogVisible"
      [modal]="true"
      [style]="{ width: '28rem' }"
      [draggable]="false"
      [resizable]="false"
      (onHide)="closeDialog()"
    >
      <form [formGroup]="form" class="create-form">
        <div class="form-field">
          <label for="code">Código</label>
          <input id="code" pInputText formControlName="code" placeholder="produtos:listar" />
          @if (form.get('code')?.invalid && form.get('code')?.touched) {
            <small class="field-error">Código é obrigatório</small>
          }
        </div>
        <div class="form-field">
          <label for="name">Nome</label>
          <input id="name" pInputText formControlName="name" placeholder="Listar produtos" />
          @if (form.get('name')?.invalid && form.get('name')?.touched) {
            <small class="field-error">Nome é obrigatório</small>
          }
        </div>
        <div class="form-field">
          <label for="description">Descrição (opcional)</label>
          <textarea id="description" pInputTextarea formControlName="description" rows="2" placeholder="Descrição da rotina"></textarea>
        </div>
        <div class="form-field">
          <label for="icon">Ícone (opcional)</label>
          <input id="icon" pInputText formControlName="icon" placeholder="pi pi-list" />
        </div>
        <div class="form-field">
          <label for="path">Path (opcional)</label>
          <input id="path" pInputText formControlName="path" placeholder="/produtos" />
        </div>
        <div class="form-field form-field-checkbox">
          <p-checkbox formControlName="active" [binary]="true" inputId="active" />
          <label for="active">Ativo</label>
        </div>
      </form>
      <ng-template pTemplate="footer">
        <p-button label="Cancelar" [text]="true" (onClick)="closeDialog()" />
        <p-button
          [label]="editId() ? 'Salvar' : 'Cadastrar'"
          icon="pi pi-check"
          (onClick)="submit()"
          [loading]="saving()"
          [disabled]="form.invalid"
        />
      </ng-template>
    </p-dialog>
  `,
  styles: [
    `
      .page-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        margin-bottom: 1.5rem;
        gap: 1rem;
        flex-wrap: wrap;
      }

      .page-title {
        margin: 0;
        font-size: 1.5rem;
        font-weight: 700;
        color: #1e293b;
      }

      .page-description {
        margin: 0.25rem 0 0;
        font-size: 0.875rem;
        color: #64748b;
      }

      .page-badge {
        display: inline-flex;
        align-items: center;
        gap: 0.5rem;
        padding: 0.375rem 0.875rem;
        border-radius: 999px;
        border: 1px solid #e2e8f0;
        background: #ffffff;
      }

      .page-badge .badge-dot {
        width: 8px;
        height: 8px;
        border-radius: 50%;
        background: var(--badge-active-color);
        flex-shrink: 0;
      }

      .page-badge .badge-value {
        color: var(--badge-active-color);
        font-weight: 600;
        font-size: 0.875rem;
      }

      .page-badge .badge-label {
        color: #64748b;
        font-size: 0.875rem;
      }

      .page-toolbar {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 1rem;
        gap: 1rem;
        flex-wrap: wrap;
      }

      .search-wrapper {
        position: relative;
        flex: 1;
        min-width: 12rem;
        max-width: 20rem;
      }

      .search-wrapper .search-icon {
        position: absolute;
        left: 0.75rem;
        top: 50%;
        transform: translateY(-50%);
        color: #94a3b8;
        font-size: 0.875rem;
      }

      .search-wrapper .search-input {
        width: 100%;
        padding: 0.5rem 0.75rem 0.5rem 2.25rem;
        border-radius: var(--p-border-radius);
        border: 1px solid #e2e8f0;
        font-size: 0.875rem;
        background: #ffffff;
        color: #1e293b;
      }

      .search-wrapper .search-input::placeholder {
        color: #94a3b8;
      }

      .toolbar-filters {
        display: flex;
        gap: 0.5rem;
      }

      .toolbar-filters .status-dropdown {
        min-width: 8rem;
      }

      .toolbar-actions {
        display: flex;
        gap: 0.5rem;
        align-items: center;
      }

      .table-card {
        background: #ffffff;
        border-radius: var(--p-border-radius);
        border: 1px solid #e2e8f0;
        overflow: hidden;
        box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
      }

      .table-card ::ng-deep .p-datatable .p-datatable-thead > tr > th {
        background: #f8fafc !important;
        color: #475569 !important;
        font-weight: 600;
        border-color: #e2e8f0;
        font-size: 0.8125rem;
        padding: 0.5rem 1rem;
      }

      .table-card ::ng-deep .p-datatable .p-datatable-tbody > tr > td {
        border-color: #e2e8f0;
        color: #334155;
        padding: 0.5rem 1rem;
      }

      .table-card ::ng-deep .p-datatable .p-datatable-tbody > tr:nth-child(even) {
        background: #f8fafc !important;
      }

      .table-card ::ng-deep .p-datatable .p-datatable-tbody > tr:hover {
        background: #f1f5f9 !important;
      }

      .table-card ::ng-deep .p-paginator {
        background: #ffffff;
        border-color: #dde2e7;
        border-top: 1px solid #dde2e7;
        color: #5d6c7f;
      }

      .table-card ::ng-deep .p-paginator .p-link,
      .table-card ::ng-deep .p-paginator .p-paginator-current,
      .table-card ::ng-deep .p-paginator .p-dropdown,
      .table-card ::ng-deep .p-paginator .p-paginator-pages .p-link {
        color: #5d6c7f !important;
      }

      .table-card ::ng-deep .p-paginator .p-link:hover {
        background: #f1f5f9;
        color: #5d6c7f;
      }

      .table-card ::ng-deep .p-paginator .p-link.p-highlight {
        background: #22c55e;
        color: white !important;
        border-radius: 50%;
      }

      .actions-cell {
        overflow: visible;
      }
      .actions-buttons {
        display: inline-flex;
        align-items: center;
        gap: 0.25rem;
      }
      .actions-buttons ::ng-deep .p-button {
        flex-shrink: 0;
        color: #475569;
      }

      .rotina-icon {
        color: #64748b;
        font-size: 1rem;
      }

      .code-cell {
        font-size: 0.8125rem;
        background: #f1f5f9;
        padding: 0.15rem 0.4rem;
        border-radius: 4px;
      }

      .name-cell {
        font-weight: 500;
        color: #334155;
      }

      .empty-message,
      .loading-message {
        text-align: center;
        padding: 2rem !important;
        color: #64748b;
      }

      .loading-message i {
        margin-right: 0.5rem;
      }

      .create-form {
        display: flex;
        flex-direction: column;
        gap: 1rem;
      }

      .form-field {
        display: flex;
        flex-direction: column;
        gap: 0.35rem;
      }

      .form-field label {
        font-weight: 500;
        font-size: 0.875rem;
      }

      .form-field-checkbox {
        flex-direction: row;
        align-items: center;
        gap: 0.5rem;
      }

      .form-field-checkbox label {
        margin: 0;
      }

      .field-error {
        color: var(--p-red-500);
        font-size: 0.75rem;
      }
    `,
  ],
})
export class RotinasListComponent {
  private fb = inject(FormBuilder);
  private rotinasService = inject(RotinasService);
  private messageService = inject(MessageService);
  private confirmationService = inject(ConfirmationService);

  rotinas = signal<Rotina[]>([]);
  totalRecords = signal(0);
  loading = signal(false);
  saving = signal(false);
  dialogVisible = false;
  editId = signal<string | null>(null);
  searchControl = new FormControl('', { nonNullable: true });
  statusControl = new FormControl<boolean | null>(null);
  statusOptions = STATUS_OPTIONS;

  form = this.fb.nonNullable.group({
    code: ['', [Validators.required, Validators.maxLength(100)]],
    name: ['', [Validators.required, Validators.maxLength(255)]],
    description: [''],
    icon: [''],
    path: [''],
    active: [true],
  });

  private currentPage = 0;
  private currentSize = 10;

  applySearch(): void {
    this.loadRotinas(0, this.currentSize);
  }

  refresh(): void {
    this.loadRotinas(this.currentPage, this.currentSize);
  }

  onLazyLoad(event: TableLazyLoadEvent): void {
    const page = event.first ?? 0;
    const size = event.rows ?? 10;
    this.currentPage = Math.floor(page / size);
    this.currentSize = size;
    this.loadRotinas(this.currentPage, size);
  }

  private loadRotinas(page: number, size: number): void {
    this.loading.set(true);
    const search = this.searchControl.value.trim() || undefined;
    const params: Record<string, unknown> = {
      page,
      size,
      sort: 'name,asc',
      name: search,
    };
    const status = this.statusControl.value;
    if (status != null) params['active'] = status;
    this.rotinasService.list(params).subscribe({
      next: (res) => {
        this.rotinas.set(res.content);
        this.totalRecords.set(res.totalElements);
      },
      error: () => {
        this.messageService.add({
          severity: 'error',
          summary: 'Erro',
          detail: 'Não foi possível carregar as rotinas.',
        });
      },
      complete: () => this.loading.set(false),
    });
  }

  openCreateDialog(): void {
    this.editId.set(null);
    this.form.reset({ code: '', name: '', description: '', icon: '', path: '', active: true });
    this.dialogVisible = true;
  }

  openEditDialog(row: Rotina): void {
    this.editId.set(row.id);
    this.form.patchValue({
      code: row.code,
      name: row.name,
      description: row.description ?? '',
      icon: row.icon ?? '',
      path: row.path ?? '',
      active: row.active,
    });
    this.dialogVisible = true;
  }

  closeDialog(): void {
    this.dialogVisible = false;
    this.editId.set(null);
  }

  submit(): void {
    if (this.form.invalid) return;
    const value = this.form.getRawValue();
    const id = this.editId();
    const data: CreateRotinaRequest | UpdateRotinaRequest = {
      code: value.code,
      name: value.name,
      description: value.description?.trim() || undefined,
      icon: value.icon?.trim() || undefined,
      path: value.path?.trim() || undefined,
      active: value.active,
    };
    this.saving.set(true);
    const req = id
      ? this.rotinasService.update(id, data)
      : this.rotinasService.create(data);
    req.subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Sucesso',
          detail: id ? 'Rotina atualizada.' : 'Rotina cadastrada.',
        });
        this.closeDialog();
        this.loadRotinas(this.currentPage, this.currentSize);
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Erro',
          detail: err.error?.message ?? 'Não foi possível salvar a rotina.',
        });
      },
      complete: () => this.saving.set(false),
    });
  }

  confirmDelete(row: Rotina): void {
    this.confirmationService.confirm({
      message: `Deseja excluir a rotina "${row.name}"?`,
      header: 'Confirmar exclusão',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.rotinasService.delete(row.id).subscribe({
          next: () => {
            this.messageService.add({
              severity: 'success',
              summary: 'Sucesso',
              detail: 'Rotina excluída.',
            });
            this.loadRotinas(this.currentPage, this.currentSize);
          },
          error: (err) => {
            this.messageService.add({
              severity: 'error',
              summary: 'Erro',
              detail: err.error?.message ?? 'Não foi possível excluir a rotina.',
            });
          },
        });
      },
    });
  }
}
