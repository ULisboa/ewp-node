import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
    imports: [RouterModule],
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'ewp-node-frontend';
}
