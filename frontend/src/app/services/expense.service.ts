import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Expense, ExpenseStats } from '../models/expense.model';
import { ApiResponse } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class ExpenseService {
  private apiUrl = `${environment.apiUrl}/expenses`;

  constructor(private http: HttpClient) {}

  // Create expense
  createExpense(expense: Expense): Observable<ApiResponse<Expense>> {
    return this.http.post<ApiResponse<Expense>>(this.apiUrl, expense);
  }

  // Get all user expenses
  getUserExpenses(): Observable<ApiResponse<Expense[]>> {
    return this.http.get<ApiResponse<Expense[]>>(this.apiUrl);
  }

  // Get expense stats
  getExpenseStats(period: 'daily' | 'weekly' | 'monthly'): Observable<ApiResponse<ExpenseStats>> {
    return this.http.get<ApiResponse<ExpenseStats>>(`${this.apiUrl}/stats?period=${period}`);
  }

  // Delete expense
  deleteExpense(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${id}`);
  }

  // Get expenses by date range
  getExpensesByDateRange(startDate: string, endDate: string): Observable<ApiResponse<Expense[]>> {
    return this.http.get<ApiResponse<Expense[]>>(
      `${this.apiUrl}/range?start=${startDate}&end=${endDate}`
    );
  }
}
