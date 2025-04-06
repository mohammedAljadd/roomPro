import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-home',
  imports: [CommonModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit{
  
  token!: string | null;

  ngOnInit(): void {
      this.token = localStorage.getItem('jwtToken'); 
  }
  
  

  
}

