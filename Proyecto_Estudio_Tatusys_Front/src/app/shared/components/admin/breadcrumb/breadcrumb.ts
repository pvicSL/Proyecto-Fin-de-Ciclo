import { Component } from '@angular/core';
import { Router, NavigationEnd, ActivatedRoute, RouterModule } from '@angular/router';
import { filter, map } from 'rxjs/operators';

@Component({
  selector: 'app-breadcrumb',
  imports: [RouterModule],
  templateUrl: './breadcrumb.html',
  styleUrl: './breadcrumb.css',
})
export class Breadcrumb {
  pageTitle: string = '';
  pageIcon: string = '';
  currentUrl: string = '';

  constructor(private router: Router, private activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd),
      map(() => {
        // Guardamos la URL actual para el routerLink
        this.currentUrl = this.router.url; 
        
        // Buscamos el objeto 'data' en la ruta hija activa
        let route = this.activatedRoute.firstChild;
        while (route?.firstChild) {
          route = route.firstChild;
        }
        return route?.snapshot.data;
      })
    ).subscribe(data => {
      // Estas variables disparan el cambio en el @if del HTML
      this.pageTitle = data?.['title'] || '';
      this.pageIcon = data?.['icon'] || '';
    });
  }
}
