import { Component, inject, OnInit } from '@angular/core';
import { UsersStatsRequest } from '../../../model/class/Request/UsersStatsRequest';
import { LoginService } from '../../../services/auth/login.service';
import { CommonModule, KeyValuePipe } from '@angular/common';

@Component({
  selector: 'app-userstats',
  imports: [CommonModule, KeyValuePipe],
  templateUrl: './userstats.component.html',
  styleUrl: './userstats.component.css'
})
export class UserstatsComponent implements OnInit{

  token!: string | null;
  
  usersStats!: UsersStatsRequest;
  
  currentMonthName!: string;
  currentYear!: number;

  sortedLoginCounts: { key: string, value: number }[] = []; //  array of objects

  loginService = inject(LoginService);

  months: string[] = [
  'January',
  'February',
  'March',
  'April',
  'May',
  'June',
  'July',
  'August',
  'September',
  'October',
  'November',
  'December'
  ];

  ngOnInit(): void {
      this.token = localStorage.getItem('jwtToken');
      this.getUsersStats();
      setInterval(() => this.getUsersStats(), 1000);
  }


   getUsersStats(): void{
    if(this.token != null){
      const today = new Date();
      const currentYear = today.getFullYear();   
      this.currentYear = currentYear; 
      const currentMonth = today.getMonth() + 1;

      this.currentMonthName = this.months[currentMonth-1];

      

      this.loginService.getUsersStats(this.token, currentYear, currentMonth).subscribe({
        next: (data)=>{
          this.usersStats = data;
          console.log(data);
          this.sortedLoginCounts = Object.entries(this.usersStats.loginCountsByName) // Object.entries :  takes an object and returns an array of its key-value pairs.
          .map(([key, value]) => ({ key, value }))
          .sort((a, b) => b.value - a.value);
          

        },
        error: error=>{
          console.log(error.error);
        }
      })
    }
    else{
      console.log("Error");
    }
  }


}
