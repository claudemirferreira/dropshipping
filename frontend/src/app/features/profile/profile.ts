import { Component, inject, signal, computed, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { InputTextModule } from 'primeng/inputtext';
import { DropdownModule } from 'primeng/dropdown';
import { RadioButtonModule } from 'primeng/radiobutton';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { AuthService } from '../../core/services/auth.service';

interface ViaCepResponse {
  cep: string;
  logradouro: string;
  bairro: string;
  localidade: string;
  uf: string;
  erro?: boolean;
}

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    InputTextModule,
    DropdownModule,
    RadioButtonModule,
    ToastModule,
  ],
  providers: [MessageService],
  templateUrl: './profile.html',
  styleUrl: './profile.scss',
})
export class ProfileComponent implements OnInit {
  private fb   = inject(FormBuilder);
  private auth = inject(AuthService);
  private http = inject(HttpClient);
  private messageService = inject(MessageService);

  saving      = signal(false);
  loadingCep  = signal(false);

  currentUser  = this.auth.currentUser;
  userPerfis   = computed(() =>
    this.auth.currentUserPerfis().map((p, i) => ({ ...p, active: i === 0 }))
  );

  tipoPessoa = computed(() => this.form.get('tipoPessoa')?.value ?? 'fisica');

  regimeTributarioOptions = [
    { label: 'MEI',                value: 'MEI'       },
    { label: 'Simples Nacional',   value: 'SIMPLES'   },
    { label: 'Lucro Presumido',    value: 'PRESUMIDO' },
    { label: 'Lucro Real',         value: 'REAL'      },
  ];

  form = this.fb.nonNullable.group({
    // Dados Básicos
    nomeCompleto:      ['', Validators.required],
    whatsapp:          [''],
    tipoPessoa:        ['fisica'],
    // Dados Fiscais
    cpfCnpj:           [''],
    inscricaoEstadual: [''],
    regimeTributario:  ['SIMPLES'],
    // Endereço
    cep:               [''],
    logradouro:        [''],
    bairro:            [''],
    cidade:            [''],
    estado:            [''],
    numero:            [''],
    complemento:       [''],
  });

  ngOnInit(): void {
    const user = this.currentUser();
    if (user) {
      this.form.patchValue({
        nomeCompleto: user.name,
        whatsapp: (user as any).phone ?? '',
      });
    }
  }

  /** Consulta o endereço pelo CEP na API ViaCEP. */
  lookupCep(): void {
    const rawCep = this.form.get('cep')?.value.replace(/\D/g, '');
    if (!rawCep || rawCep.length !== 8) return;

    this.loadingCep.set(true);
    this.http
      .get<ViaCepResponse>(`https://viacep.com.br/ws/${rawCep}/json/`)
      .subscribe({
        next: (data) => {
          if (data.erro) {
            this.messageService.add({ severity: 'warn', summary: 'CEP não encontrado', detail: 'Verifique o CEP informado.' });
            return;
          }
          this.form.patchValue({
            logradouro: data.logradouro,
            bairro:     data.bairro,
            cidade:     data.localidade,
            estado:     data.uf,
          });
        },
        error: () =>
          this.messageService.add({ severity: 'error', summary: 'Erro', detail: 'Não foi possível consultar o CEP.' }),
        complete: () => this.loadingCep.set(false),
      });
  }

  onCepBlur(): void {
    this.lookupCep();
  }

  onSave(): void {
    if (this.form.invalid) return;
    this.saving.set(true);

    // TODO: conectar ao backend quando disponível
    setTimeout(() => {
      this.saving.set(false);
      this.messageService.add({
        severity: 'success',
        summary: 'Perfil atualizado',
        detail: 'Suas informações foram salvas com sucesso.',
      });
    }, 800);
  }

  onCancel(): void {
    const user = this.currentUser();
    this.form.reset({
      nomeCompleto:    user?.name ?? '',
      whatsapp:        (user as any)?.phone ?? '',
      tipoPessoa:      'fisica',
      regimeTributario: 'SIMPLES',
    });
  }
}
