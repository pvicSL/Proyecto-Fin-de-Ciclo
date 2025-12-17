import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from '../../components/client/navbar/navbar';
import { FooterComponent } from '../../components/client/footer/footer';

@Component({
  selector: 'app-client-layout',
  imports: [RouterOutlet, NavbarComponent, FooterComponent],
  templateUrl: './client-layout.html',
  styleUrl: './client-layout.css',
})
export class ClientLayoutComponent {

}
