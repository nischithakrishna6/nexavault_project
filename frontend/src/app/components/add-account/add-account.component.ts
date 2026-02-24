import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { LinkAccountRequest, BankDetails, BankOption } from '../../models/account.model';
import { AccountService } from '../../services/account.service';
import { IfscService } from '../../services/ifsc.service';
import { AuthService } from '../../services/auth.service';
import { BankService } from '../../services/bank.service';

@Component({
  selector: 'app-add-account',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './add-account.component.html',
  styleUrls: ['./add-account.component.css']
})
export class AddAccountComponent implements OnInit {
  step: 'bank-selection' | 'account-details' | 'verification' | 'success' = 'bank-selection';

  selectedBank: BankOption | null = null;
  banks: BankOption[] = [];

  newAccount: LinkAccountRequest = {
    accountType: 'SAVINGS',
    bankCode: '',
    bankName: '',
    branchName: '',
    ifscCode: '',
    accountHolderName: '',
    existingAccountNumber: ''
  };

  bankDetails: BankDetails | null = null;
  accountError: string = '';
  successMessage: string = '';
  isLoading: boolean = false;
  isVerifyingIFSC: boolean = false;
  isVerifyingAccount: boolean = false;
  ifscVerified: boolean = false;
  accountVerified: boolean = false;
  selectedBankCode: string = '';

  currentUser: any;

  constructor(
    private accountService: AccountService,
    private ifscService: IfscService,
    private authService: AuthService,
    private bankService: BankService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.banks = this.bankService.getAllBanks();
    this.currentUser = this.authService.getCurrentUser();
    if (this.currentUser) {
      this.newAccount.accountHolderName = `${this.currentUser.firstName} ${this.currentUser.lastName}`;
    }
  }

  handleLogoError(event: any): void {
    event.target.style.display = 'none';
  }

  onBankDropdownSelect() {
    const bank = this.banks.find(b => b.code === this.selectedBankCode);

    if (bank) {
      this.selectedBank = bank;
      this.newAccount.bankCode = bank.code;
      this.newAccount.bankName = bank.name;

      console.log('Bank selected:', {
        code: this.newAccount.bankCode,
        name: this.newAccount.bankName
      });

      this.step = 'account-details';
    } else {
      console.error('Bank not found for code:', this.selectedBankCode);
    }
  }

  selectBank(bank: BankOption): void {
    this.selectedBank = bank;
    this.newAccount.bankCode = bank.code;
    this.newAccount.bankName = bank.name;
    this.step = 'account-details';
  }

  goBackToBankSelection(): void {
    this.step = 'bank-selection';
    this.selectedBank = null;
    this.resetForm();
  }

  // ✅ UPDATED: Proper IFSC Verification with Bank Matching
  verifyIFSC(): void {
    const ifsc = this.newAccount.ifscCode.trim().toUpperCase();

    // Clear previous messages
    this.accountError = '';
    this.successMessage = '';
    this.ifscVerified = false;
    this.bankDetails = null;

    // Step 1: Check length
    if (ifsc.length !== 11) {
      this.accountError = 'IFSC code must be exactly 11 characters';
      return;
    }

    // Step 2: Check format
    const ifscPattern = /^[A-Z]{4}0[A-Z0-9]{6}$/;
    if (!ifscPattern.test(ifsc)) {
      this.accountError = 'Invalid IFSC format. Example: SBIN0001234';
      return;
    }

    // Step 3: Check if bank is selected
    if (!this.selectedBank) {
      this.accountError = 'Please select a bank first';
      return;
    }

    // Step 4: ✅ Validate IFSC matches selected bank
    const bankCodeFromIFSC = ifsc.substring(0, 4);
    if (bankCodeFromIFSC !== this.selectedBank.code) {
      this.accountError = `IFSC code mismatch! You selected ${this.selectedBank.name} (${this.selectedBank.code}), but this IFSC belongs to ${bankCodeFromIFSC}. Please check the IFSC code or select the correct bank.`;
      return;
    }

    // Step 5: Call API to verify IFSC and get branch details
    this.isVerifyingIFSC = true;

    this.ifscService.verifyIFSC(ifsc).subscribe({
      next: (details) => {
        this.bankDetails = details;
        this.ifscVerified = true;
        this.newAccount.branchName = details.BRANCH;
        this.isVerifyingIFSC = false;
        this.successMessage = ` Branch verified: ${details.BRANCH}, ${details.CITY}`;
      },
      error: (error) => {
        this.isVerifyingIFSC = false;
        this.ifscVerified = false;

        if (error.status === 404) {
          this.accountError = 'IFSC code not found in database. Please verify and try again.';
        } else {
          // Even if API fails, we already validated the format and bank match
          this.accountError = 'Could not fetch branch details, but IFSC format is valid. You can proceed.';
          this.ifscVerified = true;  // Allow user to continue
        }
      }
    });
  }

