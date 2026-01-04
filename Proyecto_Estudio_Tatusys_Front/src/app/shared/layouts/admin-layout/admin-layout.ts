import { Component } from '@angular/core';
import { Sidebar } from '../../components/admin/sidebar/sidebar';
import { RouterOutlet } from '@angular/router';
import { Breadcrumb } from "../../components/admin/breadcrumb/breadcrumb";

@Component({
  selector: 'app-admin-layout',
  imports: [RouterOutlet, Sidebar, Breadcrumb],
  templateUrl: './admin-layout.html',
  styleUrl: './admin-layout.css',
})
export class AdminLayout {

}
