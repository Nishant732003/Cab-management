// earnings.component.ts
import { Component, OnInit, OnDestroy, ViewChild, ElementRef } from '@angular/core';
import { Subject, BehaviorSubject } from 'rxjs';
import { takeUntil, debounceTime, distinctUntilChanged } from 'rxjs/operators';


import { Earnings,  EarningsSummary,
  EarningsFilters,
  EarningsGoal,
  EarningsInsights,
  EarningsComparison,
  Bonus,
  Deduction,
  PayoutStatus,
  EarningsSortBy,
  GoalType,
  GoalPeriod,
  BonusType,
  DeductionType } from '../../../../core/models/driver/earning.model';

@Component({
  selector: 'app-driver-earnings',
  templateUrl: './earnings.component.html',
  styleUrls: ['./earnings.component.css'],
  standalone:false

})
export class EarningsComponent implements OnInit, OnDestroy {
  @ViewChild('earningsChart') earningsChartRef!: ElementRef<HTMLCanvasElement>;
  
  private destroy$ = new Subject<void>();
  private filtersChanged$ = new BehaviorSubject<EarningsFilters>({});
  
  // Data properties
  earnings: Earnings[] = [];
  paginatedEarnings: Earnings[] = [];
  earningsSummary: EarningsSummary | null = null;
  earningsInsights: EarningsInsights | null = null;
  earningsComparison: EarningsComparison = {
    previousPeriod: 0,
    currentPeriod: 0,
    change: 0,
    changeType: 'same'
  };
  
  // UI state
  isLoading = false;
  showFilters = false;
  showGoalModal = false;
  selectedPeriod = 'this_week';
  customDateFrom = '';
  customDateTo = '';
  
  // Pagination
  currentPage = 1;
  itemsPerPage = 10;
  totalPages = 1;
  
  // Filters
  filters: EarningsFilters = {
    sortBy: EarningsSortBy.DATE,
    sortOrder: 'desc'
  };
  
  // Goal modal
  newGoal: Partial<EarningsGoal> = {
    type: GoalType.WEEKLY,
    period: GoalPeriod.THIS_WEEK,
    targetAmount: 0
  };
  
  // Period options
  periodOptions = [
    { label: 'Today', value: 'today' },
    { label: 'This Week', value: 'this_week' },
    { label: 'This Month', value: 'this_month' },
    { label: 'Last 30 Days', value: 'last_30_days' },
    { label: 'Custom', value: 'custom' }
  ];

  constructor() {
    this.initializeMockData();
  }

