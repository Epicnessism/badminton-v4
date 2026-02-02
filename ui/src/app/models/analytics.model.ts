export interface UserAnalytics {
  userId: string;
  computedAt: string;
  
  // Owner stats
  totalStringingsAsOwner: number;
  stringingsByState: Record<string, number>;
  stringTypeUsage: Record<string, number>;
  racketUsage: Record<string, number>;
  mostUsedTensionCombination: string | null;
  mostUsedTensionCount: number | null;
  monthlyTrend: MonthlyCount[];
  topStringers: Record<string, number> | null;
  
  // Stringer stats
  totalStringingsAsStringer: number | null;
  topCustomers: Record<string, number> | null;
  averageCompletionTimeHours: number | null;
  successRate: number | null;
  busiestMonth: string | null;
  stringerStringTypeUsage: Record<string, number> | null;
  stringerRacketUsage: Record<string, number> | null;
  stringerMonthlyTrend: MonthlyCount[] | null;
}

export interface MonthlyCount {
  month: string;
  count: number;
}
