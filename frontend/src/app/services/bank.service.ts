import { Injectable } from '@angular/core';
import { BankOption } from '../models/account.model';

@Injectable({
  providedIn: 'root'
})
export class BankService {

  private banks: BankOption[] = [
    { code: 'SBIN', name: 'State Bank of India', logo: 'banks/sbi.svg', color: '#1c4587' },
    { code: 'HDFC', name: 'HDFC Bank', logo: 'banks/hdfc.svg', color: '#004c8f' },
    { code: 'ICIC', name: 'ICICI Bank', logo: 'banks/icici.svg', color: '#b95d1b' },
    { code: 'UTIB', name: 'Axis Bank', logo: 'banks/Axis_Bank_logo.svg.png', color: '#8b1538' },
    { code: 'KKBK', name: 'Kotak Mahindra Bank', logo: 'banks/kotak.svg', color: '#d91f27' },
    { code: 'PUNB', name: 'Punjab National Bank', logo: 'banks/pnb.svg', color: '#e31e24' },
    { code: 'BARB', name: 'Bank of Baroda', logo: 'banks/bob.svg', color: '#f05a29' },
    { code: 'CNRB', name: 'Canara Bank', logo: 'banks/canara.svg', color: '#d71920' },
    { code: 'UBIN', name: 'Union Bank of India', logo: 'banks/union.svg', color: '#1f4788' },
    { code: 'IDIB', name: 'IDBI Bank', logo: 'banks/idbi.svg', color: '#00539f' },
    { code: 'INDB', name: 'IndusInd Bank', logo: 'banks/indusind.svg', color: '#c90016' },
    { code: 'YESB', name: 'Yes Bank', logo: 'banks/yes.svg', color: '#003e7e' }
  ];

  constructor() {}

  getAllBanks(): BankOption[] {
    return this.banks;
  }

  getBankByCode(code: string): BankOption | undefined {
    return this.banks.find(bank => bank.code === code);
  }

  getBankByIFSC(ifsc: string): BankOption | undefined {
    const bankCode = ifsc.substring(0, 4);
    return this.banks.find(bank => bank.code === bankCode);
  }
}
