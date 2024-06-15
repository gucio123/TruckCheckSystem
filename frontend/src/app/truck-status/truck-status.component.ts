import { Component, OnInit, OnDestroy } from '@angular/core';
import { WebSocketService } from '../websocket.service';
import { TruckService, Truck } from '../truck.service';

@Component({
  selector: 'app-truck-status',
  templateUrl: './truck-status.component.html',
  styleUrls: ['./truck-status.component.scss']
})
export class TruckStatusComponent implements OnInit, OnDestroy {
  queue1: Truck[] = [];
  queue2: Truck[] = [];
  checkingQueue1: Truck[] = [];
  checkingQueue2: Truck[] = [];
  trucks: Truck[] = [];
  estimatedWaitTime: number | null = null;
  private statusSubscription: any;

  constructor(private webSocketService: WebSocketService, private truckService: TruckService) { }

  ngOnInit(): void {
    this.statusSubscription = this.webSocketService.subscribeToStatus().subscribe((data) => {
      if (data) {
        this.queue1 = data.queue1.filter((truck: Truck) => truck.status !== 'CHECKING');
        this.queue2 = data.queue2.filter((truck: Truck) => truck.status !== 'CHECKING');
        this.checkingQueue1 = data.queue1.filter((truck: Truck) => truck.status === 'CHECKING');
        this.checkingQueue2 = data.queue2.filter((truck: Truck) => truck.status === 'CHECKING');
        this.trucks = data.trucks;
      }
    });
  }

  arrive(): void {
    this.truckService.arrive().subscribe();
  }

  calculateEstimatedWaitTime(truckId: string): void {
    this.truckService.getEstimatedWaitTime(truckId).subscribe((time) => {
      this.estimatedWaitTime = time;
    });
  }

  ngOnDestroy(): void {
    if (this.statusSubscription) {
      this.statusSubscription.unsubscribe();
    }
  }

  getCheckedTrucks(): Truck[] {
    return this.trucks.filter(truck => truck.status === 'CHECKED');
  }

  getOtherTrucks(): Truck[] {
    return this.trucks.filter(truck => truck.status === 'ARRIVED');
  }
}
