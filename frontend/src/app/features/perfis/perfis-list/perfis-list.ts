import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { TableModule, TableLazyLoadEvent } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { MessageService, ConfirmationService } from 'primeng/api';
import { ConfirmDialog } from 'primeng/confirmdialog';
import { TooltipModule } from 'primeng/tooltip';
import { DialogModule } from 'primeng/dialog';
import { DropdownModule } from 'primeng/dropdown';
import { CheckboxModule } from 'primeng/checkbox';
import { PickListModule } from 'primeng/picklist';
import {
  PerfisService,
  Perfil,
  CreatePerfilRequest,
  UpdatePerfilRequest,
} from '../../../core/services/perfis.service';
import { RotinasService, Rotina } from '../../../core/services/rotinas.service';

const STATUS_OPTIONS = [
  { label: 'Todos', value: null },
  { label: 'Ativo', value: true },
  { label: 'Inativo', value: false },
];

interface RotinaOption {
  label: string;
  value: string;
}

@Component({
  selector: 'app-perfis-list',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    TableModule,
    ButtonModule,
    InputTextModule,
    ConfirmDialog,
    TooltipModule,
    DialogModule,
    DropdownModule,
    CheckboxModule,
    PickListModule,
  ],
  providers: [ConfirmationService],
  template: `
    <p-confirmDialog />

    <div class="page-header">
      <div class="page-title-block">
        <h1 class="page-title">Lista de perfis</h1>

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
          label="Novo "
          icon="pi pi-plus"
          size="small"
          severity="primary"
          (onClick)="openCreateDialog()"
        />
      </div>
    </div>

    <div class="table-card">
      <p-table
        [value]="perfis()"
        [lazy]="true"
        [paginator]="true"
        [rows]="10"
        [totalRecords]="totalRecords()"
        [loading]="loading()"
        (onLazyLoad)="onLazyLoad($event)"
        [rowsPerPageOptions]="[10, 25, 50]"
        dataKey="id"
        currentPageReportTemplate="{first} - {last} de {totalRecords}"
        [showCurrentPageReport]="true"
        paginatorTemplate="RowsPerPageDropdown CurrentPageReport PrevPageLink NextPageLink"
        [showFirstLastIcon]="false"
        styleClass="p-datatable-sm"
      >
        <ng-template pTemplate="header">
          <tr>
            <th style="width: 3rem"></th>
            <th>Código</th>
            <th>Nome</th>
            <th>Descrição</th>
            <th>Rotinas</th>
            <th>Status</th>
            <th style="width: 170px">Ações</th>
          </tr>
        </ng-template>
        <ng-template pTemplate="body" let-row>
          <tr>
            <td>
              <i [class]="row.icon || 'pi pi-id-card'" class="perfil-icon"></i>
            </td>
            <td><code class="code-cell">{{ row.code }}</code></td>
            <td class="name-cell">{{ row.name }}</td>
            <td>{{ row.rotinas?.length ?? 0 }}</td>
            <td>
              <span class="status-text">{{ row.active ? 'Ativo' : 'Inativo' }}</span>
            </td>
            <td class="actions-cell">
              <div class="actions-buttons">
                <p-button
                  icon="pi pi-link"
                  [rounded]="true"
                  [text]="true"
                  severity="secondary"
                  size="small"
                  (onClick)="openRotinasDialog(row)"
                  pTooltip="Rotinas"
                />
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
            <td colspan="6" class="empty-message">Nenhum perfil encontrado.</td>
          </tr>
        </ng-template>
        <ng-template pTemplate="loadingbody">
          <tr>
            <td colspan="6" class="loading-message">
              <i class="pi pi-spin pi-spinner"></i> Carregando...
            </td>
          </tr>
        </ng-template>
        <ng-template pTemplate="paginatorleft">
          <span class="paginator-rows-label">Itens por página:</span>
        </ng-template>
      </p-table>
    </div>

    <p-dialog
      [header]="editId() ? 'Editar perfil' : 'Novo perfil'"
      [(visible)]="dialogVisible"
      [modal]="true"
      [style]="{ width: '44rem' }"
      [contentStyle]="{ overflow: 'visible' }"
      [draggable]="false"
      [resizable]="false"
      (onHide)="closeDialog()"
    >
      <form [formGroup]="form" class="create-form">
        <div class="form-row">
          <div class="form-field">
            <label for="code">Código</label>
            <input id="code" pInputText formControlName="code" placeholder="ADMIN" />
            @if (form.get('code')?.invalid && form.get('code')?.touched) {
              <small class="field-error">Código é obrigatório</small>
            }
          </div>
          <div class="form-field">
            <label for="name">Nome</label>
            <input id="name" pInputText formControlName="name" placeholder="Administrador" />
            @if (form.get('name')?.invalid && form.get('name')?.touched) {
              <small class="field-error">Nome é obrigatório</small>
            }
          </div>
        </div>
        <div class="form-row">
          <div class="form-field">
            <label for="icon">Ícone (opcional)</label>
            <input id="icon" pInputText formControlName="icon" placeholder="pi pi-shield" />
          </div>
          <div class="form-field form-field-checkbox">
            <p-checkbox formControlName="active" [binary]="true" inputId="active" />
            <label for="active">Ativo</label>
          </div>
        </div>
        <div class="form-field">
          <label>Rotinas do perfil</label>
          <p-pickList
            [source]="formPickListSource"
            [target]="formPickListTarget"
            sourceHeader="Disponíveis"
            targetHeader="Associadas ao perfil"
            [dragdrop]="true"
            [filterBy]="'label'"
            [showSourceFilter]="true"
            [showTargetFilter]="true"
            breakpoint="768px"
            styleClass="form-picklist"
          >
            <ng-template let-item pTemplate="item">
              <div class="picklist-item">{{ item.label }}</div>
            </ng-template>
          </p-pickList>
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

    <p-dialog
      header="Gerenciar rotinas do perfil"
      [(visible)]="rotinasDialogVisible"
      [modal]="true"
      [style]="{ width: '42rem' }"
      [contentStyle]="{ overflow: 'visible' }"
      [draggable]="false"
      [resizable]="false"
      (onHide)="closeRotinasDialog()"
    >
      @if (rotinasDialogPerfil()) {
        <p class="rotinas-dialog-subtitle">{{ rotinasDialogPerfil()?.name }}</p>
        <p-pickList
          [source]="pickListSource"
          [target]="pickListTarget"
          sourceHeader="Disponíveis"
          targetHeader="Associadas ao perfil"
          [dragdrop]="true"
          [filterBy]="'label'"
          [showSourceFilter]="true"
          [showTargetFilter]="true"
          breakpoint="768px"
          styleClass="rotinas-picklist"
        >
          <ng-template let-item pTemplate="item">
            <div class="picklist-item">{{ item.label }}</div>
          </ng-template>
        </p-pickList>
      }
      <ng-template pTemplate="footer">
        <p-button label="Cancelar" [text]="true" (onClick)="closeRotinasDialog()" />
        <p-button
          label="Salvar"
          icon="pi pi-check"
          (onClick)="submitRotinas()"
          [loading]="savingRotinas()"
        />
      </ng-template>
    </p-dialog>
  `,
  styles: [
    `
      .toolbar-filters .status-dropdown {
        min-width: 8rem;
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
        background: var(--app-primary) !important;
        color: white !important;
        border-radius: 50%;
      }

      .perfil-icon {
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

      .form-row {
        display: grid;
        grid-template-columns: 1fr 1fr;
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

      .w-full {
        width: 100%;
      }

      .rotinas-dialog-subtitle {
        margin: 0 0 1rem;
        font-size: 0.875rem;
        color: #64748b;
      }

      .picklist-item {
        padding: 0.25rem 0;
        font-size: 0.875rem;
      }

      .form-picklist,
      .rotinas-picklist {
        min-height: 18rem;
      }

      ::ng-deep .form-picklist .p-picklist-list,
      ::ng-deep .rotinas-picklist .p-picklist-list {
        min-height: 16rem;
      }
    `,
  ],
})
export class PerfisListComponent {
  private fb = inject(FormBuilder);
  private perfisService = inject(PerfisService);
  private rotinasService = inject(RotinasService);
  private messageService = inject(MessageService);
  private confirmationService = inject(ConfirmationService);

