// ============================================
// FILE: src/app/components/transfer/transfer.component.ts
// ============================================

import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AccountService } from '../../services/account.service';
import { TransactionService } from '../../services/transaction.service';
import { Account } from '../../models/account.model';
import { TransferRequest } from '../../models/transaction.model';

@Component({
  selector: 'app-transfer',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './transfer.component.html',
  styleUrls: ['./transfer.component.css']
})
export class TransferComponent implements OnInit {
  accounts: Account[] = [];
  transferData: TransferRequest = {
    fromAccountId: 0,
    toAccountNumber: '',
    amount: 0,
    description: ''
  };

  errorMessage: string = '';
  successMessage: string = '';
  isLoading: boolean = false;

  constructor(
    private accountService: AccountService,
    private transactionService: TransactionService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadAccounts();
  }

  loadAccounts(): void {
    this.accountService.getUserAccounts().subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.accounts = response.data;
        }
      },
      error: (error) => {
        this.errorMessage = 'Error loading accounts';
        console.error(error);
      }
    });
  }

  onSubmit(): void {
    this.errorMessage = '';
    this.successMessage = '';

    // Validation
    if (!this.transferData.fromAccountId) {
      this.errorMessage = 'Please select a source account';
      return;
    }

    if (!this.transferData.toAccountNumber) {
      this.errorMessage = 'Please enter destination account number';
      return;
    }

    if (this.transferData.amount <= 0) {
      this.errorMessage = 'Amount must be greater than zero';
      return;
    }

    const sourceAccount = this.accounts.find(acc => acc.id === this.transferData.fromAccountId);
    if (sourceAccount && this.transferData.amount > sourceAccount.balance) {
      this.errorMessage = 'Insufficient balance';
      return;
    }

    this.isLoading = true;

    this.transactionService.transferMoney(this.transferData).subscribe({
      next: (response) => {
        if (response.success) {
          this.successMessage = 'Transfer completed successfully!';
          setTimeout(() => {
            this.router.navigate(['/dashboard']);
          }, 2000);
        } else {
          this.errorMessage = response.message;
        }
        this.isLoading = false;
      },
      error: (error) => {
        this.errorMessage = error.error?.message || 'Transfer failed. Please try again.';
        this.isLoading = false;
      }
    });
  }

  formatCurrency(amount: number): string {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR'
    }).format(amount);
  }
}
