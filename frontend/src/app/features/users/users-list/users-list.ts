import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { TableModule, TableLazyLoadEvent } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { MessageService, ConfirmationService } from 'primeng/api';
import { ConfirmDialog } from 'primeng/confirmdialog';
import { ConfirmPopupModule } from 'primeng/confirmpopup';
import { TooltipModule } from 'primeng/tooltip';
import { AvatarModule } from 'primeng/avatar';
import { DialogModule } from 'primeng/dialog';
import { DropdownModule } from 'primeng/dropdown';
import { MultiSelectModule } from 'primeng/multiselect';
import { PasswordModule } from 'primeng/password';
import { PickListModule } from 'primeng/picklist';
import { UsersService, CreateUserRequest } from '../../../core/services/users.service';
import { PerfisService } from '../../../core/services/perfis.service';
import type { User } from '../../../core/services/auth.service';
import type { Perfil } from '../../../core/services/perfis.service';

interface PerfilOption {
  label: string;
  value: string;
}

const PASSWORD_PATTERN = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]+$/;

@Component({
  selector: 'app-users-list',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    TableModule,
    ButtonModule,
    InputTextModule,
    ConfirmDialog,
    ConfirmPopupModule,
    TooltipModule,
    AvatarModule,
    DialogModule,
    DropdownModule,
    MultiSelectModule,
    PasswordModule,
    PickListModule,
  ],
  providers: [ConfirmationService],
  template: `
    <p-confirmDialog/>

    <div class="page-header">
      <div class="page-title-block">
        <h1 class="page-title">Lista   de usuários</h1>
        
      </div>
    </div>

    <div class="page-toolbar">
      <div class="search-wrapper">
        <i class="pi pi-search search-icon"></i>
        <input
          type="text"
          pInputText
          placeholder="Buscar por nome..."
          class="search-input"
          [formControl]="searchControl"
          (keyup.enter)="applySearch()"
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
        [value]="users()"
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
            <th>Nome</th>
            <th>E-mail</th>
            <th>Status</th>
            <th style="width: 170px">Ações</th>
          </tr>
        </ng-template>
        <ng-template pTemplate="body" let-user>
          <tr>
            <td>
              <div class="user-cell">
                <p-avatar
                  [label]="user.name?.charAt(0) ?? '?'"
                  shape="circle"
                  styleClass="user-avatar"
                />
                <span class="user-name">{{ user.name }}</span>
              </div>
            </td>
            <td>{{ user.email }}</td>
            <td>
              <span class="status-text">{{ user.active ? 'Ativo' : 'Inativo' }}</span>
            </td>
            <td class="actions-cell">
              <div class="actions-buttons">
                <p-button
                  icon="pi pi-id-card"
                  [rounded]="true"
                  [text]="true"
                  severity="secondary"
                  size="small"
                  (onClick)="openPerfisDialog(user)"
                  pTooltip="Perfis"
                />
                @if (user.active) {
                  <p-button
                    icon="pi pi-ban"
                    [rounded]="true"
                    [text]="true"
                    severity="secondary"
                    size="small"
                    (onClick)="deactivate(user)"
                    pTooltip="Desativar"
                  />
                } @else {
                  <p-button
                    icon="pi pi-check"
                    [rounded]="true"
                    [text]="true"
                    severity="secondary"
                    size="small"
                    (onClick)="activate(user)"
                    pTooltip="Ativar"
                  />
                }
              </div>
            </td>
          </tr>
        </ng-template>
        <ng-template pTemplate="emptymessage">
          <tr>
            <td colspan="5" class="empty-message">Nenhum usuário encontrado.</td>
          </tr>
        </ng-template>
        <ng-template pTemplate="loadingbody">
          <tr>
            <td colspan="5" class="loading-message">
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
      header="Cadastrar novo usuário"
      [(visible)]="createDialogVisible"
      [modal]="true"
      [style]="{ width: '28rem' }"
      [draggable]="false"
      [resizable]="false"
      (onHide)="resetCreateForm()"
    >
      <form [formGroup]="createForm" class="create-form">
        <div class="form-field">
          <label for="create-name">Nome</label>
          <input id="create-name" pInputText formControlName="name" placeholder="Nome completo" />
          @if (createForm.get('name')?.invalid && createForm.get('name')?.touched) {
            <small class="field-error">Nome é obrigatório</small>
          }
        </div>
        <div class="form-field">
          <label for="create-email">E-mail</label>
          <input id="create-email" pInputText type="email" formControlName="email" placeholder="email@exemplo.com" />
          @if (createForm.get('email')?.invalid && createForm.get('email')?.touched) {
            <small class="field-error">E-mail válido é obrigatório</small>
          }
        </div>
        <div class="form-field">
          <label for="create-password">Senha</label>
          <p-password
            id="create-password"
            formControlName="password"
            placeholder="Mín. 8 caracteres, 1 maiúscula, 1 minúscula, 1 número e 1 especial"
            [feedback]="true"
            [toggleMask]="true"
            inputStyleClass="w-full"
          />
          @if (createForm.get('password')?.invalid && createForm.get('password')?.touched) {
            <small class="field-error">Senha deve ter 8+ caracteres com maiúscula, minúscula, número e especial</small>
          }
        </div>
        <div class="form-field">
          <label for="create-phone">Telefone (opcional)</label>
          <input id="create-phone" pInputText formControlName="phone" placeholder="(00) 00000-0000" />
        </div>
        <div class="form-field">
          <label for="create-perfis">Perfis (opcional)</label>
          <p-multiSelect
            id="create-perfis"
            formControlName="perfilIds"
            [options]="perfisOptions()"
            placeholder="Selecione os perfis"
            optionLabel="label"
            optionValue="value"
            styleClass="w-full"
          />
        </div>
      </form>
      <ng-template pTemplate="footer">
        <p-button label="Cancelar" [text]="true" (onClick)="createDialogVisible = false" />
        <p-button label="Cadastrar" icon="pi pi-check" (onClick)="submitCreate()" [loading]="creating()" [disabled]="createForm.invalid" />
      </ng-template>
    </p-dialog>

    <p-dialog
      header="Gerenciar perfis do usuário"
      [(visible)]="perfisDialogVisible"
      [modal]="true"
      [style]="{ width: '42rem' }"
      [contentStyle]="{ overflow: 'visible' }"
      [draggable]="false"
      [resizable]="false"
      (onHide)="closePerfisDialog()"
    >
      @if (perfisDialogUser()) {
        <p class="perfis-dialog-subtitle">{{ perfisDialogUser()?.name }}</p>
        <p-pickList
          [source]="pickListSource"
          [target]="pickListTarget"
          sourceHeader="Disponíveis"
          targetHeader="Perfis atribuídos"
          [dragdrop]="true"
          [filterBy]="'label'"
          [showSourceFilter]="true"
          [showTargetFilter]="true"
          breakpoint="768px"
          styleClass="perfis-picklist"
        >
          <ng-template let-item pTemplate="item">
            <div class="picklist-item">{{ item.label }}</div>
          </ng-template>
        </p-pickList>
      }
      <ng-template pTemplate="footer">
        <p-button label="Cancelar" [text]="true" (onClick)="closePerfisDialog()" />
        <p-button
          label="Salvar"
          icon="pi pi-check"
          (onClick)="submitPerfis()"
          [loading]="savingPerfis()"
        />
      </ng-template>
    </p-dialog>
  `,
  styles: [
    `
      .table-card {
        background: #ffffff;
        border-radius: var(--p-border-radius);
        border: 1px solid #e2e8f0;
        overflow: hidden;
        box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
      }

      .table-card ::ng-deep .p-datatable {
        background: #ffffff !important;
      }

      .table-card ::ng-deep .p-datatable .p-datatable-tbody {
        background: #ffffff !important;
      }

      .table-card ::ng-deep .p-datatable .p-datatable-thead > tr > th {
        background: #f8fafc !important;
        color: #475569 !important;
        font-weight: 600;
        border-color: #e2e8f0;
        font-size: 0.8125rem;
        padding: 0.5rem 1rem;
      }

      .table-card ::ng-deep .p-datatable .p-datatable-tbody > tr {
        background: #ffffff !important;
      }

      .table-card ::ng-deep .p-datatable .p-datatable-tbody > tr > td {
        border-color: #e2e8f0;
        color: #334155;
        padding: 0.5rem 1rem;
        background: inherit !important;
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
      .table-card ::ng-deep .p-paginator .p-paginator-pages .p-link,
      .table-card ::ng-deep .p-paginator .p-paginator-first,
      .table-card ::ng-deep .p-paginator .p-paginator-prev,
      .table-card ::ng-deep .p-paginator .p-paginator-next,
      .table-card ::ng-deep .p-paginator .p-paginator-last {
        color: #5d6c7f !important;
      }

      .table-card ::ng-deep .p-paginator .p-paginator-element {
        border-color: #dde2e7;
      }

      .table-card ::ng-deep .p-paginator .p-paginator-rpp-dropdown,
      .table-card ::ng-deep .p-paginator .p-paginator-rpp-dropdown.p-select,
      .table-card ::ng-deep .p-paginator .p-paginator-rpp-dropdown .p-inputwrapper,
      .table-card ::ng-deep .p-paginator .p-paginator-rpp-dropdown.p-inputwrapper-filled,
      .table-card ::ng-deep .p-paginator p-select.p-paginator-rpp-dropdown,
      .table-card ::ng-deep .p-paginator p-select.p-paginator-rpp-dropdown .p-inputwrapper {
        background: #ffffff !important;
        border-color: #dde2e7 !important;
      }

      .table-card ::ng-deep .p-paginator .p-paginator-rpp-dropdown .p-select-label,
      .table-card ::ng-deep .p-paginator .p-paginator-rpp-dropdown .p-select-dropdown-icon,
      .table-card ::ng-deep .p-paginator .p-paginator-rpp-dropdown .p-icon,
      .table-card ::ng-deep .p-paginator .p-paginator-rpp-dropdown .p-select-dropdown svg {
        color: #5d6c7f !important;
        fill: #5d6c7f !important;
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

      .user-cell {
        display: flex;
        align-items: center;
        gap: 0.75rem;
      }

      .user-avatar {
        width: 1.75rem !important;
        height: 1.75rem !important;
        font-size: 0.6875rem !important;
      }

      .user-name {
        font-weight: 500;
        color: #334155;
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

      .field-error {
        color: var(--p-red-500);
        font-size: 0.75rem;
      }

      .toolbar-actions {
        display: flex;
        gap: 0.5rem;
        align-items: center;
      }

      .toolbar-actions .p-button {
        margin: 0;
      }

      .perfis-dialog-subtitle {
        margin: 0 0 1rem;
        font-size: 0.875rem;
        color: #64748b;
      }

      .picklist-item {
        padding: 0.25rem 0;
        font-size: 0.875rem;
      }

      .perfis-picklist {
        min-height: 18rem;
      }

      ::ng-deep .perfis-picklist .p-picklist-list {
        min-height: 16rem;
      }
    `,
  ],
})
export class UsersListComponent {
  private fb = inject(FormBuilder);
  users = signal<User[]>([]);
  totalRecords = signal(0);
  loading = signal(false);
  creating = signal(false);
  createDialogVisible = false;
  perfisDialogVisible = false;
  perfisDialogUser = signal<User | null>(null);
  pickListSource: PerfilOption[] = [];
  pickListTarget: PerfilOption[] = [];
  savingPerfis = signal(false);
  searchControl = new FormControl('', { nonNullable: true });
  perfisOptions = signal<PerfilOption[]>([]);
  createForm = this.fb.nonNullable.group({
    name: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8), Validators.pattern(PASSWORD_PATTERN)]],
    phone: [''],
    perfilIds: [[] as string[]],
  });
  private currentPage = 0;
  private currentSize = 10;

  constructor(
    private usersService: UsersService,
    private perfisService: PerfisService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService
  ) {}

  applySearch(): void {
    this.loadUsers(0, this.currentSize);
  }

  refresh(): void {
    this.loadUsers(this.currentPage, this.currentSize);
  }

  onLazyLoad(event: TableLazyLoadEvent): void {
    const page = event.first ?? 0;
    const size = event.rows ?? 10;
    this.currentPage = Math.floor(page / size);
    this.currentSize = size;
    this.loadUsers(this.currentPage, size);
  }

  private loadUsers(page: number, size: number): void {
    this.loading.set(true);
    const search = this.searchControl.value.trim() || undefined;
    const params = { page, size, sort: 'name,asc' as const, name: search };
    this.usersService.list(params).subscribe({
      next: (res) => {
        this.users.set(res.content);
        this.totalRecords.set(res.totalElements);
      },
      error: () => {
        this.messageService.add({
          severity: 'error',
          summary: 'Erro',
          detail: 'Não foi possível carregar os usuários.',
        });
      },
      complete: () => this.loading.set(false),
    });
  }

  activate(user: User): void {
    this.usersService.activate(user.id).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Sucesso',
          detail: `Usuário ${user.name} ativado.`,
        });
        this.loadUsers(this.currentPage, this.currentSize);
      },
      error: () => {
        this.messageService.add({
          severity: 'error',
          summary: 'Erro',
          detail: 'Não foi possível ativar o usuário.',
        });
      },
    });
  }

  deactivate(user: User): void {
    this.confirmationService.confirm({
      message: `Deseja desativar o usuário ${user.name}?`,
      header: 'Confirmar',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.usersService.deactivate(user.id).subscribe({
          next: () => {
            this.messageService.add({
              severity: 'success',
              summary: 'Sucesso',
              detail: `Usuário ${user.name} desativado.`,
            });
            this.loadUsers(this.currentPage, this.currentSize);
          },
          error: () => {
            this.messageService.add({
              severity: 'error',
              summary: 'Erro',
              detail: 'Não foi possível desativar o usuário.',
            });
          },
        });
      },
    });
  }

  openCreateDialog(): void {
    this.perfisService.list({ active: true }).subscribe({
      next: (res) => {
        this.perfisOptions.set(
          res.content.map((p) => ({ label: `${p.code} - ${p.name}`, value: p.id }))
        );
      },
    });
    this.createDialogVisible = true;
  }

  resetCreateForm(): void {
    this.createForm.reset({ name: '', email: '', password: '', phone: '', perfilIds: [] });
  }

  submitCreate(): void {
    if (this.createForm.invalid) return;
    const value = this.createForm.getRawValue();
    const data: CreateUserRequest = {
      name: value.name,
      email: value.email,
      password: value.password,
    };
    if (value.phone?.trim()) data.phone = value.phone.trim();
    if (value.perfilIds?.length) data.perfilIds = value.perfilIds;
    this.creating.set(true);
    this.usersService.create(data).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Sucesso',
          detail: 'Usuário cadastrado com sucesso.',
        });
        this.createDialogVisible = false;
        this.resetCreateForm();
        this.loadUsers(this.currentPage, this.currentSize);
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Erro',
          detail: err.error?.message ?? 'Não foi possível cadastrar o usuário.',
        });
      },
      complete: () => this.creating.set(false),
    });
  }

  openPerfisDialog(user: User): void {
    this.perfisDialogUser.set(user);
    this.usersService.getPerfis(user.id).subscribe({
      next: (perfis) => {
        this.setPickListFromPerfis(perfis);
        this.perfisDialogVisible = true;
      },
      error: () => {
        this.messageService.add({
          severity: 'error',
          summary: 'Erro',
          detail: 'Não foi possível carregar os perfis do usuário.',
        });
      },
    });
  }

  closePerfisDialog(): void {
    this.perfisDialogVisible = false;
    this.perfisDialogUser.set(null);
    this.pickListSource = [];
    this.pickListTarget = [];
  }

  submitPerfis(): void {
    const user = this.perfisDialogUser();
    if (!user) return;
    const perfilIds = this.pickListTarget.map((t) => t.value);
    this.savingPerfis.set(true);
    this.usersService.assignPerfis(user.id, perfilIds).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Sucesso',
          detail: 'Perfis atualizados.',
        });
        this.closePerfisDialog();
        this.loadUsers(this.currentPage, this.currentSize);
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Erro',
          detail: err.error?.message ?? 'Não foi possível atualizar os perfis.',
        });
      },
      complete: () => this.savingPerfis.set(false),
    });
  }

  private setPickListFromPerfis(userPerfis: Perfil[]): void {
    this.perfisService.list({ size: 500, sort: 'name,asc' }).subscribe({
      next: (res) => {
        const all: PerfilOption[] = res.content.map((p) => ({ label: p.name, value: p.id }));
        const selectedIds = userPerfis.map((p) => p.id);
        const selected = all.filter((o) => selectedIds.includes(o.value));
        const available = all.filter((o) => !selectedIds.includes(o.value));
        this.pickListSource = [...available];
        this.pickListTarget = [...selected];
      },
    });
  }
}
