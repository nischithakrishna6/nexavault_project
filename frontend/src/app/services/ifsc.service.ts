import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BankDetails } from '../models/account.model';

@Injectable({
  providedIn: 'root'
})
export class IfscService {
  private ifscApi = 'http://localhost:8081/api/ifsc';

  constructor(private http: HttpClient) {}

  verifyIFSC(ifscCode: string): Observable<BankDetails> {
    return this.http.get<BankDetails>(`${this.ifscApi}/${ifscCode}`);
  }
}
