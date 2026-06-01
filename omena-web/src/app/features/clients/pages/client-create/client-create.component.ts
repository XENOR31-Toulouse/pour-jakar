import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ClientService } from '../../services/client.service';
import { Client } from '../../models/client.model';

@Component({
  selector: 'app-client-create',
  templateUrl: './client-create.component.html',
  styleUrls: ['./client-create.component.css']
})
export class ClientCreateComponent implements OnInit {
  clientForm: FormGroup;
  isLoading = false;
  error: string | null = null;

  constructor(
    private fb: FormBuilder,
    private clientService: ClientService,
    private router: Router
  ) {
    this.clientForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      phoneNumber: ['', [Validators.required, Validators.minLength(5)]],
      address: ['']
    });
  }

  ngOnInit(): void {
  }

  onSubmit(): void {
    if (this.clientForm.valid) {
      this.isLoading = true;
      this.error = null;

      const clientData = this.clientForm.value;

      this.clientService.createClient(clientData).subscribe({
        next: (client: Client) => {
          this.isLoading = false;
          this.router.navigate(['/clients']);
        },
        error: (error) => {
          this.isLoading = false;
          this.error = 'Erreur lors de la création du client. Veuillez réessayer.';
          console.error('Erreur lors de la création du client:', error);
        }
      });
    } else {
      this.markFormGroupTouched();
    }
  }

  private markFormGroupTouched(): void {
    Object.keys(this.clientForm.controls).forEach(key => {
      this.clientForm.get(key)?.markAsTouched();
    });
  }
}
