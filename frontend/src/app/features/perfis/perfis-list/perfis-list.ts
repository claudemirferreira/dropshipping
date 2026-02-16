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
  templateUrl: './perfis-list.html',
  styleUrl: './perfis-list.scss',
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
