export interface Account {
  id: number;
  accountNumber: string;
  accountType: 'SAVINGS' | 'CHECKING' | 'BUSINESS';
  bankName?: string;
  branchName?: string;
  ifscCode?: string;
  accountHolderName?: string;
  balance: number;
  currency: string;
  isActive: boolean;
  createdAt: string;
}

export interface LinkAccountRequest {
  accountType: 'SAVINGS' | 'CHECKING' | 'BUSINESS';
  bankName: string;
  branchName: string;
  ifscCode: string;
  accountHolderName: string;
  bankCode: string;
  existingAccountNumber: string; // User's real account number
}

export interface BankDetails {
  BANK: string;
  IFSC: string;
  BRANCH: string;
  ADDRESS: string;
  CONTACT: string;
  CITY: string;
  DISTRICT: string;
  STATE: string;
  BANKCODE: string;
  RTGS: boolean;
  MICR: string;
}
export interface BankOption {
  code: string;
  name: string;
  logo: string;
  color: string;
}
