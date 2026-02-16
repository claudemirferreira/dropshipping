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
    ConfirmDialog,
    TooltipModule,
    DialogModule,
    DropdownModule,
    CheckboxModule,
    TextareaModule,
  ],
  providers: [ConfirmationService],
  templateUrl: './rotinas-list.html',
  styleUrl: './rotinas-list.scss',
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
