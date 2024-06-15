import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';

import { AppComponent } from './app.component';
import { TruckStatusComponent } from './truck-status/truck-status.component';
import {WebSocketService} from "./websocket.service";
import {TruckService} from "./truck.service";


@NgModule({
  declarations: [
    AppComponent,
    TruckStatusComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule
  ],
  providers: [WebSocketService, TruckService],
  bootstrap: [AppComponent]
})
export class AppModule { }
