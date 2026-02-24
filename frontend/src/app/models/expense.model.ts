export interface Expense {
  id?: number;
  userId: number;
  category: string;
  amount: number;
  description: string;
  date: string;
  createdAt?: string;
}

export interface ExpenseCategory {
  name: string;
  icon: string;
  color: string;
}

export interface ExpenseStats {
  totalExpenses: number;
  categoryBreakdown: CategoryExpense[];
  dailyExpenses: DailyExpense[];
  weeklyExpenses: WeeklyExpense[];
  monthlyExpenses: MonthlyExpense[];
}

export interface CategoryExpense {
  category: string;
  amount: number;
  percentage: number;
}

export interface DailyExpense {
  date: string;
  amount: number;
}

export interface WeeklyExpense {
  week: string;
  amount: number;
}

export interface MonthlyExpense {
  month: string;
  amount: number;
}

export const EXPENSE_CATEGORIES: ExpenseCategory[] = [
  { name: 'Food & Dining', icon: '🍔', color: '#FF6384' },
  { name: 'Transportation', icon: '🚗', color: '#36A2EB' },
  { name: 'Shopping', icon: '🛍️', color: '#FFCE56' },
  { name: 'Entertainment', icon: '🎬', color: '#4BC0C0' },
  { name: 'Bills & Utilities', icon: '💡', color: '#9966FF' },
  { name: 'Healthcare', icon: '⚕️', color: '#FF9F40' },
  { name: 'Travel', icon: '✈️', color: '#FF6384' },
  { name: 'Education', icon: '📚', color: '#C9CBCF' },
  { name: 'Personal Care', icon: '💅', color: '#4BC0C0' },
  { name: 'Other', icon: '📦', color: '#FF9F40' }
];
