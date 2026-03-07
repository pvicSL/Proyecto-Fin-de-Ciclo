import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
    selector: 'app-privacy',
    standalone: true,
    imports: [RouterLink],
    templateUrl: './dataprotection.html',
    styleUrl: './dataprotection.css',
})
export class DataProtectionComponent implements OnInit {

    ngOnInit(): void {
        console.log('[DEBUG] DataProtectionComponent (Protección de Datos) inicializado correctamente.');
    }

}