  // Account Number Validation
  validateAccountNumber(): void {
    const accNum = this.newAccount.existingAccountNumber.trim();

    // Clear previous messages
    this.accountError = '';
    this.successMessage = '';
    this.accountVerified = false;

    // Basic validation
    if (!accNum) {
      this.accountError = 'Account number is required';
      return;
    }

    if (accNum.length < 9 || accNum.length > 18) {
      this.accountError = 'Account number must be between 9-18 digits';
      return;
    }

    const accNumPattern = /^[0-9]+$/;
    if (!accNumPattern.test(accNum)) {
      this.accountError = 'Account number must contain only digits';
      return;
    }

    // Check if IFSC is verified first
    if (!this.ifscVerified) {
      this.accountError = 'Please verify IFSC code first';
      return;
    }

    // Start verification
    this.isVerifyingAccount = true;

    // Simulate validation (2.5 second delay)
    setTimeout(() => {
      // Test scenarios
      if (accNum === '111111111' || accNum === '000000000') {
        this.accountError = ' Account not found or inactive';
        this.accountVerified = false;
        this.isVerifyingAccount = false;
        return;
      }

      if (accNum === '999999999') {
        this.accountError = ' Account closed';
        this.accountVerified = false;
        this.isVerifyingAccount = false;
        return;
      }

      // Success
      this.accountVerified = true;
      this.isVerifyingAccount = false;
      this.successMessage = ` Account verified: ****${accNum.slice(-4)}`;
    }, 2500);
  }

  // Reset validation when account number changes
  onAccountNumberChange(): void {
    this.accountVerified = false;
    this.accountError = '';
    this.successMessage = '';
  }

  // Reset IFSC validation when IFSC changes
  onIFSCChange(): void {
    this.ifscVerified = false;
    this.accountError = '';
    this.successMessage = '';
    this.bankDetails = null;
  }

  // Link Account
  linkAccount(): void {
    this.accountError = '';
    this.successMessage = '';

    console.log('=== LINK ACCOUNT DEBUG ===');
    console.log('Request:', JSON.stringify(this.newAccount, null, 2));

    // Validation
    if (!this.selectedBank) {
      this.accountError = 'Please select a bank';
      console.error('Error: No bank selected');
      return;
    }

    if (!this.newAccount.bankCode) {
      this.accountError = 'Bank code missing. Select bank again.';
      console.error('Error: Missing bankCode');
      return;
    }

    if (!this.newAccount.bankName) {
      this.accountError = 'Bank name missing. Select bank again.';
      console.error('Error: Missing bankName');
      return;
    }

    if (!this.ifscVerified) {
      this.accountError = 'Please verify your IFSC code first';
      console.error('Error: IFSC not verified');
      return;
    }

    if (!this.newAccount.branchName) {
      this.accountError = 'Branch name missing. Verify IFSC first.';
      console.error('Error: Missing branchName');
      return;
    }

    if (!this.newAccount.accountHolderName.trim()) {
      this.accountError = 'Account holder name is required';
      console.error('Error: Missing accountHolderName');
      return;
    }

    if (!this.newAccount.existingAccountNumber.trim()) {
      this.accountError = 'Please enter your account number';
      console.error('Error: Missing account number');
      return;
    }

    if (!this.accountVerified) {
      this.accountError = 'Please validate your account number';
      console.error('Error: Account not verified');
      return;
    }

    console.log(' Validations passed. Sending to backend...');

    this.isLoading = true;

    this.accountService.linkAccount(this.newAccount).subscribe({
      next: (response) => {
        console.log(' Success:', response);
        if (response.success) {
          this.step = 'success';
          this.successMessage = 'Bank account linked successfully!';
          setTimeout(() => {
            this.router.navigate(['/dashboard']);
          }, 3000);
        } else {
          this.accountError = response.message;
        }
        this.isLoading = false;
      },
      error: (error) => {
        console.error('✗ Error:', error);
        console.error('Status:', error.status);
        console.error('Body:', error.error);

        if (error.status === 400) {
          this.accountError = error.error?.message || 'Invalid request';
        } else if (error.status === 401) {
          this.accountError = 'Session expired. Please login again.';
          this.router.navigate(['/login']);
        } else {
          this.accountError = 'Failed to link account. Try again.';
        }

        this.isLoading = false;
      }
    });
  }

  goBack(): void {
    if (this.step === 'account-details') {
      this.step = 'bank-selection';
    } else {
      this.router.navigate(['/dashboard']);
    }
  }

  resetForm(): void {
    this.newAccount = {
      accountType: 'SAVINGS',
      bankCode: '',
      bankName: '',
      branchName: '',
      ifscCode: '',
      accountHolderName: this.currentUser ? `${this.currentUser.firstName} ${this.currentUser.lastName}` : '',
      existingAccountNumber: ''
    };
    this.bankDetails = null;
    this.ifscVerified = false;
    this.accountVerified = false;
    this.accountError = '';
    this.successMessage = '';
    this.selectedBank = null;
    this.selectedBankCode = '';
  }
}
