import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { TextareaModule } from 'primeng/textarea';
import { PublicHeaderComponent } from '../public-header/public-header';

@Component({
  selector: 'app-contact',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ButtonModule, InputTextModule, TextareaModule, PublicHeaderComponent],
  templateUrl: './contact.html',
  styleUrl: './contact.scss',
})
export class ContactComponent {
  form: FormGroup;

  constructor(private fb: FormBuilder) {
    this.form = this.fb.nonNullable.group({
      name: ['', [Validators.required, Validators.maxLength(255)]],
      email: ['', [Validators.required, Validators.email, Validators.maxLength(255)]],
      message: ['', [Validators.required, Validators.maxLength(2000)]],
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    // Por enquanto apenas exibe no console; integração futura pode enviar para backend ou ferramenta de suporte.
    // eslint-disable-next-line no-console
    console.log('Contato enviado:', this.form.getRawValue());
    this.form.reset();
  }
}

