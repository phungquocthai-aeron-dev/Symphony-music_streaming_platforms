import { Component } from '@angular/core';
import { HeaderComponent } from '../../shared/components/header/header.component';
import { FooterComponent } from '../../shared/components/footer/footer.component';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-main-layout',
  imports: [HeaderComponent, FooterComponent, RouterOutlet],
  templateUrl: './main-layout.component.html'
})

export class MainLayoutComponent {

}
