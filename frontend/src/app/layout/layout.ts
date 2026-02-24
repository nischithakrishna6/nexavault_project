import { Component } from '@angular/core';
import { Sidebar } from '../sidebar/sidebar';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [Sidebar, RouterModule, CommonModule],
  templateUrl: './layout.html',
  styleUrls: ['./layout.css']
})
export class Layout {  // or LayoutComponent if you prefer
  isSidebarCollapsed = false;  // <-- ADD THIS

  // optional if you want a method instead of inline assignment
  onSidebarToggle(collapsed: boolean) {
    this.isSidebarCollapsed = collapsed;
  }
}
