import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration, ChartData, ChartType } from 'chart.js';
import { ExpenseService } from '../../services/expense.service';
import { Expense, EXPENSE_CATEGORIES, ExpenseStats, ExpenseCategory } from '../../models/expense.model';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-expense-tracker',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, BaseChartDirective],
  templateUrl: './expense-tracker.component.html',
  styleUrls: ['./expense-tracker.component.css']
})
export class ExpenseTrackerComponent implements OnInit {
  expenses: Expense[] = [];
  expenseStats: ExpenseStats | null = null;
  categories = EXPENSE_CATEGORIES;

  selectedPeriod: 'daily' | 'weekly' | 'monthly' = 'daily';
  showAddExpenseModal = false;
  isLoading = false;
  selectedExpenseId: number | null = null;
  showDeleteModal = false;

  newExpense: Expense = {
    userId: 0,
    category: '',
    amount: 0,
    description: '',
    date: new Date().toISOString().split('T')[0]
  };

  // Chart configurations
  public pieChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'bottom',
        labels: {
          padding: 15,
          font: { size: 12 }
        }
      },
      tooltip: {
        callbacks: {
          label: (context) => {
            const label = context.label || '';
            const value = context.parsed || 0;
            return `${label}: ₹${value.toFixed(2)}`;
          }
        }
      }
    }
  };

  public pieChartData: ChartData<'pie'> = {
    labels: [],
    datasets: [{
      data: [],
      backgroundColor: []
    }]
  };

  public pieChartType: ChartType = 'pie';

  public lineChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { display: false },
      tooltip: {
        callbacks: {
          label: (context) => `₹${context.parsed.y.toFixed(2)}`
        }
      }
    },
    scales: {
      y: {
        beginAtZero: true,
        ticks: {
          callback: (value) => `₹${value}`
        }
      }
    }
  };

  public lineChartData: ChartData<'line'> = {
    labels: [],
    datasets: [{
      data: [],
      label: 'Expenses',
      borderColor: '#667eea',
      backgroundColor: 'rgba(102, 126, 234, 0.1)',
      fill: true,
      tension: 0.4
    }]
  };

  public lineChartType: ChartType = 'line';

  constructor(
    private expenseService: ExpenseService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const user = this.authService.getCurrentUser();
    if (user) {
      this.newExpense.userId = user.userId;
      this.loadExpenses();
      this.loadStats();
    }
  }

  loadExpenses(): void {
    this.isLoading = true;
    this.expenseService.getUserExpenses().subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.expenses = response.data;
        }
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading expenses:', error);
        this.isLoading = false;
      }
    });
  }

  loadStats(): void {
    this.expenseService.getExpenseStats(this.selectedPeriod).subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.expenseStats = response.data;
          this.updateCharts();
        }
      },
      error: (error) => console.error('Error loading stats:', error)
    });
  }

  updateCharts(): void {
    if (!this.expenseStats) return;

    // Pie chart
    const categoryData = this.expenseStats.categoryBreakdown;
    this.pieChartData = {
      labels: categoryData.map(c => c.category),
      datasets: [{
        data: categoryData.map(c => c.amount),
        backgroundColor: categoryData.map(c => {
          const cat = this.categories.find(cat => cat.name === c.category);
          return cat?.color || '#999';
        })
      }]
    };

    // Line chart
    let timeData: any[] = [];
    let labels: string[] = [];

    if (this.selectedPeriod === 'daily') {
      timeData = this.expenseStats.dailyExpenses;
      labels = timeData.map(d =>
        new Date(d.date).toLocaleDateString('en-US', { month: 'short', day: 'numeric' })
      );
    } else if (this.selectedPeriod === 'weekly') {
      timeData = this.expenseStats.weeklyExpenses;
      labels = timeData.map(d => d.week);
    } else {
      timeData = this.expenseStats.monthlyExpenses;
      labels = timeData.map(d => d.month);
    }

    this.lineChartData = {
      labels,
      datasets: [{
        data: timeData.map(d => d.amount),
        label: 'Expenses',
        borderColor: '#667eea',
        backgroundColor: 'rgba(102, 126, 234, 0.1)',
        fill: true,
        tension: 0.4
      }]
    };
  }


  onPeriodChange(period: 'daily' | 'weekly' | 'monthly'): void {
    this.selectedPeriod = period;
    this.loadStats();
  }

  openAddExpenseModal(): void {
    this.showAddExpenseModal = true;
  }

  closeAddExpenseModal(): void {
    this.showAddExpenseModal = false;
    this.resetForm();
  }

deleteExpense(id: number): void {
    this.selectedExpenseId = id;
    this.showDeleteModal = true;
  }

confirmDelete(): void {
  if (!this.selectedExpenseId) return;

  this.expenseService.deleteExpense(this.selectedExpenseId).subscribe({
    next: (response) => {
      if (response.success) {
        this.loadExpenses();
        this.loadStats();
      }
      this.closeDeleteModal();
    },
    error: (error) => console.error('Error deleting expense:', error)
  });
}

closeDeleteModal(): void {
  this.showDeleteModal = false;
  this.selectedExpenseId = null;
}

  addExpense(): void {
    if (!this.newExpense.category || !this.newExpense.amount || !this.newExpense.date) {
      return;
    }

    this.expenseService.createExpense(this.newExpense).subscribe({
      next: (response) => {
        if (response.success) {
          this.loadExpenses();
          this.loadStats();
          this.closeAddExpenseModal();
        }
      },
      error: (error) => console.error('Error adding expense:', error)
    });
  }


  resetForm(): void {
    const user = this.authService.getCurrentUser();
    this.newExpense = {
      userId: user?.userId || 0,
      category: '',
      amount: 0,
      description: '',
      date: new Date().toISOString().split('T')[0]
    };
  }

  formatCurrency(amount: number): string {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR'
    }).format(amount);
  }

  getCategoryIcon(categoryName: string): string {
    const category = this.categories.find(c => c.name === categoryName);
    return category?.icon || '📦';
  }

  getCategoryColor(categoryName: string): string {
    const category = this.categories.find(c => c.name === categoryName);
    return category?.color || '#999';
  }
}
