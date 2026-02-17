import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { PublicHeaderComponent } from '../public-header/public-header';

@Component({
  selector: 'app-plans',
  standalone: true,
  imports: [CommonModule, RouterModule, ButtonModule, PublicHeaderComponent],
  templateUrl: './plans.html',
  styleUrl: './plans.scss',
})
export class PlansComponent {}

