export interface UsersStatsRequest {
  totalUser: number;
  averageLoginsPerUser: number;
  loginCountsByName: { [key: string]: number };  
  totalLoginTheCurrentMonth: number;
  userWithMostLogging: { [key: string]: number }; 
  userWithLeastLogging: { [key: string]: number }; 
  mostLoggedHour: { [key: number]: number };
}
