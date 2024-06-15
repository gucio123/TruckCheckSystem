import { Pipe, PipeTransform } from '@angular/core';
import { Truck } from './truck.service';

@Pipe({
  name: 'filter'
})
export class FilterPipe implements PipeTransform {
  transform(trucks: Truck[], status: string): Truck[] {
    if (!trucks || !status) {
      return trucks;
    }

    const [filterStatus, queueNumber] = status.split(':');

    if (filterStatus === 'IN_QUEUE') {
      return trucks.filter(truck => truck.status === 'IN_QUEUE' && truck.id % 2 === +queueNumber % 2);
    } else {
      return trucks.filter(truck => truck.status === filterStatus);
    }
  }
}
