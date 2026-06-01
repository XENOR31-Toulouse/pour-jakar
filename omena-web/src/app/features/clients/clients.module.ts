import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { ClientListComponent } from './pages/client-list/client-list.component';
import { ClientCreateComponent } from './pages/client-create/client-create.component';

@NgModule({
  declarations: [
    ClientListComponent,
    ClientCreateComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule
  ]
})
export class ClientsModule { }