  ngOnInit(): void {
    this.loadData();
    this.setupFilters();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private initializeMockData(): void {
    // Generate mock earnings data
    this.earnings = this.generateMockEarnings();
    this.updatePaginatedEarnings();
    
    // Generate mock summary
    this.earningsSummary = this.generateMockSummary();
    
    // Generate mock insights
    this.earningsInsights = this.generateMockInsights();
    
    // Generate mock comparison
    this.earningsComparison = {
      previousPeriod: 1250.50,
      currentPeriod: 1485.75,
      change: 18.8,
      changeType: 'increase'
    };
  }

  private generateMockEarnings(): Earnings[] {
    const mockEarnings: Earnings[] = [];
    const statuses: PayoutStatus[] = [PayoutStatus.PENDING, PayoutStatus.PROCESSING, PayoutStatus.COMPLETED, PayoutStatus.FAILED];
    
    for (let i = 0; i < 50; i++) {
      const date = new Date();
      date.setDate(date.getDate() - i);
      
      const bonuses: Bonus[] = Math.random() > 0.7 ? [
        {
          id: `bonus-${i}`,
          type: BonusType.PEAK_HOUR,
          amount: Math.random() * 10 + 5,
          description: 'Peak hour bonus',
          earnedAt: date
        }
      ] : [];
      
      const deductions: Deduction[] = [
        {
          id: `deduction-${i}`,
          type: DeductionType.PLATFORM_FEE,
          amount: Math.random() * 5 + 2,
          description: 'Platform service fee',
          appliedAt: date
        }
      ];
      
      const baseFare = Math.random() * 30 + 15;
      const tips = Math.random() > 0.6 ? Math.random() * 10 + 2 : 0;
      const bonusTotal = bonuses.reduce((sum, bonus) => sum + bonus.amount, 0);
      const deductionTotal = deductions.reduce((sum, deduction) => sum + deduction.amount, 0);
      const grossEarning = baseFare + tips + bonusTotal;
      const netEarning = grossEarning - deductionTotal;
      
      mockEarnings.push({
        id: `earning-${i}`,
        driverId: 'driver-123',
        tripId: `TRP${String(i).padStart(6, '0')}`,
        date: date,
        baseFare: Math.round(baseFare * 100) / 100,
        surgeAmount: Math.random() > 0.8 ? Math.random() * 10 + 5 : 0,
        tips: Math.round(tips * 100) / 100,
        bonuses: bonuses,
        deductions: deductions,
        grossEarning: Math.round(grossEarning * 100) / 100,
        netEarning: Math.round(netEarning * 100) / 100,
        platformFee: Math.round(deductionTotal * 100) / 100,
        taxes: Math.round((netEarning * 0.15) * 100) / 100,
        currency: 'USD',
        payoutStatus: statuses[Math.floor(Math.random() * statuses.length)],
        payoutDate: Math.random() > 0.3 ? date : undefined
      });
    }
    
    return mockEarnings.sort((a, b) => b.date.getTime() - a.date.getTime());
  }

  private generateMockSummary(): EarningsSummary {
    const totalEarnings = this.earnings.reduce((sum, earning) => sum + earning.netEarning, 0);
    const totalTrips = this.earnings.length;
    const totalTips = this.earnings.reduce((sum, earning) => sum + earning.tips, 0);
    const totalBonuses = this.earnings.reduce((sum, earning) => 
      sum + earning.bonuses.reduce((bonusSum, bonus) => bonusSum + bonus.amount, 0), 0);
    const totalDeductions = this.earnings.reduce((sum, earning) => 
      sum + earning.deductions.reduce((deductionSum, deduction) => deductionSum + deduction.amount, 0), 0);
    const totalPlatformFees = this.earnings.reduce((sum, earning) => sum + earning.platformFee, 0);

    return {
      totalEarnings: Math.round(totalEarnings * 100) / 100,
      totalTrips: totalTrips,
      averagePerTrip: Math.round((totalEarnings / totalTrips) * 100) / 100,
      totalBonuses: Math.round(totalBonuses * 100) / 100,
      totalDeductions: Math.round(totalDeductions * 100) / 100,
      totalTips: Math.round(totalTips * 100) / 100,
      totalPlatformFees: Math.round(totalPlatformFees * 100) / 100,
      netEarnings: Math.round((totalEarnings - totalDeductions) * 100) / 100,
      period: {
        startDate: new Date(Date.now() - 7 * 24 * 60 * 60 * 1000),
        endDate: new Date(),
        type: 'weekly'
      },
      breakdown: {
        byDay: [],
        byWeek: [],
        byMonth: [],
        byPaymentMethod: [],
        byTripType: []
      }
    };
  }

  private generateMockInsights(): EarningsInsights {
    return {
      bestEarningDay: 'Friday',
      bestEarningTime: '6:00 PM - 8:00 PM',
      averageHourlyRate: 25.50,
      peakHours: [
        { hour: 8, averageEarnings: 32.50, tripCount: 3 },
        { hour: 12, averageEarnings: 28.75, tripCount: 4 },
        { hour: 18, averageEarnings: 35.20, tripCount: 5 },
        { hour: 22, averageEarnings: 30.10, tripCount: 2 }
      ],
      monthlyTrend: [
        { month: 'Jan', earnings: 1250.50, change: 5.2 },
        { month: 'Feb', earnings: 1380.25, change: 10.4 },
        { month: 'Mar', earnings: 1485.75, change: 7.6 }
      ],
      comparisonWithPrevious: {
        previousPeriod: 1250.50,
        currentPeriod: 1485.75,
        change: 18.8,
        changeType: 'increase'
      }
    };
  }

  private loadData(): void {
    this.isLoading = true;
    // Simulate API call delay
    setTimeout(() => {
      this.applyFilters();
      this.isLoading = false;
    }, 1000);
  }

  private setupFilters(): void {
    this.filtersChanged$
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        takeUntil(this.destroy$)
      )
      .subscribe(() => {
        this.applyFiltersToData();
      });
  }

  selectPeriod(period: string): void {
    this.selectedPeriod = period;
    this.applyFilters();
  }

  applyFilters(): void {
    this.filtersChanged$.next(this.filters);
  }

  private applyFiltersToData(): void {
    let filteredEarnings = [...this.earnings];

    // Apply date filters
    if (this.selectedPeriod !== 'custom') {
      const now = new Date();
      let startDate: Date;

      switch (this.selectedPeriod) {
        case 'today':
          startDate = new Date(now.getFullYear(), now.getMonth(), now.getDate());
          break;
        case 'this_week':
          startDate = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000);
          break;
        case 'this_month':
          startDate = new Date(now.getFullYear(), now.getMonth(), 1);
          break;
        case 'last_30_days':
          startDate = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000);
          break;
        default:
          startDate = new Date(0);
      }

