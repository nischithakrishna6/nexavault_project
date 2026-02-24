import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TransactionService {
  private apiUrl = 'http://localhost:8081/api/transactions';

  constructor(private http: HttpClient) {}

  getUserTransactions(): Observable<any> {
    return this.http.get(`${this.apiUrl}/user`);
  }

  getAccountTransactions(accountId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/account/${accountId}`);
  }

  transferMoney(request: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/transfer`, request);
  }

  // ✅ ADD THIS - Download Invoice
  downloadInvoice(transactionId: number): void {
    const token = localStorage.getItem('token');
    const url = `${this.apiUrl}/${transactionId}/invoice/pdf`;

    fetch(url, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    .then(response => {
      if (!response.ok) throw new Error('Failed to download invoice');
      return response.blob();
    })
    .then(blob => {
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `Invoice-${transactionId}.pdf`;
      link.click();
      window.URL.revokeObjectURL(url);
    })
    .catch(error => {
      console.error('Error downloading invoice:', error);
      alert('Failed to download invoice. Please try again.');
    });
  }
}