  perfis = signal<Perfil[]>([]);
  totalRecords = signal(0);
  loading = signal(false);
  saving = signal(false);
  dialogVisible = false;
  rotinasDialogVisible = false;
  rotinasDialogPerfil = signal<Perfil | null>(null);
  pickListSource: RotinaOption[] = [];
  pickListTarget: RotinaOption[] = [];
  formPickListSource: RotinaOption[] = [];
  formPickListTarget: RotinaOption[] = [];
  savingRotinas = signal(false);
  editId = signal<string | null>(null);
  searchControl = new FormControl('', { nonNullable: true });
  statusControl = new FormControl<boolean | null>(null);
  statusOptions = STATUS_OPTIONS;

  form = this.fb.nonNullable.group({
    code: ['', [Validators.required, Validators.maxLength(50)]],
    name: ['', [Validators.required, Validators.maxLength(255)]],
    icon: [''],
    active: [true],
    rotinaIds: [[] as string[]],
  });

  private currentPage = 0;
  private currentSize = 10;

  applySearch(): void {
    this.loadPerfis(0, this.currentSize);
  }

  refresh(): void {
    this.loadPerfis(this.currentPage, this.currentSize);
  }

  onLazyLoad(event: TableLazyLoadEvent): void {
    const page = event.first ?? 0;
    const size = event.rows ?? 10;
    this.currentPage = Math.floor(page / size);
    this.currentSize = size;
    this.loadPerfis(this.currentPage, size);
  }

