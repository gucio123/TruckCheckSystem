import { Injectable } from '@angular/core';
import { Observable, BehaviorSubject } from 'rxjs';
import { Client, Stomp } from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';
import { HttpClient } from '@angular/common/http';
import { TruckStatusResponse } from './truck.service';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private stompClient: Client;
  private statusSubject: BehaviorSubject<any> = new BehaviorSubject(null);
  status$ = this.statusSubject.asObservable();

  constructor(private http: HttpClient) {
    const socket = new SockJS('http://localhost:8080/ws');
    this.stompClient = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: 5000,
      heartbeatIncoming: 0,
      heartbeatOutgoing: 20000
    });

    this.stompClient.onConnect = () => {
      console.log('Connected to WebSocket');
      this.stompClient.subscribe('/topic/status', message => {
        this.statusSubject.next(JSON.parse(message.body));
      });
      this.fetchInitialStatus();
    };

    this.stompClient.onStompError = (frame) => {
      console.error('Broker reported error: ' + frame.headers['message']);
      console.error('Additional details: ' + frame.body);
    };

    this.stompClient.activate();
  }

  fetchInitialStatus(): void {
    this.http.get<TruckStatusResponse>('http://localhost:8080/api/trucks/status').subscribe(
        (data) => {
          this.statusSubject.next(data);
        },
        (error) => {
          console.error('Failed to fetch initial status', error);
        }
    );
  }

  subscribeToStatus(): Observable<any> {
    return this.status$;
  }
}
