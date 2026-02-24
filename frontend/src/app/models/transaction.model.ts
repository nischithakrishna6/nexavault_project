

export interface Transaction {
  id: number;
  fromAccountId?: number;
  fromAccountNumber?: string;
  toAccountId?: number;
  toAccountNumber?: string;
  toAccountHolderName: string;
  fromAccountHolderName: string;
  toBankName?: string;
  fromBankName?: string;
  amount: number;
  transactionType: 'TRANSFER' | 'DEPOSIT' | 'WITHDRAWAL';
  status: 'PENDING' | 'COMPLETED' | 'FAILED' | 'CANCELLED';
  description?: string;
  referenceNumber: string;
  createdAt: string;
  toBankCode?: string;
  fromBankCode?: string;
}

export interface TransferRequest {
  fromAccountId: number;
  toAccountNumber: string;
  amount: number;
  description?: string;
}
