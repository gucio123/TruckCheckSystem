import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Truck {
  id: number;
  weight: number;
  waitingTime: number;
  status: string;
}

export interface TruckStatusResponse {
  queue1: Truck[];
  queue2: Truck[];
  trucks: Truck[];
}

@Injectable({
  providedIn: 'root'
})
export class TruckService {
  private apiUrl = 'http://localhost:8080/api/trucks';

  constructor(private http: HttpClient) { }

  getStatus(): Observable<TruckStatusResponse> {
    return this.http.get<TruckStatusResponse>(`${this.apiUrl}/status`);
  }

  arrive(): Observable<Truck> {
    return this.http.post<Truck>(`${this.apiUrl}/arrive`, {});
  }

  getEstimatedWaitTime(id: string): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/estimatedWaitTime/${id}`);
  }
}
