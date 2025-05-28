export const EXPENSE_CATEGORIES = [
  { id: 'food', label: 'Food & Dining', icon: 'cutlery' },
  { id: 'transportation', label: 'Transportation', icon: 'car' },
  { id: 'housing', label: 'Housing', icon: 'home' },
  { id: 'utilities', label: 'Utilities', icon: 'bolt' },
  { id: 'entertainment', label: 'Entertainment', icon: 'film' },
  { id: 'shopping', label: 'Shopping', icon: 'shopping-bag' },
  { id: 'health', label: 'Health & Medical', icon: 'medkit' },
  { id: 'education', label: 'Education', icon: 'graduation-cap' },
  { id: 'travel', label: 'Travel', icon: 'plane' },
  { id: 'personal', label: 'Personal Care', icon: 'user' },
  { id: 'gifts', label: 'Gifts & Donations', icon: 'gift' },
  { id: 'other', label: 'Other', icon: 'ellipsis-h' },
] as const;

export type ExpenseCategory = typeof EXPENSE_CATEGORIES[number]['id']; 