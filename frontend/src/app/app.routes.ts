// ============================================
// FILE: src/app/app.routes.ts
// ============================================

import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { AddAccountComponent } from './components/add-account/add-account.component';
import { RegisterComponent } from './components/register/register.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { TransferComponent } from './components/transfer/transfer.component';
import { TransactionsComponent } from './components/transactions/transactions.component';
import { ExpenseTrackerComponent } from './components/expense-tracker/expense-tracker.component';
import { ForgotPasswordComponent } from './components/forgot-password/forgot-password.component';
import { AboutComponent } from './components/about/about.component';
import { authGuard } from './guards/auth-guard';
import { Layout } from './layout/layout';
import { PrivacyPolicyComponent } from './components/privacy-policy/privacy-policy.component';
import { TermsComponent } from './components/terms/terms.component';


export const routes: Routes = [
  // Public routes
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'about', component: AboutComponent },
  { path: 'privacy', component: PrivacyPolicyComponent },
  { path: 'terms', component: TermsComponent },


  // ALL authenticated pages go inside layout
  {
    path: '',
    component: Layout,                     // ⬅️ sidebar + layout is here
    canActivate: [authGuard],
    children: [
      { path: 'dashboard', component: DashboardComponent },
      { path: 'transfer', component: TransferComponent },
      { path: 'transactions', component: TransactionsComponent },
      { path: 'add-account', component: AddAccountComponent },
      { path: 'expenses', component: ExpenseTrackerComponent}
    ]
  },

  // Fallback
  { path: '**', redirectTo: '/login' }
];

