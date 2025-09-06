export interface Driver {
  id: string;
  name: string;
  email: string;
  phone: string;
  avatar: string;
  licenseNumber: string;
  rating: number;
  totalTrips: number;
  totalEarnings: number;
  joinDate: Date;
  isOnline: boolean;
  vehicle: Vehicle;
  location: Location;
}

export interface Vehicle {
  id: string;
  make: string;
  model: string;
  year: number;
  color: string;
  plateNumber: string;
  type: VehicleType;
  fuelType: string;
  currentMileage: number;
  lastService: Date;
  nextService: Date;
  insurance: Insurance;
}

export interface Insurance {
  provider: string;
  policyNumber: string;
  expiryDate: Date;
  isActive: boolean;
}

export interface Location {
  latitude: number;
  longitude: number;
  address: string;
  city: string;
  state: string;
}

export enum VehicleType {
  SEDAN = 'sedan',
  SUV = 'suv',
  HATCHBACK = 'hatchback',
  AUTO = 'auto'
}

export interface Trip {
  id: string;
  driverId: string;
  passengerId: string;
  passengerName: string;
  passengerPhone: string;
  pickupLocation: Location;
  dropLocation: Location;
  startTime: Date;
  endTime: Date;
  distance: number;
  duration: number;
  fare: number;
  tip: number;
  status: TripStatus;
  paymentMethod: PaymentMethod;
  rating: number;
}

export enum TripStatus {
  REQUESTED = 'requested',
  ACCEPTED = 'accepted',
  IN_PROGRESS = 'in_progress',
  COMPLETED = 'completed',
  CANCELLED = 'cancelled',
  CONFIRMED='confirmed'
}

export enum PaymentMethod {
  CASH = 'cash',
  CARD = 'card',
  WALLET = 'wallet',
  UPI = 'upi'
}

export interface Earnings {
  id: string;
  driverId: string;
  date: Date;
  totalEarnings: number;
  totalTrips: number;
  tips: number;
  commission: number;
  netEarnings: number;
  breakdown: EarningsBreakdown[];
}

export interface EarningsBreakdown {
  tripId: string;
  fare: number;
  tip: number;
  commission: number;
  netAmount: number;
}