      filteredEarnings = filteredEarnings.filter(earning => earning.date >= startDate);
    } else if (this.customDateFrom && this.customDateTo) {
      const startDate = new Date(this.customDateFrom);
      const endDate = new Date(this.customDateTo);
      filteredEarnings = filteredEarnings.filter(earning => 
        earning.date >= startDate && earning.date <= endDate
      );
    }

    // Apply other filters
    if (this.filters.payoutStatus && this.filters.payoutStatus.length > 0) {
      filteredEarnings = filteredEarnings.filter(earning => 
        this.filters.payoutStatus!.includes(earning.payoutStatus)
      );
    }

    if (this.filters.minAmount) {
      filteredEarnings = filteredEarnings.filter(earning => 
        earning.netEarning >= this.filters.minAmount!
      );
    }

    if (this.filters.maxAmount) {
      filteredEarnings = filteredEarnings.filter(earning => 
        earning.netEarning <= this.filters.maxAmount!
      );
    }

    // Apply sorting
    if (this.filters.sortBy) {
      filteredEarnings.sort((a, b) => {
        let aValue: any, bValue: any;
        
        switch (this.filters.sortBy) {
          case EarningsSortBy.DATE:
            aValue = a.date.getTime();
            bValue = b.date.getTime();
            break;
          case EarningsSortBy.AMOUNT:
            aValue = a.netEarning;
            bValue = b.netEarning;
            break;
          case EarningsSortBy.TIPS:
            aValue = a.tips;
            bValue = b.tips;
            break;
          default:
            aValue = a.date.getTime();
            bValue = b.date.getTime();
        }

        if (this.filters.sortOrder === 'asc') {
          return aValue - bValue;
        } else {
          return bValue - aValue;
        }
      });
    }

    this.earnings = filteredEarnings;
    this.updatePaginatedEarnings();
    this.earningsSummary = this.generateMockSummary();
  }

  private updatePaginatedEarnings(): void {
    this.totalPages = Math.ceil(this.earnings.length / this.itemsPerPage);
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    const endIndex = startIndex + this.itemsPerPage;
    this.paginatedEarnings = this.earnings.slice(startIndex, endIndex);
  }

  goToPage(page: number): void {
    this.currentPage = page;
    this.updatePaginatedEarnings();
  }

  trackByEarningId(index: number, earning: Earnings): string {
    return earning.id;
  }

  getBonusTotal(bonuses: Bonus[]): number {
    return bonuses.reduce((sum, bonus) => sum + bonus.amount, 0);
  }

  getDeductionTotal(deductions: Deduction[]): number {
    return deductions.reduce((sum, deduction) => sum + deduction.amount, 0);
  }

  getComparisonClass(change: number): string {
    if (change > 0) return 'positive';
    if (change < 0) return 'negative';
    return 'neutral';
  }

  getComparisonIcon(changeType: string): string {
    switch (changeType) {
      case 'increase': return 'icon-trending-up';
      case 'decrease': return 'icon-trending-down';
      default: return 'icon-minus';
    }
  }

  // Action methods
  exportEarnings(): void {
    console.log('Exporting earnings...');
    // Mock export functionality
    const csvContent = this.generateCSV();
    this.downloadCSV(csvContent, 'earnings-export.csv');
  }

  private generateCSV(): string {
    const headers = ['Date', 'Trip ID', 'Base Fare', 'Tips', 'Bonuses', 'Deductions', 'Net Earning', 'Status'];
    const rows = this.earnings.map(earning => [
      earning.date.toLocaleDateString(),
      earning.tripId,
      earning.baseFare.toString(),
      earning.tips.toString(),
      this.getBonusTotal(earning.bonuses).toString(),
      this.getDeductionTotal(earning.deductions).toString(),
      earning.netEarning.toString(),
      earning.payoutStatus
    ]);

    return [headers, ...rows].map(row => row.join(',')).join('\n');
  }

  private downloadCSV(content: string, filename: string): void {
    const blob = new Blob([content], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    link.click();
    window.URL.revokeObjectURL(url);
  }

  openGoalModal(): void {
    this.showGoalModal = true;
    this.newGoal = {
      type: GoalType.WEEKLY,
      period: GoalPeriod.THIS_WEEK,
      targetAmount: 0
    };
  }

  closeGoalModal(): void {
    this.showGoalModal = false;
  }

  saveGoal(): void {
    console.log('Saving goal:', this.newGoal);
    // Mock save functionality
    this.showGoalModal = false;
    // You could show a success message here
  }

  viewEarningDetails(earning: Earnings): void {
    console.log('Viewing details for earning:', earning.id);
    // Navigate to details page or open modal
  }

  downloadReceipt(earning: Earnings): void {
    console.log('Downloading receipt for earning:', earning.id);
    // Mock receipt download
  }

  refreshData(): void {
    this.loadData();
  }
}