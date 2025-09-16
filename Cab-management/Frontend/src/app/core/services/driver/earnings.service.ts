import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

import { Earnings,EarningsBreakdown } from '../../models/driver/driver.model';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class EarningsService {
  
  private apiUrl = environment.apiUrl || 'http://localhost:3000/api';

  // Mock earnings data for development
  private mockEarningsData = {
    today: {
      summary: {
        gross: 2450,
        commission: 245,
        tips: 80,
        net: 2285
      },
      chartData: [
        { label: '6AM', value: 0 },
        { label: '9AM', value: 320 },
        { label: '12PM', value: 580 },
        { label: '3PM', value: 890 },
        { label: '6PM', value: 1450 },
        { label: '9PM', value: 2450 },
        { label: '12AM', value: 2450 }
      ]
    },
    week: {
      summary: {
        gross: 15240,
        commission: 1524,
        tips: 580,
        net: 14296
      },
      chartData: [
        { label: 'Mon', value: 2180 },
        { label: 'Tue', value: 1950 },
        { label: 'Wed', value: 2340 },
        { label: 'Thu', value: 2890 },
        { label: 'Fri', value: 3250 },
        { label: 'Sat', value: 2630 },
        { label: 'Sun', value: 2450 }
      ]
    },
    month: {
      summary: {
        gross: 68500,
        commission: 6850,
        tips: 2340,
        net: 63990
      },
      chartData: [
        { label: 'Week 1', value: 15240 },
        { label: 'Week 2', value: 18650 },
        { label: 'Week 3', value: 16890 },
        { label: 'Week 4', value: 17720 }
      ]
    }
  };

  constructor(private http: HttpClient) {}

  getEarningsSummary(period: 'today' | 'week' | 'month' = 'today'): Observable<any> {
    // In production:
    // return this.http.get<any>(`${this.apiUrl}/driver/earnings/summary?period=${period}`);
    
    // Mock implementation
    return of(this.mockEarningsData[period]);
  }

  getDetailedEarnings(startDate: Date, endDate: Date): Observable<Earnings[]> {
    // return this.http.get<Earnings[]>(`${this.apiUrl}/driver/earnings/detailed`, {
    //   params: {
    //     startDate: startDate.toISOString(),
    //     endDate: endDate.toISOString()
    //   }
    // });
    
    // Mock implementation
    const mockDetailedEarnings: Earnings[] = [
      {
        id: 'EARN001',
        driverId: 'DRV001',
        date: new Date(),
        totalEarnings: 2450,
        totalTrips: 12,
        tips: 80,
        commission: 245,
        netEarnings: 2285,
        breakdown: [
          {
            tripId: 'TRP001',
            fare: 285,
            tip: 25,
            commission: 28.5,
            netAmount: 281.5
          },
          {
            tripId: 'TRP002',
            fare: 195,
            tip: 15,
            commission: 19.5,
            netAmount: 190.5
          }
        ]
      }
    ];
    
    return of(mockDetailedEarnings);
  }

  getEarningsBreakdown(date: Date): Observable<EarningsBreakdown[]> {
    // return this.http.get<EarningsBreakdown[]>(`${this.apiUrl}/driver/earnings/breakdown`, {
    //   params: { date: date.toISOString().split('T')[0] }
    // });
    
    // Mock implementation
    const mockBreakdown: EarningsBreakdown[] = [
      {
        tripId: 'TRP001',
        fare: 285,
        tip: 25,
        commission: 28.5,
        netAmount: 281.5
      },
      {
        tripId: 'TRP002',
        fare: 195,
        tip: 15,
        commission: 19.5,
        netAmount: 190.5
      },
      {
        tripId: 'TRP003',
        fare: 165,
        tip: 0,
        commission: 16.5,
        netAmount: 148.5
      },
      {
        tripId: 'TRP004',
        fare: 125,
        tip: 10,
        commission: 12.5,
        netAmount: 122.5
      }
    ];
    
    return of(mockBreakdown);
  }

  getWeeklyEarnings(): Observable<any[]> {
    // return this.http.get<any[]>(`${this.apiUrl}/driver/earnings/weekly`);
    
    // Mock implementation
    const weeklyData = [];
    const currentDate = new Date();
    
    for (let i = 6; i >= 0; i--) {
      const date = new Date(currentDate);
      date.setDate(currentDate.getDate() - i);
      
      weeklyData.push({
        date: date,
        earnings: Math.floor(Math.random() * 2000) + 1000,
        trips: Math.floor(Math.random() * 15) + 5,
        tips: Math.floor(Math.random() * 200) + 50
      });
    }
    
    return of(weeklyData);
  }

  getMonthlyEarnings(year: number, month: number): Observable<any[]> {
    // return this.http.get<any[]>(`${this.apiUrl}/driver/earnings/monthly`, {
    //   params: { year: year.toString(), month: month.toString() }
    // });
    
    // Mock implementation
    const monthlyData = [];
    const daysInMonth = new Date(year, month + 1, 0).getDate();
    
    for (let day = 1; day <= daysInMonth; day++) {
      const date = new Date(year, month, day);
      if (date <= new Date()) {
        monthlyData.push({
          date: date,
          earnings: Math.floor(Math.random() * 3000) + 1000,
          trips: Math.floor(Math.random() * 20) + 5,
          tips: Math.floor(Math.random() * 300) + 50
        });
      }
    }
    
    return of(monthlyData);
  }

  getPaymentHistory(page: number = 1, pageSize: number = 10): Observable<{ payments: any[], total: number }> {
    // return this.http.get<{payments: any[], total: number}>(`${this.apiUrl}/driver/payments`, {
    //   params: { page: page.toString(), pageSize: pageSize.toString() }
    // });
    
    // Mock implementation
    const mockPayments = [
      {
        id: 'PAY001',
        date: new Date(Date.now() - 1000 * 60 * 60 * 24), // Yesterday
        amount: 14296,
        type: 'weekly_payout',
        status: 'completed',
        method: 'bank_transfer',
        transactionId: 'TXN1234567890'
      },
      {
        id: 'PAY002',
        date: new Date(Date.now() - 1000 * 60 * 60 * 24 * 8), // 8 days ago
        amount: 13850,
        type: 'weekly_payout',
        status: 'completed',
        method: 'bank_transfer',
        transactionId: 'TXN1234567889'
      },
      {
        id: 'PAY003',
        date: new Date(Date.now() - 1000 * 60 * 60 * 24 * 15), // 15 days ago
        amount: 15670,
        type: 'weekly_payout',
        status: 'completed',
        method: 'bank_transfer',
        transactionId: 'TXN1234567888'
      }
    ];
    
    const startIndex = (page - 1) * pageSize;
    const paginatedPayments = mockPayments.slice(startIndex, startIndex + pageSize);
    
    return of({
      payments: paginatedPayments,
      total: mockPayments.length
    });
  }

  getTotalEarningsStats(): Observable<any> {
    // return this.http.get<any>(`${this.apiUrl}/driver/earnings/total-stats`);
    
    // Mock implementation
    return of({
      totalLifetimeEarnings: 125000,
      currentMonthEarnings: 68500,
      lastMonthEarnings: 72340,
      averageWeeklyEarnings: 15240,
      averageDailyEarnings: 2177,
      totalTripsCompleted: 1250,
      averageEarningsPerTrip: 100,
      topEarningDay: {
        date: new Date(Date.now() - 1000 * 60 * 60 * 24 * 3),
        amount: 3450
      },
      monthlyGrowth: -5.3, // Percentage change from last month
      weeklyGrowth: 8.7 // Percentage change from last week
    });
  }

  getIncentiveEarnings(period: 'week' | 'month' = 'week'): Observable<any[]> {
    // return this.http.get<any[]>(`${this.apiUrl}/driver/earnings/incentives?period=${period}`);
    
    // Mock implementation
    const mockIncentives = [
      {
        id: 'INC001',
        type: 'peak_hours_bonus',
        description: 'Peak hours bonus (6-9 AM)',
        amount: 150,
        date: new Date(),
        status: 'earned'
      },
      {
        id: 'INC002',
        type: 'completion_bonus',
        description: '10 trips completion bonus',
        amount: 200,
        date: new Date(Date.now() - 1000 * 60 * 60 * 24),
        status: 'earned'
      },
      {
        id: 'INC003',
        type: 'weekend_bonus',
        description: 'Weekend surge bonus',
        amount: 300,
        date: new Date(Date.now() - 1000 * 60 * 60 * 24 * 2),
        status: 'earned'
      }
    ];
    
    return of(mockIncentives);
  }

  requestEarlyPayout(amount: number): Observable<boolean> {
    // return this.http.post<boolean>(`${this.apiUrl}/driver/earnings/early-payout`, { amount });
    
    // Mock implementation
    console.log(`Early payout requested for ₹${amount}`);
    return of(true);
  }

  updateBankDetails(bankDetails: any): Observable<boolean> {
    // return this.http.put<boolean>(`${this.apiUrl}/driver/bank-details`, bankDetails);
    
    // Mock implementation
    console.log('Bank details updated:', bankDetails);
    return of(true);
  }

  downloadEarningsReport(startDate: Date, endDate: Date, format: 'pdf' | 'csv' = 'pdf'): Observable<Blob> {
    // return this.http.get(`${this.apiUrl}/driver/earnings/report`, {
    //   params: {
    //     startDate: startDate.toISOString(),
    //     endDate: endDate.toISOString(),
    //     format
    //   },
    //   responseType: 'blob'
    // });
    
    // Mock implementation
    const mockBlob = new Blob(['Mock earnings report content'], { type: 'application/pdf' });
    return of(mockBlob);
  }
}