  private loadPerfis(page: number, size: number): void {
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
    this.perfisService.list(params).subscribe({
      next: (res) => {
        this.perfis.set(res.content);
        this.totalRecords.set(res.totalElements);
      },
      error: () => {
        this.messageService.add({
          severity: 'error',
          summary: 'Erro',
          detail: 'Não foi possível carregar os perfis.',
        });
      },
      complete: () => this.loading.set(false),
    });
  }

  private allRotinaOptions: RotinaOption[] = [];

  private loadRotinaOptionsForForm(selectedIds?: string[]): void {
    this.rotinasService.list({ size: 500, sort: 'name,asc' }).subscribe({
      next: (res) => {
        this.allRotinaOptions = res.content.map((r: Rotina) => ({
          label: r.name,
          value: r.id,
        }));
        const ids = selectedIds ?? this.form.get('rotinaIds')?.value ?? [];
        this.setFormPickListFromIds(ids);
      },
      error: () => {
        this.allRotinaOptions = [];
      },
    });
  }

  private setFormPickListFromIds(selectedIds: string[]): void {
    const selected = this.allRotinaOptions.filter((o) => selectedIds.includes(o.value));
    const available = this.allRotinaOptions.filter((o) => !selectedIds.includes(o.value));
    this.formPickListSource = [...available];
    this.formPickListTarget = [...selected];
  }

  private setPickListFromIds(selectedIds: string[]): void {
    this.rotinasService.list({ size: 500, sort: 'name,asc' }).subscribe({
      next: (res) => {
        const all = res.content.map((r: Rotina) => ({ label: r.name, value: r.id }));
        const selected = all.filter((o) => selectedIds.includes(o.value));
        const available = all.filter((o) => !selectedIds.includes(o.value));
        this.pickListSource = [...available];
        this.pickListTarget = [...selected];
      },
    });
  }

  openCreateDialog(): void {
    this.editId.set(null);
    this.form.reset({
      code: '',
      name: '',
      icon: '',
      active: true,
      rotinaIds: [],
    });
    this.formPickListSource = [];
    this.formPickListTarget = [];
    this.loadRotinaOptionsForForm();
    this.dialogVisible = true;
  }

