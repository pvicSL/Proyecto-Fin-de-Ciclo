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
    // 1. Ejecutar al cargar/refrescar la página
    this.updateBreadcrumbData();

    // 2. Ejecutar cuando cambie la navegación
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(() => {
      this.updateBreadcrumbData();
    });
  }

  private updateBreadcrumbData() {
    this.currentUrl = this.router.url;
    
    // Lógica para encontrar la ruta hija más profunda
    let route = this.activatedRoute.root;
    while (route.firstChild) {
      route = route.firstChild;
    }

    const data = route.snapshot.data;
    this.pageTitle = data['title'] || '';
    this.pageIcon = data['icon'] || '';
  }
}
