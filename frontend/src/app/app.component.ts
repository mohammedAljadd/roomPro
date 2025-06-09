import { Component } from '@angular/core';
import { NavigationEnd, Router, RouterLink, RouterOutlet } from '@angular/router';
import { HeaderComponent } from "./components/header/header.component";
import { FooterComponent } from "./components/footer/footer.component";
import { filter } from 'rxjs';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, HeaderComponent, FooterComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'roompro-frontend';
  name = ''

  paddingTop = '56px'; // default padding

  constructor(private router: Router) {
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: NavigationEnd) => {
      // Adjust padding based on current route
      if ( event.url.startsWith('/manage-rooms')) {
        this.paddingTop = '70px'; 
      } 
      
      else if( event.url.startsWith('/my-bookings')) {
        this.paddingTop = '100px'; 
      } 
      
      else if( event.url.startsWith('/meeting-rooms')) {
        this.paddingTop = '85px'; 
      } 

      else if( event.url.startsWith('/home')) {
        this.paddingTop = '85px'; 
      } 
      
      

      else {
        this.paddingTop = '56px'; // default navbar height
      }
    });
  }
}