  openEditDialog(row: Perfil): void {
    this.editId.set(row.id);
    this.dialogVisible = true;
    this.perfisService.getById(row.id).subscribe({
      next: (perfil) => {
        const rotinaIds = perfil.rotinas?.map((r) => r.id) ?? [];
        this.form.patchValue({
          code: perfil.code,
          name: perfil.name,
          icon: perfil.icon ?? '',
          active: perfil.active,
          rotinaIds,
        });
        this.loadRotinaOptionsForForm(rotinaIds);
      },
      error: () => {
        this.messageService.add({
          severity: 'error',
          summary: 'Erro',
          detail: 'Não foi possível carregar o perfil.',
        });
      },
    });
  }

  closeDialog(): void {
    this.dialogVisible = false;
    this.editId.set(null);
  }

  openRotinasDialog(row: Perfil): void {
    this.rotinasDialogPerfil.set(row);
    this.perfisService.getById(row.id).subscribe({
      next: (perfil) => {
        this.setPickListFromIds(perfil.rotinas?.map((r) => r.id) ?? []);
        this.rotinasDialogVisible = true;
      },
      error: () => {
        this.messageService.add({
          severity: 'error',
          summary: 'Erro',
          detail: 'Não foi possível carregar o perfil.',
        });
      },
    });
  }

  closeRotinasDialog(): void {
    this.rotinasDialogVisible = false;
    this.rotinasDialogPerfil.set(null);
    this.pickListSource = [];
    this.pickListTarget = [];
  }

  submitRotinas(): void {
    const perfil = this.rotinasDialogPerfil();
    if (!perfil) return;
    const rotinaIds = this.pickListTarget.map((t) => t.value);
    this.savingRotinas.set(true);
    this.perfisService.update(perfil.id, {
      code: perfil.code,
      name: perfil.name,
      icon: perfil.icon ?? undefined,
      active: perfil.active,
      rotinaIds,
    }).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Sucesso',
          detail: 'Rotinas atualizadas.',
        });
        this.closeRotinasDialog();
        this.loadPerfis(this.currentPage, this.currentSize);
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Erro',
          detail: err.error?.message ?? 'Não foi possível atualizar as rotinas.',
        });
      },
      complete: () => this.savingRotinas.set(false),
    });
  }

  submit(): void {
    if (this.form.invalid) return;
    const value = this.form.getRawValue();
    const id = this.editId();
    const rotinaIds = this.formPickListTarget.map((t) => t.value);
    const data: CreatePerfilRequest | UpdatePerfilRequest = {
      code: value.code,
      name: value.name,
      icon: value.icon?.trim() || undefined,
      active: value.active,
      rotinaIds,
    };
    this.saving.set(true);
    const req = id
      ? this.perfisService.update(id, data)
      : this.perfisService.create(data);
    req.subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Sucesso',
          detail: id ? 'Perfil atualizado.' : 'Perfil cadastrado.',
        });
        this.closeDialog();
        this.loadPerfis(this.currentPage, this.currentSize);
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Erro',
          detail: err.error?.message ?? 'Não foi possível salvar o perfil.',
        });
      },
      complete: () => this.saving.set(false),
    });
  }

  confirmDelete(row: Perfil): void {
    this.confirmationService.confirm({
      message: `Deseja excluir o perfil "${row.name}"?`,
      header: 'Confirmar exclusão',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.perfisService.delete(row.id).subscribe({
          next: () => {
            this.messageService.add({
              severity: 'success',
              summary: 'Sucesso',
              detail: 'Perfil excluído.',
            });
            this.loadPerfis(this.currentPage, this.currentSize);
          },
          error: (err) => {
            this.messageService.add({
              severity: 'error',
              summary: 'Erro',
              detail: err.error?.message ?? 'Não foi possível excluir o perfil.',
            });
          },
        });
      },
    });
  }
}
