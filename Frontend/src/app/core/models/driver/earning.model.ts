export interface Earnings {
  id: string;
  driverId: string;
  tripId: string;
  date: Date;
  baseFare: number;
  surgeAmount: number;
  tips: number;
  bonuses: Bonus[];
  deductions: Deduction[];
  grossEarning: number;
  netEarning: number;
  platformFee: number;
  taxes: number;
  currency: string;
  payoutStatus: PayoutStatus;
  payoutDate?: Date;
}

export interface Bonus {
  id: string;
  type: BonusType;
  amount: number;
  description: string;
  earnedAt: Date;
}

export interface Deduction {
  id: string;
  type: DeductionType;
  amount: number;
  description: string;
  appliedAt: Date;
}

export interface EarningsSummary {
  totalEarnings: number;
  totalTrips: number;
  averagePerTrip: number;
  totalBonuses: number;
  totalDeductions: number;
  totalTips: number;
  totalPlatformFees: number;
  netEarnings: number;
  period: EarningsPeriod;
  breakdown: EarningsBreakdown;
}

export interface EarningsBreakdown {
  byDay: DailyEarnings[];
  byWeek: WeeklyEarnings[];
  byMonth: MonthlyEarnings[];
  byPaymentMethod: PaymentMethodEarnings[];
  byTripType: TripTypeEarnings[];
}

export interface DailyEarnings {
  date: Date;
  totalEarnings: number;
  tripCount: number;
  averagePerTrip: number;
  tips: number;
  bonuses: number;
  deductions: number;
  workingHours: number;
}

export interface WeeklyEarnings {
  weekStart: Date;
  weekEnd: Date;
  totalEarnings: number;
  tripCount: number;
  averagePerTrip: number;
  tips: number;
  bonuses: number;
  deductions: number;
  workingHours: number;
}

export interface MonthlyEarnings {
  month: number;
  year: number;
  totalEarnings: number;
  tripCount: number;
  averagePerTrip: number;
  tips: number;
  bonuses: number;
  deductions: number;
  workingDays: number;
  workingHours: number;
}

export interface PaymentMethodEarnings {
  paymentMethod: string;
  totalEarnings: number;
  tripCount: number;
  percentage: number;
}

export interface TripTypeEarnings {
  tripType: string;
  totalEarnings: number;
  tripCount: number;
  averagePerTrip: number;
}

export interface Payout {
  id: string;
  driverId: string;
  amount: number;
  currency: string;
  payoutDate: Date;
  payoutMethod: PayoutMethod;
  status: PayoutStatus;
  earningsIncluded: string[]; // Array of earnings IDs
  bankAccount?: BankAccountInfo;
  transactionId?: string;
  processedAt?: Date;
  failureReason?: string;
}

export interface BankAccountInfo {
  accountHolderName: string;
  accountNumber: string;
  bankName: string;
  routingNumber: string;
}

export interface EarningsFilters {
  dateFrom?: Date;
  dateTo?: Date;
  payoutStatus?: PayoutStatus[];
  minAmount?: number;
  maxAmount?: number;
  tripType?: string;
  paymentMethod?: string;
  sortBy?: EarningsSortBy;
  sortOrder?: 'asc' | 'desc';
}

export interface EarningsGoal {
  id: string;
  driverId: string;
  type: GoalType;
  targetAmount: number;
  currentAmount: number;
  period: GoalPeriod;
  startDate: Date;
  endDate: Date;
  isActive: boolean;
  createdAt: Date;
  completedAt?: Date;
}

export interface EarningsInsights {
  bestEarningDay: string;
  bestEarningTime: string;
  averageHourlyRate: number;
  peakHours: PeakHour[];
  monthlyTrend: MonthlyTrend[];
  comparisonWithPrevious: EarningsComparison;
}

export interface PeakHour {
  hour: number;
  averageEarnings: number;
  tripCount: number;
}

export interface MonthlyTrend {
  month: string;
  earnings: number;
  change: number; // percentage change from previous month
}

export interface EarningsComparison {
  previousPeriod: number;
  currentPeriod: number;
  change: number; // percentage change
  changeType: 'increase' | 'decrease' | 'same';
}

export interface EarningsPeriod {
  startDate: Date;
  endDate: Date;
  type: 'daily' | 'weekly' | 'monthly' | 'yearly' | 'custom';
}

export enum BonusType {
  PEAK_HOUR = 'peak_hour',
  CONSECUTIVE_TRIPS = 'consecutive_trips',
  HIGH_RATING = 'high_rating',
  REFERRAL = 'referral',
  MILESTONE = 'milestone',
  SURGE = 'surge',
  LOYALTY = 'loyalty'
}

export enum DeductionType {
  PLATFORM_FEE = 'platform_fee',
  TAX = 'tax',
  FUEL = 'fuel',
  MAINTENANCE = 'maintenance',
  INSURANCE = 'insurance',
  PENALTY = 'penalty',
  CANCELLATION_FEE = 'cancellation_fee'
}

export enum PayoutStatus {
  PENDING = 'pending',
  PROCESSING = 'processing',
  COMPLETED = 'completed',
  FAILED = 'failed',
  CANCELLED = 'cancelled'
}

export enum PayoutMethod {
  BANK_TRANSFER = 'bank_transfer',
  DIGITAL_WALLET = 'digital_wallet',
  CHECK = 'check',
  CASH = 'cash'
}

export enum EarningsSortBy {
  DATE = 'date',
  AMOUNT = 'amount',
  TRIP_COUNT = 'trip_count',
  TIPS = 'tips',
  BONUSES = 'bonuses'
}

export enum GoalType {
  DAILY = 'daily',
  WEEKLY = 'weekly',
  MONTHLY = 'monthly',
  TRIP_COUNT = 'trip_count',
  DISTANCE = 'distance'
}

export enum GoalPeriod {
  TODAY = 'today',
  THIS_WEEK = 'this_week',
  THIS_MONTH = 'this_month',
  CUSTOM = 'custom'
}