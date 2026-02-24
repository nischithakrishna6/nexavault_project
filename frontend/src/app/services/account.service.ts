

// ============================================
// FILE: src/app/services/account.service.ts
// ============================================

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../models/api-response.model';
import { Account } from '../models/account.model';
import { LinkAccountRequest } from '../models/account.model';

@Injectable({
  providedIn: 'root'
})
export class AccountService {
  private apiUrl = 'http://localhost:8081/api/accounts';

  constructor(private http: HttpClient) {}

  getUserAccounts(): Observable<ApiResponse<Account[]>> {
    return this.http.get<ApiResponse<Account[]>>(this.apiUrl);
  }

  getAccountById(id: number): Observable<ApiResponse<Account>> {
    return this.http.get<ApiResponse<Account>>(`${this.apiUrl}/${id}`);
  }

  getAccountBalance(id: number): Observable<ApiResponse<number>> {
    return this.http.get<ApiResponse<number>>(`${this.apiUrl}/${id}/balance`);
  }


  linkAccount(request: LinkAccountRequest): Observable<ApiResponse<Account>> {
    return this.http.post<ApiResponse<Account>>(`${this.apiUrl}/link`, request);
  }
}

