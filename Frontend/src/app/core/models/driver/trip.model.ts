export interface Trip {
  id: string;
  driverId: string;
  passengerId: string;
  vehicleId: string;
  status: TripStatus;
  pickupLocation: TripLocation;
  dropoffLocation: TripLocation;
  requestedAt: Date;
  acceptedAt?: Date;
  startedAt?: Date;
  completedAt?: Date;
  cancelledAt?: Date;
  distance: number; // in kilometers
  duration: number; // in minutes
  estimatedFare: number;
  actualFare: number;
  surge: SurgeInfo;
  passenger: PassengerInfo;
  route: RouteInfo;
  payment: PaymentInfo;
  rating?: TripRating;
  notes?: string;
  cancellationReason?: string;
}

export interface TripLocation {
  address: string;
  latitude: number;
  longitude: number;
  landmark?: string;
}

export interface SurgeInfo {
  isActive: boolean;
  multiplier: number;
  reason?: string;
}

export interface PassengerInfo {
  id: string;
  name: string;
  phone: string;
  rating: number;
  profileImage?: string;
}

export interface RouteInfo {
  waypoints: Waypoint[];
  totalDistance: number;
  estimatedDuration: number;
  trafficInfo?: TrafficInfo;
}

export interface Waypoint {
  latitude: number;
  longitude: number;
  timestamp: Date;
  address?: string;
}

export interface PaymentInfo {
  method: PaymentMethod;
  status: PaymentStatus;
  baseFare: number;
  surgeAmount: number;
  tips: number;
  taxes: number;
  platformFee: number;
  driverEarning: number;
  totalAmount: number;
  currency: string;
  transactionId?: string;
  paidAt?: Date;
}

export interface TrafficInfo {
  level: TrafficLevel;
  delayMinutes: number;
  alternativeRoutes: AlternativeRoute[];
}

export interface AlternativeRoute {
  name: string;
  distance: number;
  duration: number;
  trafficLevel: TrafficLevel;
}

export interface TripRating {
  rating: number; // 1-5 stars
  comment?: string;
  ratedAt: Date;
  ratedBy: 'passenger' | 'driver';
}

// Trip Summary interfaces for analytics
export interface TripSummary {
  totalTrips: number;
  completedTrips: number;
  cancelledTrips: number;
  totalDistance: number;
  totalDuration: number;
  totalEarnings: number;
  averageRating: number;
  period: DateRange;
}

export interface DateRange {
  startDate: Date;
  endDate: Date;
}

export interface DailyTripStats {
  date: Date;
  tripCount: number;
  earnings: number;
  distance: number;
  duration: number;
  averageRating: number;
}

export interface TripFilters {
  status?: TripStatus[];
  dateFrom?: Date;
  dateTo?: Date;
  minFare?: number;
  maxFare?: number;
  paymentMethod?: PaymentMethod;
  sortBy?: TripSortBy;
  sortOrder?: 'asc' | 'desc';
}

export enum TripStatus {
  REQUESTED = 'requested',
  ACCEPTED = 'accepted',
  ARRIVED = 'arrived',
  STARTED = 'started',
  COMPLETED = 'completed',
  CANCELLED_BY_DRIVER = 'cancelled_by_driver',
  CANCELLED_BY_PASSENGER = 'cancelled_by_passenger',
  NO_SHOW = 'no_show'
}

export enum PaymentMethod {
  CASH = 'cash',
  CREDIT_CARD = 'credit_card',
  DEBIT_CARD = 'debit_card',
  DIGITAL_WALLET = 'digital_wallet',
  UPI = 'upi'
}

export enum PaymentStatus {
  PENDING = 'pending',
  PAID = 'paid',
  FAILED = 'failed',
  REFUNDED = 'refunded'
}

export enum TrafficLevel {
  LOW = 'low',
  MODERATE = 'moderate',
  HIGH = 'high',
  SEVERE = 'severe'
}

export enum TripSortBy {
  DATE = 'date',
  FARE = 'fare',
  DISTANCE = 'distance',
  DURATION = 'duration',
  RATING = 'rating'
}