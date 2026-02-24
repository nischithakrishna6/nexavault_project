import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { AccountService } from '../../services/account.service';
import { TransactionService } from '../../services/transaction.service';
import { WebsocketService } from '../../services/websocket.service';
import { Account } from '../../models/account.model';
import { Transaction } from '../../models/transaction.model';
import { AuthResponse } from '../../models/user.model';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit, OnDestroy {
  currentUser: AuthResponse | null = null;
  accounts: Account[] = [];
  recentTransactions: Transaction[] = [];
  totalBalance: number = 0;
  isLoading: boolean = true;
  notification: string = '';
  showAllAccounts: boolean = false;

  constructor(
    private authService: AuthService,
    private accountService: AccountService,
    private transactionService: TransactionService,
    private websocketService: WebsocketService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();

    if (this.currentUser) {
      this.loadAccounts();
      this.loadTransactions();
      this.connectWebSocket();
    }
  }

  ngOnDestroy(): void {
    this.websocketService.disconnect();
  }

  toggleAccountsView(): void {
    this.showAllAccounts = !this.showAllAccounts;
  }

  loadAccounts(): void {
    this.accountService.getUserAccounts().subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.accounts = response.data;
          console.log('Accounts loaded:', this.accounts);
          this.calculateTotalBalance();
        }
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading accounts:', error);
        this.isLoading = false;
      }
    });
  }

  loadTransactions(): void {
    this.transactionService.getUserTransactions().subscribe({
      next: (response) => {
        if (Array.isArray(response)) {
          this.recentTransactions = response.slice(0, 5);
        }
      },
      error: (error) => {
        console.error('Error loading transactions:', error);
      }
    });
  }

  calculateTotalBalance(): void {
    this.totalBalance = this.accounts.reduce((sum, account) => sum + account.balance, 0);
  }

  connectWebSocket(): void {
    if (this.currentUser) {
      this.websocketService.connect(this.currentUser.userId);
      this.websocketService.notifications$.subscribe({
        next: (message) => {
          this.notification = message;
          setTimeout(() => (this.notification = ''), 5000);
          this.loadAccounts();
          this.loadTransactions();
        }
      });
    }
  }

  logout(): void {
    this.authService.logout();
    this.websocketService.disconnect();
    this.router.navigate(['/login']);
  }

  formatCurrency(amount: number): string {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR'
    }).format(amount);
  }

  getAccountHolderName(account: Account): string {
    return account.accountHolderName ||
           `${this.currentUser?.firstName || ''} ${this.currentUser?.lastName || ''}`.trim() ||
           'Account Holder';
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }
}
